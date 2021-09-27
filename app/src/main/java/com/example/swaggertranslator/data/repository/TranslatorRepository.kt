package com.example.swaggertranslator.data.repository

import com.example.swaggertranslator.data.Status
import com.example.swaggertranslator.data.network.Client
import com.example.swaggertranslator.data.response.language.Language
import com.example.swaggertranslator.data.response.translated.Translated
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object TranslatorRepository {

    var textToTranslate = ""


    fun getLanguagesList(): Flow<Status<List<Language>>> {
        return flow {
            emit(Status.Loading)
            emit(Client.getLanguages())
        }

    }

    fun translateText(): Flow<Status<Translated>> {
        return flow {
            emit(Status.Loading)
            emit(Client.translate())
        }
    }

}