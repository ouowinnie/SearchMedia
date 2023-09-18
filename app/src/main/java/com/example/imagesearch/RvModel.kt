package com.example.imagesearch

import com.google.gson.annotations.SerializedName

data class RvModel(
    @SerializedName("thumbnail_url")
    var thumbnail: String,
    @SerializedName("thumbnail")
    var videoThumbnail: String,
    @SerializedName("title")
    var videoTitle: String,
    @SerializedName("display_sitename")
    var sitename: String,
    @SerializedName("datetime")
    var datetime: String,
    var isLiked: Boolean = false,
    var storageFragment: String = ""
)
data class RvModelList(
    @SerializedName("documents")
    val data: ArrayList<RvModel>
)