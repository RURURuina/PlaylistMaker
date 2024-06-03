package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var adapter: TrackAdapter
    private lateinit var inputEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var nothingFoundPlaceHolder: LinearLayout
    private lateinit var serverErrorPlaceholder: LinearLayout
    private lateinit var searchHistory: SearchHistory
    private lateinit var historyCleanButton: Button
    private lateinit var youSearch: TextView
    private lateinit var progressBar: ProgressBar
    private var inputValue: String = ""

    private val iTunesService: ITunesService by lazy {
        RetrofitClient.getClient(getString(R.string.itunes_url)).create(ITunesService::class.java)
    }

    companion object {
        private const val INPUT_VALUE_KEY = "input_value"
        const val AUDIO_PLAYER_KEY = "track"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val sharedPreferences = getSharedPreferences("SearchHistory", MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)

        progressBar = findViewById(R.id.progressBar)
        val updateButton = findViewById<Button>(R.id.updateButton)
        val backButton = findViewById<LinearLayout>(R.id.back_button)
        inputEditText = findViewById(R.id.inputEditText)
        youSearch = findViewById(R.id.you_search)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        nothingFoundPlaceHolder = findViewById(R.id.nothing_found_placeholder)
        serverErrorPlaceholder = findViewById(R.id.server_error_placeholder)
        historyCleanButton = findViewById(R.id.history_clean_button)
        val scrollView = findViewById<NestedScrollView>(R.id.scrollView)


        backButton.setOnClickListener {
            finish()
        }

        updateButton.setOnClickListener {
            onUpdateButtonClick()
        }

        historyCleanButton.setOnClickListener {
            searchHistory.clearSearchHistory()
            hideSearchHistory()
            adapter.updateList(searchHistory.getSearchHistory())
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)



        adapter = TrackAdapter(object : TrackAdapter.OnItemClickListener {
            override fun onItemClick(track: Track) {
                searchHistory.addTrackToHistory(track)

                if (clickDebounce()) {
                    val intent = Intent(this@SearchActivity, AudioPlayerActivity::class.java)
                    intent.putExtra(AUDIO_PLAYER_KEY, track)
                    this@SearchActivity.startActivity(intent)
                }
            }
        })

        inputEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty() && searchHistory.isNotEmpty()) {
                showSearchHistory()
            } else {
                hideSearchHistory()
            }
        }

        recyclerView.adapter = adapter

        clearButton.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            inputEditText.clearFocus()
            adapter.clearList()
            hidePlaceholder()
            scrollView.smoothScrollTo(0, 0)
            searchHistoryVisibilityCondition()

        }

        searchHistoryVisibilityCondition()



        inputEditText.setOnEditorActionListener { _, actionId, _ ->
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
                inputValue = s?.toString() ?: ""
                clearButton.visibility = clearButtonVisibility(s)
                if (s?.isEmpty() == false) {
                    searchDebounce(inputValue)
                }


                if (inputEditText.hasFocus() && s?.isEmpty() == true && searchHistory.isNotEmpty()) {
                    hidePlaceholder()
                    showSearchHistory()
                    loadSearchHistory()

                } else {
                    hidePlaceholder()
                    hideSearchHistory()
                    adapter.clearList()
                }

            }

            override fun afterTextChanged(s: Editable?) {

            }

            private fun clearButtonVisibility(s: CharSequence?): Int =
                if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

        }
        inputEditText.addTextChangedListener(textWatcher)

    }


    private fun loadSearchHistory() {
        val history = searchHistory.getSearchHistory()
        adapter.updateList(history)
    }

    private fun performSearch(query: String) {
        val call = iTunesService.search(query)
        hidePlaceholder()
        progressBar.visibility = View.VISIBLE
        call.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                progressBar.visibility = View.GONE
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
                    hideSearchHistory()
                    showServerErrorPlaceholder()
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                hidePlaceholder()
                adapter.clearList()
                hideSearchHistory()
                showServerErrorPlaceholder()
            }
        })

    }

    private var currentQuery: String? = null
    private val searchRunnable = Runnable {
        currentQuery?.let { performSearch(it) }
    }


    private fun searchDebounce(query: String) {
        currentQuery = query
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
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

    private fun onUpdateButtonClick() {
        hidePlaceholder()
        performSearch(inputEditText.text.toString())
    }

    private fun showSearchHistory() {
        historyCleanButton.visibility = View.VISIBLE
        youSearch.visibility = View.VISIBLE
    }

    private fun hideSearchHistory() {
        historyCleanButton.visibility = View.GONE
        youSearch.visibility = View.GONE
    }

    private fun searchHistoryVisibilityCondition() {
        if (searchHistory.isNotEmpty()) {
            hidePlaceholder()
            showSearchHistory()
            loadSearchHistory()
        } else {
            hidePlaceholder()
            hideSearchHistory()
            adapter.clearList()
        }
    }
}