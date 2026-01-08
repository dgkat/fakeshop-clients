package org.example.fakeshop_clients.core.error_handling

/**
 * Result Extension Functions
 *
 * Utility extensions for working with Result<D, E> in a functional style.
 * These extensions enable chaining operations, handling errors, and transforming
 * data without explicit when() expressions everywhere.
 */

// ============================================================================
// BASIC PROPERTIES
// ============================================================================

/**
 * Returns true if this Result is a Success.
 *
 * Example:
 * ```
 * val result = repository.getProduct(id)
 * if (result.isSuccess) {
 *     println("Got the product!")
 * }
 * ```
 */
val <D, E : BaseError> Result<D, E>.isSuccess: Boolean
    get() = this is Result.Success

/**
 * Returns true if this Result is an Error.
 *
 * Example:
 * ```
 * val result = repository.getProduct(id)
 * if (result.isError) {
 *     showErrorState()
 * }
 * ```
 */
val <D, E : BaseError> Result<D, E>.isError: Boolean
    get() = this is Result.Error

// ============================================================================
// DATA EXTRACTION
// ============================================================================

/**
 * Returns the success data or null if this is an Error.
 * Useful when you want to safely extract data without handling the error case.
 *
 * Example:
 * ```
 * val product: Product? = repository.getProduct(id).getOrNull()
 * product?.let { displayProduct(it) }
 * ```
 */
fun <D, E : BaseError> Result<D, E>.getOrNull(): D? = when (this) {
    is Result.Success -> data
    is Result.Error -> null
}

/**
 * Returns the error or null if this is a Success.
 * Useful for logging or analytics when you only care about failures.
 *
 * Example:
 * ```
 * repository.getProduct(id)
 *     .errorOrNull()
 *     ?.let { error -> analytics.logError(error) }
 * ```
 */
fun <D, E : BaseError> Result<D, E>.errorOrNull(): E? = when (this) {
    is Result.Success -> null
    is Result.Error -> error
}

/**
 * Returns the success data or computes a default value from the error.
 * The default is lazily evaluated only if this is an Error.
 *
 * Example:
 * ```
 * val products: List<Product> = repository.getProducts()
 *     .getOrElse { error ->
 *         logger.warn("Failed to load products: $error")
 *         emptyList()
 *     }
 * ```
 */
inline fun <D, E : BaseError> Result<D, E>.getOrElse(default: (E) -> D): D = when (this) {
    is Result.Success -> data
    is Result.Error -> default(error)
}

/**
 * Returns the success data or a fixed default value.
 * Use this when the default doesn't depend on the error.
 *
 * Example:
 * ```
 * val username: String = repository.getUser(id)
 *     .map { it.name }
 *     .getOrDefault("Guest")
 * ```
 */
fun <D, E : BaseError> Result<D, E>.getOrDefault(default: D): D = when (this) {
    is Result.Success -> data
    is Result.Error -> default
}

/**
 * Returns the success data or throws an IllegalStateException.
 * Use sparingly - prefer handling errors explicitly.
 * Useful in tests or when you've already validated success.
 *
 * Example:
 * ```
 * // In tests
 * val product = repository.getProduct(id).getOrThrow()
 * assertEquals("Expected Name", product.name)
 * ```
 */
fun <D, E : BaseError> Result<D, E>.getOrThrow(): D = when (this) {
    is Result.Success -> data
    is Result.Error -> throw IllegalStateException("Result was Error: $error")
}

/**
 * Returns the success data or throws an exception with a custom message.
 *
 * Example:
 * ```
 * val config = configRepository.load()
 *     .getOrThrow { "Failed to load config: $it" }
 * ```
 */
inline fun <D, E : BaseError> Result<D, E>.getOrThrow(message: (E) -> String): D = when (this) {
    is Result.Success -> data
    is Result.Error -> throw IllegalStateException(message(error))
}

// ============================================================================
// TRANSFORMATIONS
// ============================================================================

/**
 * Transforms the success data using the provided function.
 * If this is an Error, it passes through unchanged.
 *
 * This is the most commonly used transformation - use it to convert
 * between data types (e.g., domain model to UI model).
 *
 * Example:
 * ```
 * val uiModel: Result<ProductUiModel, ProductError> = repository.getProduct(id)
 *     .map { product ->
 *         ProductUiModel(
 *             displayName = product.name.uppercase(),
 *             formattedPrice = "$${product.price}"
 *         )
 *     }
 *
 * // Chaining multiple maps
 * val priceText: Result<String, ProductError> = repository.getProduct(id)
 *     .map { it.price }        // Result<Double, ProductError>
 *     .map { it * 1.2 }        // Apply tax
 *     .map { "$$it" }          // Format as string
 * ```
 */
inline fun <D, E : BaseError, R> Result<D, E>.map(transform: (D) -> R): Result<R, E> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error -> this
}

/**
 * Transforms the error using the provided function.
 * If this is a Success, it passes through unchanged.
 *
 * Use this to convert between error types, typically at layer boundaries
 * (e.g., NetworkError -> DomainError, DomainError -> UiError).
 *
 * Example:
 * ```
 * // In repository - convert network errors to domain errors
 * fun getProduct(id: String): Result<Product, ProductError> {
 *     return api.fetchProduct(id)
 *         .mapError { networkError ->
 *             when (networkError) {
 *                 is NetworkError.HttpError -> when (networkError.code) {
 *                     404 -> ProductError.NotFound(id)
 *                     else -> ProductError.ServiceUnavailable
 *                 }
 *                 is NetworkError.Timeout -> ProductError.ServiceUnavailable
 *                 is NetworkError.NoConnection -> ProductError.ServiceUnavailable
 *             }
 *         }
 * }
 * ```
 */
inline fun <D, E : BaseError, F : BaseError> Result<D, E>.mapError(
    transform: (E) -> F
): Result<D, F> = when (this) {
    is Result.Success -> this
    is Result.Error -> Result.Error(transform(error))
}

/**
 * Transforms the success data with a function that itself returns a Result.
 * This "flattens" the nested Result, avoiding Result<Result<D, E>, E>.
 *
 * Use this when chaining operations that can each fail independently.
 *
 * Example:
 * ```
 * // Each operation returns Result
 * fun getUser(id: String): Result<User, AuthError>
 * fun getCart(userId: String): Result<Cart, CartError>
 *
 * // Without flatMap: Result<Result<Cart, CartError>, AuthError> - messy!
 * // With flatMap: Result<Cart, Error> - clean!
 *
 * val cart: Result<Cart, BaseError> = getUser(userId)
 *     .flatMap { user -> getCart(user.id) }
 *
 * // Chaining multiple operations
 * val order: Result<Order, BaseError> = authRepository.getCurrentUser()
 *     .flatMap { user -> cartRepository.getCart(user.id) }
 *     .flatMap { cart -> orderRepository.createOrder(cart) }
 *     .flatMap { order -> paymentRepository.processPayment(order) }
 * ```
 *
 * Note: @UnsafeVariance is needed because we're using E in an "in" position
 * (as a return type of transform), but it's declared as "out". This is safe
 * because we only return the error, never modify it.
 */
inline fun <D, E : BaseError, R> Result<D, E>.flatMap(
    transform: (D) -> Result<R, @UnsafeVariance E>
): Result<R, E> = when (this) {
    is Result.Success -> transform(data)
    is Result.Error -> this
}

/**
 * Like flatMap but for the error case.
 * Allows attempting recovery with an operation that might also fail.
 *
 * Example:
 * ```
 * // Try cache first, fall back to network
 * val product: Result<Product, ProductError> = cache.getProduct(id)
 *     .flatMapError { cacheError ->
 *         // Cache miss - try network
 *         api.fetchProduct(id)
 *     }
 * ```
 */
inline fun <D, E : BaseError, F : BaseError> Result<D, E>.flatMapError(
    transform: (E) -> Result<@UnsafeVariance D, F>
): Result<D, F> = when (this) {
    is Result.Success -> this
    is Result.Error -> transform(error)
}

/**
 * Transforms both success and error cases into a single return type.
 * This is the most general transformation - use it when you need to
 * "exit" the Result context and produce a final value.
 *
 * Example:
 * ```
 * // Convert Result to UI state
 * val uiState: ProductUiState = repository.getProduct(id).fold(
 *     onSuccess = { product ->
 *         ProductUiState.Loaded(
 *             name = product.name,
 *             price = formatPrice(product.price)
 *         )
 *     },
 *     onError = { error ->
 *         ProductUiState.Error(
 *             message = when (error) {
 *                 is ProductError.NotFound -> "Product not found"
 *                 is ProductError.OutOfStock -> "Out of stock"
 *             }
 *         )
 *     }
 * )
 * ```
 */
inline fun <D, E : BaseError, R> Result<D, E>.fold(
    onSuccess: (D) -> R,
    onError: (E) -> R
): R = when (this) {
    is Result.Success -> onSuccess(data)
    is Result.Error -> onError(error)
}

// ============================================================================
// SIDE EFFECTS
// ============================================================================

/**
 * Executes the given action if this is a Success, then returns the original Result.
 * Use for side effects like logging, analytics, or cache updates.
 *
 * Example:
 * ```
 * repository.getProduct(id)
 *     .onSuccess { product ->
 *         analytics.logProductView(product.id)
 *         cache.store(product)
 *     }
 *     .onError { error ->
 *         logger.warn("Failed to load product: $error")
 *     }
 *     .fold(
 *         onSuccess = { _state.value = State.Loaded(it) },
 *         onError = { _state.value = State.Error(it) }
 *     )
 * ```
 */
inline fun <D, E : BaseError> Result<D, E>.onSuccess(action: (D) -> Unit): Result<D, E> {
    if (this is Result.Success) action(data)
    return this
}

/**
 * Executes the given action if this is an Error, then returns the original Result.
 * Use for side effects like logging, analytics, or showing notifications.
 *
 * Example:
 * ```
 * repository.syncData()
 *     .onError { error ->
 *         logger.error("Sync failed", error)
 *         notificationManager.showSyncError()
 *         if (error is SyncError.NetworkUnavailable) {
 *             workManager.scheduleRetry()
 *         }
 *     }
 * ```
 */
inline fun <D, E : BaseError> Result<D, E>.onError(action: (E) -> Unit): Result<D, E> {
    if (this is Result.Error) action(error)
    return this
}

// ============================================================================
// RECOVERY
// ============================================================================

/**
 * Converts an Error into a Success by transforming the error into data.
 * The result is always a Success (error type becomes Nothing).
 *
 * Use when you have a sensible fallback value for any error.
 *
 * Example:
 * ```
 * // Always get a list, even on error
 * val products: List<Product> = repository.getProducts()
 *     .recover { emptyList() }
 *     .getOrThrow()  // Safe - always Success after recover
 *
 * // Use error info in recovery
 * val displayName: String = repository.getUser(id)
 *     .map { it.displayName }
 *     .recover { error ->
 *         when (error) {
 *             is UserError.NotFound -> "Unknown User"
 *             is UserError.Suspended -> "Suspended Account"
 *         }
 *     }
 *     .getOrThrow()
 * ```
 */
inline fun <D, E : BaseError> Result<D, E>.recover(transform: (E) -> D): Result<D, Nothing> =
    when (this) {
        is Result.Success -> this
        is Result.Error -> Result.Success(transform(error))
    }

/**
 * Attempts to recover from an Error with another Result-returning operation.
 * Unlike recover(), this can still fail if the recovery operation fails.
 *
 * Example:
 * ```
 * // Try primary source, fall back to secondary
 * val product: Result<Product, ProductError> = primaryApi.getProduct(id)
 *     .recoverWith { error ->
 *         if (error is ProductError.ServiceUnavailable) {
 *             fallbackApi.getProduct(id)  // Try backup
 *         } else {
 *             Result.error(error)  // Don't recover from NotFound
 *         }
 *     }
 *
 * // Retry logic
 * val data: Result<Data, NetworkError> = api.fetchData()
 *     .recoverWith { api.fetchData() }  // Simple retry
 * ```
 */
inline fun <D, E : BaseError> Result<D, E>.recoverWith(
    transform: (E) -> Result<D, @UnsafeVariance E>
): Result<D, E> = when (this) {
    is Result.Success -> this
    is Result.Error -> transform(error)
}

// ============================================================================
// COMBINING RESULTS
// ============================================================================

/**
 * Combines this Result with another, applying a transform function if both succeed.
 * If either fails, returns the first error encountered.
 *
 * Use when you need data from multiple independent operations.
 *
 * Example:
 * ```
 * // Combine product and inventory data
 * val details: Result<ProductDetails, BaseError> = productRepo.getProduct(id)
 *     .zip(inventoryRepo.getStock(id)) { product, stock ->
 *         ProductDetails(
 *             product = product,
 *             stockLevel = stock.quantity,
 *             isAvailable = stock.quantity > 0
 *         )
 *     }
 *
 * // Combine three results
 * val checkout: Result<CheckoutData, BaseError> = userRepo.getUser(userId)
 *     .zip(cartRepo.getCart(userId)) { user, cart -> user to cart }
 *     .zip(shippingRepo.getOptions(userId)) { (user, cart), shipping ->
 *         CheckoutData(user, cart, shipping)
 *     }
 * ```
 */
inline fun <D1, D2, E : BaseError, R> Result<D1, E>.zip(
    other: Result<D2, @UnsafeVariance E>,
    transform: (D1, D2) -> R
): Result<R, E> = when (this) {
    is Result.Success -> when (other) {
        is Result.Success -> Result.Success(transform(this.data, other.data))
        is Result.Error -> other
    }
    is Result.Error -> this
}

/**
 * Combines this Result with another into a Pair if both succeed.
 * Convenience overload of zip when you just want both values.
 *
 * Example:
 * ```
 * val pair: Result<Pair<User, Cart>, BaseError> = userRepo.getUser(userId)
 *     .zip(cartRepo.getCart(userId))
 *
 * pair.onSuccess { (user, cart) ->
 *     println("${user.name} has ${cart.items.size} items")
 * }
 * ```
 */
fun <D1, D2, E : BaseError> Result<D1, E>.zip(
    other: Result<D2, @UnsafeVariance E>
): Result<Pair<D1, D2>, E> = zip(other, ::Pair)

/**
 * Combines three Results, applying a transform function if all succeed.
 *
 * Example:
 * ```
 * val checkout: Result<CheckoutData, BaseError> = zip(
 *     userRepo.getUser(userId),
 *     cartRepo.getCart(userId),
 *     shippingRepo.getOptions(userId)
 * ) { user, cart, shipping ->
 *     CheckoutData(user, cart, shipping)
 * }
 * ```
 */
inline fun <D1, D2, D3, E : BaseError, R> zip(
    first: Result<D1, E>,
    second: Result<D2, @UnsafeVariance E>,
    third: Result<D3, @UnsafeVariance E>,
    transform: (D1, D2, D3) -> R
): Result<R, E> = first.zip(second, ::Pair).zip(third) { (a, b), c -> transform(a, b, c) }

// ============================================================================
// LIST UTILITIES
// ============================================================================

/**
 * Converts a List of Results into a Result of List.
 * Returns Success with all data if ALL Results are Success.
 * Returns the first Error encountered otherwise.
 *
 * Example:
 * ```
 * val productIds = listOf("1", "2", "3")
 * val results: List<Result<Product, ProductError>> = productIds.map { repository.getProduct(it) }
 *
 * val allProducts: Result<List<Product>, ProductError> = results.allSuccessOrFirstError()
 *
 * allProducts.fold(
 *     onSuccess = { products -> displayProducts(products) },
 *     onError = { error -> showError(error) }  // First failure
 * )
 * ```
 */
fun <D, E : BaseError> List<Result<D, E>>.allSuccessOrFirstError(): Result<List<D>, E> {
    val successes = mutableListOf<D>()
    for (result in this) {
        when (result) {
            is Result.Success -> successes.add(result.data)
            is Result.Error -> return result
        }
    }
    return Result.Success(successes)
}

/**
 * Separates a List of Results into successful data and errors.
 * Use when you want to process successes even if some items failed.
 *
 * Example:
 * ```
 * val productIds = listOf("1", "2", "3", "invalid")
 * val results = productIds.map { repository.getProduct(it) }
 *
 * val (products, errors) = results.partition()
 *
 * displayProducts(products)  // Show what we got
 * if (errors.isNotEmpty()) {
 *     showPartialErrorBanner(errors.size)  // "3 of 4 products loaded"
 * }
 * ```
 */
fun <D, E : BaseError> List<Result<D, E>>.partition(): Pair<List<D>, List<E>> {
    val successes = mutableListOf<D>()
    val errors = mutableListOf<E>()
    for (result in this) {
        when (result) {
            is Result.Success -> successes.add(result.data)
            is Result.Error -> errors.add(result.error)
        }
    }
    return successes to errors
}

/**
 * Maps each element and collects all successful results.
 * Errors are silently ignored - use when partial success is acceptable.
 *
 * Example:
 * ```
 * val userIds = listOf("1", "2", "3")
 * val users: List<User> = userIds.mapNotNullResult { id ->
 *     userRepository.getUser(id)
 * }
 * // Returns list of successfully fetched users, ignoring failures
 * ```
 */
inline fun <T, D, E : BaseError> List<T>.mapNotNullResult(
    transform: (T) -> Result<D, E>
): List<D> = mapNotNull { transform(it).getOrNull() }

// ============================================================================
// COROUTINE / SUSPEND SUPPORT
// ============================================================================

/**
 * Suspending version of map for async transformations.
 *
 * Example:
 * ```
 * val enrichedProduct: Result<EnrichedProduct, ProductError> = repository.getProduct(id)
 *     .mapAsync { product ->
 *         // Async enrichment
 *         val reviews = reviewService.fetchReviews(product.id)
 *         product.withReviews(reviews)
 *     }
 * ```
 */
suspend inline fun <D, E : BaseError, R> Result<D, E>.mapAsync(
    crossinline transform: suspend (D) -> R
): Result<R, E> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error -> this
}

/**
 * Suspending version of flatMap for chaining async operations.
 *
 * Example:
 * ```
 * val order: Result<Order, OrderError> = authRepository.getCurrentUser()
 *     .flatMapAsync { user ->
 *         cartRepository.getCart(user.id)
 *     }
 *     .flatMapAsync { cart ->
 *         orderRepository.createOrder(cart)
 *     }
 * ```
 */
suspend inline fun <D, E : BaseError, R> Result<D, E>.flatMapAsync(
    crossinline transform: suspend (D) -> Result<R, @UnsafeVariance E>
): Result<R, E> = when (this) {
    is Result.Success -> transform(data)
    is Result.Error -> this
}

/**
 * Suspending version of onSuccess for async side effects.
 *
 * Example:
 * ```
 * repository.createOrder(cart)
 *     .onSuccessAsync { order ->
 *         notificationService.sendConfirmation(order)
 *         analyticsService.trackPurchase(order)
 *     }
 * ```
 */
suspend inline fun <D, E : BaseError> Result<D, E>.onSuccessAsync(
    crossinline action: suspend (D) -> Unit
): Result<D, E> {
    if (this is Result.Success) action(data)
    return this
}

/**
 * Suspending version of onError for async side effects.
 *
 * Example:
 * ```
 * repository.syncData()
 *     .onErrorAsync { error ->
 *         errorReportingService.report(error)
 *         retryScheduler.scheduleRetry()
 *     }
 * ```
 */
suspend inline fun <D, E : BaseError> Result<D, E>.onErrorAsync(
    crossinline action: suspend (E) -> Unit
): Result<D, E> {
    if (this is Result.Error) action(error)
    return this
}

// ============================================================================
// RESULT BUILDERS
// ============================================================================

/**
 * Wraps a throwing block into a Result.
 * Catches any Throwable and wraps it in ThrowableError.
 *
 * Use when integrating with code that throws exceptions
 * (e.g., third-party libraries, Java interop).
 *
 * Example:
 * ```
 * val parsed: Result<Config, ThrowableError> = resultOf {
 *     jsonParser.parse(configJson)  // Might throw
 * }
 *
 * parsed.fold(
 *     onSuccess = { config -> useConfig(config) },
 *     onError = { error -> logger.error("Parse failed", error.throwable) }
 * )
 * ```
 */
inline fun <D> resultOf(block: () -> D): Result<D, ThrowableError> = try {
    Result.Success(block())
} catch (e: Throwable) {
    Result.Error(ThrowableError(e))
}

/**
 * Suspending version of resultOf for async throwing operations.
 *
 * Example:
 * ```
 * val data: Result<Data, ThrowableError> = suspendResultOf {
 *     httpClient.get(url)  // Suspending call that might throw
 * }
 * ```
 */
suspend inline fun <D> suspendResultOf(
    crossinline block: suspend () -> D
): Result<D, ThrowableError> = try {
    Result.Success(block())
} catch (e: Throwable) {
    Result.Error(ThrowableError(e))
}

/**
 * Wrapper error type for exceptions.
 * Use with resultOf/suspendResultOf when integrating with throwing code.
 */
data class ThrowableError(val throwable: Throwable) : BaseError