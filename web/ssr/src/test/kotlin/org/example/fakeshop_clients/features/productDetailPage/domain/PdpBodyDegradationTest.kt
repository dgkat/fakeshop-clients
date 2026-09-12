package org.example.fakeshop_clients.features.productDetailPage.domain

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.interactions.domain.InteractionContext
import org.example.fakeshop_clients.features.bdui.data.SSRBduiTemplateDatasource
import org.example.fakeshop_clients.features.bdui.data.mappers.DataToDomainBduiTemplateMapper
import org.example.fakeshop_clients.features.bdui.data.models.BduiTemplateResponse
import org.example.fakeshop_clients.features.core.models.Cookies
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct
import org.example.fakeshop_clients.features.productDetail.domain.models.DetailedProduct
import org.example.fakeshop_clients.features.productDetailPage.domain.models.PdpBody
import org.example.fakeshop_clients.features.productDetailPage.domain.models.PdpData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * The PDP's failure seam: the brief leg fails the page, the BDUI legs never do. Products
 * predating BDUI have a brief record but no v2 detailed record (or land in a category with no
 * template), and before this split either one 500'd a page whose top half was perfectly fine.
 */
class PdpBodyDegradationTest {

    private val brief = BriefProduct(
        id = "p1",
        name = "Legacy product",
        price = 12.5,
        imageUrl = "https://example.com/p1.jpg",
        category = "shoes"
    )

    private val detailed = DetailedProduct(
        productId = "p1",
        category = "shoes",
        fullDescription = "A description that predates BDUI.",
        galleryUrls = listOf("https://example.com/p1-2.jpg"),
        data = buildJsonObject { put("specs", JsonPrimitive("none")) }
    )

    private val template = BduiTemplateResponse(
        schemaVersion = 1,
        screen = "pdp",
        category = "shoes",
        root = buildJsonObject { put("type", JsonPrimitive("Column")) }
    )

    private fun service(
        briefResult: Result<BriefProduct, NetworkError> = Result.Success(brief),
        detailedResult: Result<DetailedProduct, NetworkError> = Result.Success(detailed),
        templateResult: Result<BduiTemplateResponse, NetworkError> = Result.Success(template)
    ) = ProductDetailServiceImpl(
        productDetailRepository = FakeRepository(briefResult, detailedResult),
        bduiTemplateDatasource = FakeTemplateDatasource(templateResult),
        bduiTemplateMapper = DataToDomainBduiTemplateMapper()
    )

    private suspend fun pdp(service: ProductDetailServiceImpl) =
        service.getPdpData("p1", Cookies(data = emptyMap()), InteractionContext.None)

    @Test
    fun aMissingDetailedRecordStillShipsTheTopHalf() = runTest {
        val result = pdp(service(detailedResult = Result.Error(NetworkError.HttpError(404, null, null))))

        val data = assertIs<Result.Success<PdpData>>(result).data
        assertEquals(brief, data.brief)
        val body = assertIs<PdpBody.Unavailable>(data.body)
        // No detailed record ⇒ nothing to show but the note.
        assertEquals(null, body.fullDescription)
        assertTrue(data.galleryUrls.isEmpty())
    }

    @Test
    fun aMissingTemplateKeepsTheProseTheDetailedLegReturned() = runTest {
        val result = pdp(service(templateResult = Result.Error(NetworkError.HttpError(404, null, null))))

        val data = assertIs<Result.Success<PdpData>>(result).data
        val body = assertIs<PdpBody.Unavailable>(data.body)
        assertEquals(detailed.fullDescription, body.fullDescription)
        // The gallery comes off the detailed leg, which succeeded — the carousel keeps working.
        assertEquals(detailed.galleryUrls, data.galleryUrls)
    }

    @Test
    fun aBriefFailureIsStillAPageFailure() = runTest {
        val result = pdp(service(briefResult = Result.Error(NetworkError.Timeout)))

        assertIs<Result.Error<NetworkError>>(result)
    }

    @Test
    fun theHappyPathStillRendersTheBduiBody() = runTest {
        val data = assertIs<Result.Success<PdpData>>(pdp(service())).data

        assertIs<PdpBody.Ready>(data.body)
        assertEquals(detailed.galleryUrls, data.galleryUrls)
    }

    private class FakeRepository(
        private val briefResult: Result<BriefProduct, NetworkError>,
        private val detailedResult: Result<DetailedProduct, NetworkError>
    ) : ProductDetailRepository {
        override suspend fun getBriefProductById(
            id: String,
            cookies: Cookies,
            interaction: InteractionContext
        ) = briefResult

        override suspend fun getDetailedProductById(id: String, cookies: Cookies) = detailedResult

        override suspend fun getRecommendations(
            productId: String,
            cookies: Cookies,
            limit: Int
        ): Result<List<BriefProduct>, NetworkError> = error("not used")

        override suspend fun addFavorite(
            productId: String,
            cookies: Cookies,
            interaction: InteractionContext
        ): Result<Unit, NetworkError> = error("not used")

        override suspend fun removeFavorite(
            productId: String,
            cookies: Cookies,
            interaction: InteractionContext
        ): Result<Unit, NetworkError> = error("not used")

        override suspend fun checkFavorite(
            productId: String,
            cookies: Cookies
        ): Result<Boolean, NetworkError> = error("not used")
    }

    private class FakeTemplateDatasource(
        private val templateResult: Result<BduiTemplateResponse, NetworkError>
    ) : SSRBduiTemplateDatasource {
        override suspend fun getPdpTemplate(category: String, cookies: Cookies) = templateResult
    }
}
