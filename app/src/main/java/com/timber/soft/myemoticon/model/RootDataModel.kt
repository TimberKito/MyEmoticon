package com.timber.soft.myemoticon.model

import java.io.Serializable

data class RootDataModel(
    val categoryName: String,
    val childList: List<ChildDataModel>
) : Serializable