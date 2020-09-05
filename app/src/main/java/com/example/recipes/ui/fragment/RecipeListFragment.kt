package com.example.recipes.ui.fragment

import com.example.recipes.ui.adapter.RecipeAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.recipes.R
import com.example.recipes.db.cursor.CursorReader
import com.example.recipes.db.entity.Recipe
import com.example.recipes.db.handler.RecipeAsyncQueryHandler
import com.example.recipes.db.handler.RecipeQueryHandler
import com.example.recipes.io.bus.BusProvider
import com.example.recipes.io.bus.event.ApiEvent
import com.example.recipes.io.service.RecipeIntentService
import com.example.recipes.ui.activity.RecipeAddActivity
import com.example.recipes.ui.activity.RecipeInfoActivity
import com.example.recipes.ui.adapter.decorator.MyDividerItemDecoration
import com.example.recipes.ui.util.AppUtil
import com.example.recipes.ui.util.Constant
import com.example.recipes.ui.util.NetworkUtil
import com.example.recipes.ui.util.SwipeToDeleteCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.common.eventbus.Subscribe


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "IMPLICIT_BOXING_IN_IDENTITY_EQUALS")
class RecipeListFragment : BaseFragment(), RecipeAdapter.OnItemClickListener,
    View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
    RecipeAsyncQueryHandler.AsyncQueryListener {

    companion object {
        fun newInstance(): RecipeListFragment = RecipeListFragment()
        private const val REQUEST_CODE = 100
        private val LOG_TAG: String = RecipeListFragment::class.java.simpleName
    }


    private var icon: Drawable? = null
    private var background: ColorDrawable? = null

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mRecyclerViewAdapter: RecipeAdapter
    private lateinit var mRecipeList: ArrayList<Recipe>
    private lateinit var mFloatingActionButton: FloatingActionButton
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mRecipeAQH: RecipeAsyncQueryHandler

    override fun onStart() {
        super.onStart()
        mRecipeAQH.getRecipes()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_recipe_list, container, false)
        BusProvider.register(this)
        findViews(view)
        init()
        setListeners()
        onRefresh()
        enableSwipeToDeleteAndUndo()

        return view
    }

    private fun findViews(view: View?) {
        mRecyclerView = view?.findViewById<View>(R.id.rv_main) as RecyclerView
        mSwipeRefreshLayout = view.findViewById(R.id.sw_fragment_recipe_list) as SwipeRefreshLayout
        mFloatingActionButton = view.findViewById(R.id.fl_btn_recipe_add) as FloatingActionButton
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_recipe_search, menu)

        val item = menu.findItem(R.id.search)
        val searchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                mRecyclerViewAdapter.getFilter().filter(query)
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sort_by_name -> {
                mRecyclerViewAdapter.sortByName(mRecipeList)
                mRecyclerViewAdapter.notifyDataSetChanged()

            }
            R.id.menu_sort_by_price -> {
                mRecyclerViewAdapter.sortByPrice(mRecipeList)
                mRecyclerViewAdapter.notifyDataSetChanged()

            }
            R.id.menu_sort_by_fav -> {
                mRecyclerViewAdapter.sortByFav(mRecipeList)
                mRecyclerViewAdapter.notifyDataSetChanged()

            }
        }
        onRefresh()
        return true
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        mRecipeAQH = activity?.applicationContext?.let { RecipeAsyncQueryHandler(it, this) }!!

        icon = context?.let { ContextCompat.getDrawable(it, R.drawable.ic_delete_white) };
        background = ColorDrawable(Color.RED)

        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        mRecyclerView.itemAnimator = DefaultItemAnimator()
        context?.let {
            MyDividerItemDecoration(it, DividerItemDecoration.VERTICAL, 16)
        }?.let {
            mRecyclerView.addItemDecoration(it)
        }

        mRecipeList = ArrayList()
        mRecyclerViewAdapter = context?.let { RecipeAdapter(it, mRecipeList, this) }!!
        mRecyclerView.adapter = mRecyclerViewAdapter
        mSwipeRefreshLayout.isRefreshing = false

    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {
        mSwipeRefreshLayout.setOnRefreshListener(this)
        mFloatingActionButton.setOnClickListener(this)
        mRecyclerView.setOnTouchListener { _, _ ->
            AppUtil.closeKeyboard(activity)
            false
        }
    }

// ===========================================================
// Constructors
// ===========================================================


// ===========================================================
// Other Listeners, methods for/from Interfaces
// ===========================================================


    private fun enableSwipeToDeleteAndUndo() {
        val swipeToDeleteCallback: SwipeToDeleteCallback =
            object : SwipeToDeleteCallback(this@RecipeListFragment) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                    val position = viewHolder.adapterPosition
                    val item: Recipe = mRecyclerViewAdapter.getData()[position]
                    mRecipeList.removeAt(position)
                    mRecyclerViewAdapter.notifyItemRemoved(position)
                    mRecipeAQH.deleteRecipe(item)
                    showSnackbar(item, position)

                }

                private fun showSnackbar(item: Recipe, position: Int) {
                    val snackbar = mRecyclerView.let {
                        Snackbar
                            .make(
                                it,
                                "Item was removed from the list.",
                                Snackbar.LENGTH_LONG
                            )
                    }
                    snackbar.setAction("UNDO") {
                        mRecyclerViewAdapter.restoreItem(item, position)
                        mRecyclerView.scrollToPosition(position)

                    }
                    snackbar.setActionTextColor(Color.YELLOW)
                    snackbar.show()
                }
            }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(mRecyclerView)
    }

    override fun onItemClick(item: Recipe, position: Int) {
        val intent = Intent(activity, RecipeInfoActivity::class.java)
        intent.putExtra(Constant.Extra.recipe_ID, item.id)
        startActivity(intent)
    }

    override fun onItemLongClick(item: Recipe, position: Int) {
        openDeleteRecipeDialog(item, position)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_btn_recipe_add -> {
                val intent = Intent(activity, RecipeAddActivity::class.java)
                startActivityForResult(intent, Constant.RequestCode.ADD_RECIPE_ACTIVITY)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constant.RequestCode.ADD_RECIPE_ACTIVITY -> {
                    val recipe: Recipe = data!!.getParcelableExtra(Constant.Extra.EXTRA_recipe)
                    mRecipeList.add(recipe)
                    mRecyclerViewAdapter.notifyDataSetChanged()
                }
            }
        }
    }


    private fun openDeleteRecipeDialog(recipe: Recipe, position: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setMessage(R.string.msg_dialog_delete_recipe)
            .setCancelable(false)
            .setPositiveButton(R.string.text_btn_dialog_yes) { dialog, _ ->
                mRecipeAQH.deleteRecipe(recipe)
                mRecipeList.removeAt(position)
                mRecyclerViewAdapter.notifyItemRemoved(position)
                dialog.cancel()
            }
            .setNegativeButton(
                R.string.text_btn_dialog_no
            ) { dialog, _ -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }


    override fun onQueryComplete(token: Int, cookie: Any?, cursor: Cursor?) {
        when (token) {
            RecipeAsyncQueryHandler.QueryToken.GET_RECIPES -> {
                val recipes = cursor?.let { CursorReader.parseRecipes(it) }
                if (recipes!!.size != 0) {
                    mRecipeList.clear()
                    mRecipeList.addAll(recipes)
                    mRecyclerViewAdapter.notifyDataSetChanged()
                } else {
                    mRecipeList.clear()
                    mRecyclerViewAdapter.notifyDataSetChanged()
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

    @Subscribe
    fun onEventReceived(apiEvent: ApiEvent<Any?>) {
        if (apiEvent.isSuccess) {
            mRecipeAQH.getRecipes()
            mSwipeRefreshLayout.isRefreshing = true
        } else {
            Toast.makeText(activity, LOG_TAG + "pragram", Toast.LENGTH_SHORT).show()

        }
        mSwipeRefreshLayout.isRefreshing = false
    }

    override fun onRefresh() {
        mSwipeRefreshLayout.isRefreshing = true
        if (NetworkUtil.instance!!.isConnected(context!!) && RecipeQueryHandler.getRecipes(context!!)!!
                .isEmpty()
        ) {
            RecipeIntentService.start(
                activity!!,
                Constant.API.RECIPE_LIST,
                Constant.RequestType.RECIPE_LIST
            )
        } else {
            mRecipeAQH.getRecipes()
            mSwipeRefreshLayout.isRefreshing = false
        }
    }


    override fun onDestroyView() {
        BusProvider.unregister(this)
        super.onDestroyView()
    }

}