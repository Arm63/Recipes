package com.example.recipes.ui.activity

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.recipes.db.cursor.CursorReader
import com.example.recipes.R
import com.example.recipes.db.entity.Recipe
import com.example.recipes.db.handler.RecipeAsyncQueryHandler
import com.example.recipes.io.bus.BusProvider
import com.example.recipes.io.bus.event.ApiEvent
import com.example.recipes.io.service.RecipeIntentService
import com.example.recipes.ui.util.AppUtil
import com.example.recipes.ui.util.Constant
import com.example.recipes.ui.util.NetworkUtil
import com.google.common.eventbus.Subscribe
import kotlinx.android.synthetic.main.activity_recipe_info.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "CAST_NEVR_SUCCEEDS")
class RecipeInfoActivity : BaseActivity(), RecipeAsyncQueryHandler.AsyncQueryListener {

    // ===========================================================
    // Constants
    // ===========================================================


    // ===========================================================
    // Constants
    // ===========================================================
    private val LOG_TAG: String =
        RecipeInfoActivity::class.java.simpleName

    // ===========================================================
    // Fields
    // ===========================================================
    private var isStillEditing: Boolean = false
    private var mRecipe: Recipe? = null
    private lateinit var mMenuEdit: MenuItem
    private lateinit var mMenuDone: MenuItem
    private lateinit var mMenuFavorite: MenuItem

    private lateinit var mRecipeAQH: RecipeAsyncQueryHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BusProvider.register(this)
        setListeners()
        init()
        getData()
        customizeActionBar()
    }

    private fun setListeners() {
        et_recipe_price.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty() && s[0] == '0') {
                    var i = 0
                    while (s.length >= i + 2 && s.toString()[i] == '0') {
                        i++
                    }
                    et_recipe_price.removeTextChangedListener(this)
                    et_recipe_price.setText(s.subSequence(i, s.length))
                    et_recipe_price.addTextChangedListener(this)
                    if (count > 0) {
                        et_recipe_price.setSelection(start + count - i)
                    } else {
                        et_recipe_price.setSelection(start)
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isEmpty()) {
                    et_recipe_price.removeTextChangedListener(this)
                    et_recipe_price.setText("0")
                    et_recipe_price.addTextChangedListener(this)
                    et_recipe_price.setSelection(1)
                }
            }
        })
    }

    private fun customizeActionBar() {
        setActionBarTitle(mRecipe?.name)
    }

    override fun getLayoutResource(): Int = R.layout.activity_recipe_info


    private fun init() {
        mRecipeAQH = RecipeAsyncQueryHandler(this@RecipeInfoActivity, this)
    }

    private fun getData() {
        val productId = intent.getLongExtra(Constant.Extra.recipe_ID, 0)
        mRecipeAQH.getRecipe(productId)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_recipe_item, menu)

        mMenuEdit = menu!!.findItem(R.id.menu_recipe_edit)
        mMenuDone = menu.findItem(R.id.menu_recipe_done)
        mMenuFavorite = menu.findItem(R.id.menu_recipe_favorite)



        if (mRecipe?.isFavorite!!) {
            mMenuFavorite.setIcon(R.drawable.ic_favorite)
        }
        if (isStillEditing) {
            mMenuDone.isVisible = true
            mMenuEdit.isVisible = false
            mMenuFavorite.isVisible = false
        } else {
            mMenuEdit.isVisible = true
        }
        mMenuEdit.isVisible = mRecipe?.isFromUser!!

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.menu_recipe_edit -> {
                isStillEditing = true
                mMenuDone.isVisible = true
                mMenuEdit.isVisible = false
                mMenuFavorite.isVisible = false
                mRecipe?.let { openEditLayout(it) }
                return true
            }
            R.id.menu_recipe_done -> {
                isStillEditing = false
                mMenuDone.isVisible = false
                mMenuEdit.isVisible = true
                mMenuFavorite.isVisible = true

                updateRecipe(
                    et_recipe_title.text.toString(),
                    et_recipe_price.text.toString().toLong(),
                    et_recipe_desc.text.toString()
                )
                mRecipe?.let { openViewLayout(it) }
                return true
            }
            R.id.menu_recipe_favorite -> {
                if (mRecipe?.isFavorite!!) {
                    mMenuFavorite.setIcon(R.drawable.ic_unfavorite)
                    mRecipe!!.isFavorite = false
                } else {
                    mMenuFavorite.setIcon(R.drawable.ic_favorite)
                    mRecipe!!.isFavorite = true
                }
                mRecipeAQH.updateRecipe(mRecipe!!)
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    // ===========================================================
    // Click Listeners
    // ===========================================================
//poxelem
    private fun updateRecipe(name: String, price: Long, desc: String) {
        mRecipe?.name = name
        mRecipe?.price = price
        mRecipe?.description = desc
        mRecipe?.let { mRecipeAQH.updateRecipe(it) }
    }

    override fun onSaveInstanceState(saveInstanceState: Bundle) {
        super.onSaveInstanceState(saveInstanceState)
        if (isStillEditing) {
            mRecipe?.description = et_recipe_desc.text.toString()
            mRecipe?.price = et_recipe_price.text.toString().toLong()
            mRecipe?.name = et_recipe_title.text.toString()
            saveInstanceState.putParcelable(Constant.Extra.RECIPE, mRecipe)
        }
    }


    private fun openEditLayout(recipe: Recipe) {
        ll_recipe_edit.visibility = View.GONE
        ll_recipe_view.visibility = View.VISIBLE
        Glide.with(this)
            .load(recipe.image)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(iv_recipe_image)

        et_recipe_title.setText(recipe.name)
        et_recipe_price.setText(recipe.price.toString())
        et_recipe_desc.setText(recipe.description)
    }

    private fun openViewLayout(recipe: Recipe) {
        AppUtil.closeKeyboard(this)

        ll_recipe_view.visibility = View.VISIBLE
        ll_recipe_edit.visibility = View.GONE
        Glide.with(applicationContext)
            .load(recipe.image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(iv_recipe_image)

        tv_recipe_title.text = recipe.name
        tv_recipe_price.text = recipe.price.toString()
        tv_recipe_desc.text = recipe.description
    }


    override fun onQueryComplete(token: Int, cookie: Any?, cursor: Cursor?) {
        when (token) {
            RecipeAsyncQueryHandler.QueryToken.GET_RECIPE -> {
                mRecipe = cursor?.let { CursorReader.parseRecipe(it) }
                if (!mRecipe!!.isFromUser!!)
                    loadRecipe()
                openViewLayout(mRecipe!!)
            }
        }
    }

    private fun loadRecipe() {
        if (NetworkUtil.instance?.isConnected(this)!!) {
            RecipeIntentService.start(
                this,
                Constant.API.RECIPE_ITEM + java.lang.String.valueOf(mRecipe?.id) + Constant.API.RECIPE_ITEM_POSTFIX,
                Constant.RequestType.RECIPE_ITEM
            )
        }
    }

    override fun onInsertComplete(token: Int, cookie: Any?, uri: Uri?) {
    }

    override fun onUpdateComplete(token: Int, cookie: Any?, result: Int) {
    }

    override fun onDeleteComplete(token: Int, cookie: Any?, result: Int) {
        RecipeAsyncQueryHandler.QueryToken.UPDATE_RECIPE
        openViewLayout(mRecipe!!)
    }

    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================
    @Subscribe
    fun onEventReceived(apiEvent: ApiEvent<Any?>) {
        if (apiEvent.eventType == ApiEvent.EventType.RECIPE_ITEM_LOADED) {
            if (apiEvent.isSuccess) {
                mRecipe = apiEvent.eventData as Recipe
                openViewLayout(mRecipe!!)
            } else {
                Toast.makeText(this, R.string.msg_some_error, Toast.LENGTH_SHORT).show()
            }
        }
    }
}