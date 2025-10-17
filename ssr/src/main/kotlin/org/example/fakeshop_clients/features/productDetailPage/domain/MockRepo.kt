import org.example.fakeshop_clients.features.productDetailPage.domain.models.FullProduct

object ProductRepository {
    private val products = mutableMapOf<String, FullProduct>(
        "1" to FullProduct(
            id = "1",
            name = "Wireless Headphones",
            price = 99.99,
            imageUrl = "https://placehold.co/600x400.png?text=Wireless+Headphones",
            category = "Electronics",
            description = "Premium wireless headphones with active noise cancellation. Enjoy crystal-clear sound quality and all-day comfort with our advanced audio technology. Features 30-hour battery life and quick charge capability.",
            rating = 4.5,
            reviews = 328,
            inStock = true
        ),
        "2" to FullProduct(
            id = "2",
            name = "Smart Watch",
            price = 249.99,
            imageUrl = "https://via.placeholder.com/600x400.png?text=Smart+Watch",
            category = "Electronics",
            description = "Stay connected and track your fitness goals with this advanced smartwatch. Features heart rate monitoring, GPS tracking, and seamless smartphone integration. Water-resistant up to 50 meters.",
            rating = 4.7,
            reviews = 542,
            inStock = true
        ),
        "3" to FullProduct(
            id = "3",
            name = "Running Shoes",
            price = 79.99,
            imageUrl = "https://via.placeholder.com/600x400.png?text=Running+Shoes",
            category = "Sports",
            description = "Lightweight running shoes designed for maximum comfort and performance. Breathable mesh upper with responsive cushioning for your daily runs. Available in multiple colors.",
            rating = 4.3,
            reviews = 215,
            inStock = true
        ),
        "4" to FullProduct(
            id = "4",
            name = "Laptop Backpack",
            price = 49.99,
            imageUrl = "https://via.placeholder.com/600x400.png?text=Laptop+Backpack",
            category = "Accessories",
            description = "Durable laptop backpack with padded compartment for devices up to 15.6 inches. Multiple pockets for organization and USB charging port. Water-resistant material.",
            rating = 4.6,
            reviews = 189,
            inStock = true
        ),
        "5" to FullProduct(
            id = "5",
            name = "Bluetooth Speaker",
            price = 59.99,
            imageUrl = "https://via.placeholder.com/600x400.png?text=Bluetooth+Speaker",
            category = "Electronics",
            description = "Portable Bluetooth speaker with 360-degree sound. 12-hour battery life and waterproof design make it perfect for outdoor adventures. Rich bass and clear highs.",
            rating = 4.4,
            reviews = 412,
            inStock = true
        ),
        "6" to FullProduct(
            id = "6",
            name = "Coffee Maker",
            price = 129.99,
            imageUrl = "https://via.placeholder.com/600x400.png?text=Coffee+Maker",
            category = "Home",
            description = "Programmable coffee maker with thermal carafe. Brew up to 12 cups of perfect coffee every time. Features auto-shutoff and programmable timer for wake-up coffee.",
            rating = 4.8,
            reviews = 267,
            inStock = false
        ),
        "7" to FullProduct(
            id = "7",
            name = "Yoga Mat",
            price = 29.99,
            imageUrl = "https://via.placeholder.com/600x400.png?text=Yoga+Mat",
            category = "Sports",
            description = "Premium non-slip yoga mat with extra cushioning. Made from eco-friendly materials with excellent grip. Includes carrying strap for easy transport.",
            rating = 4.5,
            reviews = 156,
            inStock = true
        ),
        "8" to FullProduct(
            id = "8",
            name = "Phone Case",
            price = 19.99,
            imageUrl = "https://via.placeholder.com/600x400.png?text=Phone+Case",
            category = "Accessories",
            description = "Slim protective phone case with military-grade drop protection. Wireless charging compatible with precise cutouts for all buttons and ports.",
            rating = 4.2,
            reviews = 891,
            inStock = true
        )
    )

    fun getProductById(id: String): FullProduct? = products[id]

    fun toggleLike(id: String): FullProduct? {
        val product = products[id] ?: return null
        products[id] = product.copy(isLiked = !product.isLiked)
        return product.copy(isLiked = !product.isLiked)
    }
}