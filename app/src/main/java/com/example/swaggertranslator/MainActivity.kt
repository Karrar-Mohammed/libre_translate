package com.example.swaggertranslator

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.swaggertranslator.data.Status
import com.example.swaggertranslator.data.repository.TranslatorRepository
import com.example.swaggertranslator.data.response.language.Language
import com.example.swaggertranslator.data.response.translated.Translated
import com.example.swaggertranslator.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setup()
        callBacks()

    }

    private fun setup() {
        requestLanguage()
    }

    private fun callBacks() {
        binding.apply {
            translateButton.setOnClickListener {
                TranslatorRepository.textToTranslate = inputText.text.toString()
                translateText()
            }
        }
    }

    private fun initFromSpinner() {
        val translateFromList = TranslatorRepository.translateFromLanguagesList.map { it.name }
        val fromAdapter = ArrayAdapter(this, R.layout.list_item, translateFromList)
        binding.translateFromSpinner.apply {
            adapter = fromAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    TranslatorRepository.sourceLanguageCode =
                        TranslatorRepository.translateFromLanguagesList[p2].code
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }
        }
    }

    private fun initToSpinner() {
        val translateToList = TranslatorRepository.translateToLanguageList.map { it.name }
        val toAdapter = ArrayAdapter(this, R.layout.list_item, translateToList)
        binding.translateToSpinner.apply {
            adapter = toAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    TranslatorRepository.targetLanguageCode =
                        TranslatorRepository.translateToLanguageList[p2].code
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }
    }

    private fun requestLanguage() {
        val flow = TranslatorRepository.getLanguagesList().flowOn(Dispatchers.Default)

        lifecycleScope.launch {
            flow.collect { collectLanguage(it) }
        }
    }



    private fun collectLanguage(status: Status<List<Language>>) {
        when (status) {
            is Status.Fail -> Log.i(TAG, "collect: ${status.message}")
            Status.Loading -> Log.i(TAG, "loading")
            is Status.Success -> {
                initLanguageList(status.data)
            }
        }
    }


    private fun initLanguageList(list: List<Language>) {
        val autoDetect = Language(code = "auto", name = "Auto Detect")
        TranslatorRepository.apply {
            translateToLanguageList = list
            translateFromLanguagesList = list.toMutableList()
            translateFromLanguagesList.add(0, autoDetect)
        }

        Log.i(TAG, "collect language: ${TranslatorRepository.translateFromLanguagesList}")
        initFromSpinner()
        initToSpinner()
    }


    private fun translateText() {
        val flow = TranslatorRepository.translateText().flowOn(Dispatchers.Default)

        lifecycleScope.launch {
            flow.collect { collectTranslatedText(it) }
        }
    }

    private fun collectTranslatedText(status: Status<Translated>) {
        when (status) {
            is Status.Fail -> Log.i(TAG, "collect: ${status.message}")
            Status.Loading -> binding.translatedText.text = ""
            is Status.Success -> {
                binding.translatedText.text = status.data.translatedText
                Log.i(TAG, "collect language: ${status.data.translatedText}")
            }
        }
    }


    companion object {
        const val TAG = "karrar"
    }


}