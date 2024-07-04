package com.practicum.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.ui.AudioPlayerActivity
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.adapters.TrackAdapter
import com.practicum.playlistmaker.search.ui.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TrackAdapter
    private lateinit var searchHistory: MutableList<Track?>
    private var inputValue: String = ""
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val INPUT_VALUE_KEY = "input_value"
        const val AUDIO_PLAYER_KEY = "track"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var isClickAllowed = true


    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }


    private val viewModel by viewModel<SearchViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.recyclerView.layoutManager = LinearLayoutManager(context)



        adapter = TrackAdapter(object : TrackAdapter.OnItemClickListener {
            override fun onItemClick(track: Track) {
                viewModel.addTrackToHistory(track)

                if (clickDebounce()) {
                    val intent = Intent(context, AudioPlayerActivity::class.java)
                    intent.putExtra(AUDIO_PLAYER_KEY, track)
                    startActivity(intent)
                }
            }
        })




        viewModel.searchHistory.observe(viewLifecycleOwner) { history ->
            searchHistory = history
            if (history.isNotEmpty()) {
                adapter.updateList(history)
                showSearchHistory()
            } else {
                hideSearchHistory()
            }
        }

        binding.inputEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.inputEditText.text.isEmpty() && searchHistory.isNotEmpty()) {
                showSearchHistory()
            } else {
                hideSearchHistory()
            }
        }

        binding.recyclerView.adapter = adapter

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            val inputMethodManager = context?.getSystemService(InputMethodManager::class.java)
            inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
            binding.inputEditText.clearFocus()
            adapter.clearList()
            hidePlaceholder()
            binding.scrollView.smoothScrollTo(0, 0)
            viewModel.loadSearchHistory()


        }




        binding.serverErrorPlaceholder.updateButton.setOnClickListener {
            onUpdateButtonClick()
        }

        binding.historyCleanButton.setOnClickListener {
            viewModel.clearSearchHistory()
            hideSearchHistory()

        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(binding.inputEditText.text.toString())
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
                binding.clearIcon.visibility = clearButtonVisibility(s)
                if (s?.isEmpty() == false) {
                    searchDebounce(inputValue)
                }


                if (binding.inputEditText.hasFocus() && s.isNullOrEmpty() && viewModel.searchHistory.value?.isNotEmpty() == true) {
                    hidePlaceholder()
                    showSearchHistory()
                } else {
                    hidePlaceholder()
                    hideSearchHistory()
                    adapter.clearList()
                }

            }

            override fun afterTextChanged(s: Editable?) {}

            private fun clearButtonVisibility(s: CharSequence?): Int =
                if (s.isNullOrEmpty()) View.GONE else View.VISIBLE


        }

        binding.inputEditText.addTextChangedListener(textWatcher)

        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            binding.progressBar.visibility = View.GONE
            if (tracks.isNotEmpty()) {
                adapter.updateList(tracks)
                binding.recyclerView.scrollToPosition(0)
            } else {
                adapter.clearList()
                if (inputValue.isNotEmpty()) {
                    showNoResultPlaceholder()
                } else {
                    hidePlaceholder()
                }
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            binding.progressBar.visibility = View.GONE
            adapter.clearList()
            if (inputValue.isNotEmpty()) {
                showServerErrorPlaceholder()
            } else {
                hidePlaceholder()
            }

        }

        viewModel.loadSearchHistory()


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_VALUE_KEY, binding.inputEditText.text.toString())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            inputValue = savedInstanceState.getString(INPUT_VALUE_KEY, "")
        } else {
            binding.inputEditText.setText("")
        }
        binding.inputEditText.setText(inputValue)

    }


    private fun performSearch(query: String) {
        if (inputValue.isNotEmpty()) {
            binding.progressBar.visibility = View.VISIBLE
            hidePlaceholder()
            viewModel.searchTracks(query)
        }

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


    private fun onUpdateButtonClick() {
        hidePlaceholder()
        performSearch(binding.inputEditText.text.toString())
    }

    private fun showSearchHistory() {
        binding.historyCleanButton.visibility = View.VISIBLE
        binding.youSearch.visibility = View.VISIBLE
    }

    private fun hideSearchHistory() {
        binding.historyCleanButton.visibility = View.GONE
        binding.youSearch.visibility = View.GONE
        adapter.clearList()
    }

    private fun searchHistoryVisibilityCondition() {
        if (viewModel.searchHistory.value?.isNotEmpty() == true) {
            hidePlaceholder()
            showSearchHistory()

        } else {
            hidePlaceholder()
            hideSearchHistory()
            adapter.clearList()
        }
    }

    private fun showNoResultPlaceholder() {

        binding.nothingFoundPlaceholder.root.visibility = View.VISIBLE
    }

    private fun showServerErrorPlaceholder() {

        binding.serverErrorPlaceholder.root.visibility = View.VISIBLE
    }

    fun hidePlaceholder() {

        binding.nothingFoundPlaceholder.root.visibility = View.GONE
        binding.serverErrorPlaceholder.root.visibility = View.GONE
    }


    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}