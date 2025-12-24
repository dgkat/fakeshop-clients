package org.example.fakeshop_clients.features.search.domain

import org.example.fakeshop_clients.features.search.domain.models.SearchResult

class SearchServiceMock:SearchService {
    private val mockProducts = listOf(
        SearchResult("1", "Wireless Headphones"),
        SearchResult("2", "Smart Watch"),
        SearchResult("3", "Laptop Stand"),
        SearchResult("4", "USB-C Cable"),
        SearchResult("5", "Mechanical Keyboard"),
        SearchResult("6", "Mouse Pad"),
        SearchResult("7", "Phone Case"),
        SearchResult("8", "Portable Charger"),
        SearchResult("9", "Bluetooth Speaker"),
        SearchResult("10", "Webcam HD"),
        SearchResult("11", "Gaming Mouse"),
        SearchResult("12", "Monitor 27 inch"),
        SearchResult("13", "Desk Lamp LED"),
        SearchResult("14", "Ergonomic Chair"),
        SearchResult("15", "Tablet Stand")
    )

    override suspend fun searchByQuery(query: String): List<SearchResult> {
        val results = mockProducts.filter {
            it.productName.contains(query, ignoreCase = true)
        }

        return results
    }

}