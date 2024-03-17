package com.practicum.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var  adapter: TrackAdapter
    private lateinit var  inputEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var nothingFoundPlaceHolder: LinearLayout
    private lateinit var serverErrorPlaceholder: LinearLayout
    private var inputValue : String = ""

    private val iTunesService: ITunesService by lazy {
        RetrofitClient.getClient(getString(R.string.itunes_url)).create(ITunesService::class.java)
    }

    companion object {
        private const val INPUT_VALUE_KEY = "input_value"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val updateButton = findViewById<Button>(R.id.updateButton)
        val backButton = findViewById<LinearLayout>(R.id.back_button)
        inputEditText = findViewById(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
         nothingFoundPlaceHolder = findViewById(R.id.nothing_found_placeholder)
         serverErrorPlaceholder = findViewById(R.id.server_error_placeholder)

        backButton.setOnClickListener {
            finish()
        }

        updateButton.setOnClickListener {
            onUpdateButtonClick()
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)



        adapter = TrackAdapter()
        recyclerView.adapter = adapter

        clearButton.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            inputEditText.clearFocus()
            adapter.clearList()
            hidePlaceholder()
        }

        inputEditText.setOnEditorActionListener {_, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(inputEditText.text.toString())
                true
            } else {
                false
            }
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputValue = s?.toString() ?:""
                clearButton.visibility = clearButtonVisibility(s)

            }

            override fun afterTextChanged(s: Editable?) {

            }

            private fun clearButtonVisibility (s: CharSequence?) : Int =  if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

        }
        inputEditText.addTextChangedListener(textWatcher)

    }

    private fun performSearch (query: String) {
        val call = iTunesService.search(query)

        call.enqueue(object : Callback<SearchResponse> {
            override fun onResponse (call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    val searchResponse = response.body()
                    searchResponse?.let {
                        if (it.resultCount > 0) {
                            adapter.updateList(it.results)
                            recyclerView.scrollToPosition(0)
                        } else {
                            adapter.clearList()
                            hidePlaceholder()
                            showNoResultPlaceholder()
                        }
                    }
                } else {
                    hidePlaceholder()
                    adapter.clearList()
                    showServerErrorPlaceholder()
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                hidePlaceholder()
                adapter.clearList()
                showServerErrorPlaceholder()
            }
        })

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_VALUE_KEY, inputValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputValue = savedInstanceState.getString(INPUT_VALUE_KEY, "")
        inputEditText.setText(inputValue)
    }


    private fun showNoResultPlaceholder() {
        nothingFoundPlaceHolder.visibility = View.VISIBLE
    }
    private fun showServerErrorPlaceholder() {
        serverErrorPlaceholder.visibility = View.VISIBLE
    }
    private fun hidePlaceholder() {

        val nothingFoundPlaceholder = findViewById<LinearLayout>(R.id.nothing_found_placeholder)
        nothingFoundPlaceholder.visibility = View.GONE
        serverErrorPlaceholder.visibility = View.GONE
    }
    private fun onUpdateButtonClick () {
        hidePlaceholder()
        performSearch(inputEditText.text.toString())
    }
}