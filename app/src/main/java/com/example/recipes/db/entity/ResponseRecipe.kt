package com.example.recipes.db.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseRecipe(

	@field:SerializedName("recipes")
	var recipes: List<Recipe?>? = null
) : Parcelable

@Parcelize
data class Recipe(

	@field:SerializedName("id")
	var id: Long? = null,

	@field:SerializedName("name")
	var name: String? = null,

	@field:SerializedName("price")
	var price: Long? = null,

	@field:SerializedName("from_user")
	var isFromUser: Boolean? = null,

	var isFavorite:Boolean = false,


	@field:SerializedName("description")
	var description:String? = null,
	
	@field:SerializedName("image")
	var image: String? = null
	
) : Parcelable
