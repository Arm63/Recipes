package com.example.recipes.db.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.text.TextUtils
import com.example.recipes.BuildConfig
import com.example.recipes.db.RecipeDB.RECIPE_ID
import com.example.recipes.db.RecipeDB.RECIPE_TABLE
import com.example.recipes.db.RecipeDBHelper

class RecipeProvider : ContentProvider() {

    private object Code {
        const val ALL_RECIPES = 1
        const val SINGLE_RECIPE = 2
    }

    private class ContentType {
        companion object {
            const val ALL_RECIPES = ("vnd.android.cursor.dir/vnd."
                    + BuildConfig.APPLICATION_ID + "." + RECIPE_TABLE)

            const val SINGLE_RECIPE = ("vnd.android.cursor.item/vnd."
                    + BuildConfig.APPLICATION_ID + "." + RECIPE_TABLE)
        }

    }

    private val sUriMatcher: UriMatcher? = buildUriMatcher()
    private lateinit var mDBHelper: RecipeDBHelper


// ===========================================================
// Listeners, methods for/from Interfaces
// ===========================================================
// ===========================================================
// Methods
// ===========================================================


    override fun onCreate(): Boolean {
        mDBHelper = RecipeDBHelper(context)
        return true
    }

    override fun getType(uri: Uri): String? {
        return when (sUriMatcher?.match(uri)) {
            Code.ALL_RECIPES -> {
                ContentType.ALL_RECIPES
            }
            Code.SINGLE_RECIPE -> {
                ContentType.SINGLE_RECIPE
            }
            else -> throw IllegalArgumentException("Unsupported URI$uri")
        }
    }


    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val cursor: Cursor
        val db: SQLiteDatabase = mDBHelper.writableDatabase
        val queryBuilder = SQLiteQueryBuilder()

        when (sUriMatcher?.match(uri)) {
            Code.SINGLE_RECIPE -> {
                queryBuilder.tables = RECIPE_TABLE
                cursor = queryBuilder.query(
                    db,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
            }
            Code.ALL_RECIPES -> {
                queryBuilder.tables = RECIPE_TABLE
                cursor = queryBuilder.query(
                    db,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
            }
            else ->
                throw IllegalArgumentException("Unsupported URI: $uri")
        }
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val contentUri: Uri
        val db = mDBHelper.writableDatabase
        val id: Long

        when (sUriMatcher?.match(uri)) {
            Code.SINGLE_RECIPE -> {
                id = db.insertWithOnConflict(
                    RECIPE_TABLE,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
                )
                contentUri = ContentUris.withAppendedId(UriBuilder.buildRecipeUri(), id)
            }
            Code.ALL_RECIPES -> {
                id = db.insertWithOnConflict(
                    RECIPE_TABLE,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
                )
                contentUri = ContentUris.withAppendedId(UriBuilder.buildRecipeUri(), id)
            }
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
        return contentUri
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val deleteCount: Int
        val db = mDBHelper.writableDatabase

        deleteCount = when (sUriMatcher?.match(uri)) {
            Code.ALL_RECIPES -> db.delete(
                RECIPE_TABLE,
                selection,
                selectionArgs
            )
            Code.SINGLE_RECIPE -> db.delete(
                RECIPE_TABLE,
                selection,
                selectionArgs
            )
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
        return deleteCount
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val updateCount: Int
        var sss = selection
        val db = mDBHelper.writableDatabase

        updateCount = when (sUriMatcher?.match(uri)) {
            Code.ALL_RECIPES -> db.update(RECIPE_TABLE, values, selection, selectionArgs)
            Code.SINGLE_RECIPE -> {
                var id = uri.lastPathSegment
                sss = if (TextUtils.isEmpty(selection)) {
                    "$RECIPE_ID=$id"
                } else {
                    "$sss AND $RECIPE_ID=$id"
                }
                return db.update(RECIPE_TABLE, values, sss, selectionArgs)
//                db.update(recipe_TABLE, values, selection, selectionArgs)
            }
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
        return updateCount
    }

    companion object {
        private fun buildUriMatcher(): UriMatcher? {
            val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
            uriMatcher.addURI(
                BuildConfig.APPLICATION_ID,
                RECIPE_TABLE,
                Code.ALL_RECIPES
            )
            uriMatcher.addURI(
                BuildConfig.APPLICATION_ID,
                "$RECIPE_TABLE/#",
                Code.SINGLE_RECIPE
            )
            return uriMatcher
        }
    }
}