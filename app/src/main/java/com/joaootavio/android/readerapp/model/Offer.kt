package com.joaootavio.android.readerapp.model


import com.google.gson.annotations.SerializedName

data class Offer(
    @SerializedName("finskyOfferType")
    val finskyOfferType: Int,
    @SerializedName("giftable")
    val giftable: Boolean,
    @SerializedName("listPrice")
    val listPrice: ListPriceX,
    @SerializedName("retailPrice")
    val retailPrice: RetailPrice
)