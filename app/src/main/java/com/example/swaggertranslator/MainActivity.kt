package com.example.swaggertranslator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.swaggertranslator.data.Status
import com.example.swaggertranslator.data.repository.TranslatorRepository
import com.example.swaggertranslator.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        makeRequest()
        requestLanguage()
    }

    private fun requestLanguage() {
        val flow = TranslatorRepository.getLanguagesList().flowOn(Dispatchers.Default)

        lifecycleScope.launch {
            flow.collect {
                when(it){
                    is Status.Fail -> Log.i(TAG,"collect: ${it.message}")
                    Status.Loading -> Log.i(TAG,"loading")
                    is Status.Success -> Log.i(TAG,"collect language: ${it.data}")
                }
            }
        }
    }

    private fun makeRequest() {
        val flow = TranslatorRepository.translateText().flowOn(Dispatchers.Default)

        lifecycleScope.launch {
            flow.collect {
                when(it){
                    is Status.Fail -> Log.i(TAG,"collect: ${it.message}")
                    Status.Loading -> Log.i(TAG,"loading")
                    is Status.Success -> Log.i(TAG,"collect language: ${it.data.translatedText}")
                }
            }
        }
    }




    companion object{
        const val TAG = "karrar"
    }


}