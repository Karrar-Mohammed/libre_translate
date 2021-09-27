package com.example.swaggertranslator.utils

object Constants {
    object Url {
        const val SCHEMA = "https"
        const val HOST = "translate.argosopentech.com"
        const val PATH_LANGUAGE = "languages"
        const val PATH_TRANSLATE = "translate"
    }

    object FormBody{
        const val QUERY = "q"
        const val SOURCE_LANGUAGE = "source"
        const val TARGET_LANGUAGE = "target"
    }
}