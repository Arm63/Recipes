package com.example.recipes.ui.fragment

import com.example.recipes.ui.adapter.RecipeAdapter
import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipes.R
import com.example.recipes.db.cursor.CursorReader
import com.example.recipes.db.entity.Recipe
import com.example.recipes.db.handler.RecipeAsyncQueryHandler
import com.example.recipes.io.bus.BusProvider
import com.example.recipes.ui.activity.RecipeInfoActivity
import com.example.recipes.ui.util.Constant

class FavoriteFragment : BaseFragment(), RecipeAdapter.OnItemClickListener,
    View.OnClickListener, RecipeAsyncQueryHandler.AsyncQueryListener {

    companion object {
        private val LOG_TAG: String = RecipeListFragment::class.java.simpleName
        fun newInstance(): FavoriteFragment = FavoriteFragment()
    }

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mRecipeAdapter: RecipeAdapter
    private lateinit var mRecipeFavList: ArrayList<Recipe>
    private lateinit var mRecipeAQH: RecipeAsyncQueryHandler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite_list, container, false)
        BusProvider.register(this)
        init()
        findViews(view)
        setListeners()
        loadData()
        return view
    }

    private fun findViews(view: View?) {
        mRecyclerView = view!!.findViewById(R.id.rv_fv_list) as RecyclerView
    }

    override fun onStart() {
        super.onStart()
        mRecipeAQH.getFavoriteRecipes()
    }


    private fun init() {
        mRecipeAQH = activity?.applicationContext?.let { RecipeAsyncQueryHandler(it, this) }!!

        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        mRecyclerView.itemAnimator = DefaultItemAnimator()

        mRecipeFavList = ArrayList()
        mRecipeAdapter = context?.let { RecipeAdapter(it, mRecipeFavList, this) }!!
        mRecyclerView.adapter = mRecipeAdapter
    }

    private fun setListeners() {

    }

    private fun loadData() {

    }

    override fun onItemClick(item: Recipe, position: Int) {
        val intent = Intent(activity, RecipeInfoActivity::class.java)
        intent.putExtra(Constant.Extra.recipe_ID, item.id)
        startActivity(intent)
    }

    override fun onItemLongClick(item: Recipe, position: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setMessage(R.string.msg_dialog_delete_recipe)
            .setCancelable(false)
            .setPositiveButton(R.string.text_btn_dialog_yes) { dialog, _ ->
                item.isFavorite = false
                mRecipeAQH.updateRecipe(item)
                mRecipeFavList.removeAt(position)
                mRecipeAdapter.notifyItemRemoved(position)
                dialog.cancel()
            }
            .setNegativeButton(
                R.string.text_btn_dialog_no
            ) { dialog, _ -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onClick(v: View?) {

    }

    override fun onQueryComplete(token: Int, cookie: Any?, cursor: Cursor?) {
        when (token) {
            RecipeAsyncQueryHandler.QueryToken.GET_FAVORITE_RECIPES -> {
                val favoriterecipes = cursor?.let { CursorReader.parseRecipes(it) }

                if (favoriterecipes!!.size != 0) {
                    mRecipeFavList.clear()
                    mRecipeFavList.addAll(favoriterecipes)
                    mRecipeAdapter.notifyDataSetChanged()
                } else {
                    mRecipeFavList.clear()
                    mRecipeAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onInsertComplete(token: Int, cookie: Any?, uri: Uri?) {

    }

    override fun onUpdateComplete(token: Int, cookie: Any?, result: Int) {

    }

    override fun onDeleteComplete(token: Int, cookie: Any?, result: Int) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        BusProvider.unregister(this)
    }

}