package com.example.imagesearch

import com.google.gson.annotations.SerializedName

data class RvModel(
    @SerializedName("image_url")
    var thumbnail: String,
    @SerializedName("display_sitename")
    var sitename: String,
    @SerializedName("datetime")
    var datetime: String
)
data class RvModelList(
    @SerializedName("documents")
    val data: List<RvModel>
)
