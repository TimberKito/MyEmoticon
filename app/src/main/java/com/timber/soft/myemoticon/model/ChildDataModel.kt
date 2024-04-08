package com.timber.soft.myemoticon.model

import java.io.Serializable

data class ChildDataModel(
    val count: Int,
    val identifierName: String,
    val previewList: List<String>,
    val title: String,
    val zipUrl: String
) : Serializable