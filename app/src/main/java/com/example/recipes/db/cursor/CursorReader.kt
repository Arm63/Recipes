package com.example.recipes.db.cursor

import android.database.Cursor
import com.example.recipes.db.RecipeDB.RECIPE_DESCRIPTION
import com.example.recipes.db.RecipeDB.RECIPE_FAVORITE
import com.example.recipes.db.RecipeDB.RECIPE_ID
import com.example.recipes.db.RecipeDB.RECIPE_IMAGE
import com.example.recipes.db.RecipeDB.RECIPE_NAME
import com.example.recipes.db.RecipeDB.RECIPE_PRICE
import com.example.recipes.db.RecipeDB.RECIPE_USER
import com.example.recipes.db.entity.Recipe
import com.example.recipes.ui.util.AppUtil


class CursorReader {
    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Constants
    // ===========================================================
    private val LOG_TAG = CursorReader::class.java.simpleName

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
     * RECIPE
     *************************************************************/

    companion object {
        fun parseRecipe(cursor: Cursor): Recipe? {
            var recipe: Recipe? = null
            if (!cursor.isClosed && cursor.moveToFirst())
                recipe = composeRecipe(cursor)
            cursor.close()
            return recipe
        }

        fun parseRecipes(cursor: Cursor): ArrayList<Recipe> {
            val recipes: ArrayList<Recipe> = ArrayList()
            if (!cursor.isClosed && cursor.moveToFirst()) {
                do {
                    val item: Recipe = composeRecipe(cursor)
                    recipes.add(item)
                } while (cursor.moveToNext())
            }
            cursor.close()
            return recipes
        }


        private fun composeRecipe(cursor: Cursor): Recipe {
            val recipe = Recipe()
            recipe.id = cursor.getLong(cursor.getColumnIndex(RECIPE_ID))
            recipe.name = cursor.getString(cursor.getColumnIndex(RECIPE_NAME))
            recipe.price = cursor.getLong(cursor.getColumnIndex(RECIPE_PRICE))
            recipe.isFavorite = AppUtil.intToBoolean(cursor.getInt(cursor.getColumnIndex(RECIPE_FAVORITE)))
            recipe.isFromUser = AppUtil.intToBoolean(cursor.getInt(cursor.getColumnIndex(RECIPE_USER)))
            recipe.description = cursor.getString(cursor.getColumnIndex(RECIPE_DESCRIPTION))
            recipe.image = cursor.getString(cursor.getColumnIndex(RECIPE_IMAGE))
            return recipe
        }
    }
}
