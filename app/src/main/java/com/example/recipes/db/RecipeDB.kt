@file:Suppress("UNCHECKED_CAST")

package com.example.recipes.db

import android.content.ContentValues
import com.example.recipes.db.RecipeDB.ContentValuesType.DESCRIPTION
import com.example.recipes.db.RecipeDB.ContentValuesType.RECIPES
import com.example.recipes.db.entity.Recipe
import com.example.recipes.ui.util.AppUtil

object RecipeDB {

    object ContentValuesType {
        const val RECIPES = "RECIPES"
        const val DESCRIPTION = "DESCRIPTION"
        const val ALL_EXCEPT_FAVORITE = "ALL_EXCEPT_FAVORITE"

    }


    /**
     * TABLES
     */

    const val RECIPE_TABLE = "RECIPE_TABLE"

    const val RECIPE_PK = "_id"
    const val RECIPE_ID = "RECIPE_ID"
    const val RECIPE_NAME = "RECIPE_NAME"
    const val RECIPE_PRICE = "RECIPE_PRICE"
    const val RECIPE_IMAGE = "RECIPE_IMAGE"
    const val RECIPE_FAVORITE = "RECIPE_FAVORITE"
    const val RECIPE_USER = "RECIPE_USER"
    const val RECIPE_DESCRIPTION = "RECIPE_DESCRIPTION"
    const val CREATE_RECIPE_TABLE = ("CREATE TABLE IF NOT EXISTS " + RECIPE_TABLE
            + " ("
            + RECIPE_PK + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RECIPE_ID + " INTEGER UNIQUE, "
            + RECIPE_NAME + " TEXT, "
            + RECIPE_PRICE + " INTEGER, "
            + RECIPE_FAVORITE + " INTEGER, "
            + RECIPE_USER + " INTEGER, "
            + RECIPE_DESCRIPTION + " TEXT, "
            + RECIPE_IMAGE + " TEXT "
            + ");")


    /**
     * PROJECTIONS
     */
    object Projection {
        var RECIPES = arrayOf(
            RECIPE_PK,
            RECIPE_ID,
            RECIPE_NAME,
            RECIPE_PRICE,
            RECIPE_FAVORITE,
            RECIPE_USER,
            RECIPE_DESCRIPTION,
            RECIPE_IMAGE
        )
    }


    /**
     * VALUES
     */

    fun composeValues(item: Any, table: String?): ContentValues {
        val values = ContentValues()
        val recipe: Recipe
        when (table) {
            RECIPES -> {
                recipe = item as Recipe
                values.put(RECIPE_ID, recipe.id)
                values.put(RECIPE_NAME, recipe.name)
                values.put(RECIPE_PRICE, recipe.price)
                values.put(RECIPE_FAVORITE, recipe.isFavorite.let { AppUtil.booleanToInt(it) })
                values.put(RECIPE_USER, recipe.isFromUser?.let { AppUtil.booleanToInt(it) })
                values.put(RECIPE_DESCRIPTION, recipe.description)
                values.put(RECIPE_IMAGE, recipe.image)
            }
            DESCRIPTION -> {
                recipe = item as Recipe
                values.put(RECIPE_DESCRIPTION, recipe.description)
            }
        }
        return values
    }

    fun composeValuesArray(objects: ArrayList<*>, table: String?): Array<ContentValues> {
        val valuesList = ArrayList<ContentValues>()
        val recipes = objects as ArrayList<Recipe>

        when (table) {
            RECIPES -> for (recipe in recipes) {
                val values = ContentValues()
                values.put(RECIPE_ID, recipe.id)
                values.put(RECIPE_NAME, recipe.name)
                values.put(RECIPE_PRICE, recipe.price)
                values.put(RECIPE_FAVORITE, recipe.isFavorite.let { AppUtil.booleanToInt(it) })
                values.put(RECIPE_USER, recipe.isFromUser)
                values.put(RECIPE_DESCRIPTION, recipe.description)
                values.put(RECIPE_IMAGE, recipe.image)

                valuesList.add(values)
            }
        }
        return valuesList.toTypedArray()

    }

}