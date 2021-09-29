package com.example.swaggertranslator.data.network

import com.example.swaggertranslator.data.Status
import com.example.swaggertranslator.data.repository.TranslatorRepository
import com.example.swaggertranslator.data.response.language.Language
import com.example.swaggertranslator.data.response.language.LanguageList
import com.example.swaggertranslator.data.response.translated.Translated
import com.example.swaggertranslator.utils.Constants
import com.google.gson.Gson
import okhttp3.*

object Client {

    private val client = OkHttpClient()

    fun getLanguages(): Status<List<Language>> {
        val url = initUrl(Constants.Url.PATH_LANGUAGE)
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        return if (response.isSuccessful) {
            val result =
                Gson().fromJson(response.body?.string(), LanguageList::class.java)
            Status.Success(result)

        } else {
            Status.Fail(response.message)
        }
    }

    fun translate(): Status<Translated> {
        val url = initUrl(Constants.Url.PATH_TRANSLATE)
        val formBody = FormBody.Builder()
            .add(Constants.FormBody.QUERY, TranslatorRepository.textToTranslate)
            .add(Constants.FormBody.SOURCE_LANGUAGE, TranslatorRepository.sourceLanguageCode)
            .add(Constants.FormBody.TARGET_LANGUAGE, TranslatorRepository.targetLanguageCode)
            .build()
        val request = Request.Builder().url(url).post(formBody).build()
        val response = client.newCall(request).execute()
        return if (response.isSuccessful) {
            val result = Gson().fromJson(response.body?.string(), Translated::class.java)
            Status.Success(result)

        } else {
            Status.Fail(response.message)
        }

    }


    private fun initUrl(path: String): HttpUrl {
        return HttpUrl.Builder()
            .scheme(Constants.Url.SCHEMA)
            .host(Constants.Url.HOST)
            .addPathSegments(path)
            .build()
    }

}













