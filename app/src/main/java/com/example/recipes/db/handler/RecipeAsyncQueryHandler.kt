package com.example.recipes.db.handler

import android.content.AsyncQueryHandler
import android.content.Context
import android.database.Cursor
import android.net.Uri
import com.example.recipes.db.RecipeDB
import com.example.recipes.db.entity.Recipe
import com.example.recipes.db.provider.UriBuilder
import com.example.recipes.ui.util.AppUtil
import java.lang.ref.WeakReference

class RecipeAsyncQueryHandler(context: Context, queryListenerReference: AsyncQueryListener) :
    AsyncQueryHandler(context.contentResolver) {
    // ===========================================================
    // Constants
    // ===========================================================

    private val LOG_TAG: String = RecipeAsyncQueryHandler::class.java.simpleName

    object QueryToken {
        const val GET_RECIPE = 100
        const val GET_RECIPES = 101
        const val ADD_RECIPE = 102
        const val UPDATE_RECIPE = 104
        const val DELETE_recipe = 105
        const val DELETE_RECIPES = 106
        const val GET_FAVORITE_RECIPES = 107
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    interface AsyncQueryListener {
        fun onQueryComplete(token: Int, cookie: Any?, cursor: Cursor?)
        fun onInsertComplete(token: Int, cookie: Any?, uri: Uri?)
        fun onUpdateComplete(token: Int, cookie: Any?, result: Int)
        fun onDeleteComplete(token: Int, cookie: Any?, result: Int)
    }

    // ===========================================================
    // Fields
    // ===========================================================

    private var mQueryListenerReference
            = WeakReference(queryListenerReference)

    // ===========================================================
    // Constructors
    // ===========================================================

    override fun onQueryComplete(token: Int, cookie: Any?, cursor: Cursor) {
        val queryListener = mQueryListenerReference.get()
        if (queryListener != null) {
            queryListener.onQueryComplete(token, cookie, cursor)
        } else cursor.close()
    }

    override fun onInsertComplete(token: Int, cookie: Any?, uri: Uri?) {
        mQueryListenerReference.get()?.onInsertComplete(token, cookie, uri)
    }

    override fun onUpdateComplete(token: Int, cookie: Any?, result: Int) {
        mQueryListenerReference.get()?.onUpdateComplete(token, cookie, result)

    }

    override fun onDeleteComplete(token: Int, cookie: Any?, result: Int) {
        mQueryListenerReference.get()?.onDeleteComplete(token, cookie, result)
    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Methods systems
    // ===========================================================

    // ===========================================================
    // Methods controls
    // ===========================================================

    /**
     * RECIPE Methods
     *************************************************************/

    @Synchronized
    fun getRecipe(id: Long) {
        startQuery(
            QueryToken.GET_RECIPE,
            null,
            UriBuilder.buildRecipeUri(),
            RecipeDB.Projection.RECIPES,
            RecipeDB.RECIPE_ID + "=?",
            arrayOf(id.toString()),
            null
        )
    }

    @Synchronized
    fun getRecipes() {
        startQuery(
            QueryToken.GET_RECIPES,
            null,
            UriBuilder.buildRecipeUri(),
            RecipeDB.Projection.RECIPES,
            null,
            null,
            null
        )
    }

    @Synchronized
    fun addRecipe(recipe: Recipe) {
        startInsert(
            QueryToken.ADD_RECIPE,
            null,
            UriBuilder.buildRecipeUri(),
            RecipeDB.composeValues(recipe, RecipeDB.ContentValuesType.RECIPES)
        )
    }

    //134Tox@ poxaca id qcaca
    @Synchronized
    fun updateRecipe(recipe: Recipe) {
        startUpdate(
            QueryToken.UPDATE_RECIPE,
            null,
            recipe.id?.let { UriBuilder.buildRecipeUri(it) },
            RecipeDB.composeValues(recipe, RecipeDB.ContentValuesType.RECIPES),
            "${RecipeDB.RECIPE_ID}=?",
            arrayOf((recipe.id).toString())
        )
    }

    @Synchronized
    fun deleteRecipe(recipe: Recipe) {
        startDelete(
            QueryToken.DELETE_recipe,
            null,
            UriBuilder.buildRecipeUri(recipe.id!!),
            RecipeDB.RECIPE_ID + "=?",
            arrayOf((recipe.id).toString())
        )
    }


    @Synchronized
    fun deleteRecipes() {
        startDelete(
            QueryToken.DELETE_RECIPES,
            null,
            UriBuilder.buildRecipeUri(),
            null,
            null
        )
    }

    @Synchronized
    fun getFavoriteRecipes() {
        startQuery(
            QueryToken.GET_FAVORITE_RECIPES,
            null,
            UriBuilder.buildRecipeUri(),
            RecipeDB.Projection.RECIPES,
            RecipeDB.RECIPE_FAVORITE + "=?",
            arrayOf((AppUtil.booleanToInt(true)).toString()),
            null
        )
    }

}
