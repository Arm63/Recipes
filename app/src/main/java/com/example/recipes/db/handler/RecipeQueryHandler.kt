package com.example.recipes.db.handler

import android.content.Context
import com.example.recipes.db.RecipeDB
import com.example.recipes.db.RecipeDB.composeValues
import com.example.recipes.db.RecipeDB.composeValuesArray
import com.example.recipes.db.cursor.CursorReader
import com.example.recipes.db.entity.Recipe
import com.example.recipes.db.provider.UriBuilder
import com.example.recipes.ui.util.AppUtil


object RecipeQueryHandler {
    // ===========================================================
    // Constants
    // ===========================================================
    private val LOG_TAG = RecipeQueryHandler::class.java.simpleName
    // ===========================================================
    // Fields
    // ===========================================================
    // ===========================================================
    // Constructors
    // ===========================================================
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================
    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================
    // ===========================================================
    // Methods
    // ===========================================================
    /**
     * recipe METHODS
     */
    @Synchronized
    fun addRecipe(context: Context, recipe: Recipe?) {
        context.contentResolver.insert(
            UriBuilder.buildRecipeUri(),
            composeValues(recipe!!, RecipeDB.ContentValuesType.RECIPES)
        )
    }

    @Synchronized
    fun addRecipes(context: Context, RECIPES: ArrayList<Recipe>?) {
        context.contentResolver.bulkInsert(
            UriBuilder.buildRecipeUri(),
            composeValuesArray(RECIPES!!, RecipeDB.ContentValuesType.RECIPES)
        )
    }

    @Synchronized
    fun updateRecipe(context: Context, recipe: Recipe) {
        context.contentResolver.update(
            UriBuilder.buildRecipeUri(),
            composeValues(recipe, RecipeDB.RECIPE_TABLE),
            RecipeDB.RECIPE_ID + "=?", arrayOf(recipe.id.toString())
        )
    }

    @Synchronized
    fun updateRecipeDescription(context: Context, recipe: Recipe) {
        context.contentResolver.update(
            UriBuilder.buildRecipeUri(),
            composeValues(recipe, RecipeDB.ContentValuesType.DESCRIPTION),
            RecipeDB.RECIPE_ID + "=?",
            arrayOf(recipe.id.toString())
        )
    }

    @Synchronized
    fun getRecipe(context: Context, id: Long): Recipe? {
        val cursor = context.contentResolver.query(
            UriBuilder.buildRecipeUri(id),
            RecipeDB.Projection.RECIPES,
            null,
            null,
            null
        )
        return cursor?.let { CursorReader.parseRecipe(it) }
    }

    @Synchronized
    fun getRecipes(context: Context): ArrayList<Recipe>? {
        val cursor = context.contentResolver.query(
            UriBuilder.buildRecipeUri(),
            RecipeDB.Projection.RECIPES,
            null,
            null,
            null
        )
        return cursor?.let { CursorReader.parseRecipes(it) }
    }

    @Synchronized
    fun deleteRecipe(context: Context, recipe: Recipe) {
        context.contentResolver.delete(
            UriBuilder.buildRecipeUri(),
            RecipeDB.RECIPE_ID + "=?", arrayOf(recipe.id.toString())
        )
    }

    @Synchronized
    fun deleteRecipes(context: Context) {
        context.contentResolver.delete(
            UriBuilder.buildRecipeUri(),
            null,
            null
        )
    }

    @Synchronized
    fun getAllFavoriteRecipes(context: Context): ArrayList<Recipe>? {
        val cursor = context.contentResolver.query(
            UriBuilder.buildRecipeUri(),
            RecipeDB.Projection.RECIPES,
            RecipeDB.RECIPE_FAVORITE + "=?", arrayOf(
                AppUtil.booleanToInt(true).toString()),
            null
        )
        return cursor?.let { CursorReader.parseRecipes(it) }
    }

//    @Synchronized
//    fun updateProductsExceptFavorite(context: Context, RECIPES: ArrayList<Recipe>) {
//        for (recipe in RECIPES) {
//            recipe.id?.let { UriBuilder.buildRecipeUri(it) }?.let {
//                context.contentResolver.update(
//                    it,
//                    RecipeDB.composeValues(recipe, RecipeDB.ContentValuesType.ALL_EXCEPT_FAVORITE),
//                    null,
//                    null
//                )
//            }
//        }
//    }
//

//    @Synchronized
//    fun getAllFromUserRECIPES(context: Context): ArrayList<Recipe> {
//        val cursor = context.contentResolver.query(
//            UriBuilder.buildRecipeUri(),
//            RecipeDB.Projection.RECIPE,
//            RecipeDB.recipe_USER + "=?", arrayOf(AppUtil.booleanToInt(true).toString()),
//            null
//        )
//        return CursorReader.parseRECIPES(cursor)
//    } // ===========================================================
    // Inner and Anonymous Clases
    // ===========================================================
}