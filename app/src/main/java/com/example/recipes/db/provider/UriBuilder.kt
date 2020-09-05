package com.example.recipes.db.provider

import android.net.Uri
import com.example.recipes.BuildConfig
import com.example.recipes.db.RecipeDB.RECIPE_TABLE

class UriBuilder {
    companion object {
        fun buildRecipeUri(id: Long): Uri =
            Uri.parse("content://" + BuildConfig.APPLICATION_ID + "/" + RECIPE_TABLE + "/" + id)

        fun buildRecipeUri(): Uri =
            Uri.parse("content://" + BuildConfig.APPLICATION_ID + "/" + RECIPE_TABLE)
    }
}