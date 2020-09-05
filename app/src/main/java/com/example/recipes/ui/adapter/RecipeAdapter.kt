package com.example.recipes.ui.adapter
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.recipes.R
import com.example.recipes.db.entity.Recipe
import com.example.recipes.ui.util.CustomFilter
import kotlinx.android.synthetic.main.layout_recipe_list_item.view.*
import java.util.*


class RecipeAdapter(
    var mContext: Context,
    var mRecipeList: ArrayList<Recipe>,
    var onItemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>(), Filterable {

    // ===========================================================
    // Constants
    // ===========================================================

    private val LOG_TAG: String = RecipeAdapter::class.java.simpleName

    // ===========================================================
    // Fields
    // ===========================================================

    var lastPosition = -1
    var filter: CustomFilter? = null


    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_recipe_list_item, parent, false)
        return RecipeViewHolder(view, mRecipeList, onItemClickListener)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row)
        holder.itemView.startAnimation(animation)
        holder.bindData(mRecipeList[position])
        lastPosition = holder.adapterPosition
    }

    override fun getItemCount(): Int = mRecipeList.size


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }
    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Click Listeners
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    //------------------------------------------------------------------------------------------------

    class RecipeViewHolder constructor(
        itemView: View,
        var recipeList: ArrayList<Recipe>,
        private var onItemClickListener: OnItemClickListener
    ) : RecyclerView.ViewHolder(itemView) {


        var llItemContainer: LinearLayout? = itemView.findViewById(R.id.ll_recipe_item_container)

        fun bindData(recipe: Recipe) {
            val requestOption = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOption)
                .load(recipe.image)
                .into(itemView.iv_recipe_item)

            itemView.tv_recipe_item_name.text = recipe.name
            itemView.tv_recipe_item_price.text = recipe.price.toString()

            llItemContainer!!.setOnClickListener {
                onItemClickListener.onItemClick(recipeList[adapterPosition], adapterPosition)
            }
            llItemContainer!!.setOnLongClickListener {
                onItemClickListener.onItemLongClick(recipeList[adapterPosition], adapterPosition)
                true
            }
        }
    }

    fun removeItem(position: Int) {
        mRecipeList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreItem(item: Recipe, position: Int) {
        mRecipeList.add(position, item)
        notifyItemInserted(position)
    }

    fun getData(): ArrayList<Recipe> {
        return mRecipeList
    }

    fun sortByName(recipeList: ArrayList<Recipe>) {
        recipeList.sortWith(Comparator { f1, f2 -> f2.name?.let { f1.name?.compareTo(it) }!! })
        mRecipeList = recipeList
    }

    fun sortByPrice(recipeList: ArrayList<Recipe>) {
        recipeList.sortWith(Comparator { f1, f2 -> f2.price?.let { f1.price?.compareTo(it) }!! })
        mRecipeList = recipeList
    }

    fun sortByFav(recipeList: ArrayList<Recipe>) {
        recipeList.sortWith(Comparator { f1, f2 -> f1.isFavorite.let { f2.isFavorite.compareTo(it) } })
        mRecipeList = recipeList
    }


    interface OnItemClickListener {
        fun onItemClick(item: Recipe, position: Int)
        fun onItemLongClick(item: Recipe, position: Int)

    }

    override fun getFilter(): Filter {
        if (filter == null)
            filter = CustomFilter(mRecipeList,this)
        return filter as CustomFilter
    }

}