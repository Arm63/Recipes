package com.example.recipes.ui.fragment

import com.example.recipes.ui.adapter.RecipeAdapter
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.example.recipes.R
import com.example.recipes.db.entity.Recipe
import com.example.recipes.db.handler.RecipeAsyncQueryHandler
import com.example.recipes.io.bus.BusProvider

class SortFragment : BaseFragment() {


    // ===========================================================
    // Constants
    // ===========================================================

    companion object {
        private val LOG_TAG: String = SortFragment::class.java.simpleName
        fun newInstance(): SortFragment = SortFragment()
    }


    // ===========================================================
    // Fields
    // ===========================================================

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mRecyclerViewAdapter: RecipeAdapter
    private lateinit var mRecipeList: ArrayList<Recipe>
    private lateinit var mRecipeAQH: RecipeAsyncQueryHandler

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite_list, container, false)
        BusProvider.register(this)
        findViews(view)
        init()
        setListeners()
        loadData()
        return view
    }

    private fun findViews(view: View?) {
        mRecyclerView = view!!.findViewById(R.id.rv_sort_list)
    }

    private fun init() {

    }

    private fun setListeners() {

    }

    private fun loadData(){

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}