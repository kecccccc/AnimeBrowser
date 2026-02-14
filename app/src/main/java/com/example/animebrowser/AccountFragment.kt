package com.example.animebrowser

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

class AccountFragment : Fragment() {

    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvProfileInitial: TextView
    private lateinit var tvFavoritesCount: TextView
    private lateinit var tvWatchedCount: TextView
    private lateinit var tvWatchlistCount: TextView
    private lateinit var llMyFavorites: LinearLayout
    private lateinit var llMyWatchlist: LinearLayout
    private lateinit var llMyWatched: LinearLayout
    private lateinit var llLogout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        // Initialize views
        tvUsername = view.findViewById(R.id.tvUsername)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvProfileInitial = view.findViewById(R.id.tvProfileInitial)
        tvFavoritesCount = view.findViewById(R.id.tvFavoritesCount)
        tvWatchedCount = view.findViewById(R.id.tvWatchedCount)
        tvWatchlistCount = view.findViewById(R.id.tvWatchlistCount)
        llMyFavorites = view.findViewById(R.id.llMyFavorites)
        llMyWatchlist = view.findViewById(R.id.llMyWatchlist)
        llMyWatched = view.findViewById(R.id.llMyWatched)
        llLogout = view.findViewById(R.id.llLogout)

        // Load user data
        loadUserData()

        // Set click listeners (only work for logged-in users)
        llMyFavorites.setOnClickListener {
            if (UserSession.isLoggedIn(requireContext())) {
                openAnimeListActivity("favorites")
            }
        }

        llMyWatchlist.setOnClickListener {
            if (UserSession.isLoggedIn(requireContext())) {
                openAnimeListActivity("watchlist")
            }
        }

        llMyWatched.setOnClickListener {
            if (UserSession.isLoggedIn(requireContext())) {
                openAnimeListActivity("watched")
            }
        }

        llLogout.setOnClickListener {
            showLogoutDialog()
        }

        return view
    }

    private fun loadUserData() {
        if (!UserSession.isLoggedIn(requireContext())) {
            // Guest user - show default info and zero counts
            tvUsername.text = "Guest"
            tvEmail.text = "Continue as guest to browse"
            tvProfileInitial.text = "G"
            tvFavoritesCount.text = "0"
            tvWatchedCount.text = "0"
            tvWatchlistCount.text = "0"
            return
        }

        val username = UserSession.getUsername(requireContext())
        val email = UserSession.getEmail(requireContext())

        tvUsername.text = username
        tvEmail.text = email
        tvProfileInitial.text = username.firstOrNull()?.uppercase() ?: "U"

        // Load actual counts
        tvFavoritesCount.text = FavoritesManager.getFavoritesCount(requireContext()).toString()
        tvWatchedCount.text = FavoritesManager.getWatchedCount(requireContext()).toString()
        tvWatchlistCount.text = FavoritesManager.getWatchlistCount(requireContext()).toString()
    }

    private fun openAnimeListActivity(listType: String) {
        val intent = Intent(requireContext(), MyAnimeListActivity::class.java).apply {
            putExtra("LIST_TYPE", listType)
        }
        startActivity(intent)
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performLogout() {
        UserSession.logout(requireContext())

        // Go back to login screen
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}