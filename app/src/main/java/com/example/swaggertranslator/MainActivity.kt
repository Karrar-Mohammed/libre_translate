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
import com.example.swaggertranslator.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
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

    private fun initSpinner() {
        val items = TranslatorRepository.languagesList.map { it.name }
        val mAdapter = ArrayAdapter(this, R.layout.list_item, items)

        binding.apply {

            translateFromSpinner.apply{
                adapter = mAdapter
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        TranslatorRepository.sourceLanguageCode = TranslatorRepository.languagesList[p2].code
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }
                }
            }

            translateToSpinner.apply {
                adapter = mAdapter
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        TranslatorRepository.targetLanguageCode = TranslatorRepository.languagesList[p2].code
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }
                }
            }

        }
    }

    private fun requestLanguage() {
        val flow = TranslatorRepository.getLanguagesList().flowOn(Dispatchers.Default)

        lifecycleScope.launch {
            flow.collect {
                when (it) {
                    is Status.Fail -> Log.i(TAG, "collect: ${it.message}")
                    Status.Loading -> Log.i(TAG, "loading")
                    is Status.Success -> {
                        TranslatorRepository.languagesList = it.data
                        initSpinner()
                        Log.i(TAG, "collect language: ${TranslatorRepository.languagesList}")
                    }
                }
            }
        }
    }

    private fun translateText() {
        val flow = TranslatorRepository.translateText().flowOn(Dispatchers.Default)

        lifecycleScope.launch {
            flow.collect {
                when (it) {
                    is Status.Fail -> Log.i(TAG, "collect: ${it.message}")
                    Status.Loading -> binding.translatedText.text = ""
                    is Status.Success -> {
                        binding.translatedText.text = it.data.translatedText
                        Log.i(TAG, "collect language: ${it.data.translatedText}")
                    }
                }
            }
        }
    }


    companion object {
        const val TAG = "karrar"
    }


}