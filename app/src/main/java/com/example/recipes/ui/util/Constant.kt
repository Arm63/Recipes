package com.example.recipes.ui.util

internal class Constant {

    object API {
        const val HOST = "https://raw.githubusercontent.com/Arm63/armen63.io/master"
        const val RECIPE_LIST = "$HOST/fruit_list/recipes.json"
        const val RECIPE_ITEM = "$HOST/fruit_list/fruits/" // id
        const val RECIPE_ITEM_POSTFIX = "/details.json"
        const val RECIPE_ITEM_DEFAULT_IMAGE =
            "https://www.tastefullysimple.com/_/media/images/recipe-default-image.png"
    }

    object Argument {
        const val ARGUMENT_DATA = "ARGUMENT_DATA"
        const val ARGUMENT_recipe = "ARGUMENT_recipe"
    }


    object NotifyType {
        const val ADD = 100
        const val UPDATE = 101
        const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
    }

    object Extra {
        const val EXTRA_NOTIFY_DATA = "EXTRA_NOTIFY_DATA"
        const val EXTRA_NOTIFY_TYPE = "EXTRA_NOTIFY_TYPE"
        const val EXTRA_CAMERA_DATA = "data"
        const val EXTRA_PHOTO_URI = "EXTRA_PHOTO_URI"

        const val recipe_ID = "RECIPE_ID"
        const val URL = "URL"
        const val POST_ENTITY = "POST_ENTITY"
        const val REQUEST_TYPE = "REQUEST_TYPE"
        const val NOTIFICATION_DATA = "NOTIFICATION_DATA"
        const val RECIPE = "RECIPE"
        const val EXTRA_recipe = "EXTRA_recipe"
        const val MENU_STATE = "MENU_STATE"
    }

    object Symbol {
        const val ASTERISK = "*"
        const val NEW_LINE = "\n"
        const val SPACE = " "
        const val NULL = ""
        const val COLON = ":"
        const val COMMA = ","
        const val SLASH = "/"
        const val DOT = "."
        const val UNDERLINE = "_"
        const val DASH = "-"
        const val AT = "@"
        const val AMPERSAND = "&"
    }

    object Util {
        const val UTF_8 = "UTF-8"
    }

    object RequestType {
        const val RECIPE_LIST = 1
        const val RECIPE_ITEM = 2
    }

    object RequestMethod {
        const val POST = "POST"
        const val GET = "GET"
        const val PUT = "PUT"
    }
    object RequestCode {
        const val ADD_RECIPE_ACTIVITY = 100
        const val CAMERA_ACTIVITY = 101
        const val CAMERA = 102
    }
}