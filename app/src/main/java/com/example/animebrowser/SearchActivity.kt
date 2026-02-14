package com.example.animebrowser

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var ivBack: ImageView
    private lateinit var ivClear: ImageView
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var emptyStateContainer: LinearLayout
    private lateinit var tvEmptyIcon: TextView
    private lateinit var tvEmptyMessage: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var adapter: SearchResultAdapter
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Initialize views
        etSearch = findViewById(R.id.etSearch)
        ivBack = findViewById(R.id.ivBack)
        ivClear = findViewById(R.id.ivClear)
        rvSearchResults = findViewById(R.id.rvSearchResults)
        emptyStateContainer = findViewById(R.id.emptyStateContainer)
        tvEmptyIcon = findViewById(R.id.tvEmptyIcon)
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage)
        progressBar = findViewById(R.id.progressBar)

        // Setup RecyclerView
        adapter = SearchResultAdapter(emptyList()) { anime ->
            openAnimeDetail(anime)
        }
        rvSearchResults.layoutManager = LinearLayoutManager(this)
        rvSearchResults.adapter = adapter

        // Show initial state
        showEmptyState("🔍", "Search for your favourite anime")

        // Back button
        ivBack.setOnClickListener { finish() }

        // Clear button
        ivClear.setOnClickListener {
            etSearch.text.clear()
            ivClear.visibility = View.GONE
            showEmptyState("🔍", "Search for your favourite anime")
        }

        // Search on keyboard action
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = etSearch.text.toString().trim()
                if (query.isNotEmpty()) {
                    performSearch(query)
                }
                true
            } else false
        }

        // Live search with debounce
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()

                // Show/hide clear button
                ivClear.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE

                // Cancel previous search
                searchRunnable?.let { handler.removeCallbacks(it) }

                if (query.isEmpty()) {
                    showEmptyState("🔍", "Search for your favourite anime")
                    adapter.updateResults(emptyList())
                    return
                }

                // Debounce: wait 500ms after user stops typing
                if (query.length >= 3) {
                    searchRunnable = Runnable { performSearch(query) }
                    handler.postDelayed(searchRunnable!!, 500)
                }
            }
        })

        // Auto-focus the search field and show keyboard
        etSearch.requestFocus()
        val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.showSoftInput(etSearch, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
    }

    private fun performSearch(query: String) {
        // Show loading
        progressBar.visibility = View.VISIBLE
        emptyStateContainer.visibility = View.GONE
        rvSearchResults.visibility = View.GONE

        RetrofitClient.apiService.searchAnime(query = query, limit = 20).enqueue(object : retrofit2.Callback<JikanSearchResponse> {
            override fun onResponse(call: retrofit2.Call<JikanSearchResponse>, response: retrofit2.Response<JikanSearchResponse>) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    val results = response.body()!!.data.map { it.toAnime() }

                    // Save anime data
                    results.forEach { anime ->
                        AnimeDataStore.saveAnime(this@SearchActivity, anime)
                    }

                    if (results.isEmpty()) {
                        showEmptyState("😔", "No results found for \"$query\"")
                        adapter.updateResults(emptyList())
                    } else {
                        emptyStateContainer.visibility = View.GONE
                        rvSearchResults.visibility = View.VISIBLE
                        adapter.updateResults(results)
                    }
                } else {
                    showEmptyState("⚠️", "Search failed. Please try again.")
                }
            }

            override fun onFailure(call: retrofit2.Call<JikanSearchResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                showEmptyState("⚠️", "Error: ${t.message}")
            }
        })
    }

    private fun showEmptyState(icon: String, message: String) {
        rvSearchResults.visibility = View.GONE
        progressBar.visibility = View.GONE
        emptyStateContainer.visibility = View.VISIBLE
        tvEmptyIcon.text = icon
        tvEmptyMessage.text = message
    }

    private fun openAnimeDetail(anime: Anime) {
        val intent = Intent(this, AnimeDetailActivity::class.java).apply {
            putExtra("ANIME_ID", anime.id)
            putExtra("ANIME_TITLE", anime.title)
            putExtra("ANIME_RATING", anime.rating)
        }
        startActivity(intent)
    }
}
