package com.example.recipes.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.recipes.R
import com.example.recipes.db.entity.Recipe
import com.example.recipes.db.handler.RecipeAsyncQueryHandler
import com.example.recipes.ui.util.AppUtil
import com.example.recipes.ui.util.Constant
import com.example.recipes.ui.util.Constant.API.RECIPE_ITEM_DEFAULT_IMAGE
import kotlinx.android.synthetic.main.activity_add_recipe.*

class RecipeAddActivity : BaseActivity(), View.OnClickListener,
    RecipeAsyncQueryHandler.AsyncQueryListener {

    private lateinit var mMenuFavorite: MenuItem
    private lateinit var mRecipe: Recipe
    private lateinit var mRecipeAQH: RecipeAsyncQueryHandler

    override fun getLayoutResource(): Int = R.layout.activity_add_recipe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setListeners()
        if (savedInstanceState == null)
            init()
        else
            getData(savedInstanceState)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_recipe_item, menu)
        mMenuFavorite = menu!!.findItem(R.id.menu_recipe_favorite)
        if (mRecipe.isFavorite)
            mMenuFavorite.setIcon(R.drawable.ic_favorite)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.menu_recipe_favorite -> {
                if (mRecipe.isFavorite) {
                    mMenuFavorite.setIcon(R.drawable.ic_unfavorite)
                    mRecipe.isFavorite = false
                } else {
                    mMenuFavorite.setIcon(R.drawable.ic_favorite)
                    mRecipe.isFavorite = true
                }
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setListeners() {
        btn_add_recipe_add.setOnClickListener(this)
        iv_add_recipe_logo.setOnClickListener(this)
        et_add_recipe_price.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty() && s[0] == '0') {
                    var i = 0
                    while (s.length >= i + 2 && s.toString()[i] == '0') {
                        i++
                    }
                    et_add_recipe_price.removeTextChangedListener(this)
                    et_add_recipe_price.setText(s.subSequence(i, s.length))
                    et_add_recipe_price.addTextChangedListener(this)
                    if (count > 0) {
                        et_add_recipe_price.setSelection(start + count - i)
                    } else {
                        et_add_recipe_price.setSelection(start)
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isEmpty()) {
                    et_add_recipe_price.removeTextChangedListener(this)
                    et_add_recipe_price.setText("0")
                    et_add_recipe_price.addTextChangedListener(this)
                    et_add_recipe_price.setSelection(1)
                }
            }
        })
    }

    private fun init() {
        mRecipeAQH = RecipeAsyncQueryHandler(applicationContext, this)
        mRecipe = Recipe()
        mRecipe.id = System.currentTimeMillis()
        mRecipe.isFromUser = true
        mRecipe.image = RECIPE_ITEM_DEFAULT_IMAGE
        et_add_recipe_price.setText("0")
        Glide.with(this)
            .load(mRecipe.image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(iv_add_recipe_logo)
    }

    private fun getData(savedInstanceState: Bundle) {
        if (savedInstanceState.getParcelable<Recipe>(Constant.Extra.EXTRA_recipe) != null) {
            mRecipe = savedInstanceState.getParcelable(Constant.Extra.EXTRA_recipe)!!
            et_add_recipe_title.setText(mRecipe.name)
            et_add_recipe_price.setText(mRecipe.price.toString())
            et_add_recipe_description.setText(mRecipe.description)
            Glide.with(this)
                .load(mRecipe.image)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iv_add_recipe_logo)
        }
    }

    override fun onSaveInstanceState(saveInstanceState: Bundle) {
        super.onSaveInstanceState(saveInstanceState)
        mRecipe.name = et_add_recipe_title.text.toString()
        mRecipe.price = et_add_recipe_price.text.toString().toLong()
        mRecipe.description = et_add_recipe_description.text.toString()
        saveInstanceState.putParcelable(Constant.Extra.EXTRA_recipe, mRecipe)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_add_recipe_add -> {
                if (et_add_recipe_title.text.isEmpty()) {
                    Toast.makeText(this, R.string.msg_edt_title_error, Toast.LENGTH_SHORT).show()
                    return
                } else if (et_add_recipe_price.text.isEmpty()) {
                    Toast.makeText(this, R.string.msg_edt_price_error, Toast.LENGTH_SHORT).show()
                    return
                }
                mRecipe.name = et_add_recipe_title.text.toString()
                mRecipe.price = et_add_recipe_price.text.toString().toLong()
                mRecipe.description = et_add_recipe_description.text.toString()
                addRecipeDialog()
                return
            }
            R.id.iv_add_recipe_logo -> {
                val intent = Intent(this, CameraActivity::class.java)
                startActivityForResult(intent, Constant.RequestCode.CAMERA_ACTIVITY)
                return
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                Constant.RequestCode.CAMERA_ACTIVITY -> {
                    val photoUri = data!!.extras!![Constant.Extra.EXTRA_PHOTO_URI] as Uri?
                    mRecipe.image = photoUri.toString()
                    Glide.with(this)
                        .load(photoUri)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(iv_add_recipe_logo)
                    return
                }
            }
        }
    }

    private fun addRecipeDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            .setMessage(R.string.msg_dialog_add_recipe)
            .setPositiveButton(R.string.text_btn_dialog_ok) { _, _ ->
                mRecipeAQH.addRecipe(mRecipe)
            }
            .setNegativeButton(R.string.text_btn_dialog_cancel, null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onQueryComplete(token: Int, cookie: Any?, cursor: Cursor?) {
    }

    override fun onInsertComplete(token: Int, cookie: Any?, uri: Uri?) {
        val intent = Intent()
        intent.putExtra(Constant.Extra.EXTRA_recipe, mRecipe)
        setResult(RESULT_OK, intent)
        AppUtil.sendNotification(
            applicationContext,
            MainActivity::class.java,
            getString(R.string.app_name),
            getString(R.string.notification_add) + " " + mRecipe.name,
            mRecipe.name,
            Constant.NotifyType.ADD
        )
        finish()
    }

    override fun onUpdateComplete(token: Int, cookie: Any?, result: Int) {
    }

    override fun onDeleteComplete(token: Int, cookie: Any?, result: Int) {
    }


}