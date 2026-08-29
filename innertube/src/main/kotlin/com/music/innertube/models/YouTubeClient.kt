package com.music.innertube.models

import kotlinx.serialization.Serializable

@Serializable
data class YouTubeClient(
    val clientName: String,
    val clientVersion: String,
    val clientId: String,
    val userAgent: String,
    val osName: String? = null,
    val osVersion: String? = null,
    val deviceMake: String? = null,
    val deviceModel: String? = null,
    val androidSdkVersion: String? = null,
    val buildId: String? = null,
    val cronetVersion: String? = null,
    val packageName: String? = null,
    val friendlyName: String? = null,
    val loginSupported: Boolean = false,
    val loginRequired: Boolean = false,
    val useSignatureTimestamp: Boolean = false,
    val isEmbedded: Boolean = false,
    val useWebPoTokens: Boolean = false,
) {
    fun toContext(locale: YouTubeLocale, visitorData: String?, dataSyncId: String?) = Context(
        client = Context.Client(
            clientName = clientName,
            clientVersion = clientVersion,
            osName = osName,
            osVersion = osVersion,
            deviceMake = deviceMake,
            deviceModel = deviceModel,
            androidSdkVersion = androidSdkVersion,
            gl = locale.gl,
            hl = locale.hl,
            visitorData = visitorData
        ),
        user = Context.User(
            onBehalfOfUser = if (loginSupported) dataSyncId else null
        ),
    )

    companion object {
        const val USER_AGENT_WEB = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:140.0) Gecko/20100101 Firefox/140.0"

        const val ORIGIN_YOUTUBE_MUSIC = "https://music.youtube.com"
        const val REFERER_YOUTUBE_MUSIC = "$ORIGIN_YOUTUBE_MUSIC/"
        const val API_URL_YOUTUBE_MUSIC = "$ORIGIN_YOUTUBE_MUSIC/youtubei/v1/"

        val WEB = YouTubeClient(
            clientName = "WEB",
            clientVersion = "2.20260213.00.00",
            clientId = "1",
            userAgent = USER_AGENT_WEB,
        )

        val WEB_REMIX = YouTubeClient(
            clientName = "WEB_REMIX",
            clientVersion = "1.20260213.01.00",
            clientId = "67",
            userAgent = USER_AGENT_WEB,
            loginSupported = true,
            useSignatureTimestamp = true,
            useWebPoTokens = true,
        )

        val WEB_CREATOR = YouTubeClient(
            clientName = "WEB_CREATOR",
            clientVersion = "1.20260213.00.00",
            clientId = "62",
            userAgent = USER_AGENT_WEB,
            loginSupported = true,
            loginRequired = true,
            useSignatureTimestamp = true,
        )

        val IOS = YouTubeClient(
            clientName = "IOS",
            clientVersion = "21.03.1",
            clientId = "5",
            userAgent = "com.google.ios.youtube/21.03.1 (iPhone16,2; U; CPU iOS 18_2 like Mac OS X;)",
            osVersion = "18.2.22C152",
        )

        val MOBILE = YouTubeClient(
            clientName = "ANDROID",
            clientVersion = "21.03.38",
            clientId = "3",
            userAgent = "com.google.android.youtube/21.03.38 (Linux; U; Android 14) gzip",
            loginSupported = true,
            useSignatureTimestamp = true
        )

        /**
         * Cannot play livestreams and lacks HDR, but can play videos with music and labeled "for children".
         */
        val ANDROID_CREATOR = YouTubeClient(
            clientName = "ANDROID_CREATOR",
            clientVersion = "25.03.101",
            clientId = "14",
            userAgent = "com.google.android.apps.youtube.creator/25.03.101 (Linux; U; Android 15; en_US; Pixel 9 Pro Fold; Build/AP3A.241005.015.A2; Cronet/132.0.6779.0)",
            osName = "Android",
            osVersion = "15",
            deviceMake = "Google",
            deviceModel = "Pixel 9 Pro Fold",
            androidSdkVersion = "35",
            buildId = "AP3A.241005.015.A2",
            cronetVersion = "132.0.6779.0",
            packageName = "com.google.android.apps.youtube.creator",
            friendlyName = "Android Studio",
            loginSupported = true,
            useSignatureTimestamp = true
        )

        /**
         * The device machine id for the iPad 6th Gen (iPad7,6).
         * AV1 hardware decoding is not supported.
         */
        val IPADOS = YouTubeClient(
            clientName = "IOS",
            clientVersion = "21.03.3",
            clientId = "5",
            userAgent = "com.google.ios.youtube/21.03.3 (iPad7,6; U; CPU iPadOS 17_7_10 like Mac OS X; en-US)",
            osName = "iPadOS",
            osVersion = "17.7.10.21H450",
            deviceMake = "Apple",
            deviceModel = "iPad7,6",
            friendlyName = "iPadOS",
            loginSupported = false,
            useSignatureTimestamp = false,
            packageName = "com.google.ios.youtube"
        )

    }
}
