package com.example.recipes.io.bus.event

import androidx.annotation.Nullable

class ApiEvent<T> {
    object EventType {
        const val RECIPE_LIST_LOADED = 100
        const val RECIPE_ITEM_LOADED = 101
    }

    var eventType = 0
    var isSuccess = false

    @get:Nullable
    var eventData: T? = null
        private set

    constructor() {}
    constructor(eventType: Int, eventData: T) {
        this.eventType = eventType
        this.eventData = eventData
    }

    constructor(eventType: Int, success: Boolean, eventData: T) {
        this.eventType = eventType
        isSuccess = success
        this.eventData = eventData
    }

    fun setEventData(eventData: T) {
        this.eventData = eventData
    }
}