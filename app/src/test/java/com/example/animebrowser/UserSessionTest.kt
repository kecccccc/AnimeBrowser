package com.example.animebrowser

import android.content.Context
import android.content.SharedPreferences
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class UserSessionTest {

    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    @Before
    fun setUp() {
        // Create mock SharedPreferences and Editor
        editor = mock<SharedPreferences.Editor> {
            on { putBoolean(any(), any()) } doReturn it
            on { putString(any(), any()) } doReturn it
            on { clear() } doReturn it
        }
        prefs = mock<SharedPreferences> {
            on { edit() } doReturn editor
        }
        context = mock<Context> {
            on { getSharedPreferences(eq("AnimeBrowserPrefs"), eq(Context.MODE_PRIVATE)) } doReturn prefs
        }
    }

    @Test
    fun `login saves credentials`() {
        UserSession.login(context, "test@email.com", "TestUser")

        verify(editor).putBoolean("isLoggedIn", true)
        verify(editor).putBoolean("isGuest", false)
        verify(editor).putString("userEmail", "test@email.com")
        verify(editor).putString("username", "TestUser")
        verify(editor).apply()
    }

    @Test
    fun `setGuestMode sets guest flag`() {
        UserSession.setGuestMode(context, true)

        verify(editor).putBoolean("isGuest", true)
        verify(editor).putBoolean("isLoggedIn", false)
        verify(editor).apply()
    }

    @Test
    fun `logout clears all data`() {
        UserSession.logout(context)

        verify(editor).clear()
        verify(editor).apply()
    }

    @Test
    fun `isLoggedIn returns true when logged in`() {
        whenever(prefs.getBoolean("isLoggedIn", false)).thenReturn(true)

        assertTrue(UserSession.isLoggedIn(context))
    }

    @Test
    fun `isLoggedIn returns false by default`() {
        whenever(prefs.getBoolean("isLoggedIn", false)).thenReturn(false)

        assertFalse(UserSession.isLoggedIn(context))
    }

    @Test
    fun `isGuest returns true when in guest mode`() {
        whenever(prefs.getBoolean("isGuest", false)).thenReturn(true)

        assertTrue(UserSession.isGuest(context))
    }

    @Test
    fun `isGuest returns false by default`() {
        whenever(prefs.getBoolean("isGuest", false)).thenReturn(false)

        assertFalse(UserSession.isGuest(context))
    }

    @Test
    fun `getUsername returns stored username`() {
        whenever(prefs.getString("username", "User")).thenReturn("TestUser")

        assertEquals("TestUser", UserSession.getUsername(context))
    }

    @Test
    fun `getUsername returns default when not set`() {
        whenever(prefs.getString("username", "User")).thenReturn("User")

        assertEquals("User", UserSession.getUsername(context))
    }

    @Test
    fun `getEmail returns stored email`() {
        whenever(prefs.getString("userEmail", "")).thenReturn("test@email.com")

        assertEquals("test@email.com", UserSession.getEmail(context))
    }

    @Test
    fun `getEmail returns empty string when not set`() {
        whenever(prefs.getString("userEmail", "")).thenReturn("")

        assertEquals("", UserSession.getEmail(context))
    }
}
