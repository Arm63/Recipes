package com.example.recipes.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.recipes.db.RecipeDB.RECIPE_TABLE

class RecipeDBHelper(context: Context?) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "RECIPES.DB"
        private const val DATABASE_VERSION = 8
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(RecipeDB.CREATE_RECIPE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $RECIPE_TABLE")
        onCreate(db)
    }
}