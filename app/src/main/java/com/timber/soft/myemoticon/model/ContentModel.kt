package com.timber.soft.myemoticon.model

import com.google.gson.annotations.SerializedName

data class ContentModel(
    @SerializedName("android_play_store_link") val storeLink: String,
    @SerializedName("ios_app_store_link") val iosLink: String,
    @SerializedName("sticker_packs") val packs: List<Pack>
)

data class Pack(
    @SerializedName("identifier") val identifier: String,
    @SerializedName("name") val name: String,
    @SerializedName("tray_image_file") val trayName: String,
    @SerializedName("publisher") private val publisher: String,
    @SerializedName("publisher_full") val publisherFull: String,
    @SerializedName("publisher_email") val email: String,
    @SerializedName("publisher_website") val websitePublisher: String,
    @SerializedName("privacy_policy_website") val websitePrivacy: String,
    @SerializedName("license_agreement_website") val websiteLicense: String,
    @SerializedName("share_url") val shareUrl: String,
    @SerializedName("is_premium") val isPremium: Boolean,
    @SerializedName("animated_sticker_pack") val animated: Boolean,
    @SerializedName("stickers") val stickers: List<PackImg>
)

data class PackImg(
    @SerializedName("image_file") var imFileName: String,
    @SerializedName("emojis") val emojis: List<String>,
)
