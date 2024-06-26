package com.betrybe.currencyview.ui.views.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.betrybe.currencyview.R
import com.betrybe.currencyview.common.ApiIdlingResource
import com.betrybe.currencyview.data.api.Api
import com.betrybe.currencyview.ui.adapters.ExchangeAdapter
import com.betrybe.currencyview.ui.adapters.ExchangeData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private val mExchangeList: RecyclerView by lazy { findViewById(R.id.currency_rates_state) }
    private val mAutoComplete: AutoCompleteTextView by lazy { findViewById(R.id.currency_selection_input_layout) }
    private val mSelectState: TextView by lazy { findViewById(R.id.select_currency_state) }
    private val mLoadState: TextView by lazy { findViewById(R.id.load_currency_state) }
    private val mWaitingState: FrameLayout by lazy { findViewById(R.id.waiting_response_state) }

    private val api = Api.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mExchangeList.layoutManager = LinearLayoutManager(this)

        this.updateCurrencySymbols()
    }

    private fun updateCurrencySymbols() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ApiIdlingResource.increment()
                withContext(Dispatchers.Main) {
                    startLoadingCurrencySymbols()
                    val response = api.getSymbols()
                    if (response.isSuccessful) {
                        val exchangeData = response.body() ?: return@withContext
                        val adapter = ArrayAdapter(
                            this@MainActivity,
                            android.R.layout.simple_dropdown_item_1line,
                            exchangeData.symbols.keys.toList()
                        )
                        mAutoComplete.setAdapter(adapter)
                        endLoadingCurrencySymbols()

                        mAutoComplete.setOnItemClickListener { parent, _, position, _ ->
                            val base = parent.getItemAtPosition(position).toString()
                            updateLatest(base)
                        }
                    }
                }
                ApiIdlingResource.decrement()
            } catch (ex: Exception) {
                ApiIdlingResource.decrement()
                Log.e("Error", ex.toString())
            }
        }
    }

    private fun updateLatest(base: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ApiIdlingResource.increment()
                withContext(Dispatchers.Main) {
                    startLoadingRates()
                    val response = api.getLatestRates(base).body() ?: return@withContext
                    val exchangeList = response.rates.map {
                        object : ExchangeData {
                            override val title: String = it.key
                            override val rate: Double = it.value
                        }
                    }
                    mExchangeList.adapter = ExchangeAdapter(exchangeList)
                    endLoadingRates()
                }
                ApiIdlingResource.decrement()
            } catch (ex: Exception) {
                ApiIdlingResource.decrement()
                Log.e("Error", ex.toString())
            }
        }
    }

    private fun startLoadingCurrencySymbols () {
        mSelectState.visibility = View.GONE
        mLoadState.visibility = View.VISIBLE
    }

    private fun endLoadingCurrencySymbols () {
        mLoadState.visibility = View.GONE
        mSelectState.visibility = View.VISIBLE
    }

    private fun startLoadingRates () {
        mExchangeList.visibility = View.GONE
        mSelectState.visibility = View.GONE
        mWaitingState.visibility = View.VISIBLE
    }

    private fun endLoadingRates () {
        mSelectState.visibility = View.GONE
        mWaitingState.visibility = View.GONE
        mExchangeList.visibility = View.VISIBLE
    }
}
