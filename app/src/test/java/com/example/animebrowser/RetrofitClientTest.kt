package com.example.animebrowser

import org.junit.Assert.*
import org.junit.Test

class RetrofitClientTest {

    @Test
    fun `apiService is not null`() {
        val service = RetrofitClient.apiService
        assertNotNull(service)
    }

    @Test
    fun `apiService returns same instance`() {
        val service1 = RetrofitClient.apiService
        val service2 = RetrofitClient.apiService
        assertSame(service1, service2)
    }

    @Test
    fun `apiService is of correct type`() {
        val service = RetrofitClient.apiService
        assertTrue(service is JikanApiService)
    }
}
