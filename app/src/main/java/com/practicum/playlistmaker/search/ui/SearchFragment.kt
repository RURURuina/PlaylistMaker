package com.practicum.playlistmaker.search.ui

import TrackAdapter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.models.SearchFragmentState
import com.practicum.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.practicum.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TrackAdapter
    private var inputValue: String = ""

    companion object {
        private const val INPUT_VALUE_KEY = "input_value"
        private const val CLICK_DEBOUNCE_DELAY = 300L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var isClickAllowed = true
    private val viewModel by viewModel<SearchViewModel>()
    private lateinit var clickDebounce: (Unit) -> Unit
    private lateinit var searchDebounce: (String) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupDebounceFunctions()
        setupTextWatcher()

        binding.clearIcon.setOnClickListener {
            clearSearchInput()
        }

        binding.serverErrorPlaceholder.updateButton.setOnClickListener {
            onUpdateButtonClick()
        }

        binding.historyCleanButton.setOnClickListener {
            hideSearchHistory()
            adapter.notifyDataSetChanged()
            viewModel.clearSearchHistory()
        }

        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(binding.inputEditText.text.toString())
                true
            } else {
                false
            }
        }

        observeViewModel()

        viewModel.loadSearchHistory()
    }


    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = TrackAdapter(object : TrackAdapter.OnItemClickListener {
            override fun onItemClick(track: Track) {
                viewModel.addTrackToHistory(track)
                if (isClickAllowed) {
                    isClickAllowed = false
                    clickDebounce(Unit)
                    hideSearchHistory()
                    val action = SearchFragmentDirections.actionSearchFragmentToAudioPlayerFragment(track)
                    findNavController().navigate(action)
                }
            }
        })
        binding.recyclerView.adapter = adapter
    }

    private fun setupDebounceFunctions() {
        clickDebounce = debounce(
            delayMillis = CLICK_DEBOUNCE_DELAY,
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            useLastParam = true
        ) {
            isClickAllowed = true
        }

        searchDebounce = debounce(
            delayMillis = SEARCH_DEBOUNCE_DELAY,
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            useLastParam = true
        ) { query ->
            performSearch(query)
        }
    }

    private fun setupTextWatcher() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputValue = s?.toString() ?: ""
                binding.clearIcon.visibility = if (inputValue.isEmpty()) View.GONE else View.VISIBLE

                if (s?.isNotEmpty() == true) {
                    searchDebounce(inputValue)
                    hideSearchHistory()
                } else if (binding.inputEditText.hasFocus()) {
                    viewModel.loadSearchHistory()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.inputEditText.addTextChangedListener(textWatcher)
    }

    private fun observeViewModel() {
        viewModel.screenState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchFragmentState.Loading -> showLoading()
                is SearchFragmentState.Content -> showContent(state.tracks)
                is SearchFragmentState.Empty -> {
                    if (inputValue.isNotEmpty()) {
                        showNoResultPlaceholder()
                    } else {
                        hidePlaceholders()
                    }
                }
                is SearchFragmentState.Error -> showServerErrorPlaceholder(state.errorMessage)
                is SearchFragmentState.History -> showSearchHistory(state.tracks)
            }
        }
        viewModel.searchResult.observe(viewLifecycleOwner) {results ->
            if (results.isNotEmpty()) {
                showContent(results)
            }
        }
    }

    private fun performSearch(query: String) {
        if (adapter.trackList.isEmpty()) {
            if (query.isNotEmpty()) {
                viewModel.searchTracks(query)
            }
        }

    }

    private fun clearSearchInput() {
        binding.inputEditText.setText("")
        binding.inputEditText.clearFocus()
        adapter.clearList()
        adapter.notifyDataSetChanged()
        hidePlaceholders()
        hideKeyboard()
        viewModel.loadSearchHistory()
    }

    private fun onUpdateButtonClick() {
        hidePlaceholders()
        performSearch(binding.inputEditText.text.toString())
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        hidePlaceholders()
        hideSearchHistory()
    }

    private fun showContent(tracks: MutableList<Track?>) {
        binding.progressBar.visibility = View.GONE
        hidePlaceholders()
        adapter.updateList(tracks)
        binding.recyclerView.scrollToPosition(0)
    }

    private fun showNoResultPlaceholder() {
        binding.progressBar.visibility = View.GONE
        binding.nothingFoundPlaceholder.root.visibility = View.VISIBLE
    }

    private fun showServerErrorPlaceholder(errorMessage: String) {
        binding.progressBar.visibility = View.GONE
        binding.serverErrorPlaceholder.root.visibility = View.VISIBLE
    }

    private fun showSearchHistory(tracks: MutableList<Track?>) {
        hidePlaceholders()
        binding.historyCleanButton.visibility = View.VISIBLE
        binding.youSearch.visibility = View.VISIBLE
        adapter.updateList(tracks)
    }

    private fun hidePlaceholders() {
        binding.nothingFoundPlaceholder.root.visibility = View.GONE
        binding.serverErrorPlaceholder.root.visibility = View.GONE
        binding.historyCleanButton.visibility = View.GONE
        binding.youSearch.visibility = View.GONE
    }

    private fun hideSearchHistory() {
        binding.historyCleanButton.visibility = View.GONE
        binding.youSearch.visibility = View.GONE
        adapter.clearList()
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(InputMethodManager::class.java)
        inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_VALUE_KEY, binding.inputEditText.text.toString())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            inputValue = savedInstanceState.getString(INPUT_VALUE_KEY, "")
            binding.inputEditText.setText(inputValue)
        }
    }

    override fun onResume() {
        super.onResume()
        isClickAllowed = true
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
