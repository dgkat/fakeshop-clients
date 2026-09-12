package org.example.fakeshop_clients.features.productDetailPage.domain

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.interactions.domain.InteractionContext
import org.example.fakeshop_clients.core.presentation.models.UiBriefProduct
import org.example.fakeshop_clients.features.bdui.data.SSRBduiTemplateDatasource
import org.example.fakeshop_clients.features.bdui.data.mappers.DataToDomainBduiTemplateMapper
import org.example.fakeshop_clients.features.bdui.domain.buildPdpBindData
import org.example.fakeshop_clients.features.core.models.Cookies
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct
import org.example.fakeshop_clients.features.productDetailPage.domain.models.PdpBody
import org.example.fakeshop_clients.features.productDetailPage.domain.models.PdpData
import org.slf4j.LoggerFactory
import kotlin.time.Duration
import kotlin.time.TimeSource
import kotlin.time.measureTimedValue

class ProductDetailServiceImpl(
    private val productDetailRepository: ProductDetailRepository,
    private val bduiTemplateDatasource: SSRBduiTemplateDatasource,
    private val bduiTemplateMapper: DataToDomainBduiTemplateMapper
) : ProductDetailService {

    private val logger = LoggerFactory.getLogger(ProductDetailServiceImpl::class.java)

    override suspend fun getPdpData(
        id: String,
        cookies: Cookies,
        interaction: InteractionContext
    ): Result<PdpData, NetworkError> = coroutineScope {
        val pageStart = TimeSource.Monotonic.markNow()
        // Only the brief call carries the interaction context — it is the one the gateway records
        // the VIEW from. Attaching it to the detailed call too would double-count the view.
        val briefDef = async {
            measureTimedValue {
                productDetailRepository.getBriefProductById(
                    id,
                    cookies,
                    interaction
                )
            }
        }
        val detailedDef = async {
            measureTimedValue {
                productDetailRepository.getDetailedProductById(
                    id,
                    cookies
                )
            }
        }

        val (briefRes, briefTime) = briefDef.await()
        // The brief leg is the only page-level failure: without it there is no top half either.
        if (briefRes is Result.Error) {
            detailedDef.cancel()
            return@coroutineScope Result.Error(briefRes.error)
        }
        val brief = (briefRes as Result.Success).data

        val templateDef = async {
            measureTimedValue {
                bduiTemplateDatasource.getPdpTemplate(
                    brief.category,
                    cookies
                )
            }
        }

        val (detailedRes, detailedTime) = detailedDef.await()
        if (detailedRes is Result.Error) {
            templateDef.cancel()
            logBodyUnavailable(id, "detailed", detailedRes.error)
            return@coroutineScope Result.Success(
                PdpData(
                    brief = brief,
                    galleryUrls = emptyList(),
                    body = PdpBody.Unavailable(fullDescription = null, error = detailedRes.error)
                )
            )
        }
        val detailed = (detailedRes as Result.Success).data

        val (templateRes, templateTime) = templateDef.await()
        logLegTimings(
            productId = id,
            brief = briefTime,
            detailed = detailedTime,
            template = templateTime,
            total = pageStart.elapsedNow()
        )

        val body = when (templateRes) {
            is Result.Error -> {
                // Detailed data exists but the category has no renderable template: keep the prose.
                logBodyUnavailable(id, "template=${brief.category}", templateRes.error)
                PdpBody.Unavailable(
                    fullDescription = detailed.fullDescription,
                    error = templateRes.error
                )
            }

            is Result.Success -> {
                val uiBrief = UiBriefProduct(
                    id = brief.id,
                    name = brief.name,
                    price = brief.price,
                    imageUrl = brief.imageUrl,
                    category = brief.category
                )
                PdpBody.Ready(
                    template = bduiTemplateMapper.map(templateRes.data),
                    bindData = buildPdpBindData(uiBrief, detailed)
                )
            }
        }

        Result.Success(
            PdpData(
                brief = brief,
                galleryUrls = detailed.galleryUrls,
                body = body
            )
        )
    }

    private fun logBodyUnavailable(productId: String, leg: String, error: NetworkError) {
        logger.warn(
            "PDP {} rendered without its BDUI body: {} leg failed with {}",
            productId,
            leg,
            error
        )
    }

    /**
     * Per-leg timings for the shell, kept after the Phase 2.5 split so the next round of the same
     * question has numbers: the shelf is gone from here, so `critical` now names whichever of the
     * remaining legs decides the page. Once the shared legs are served from Redis this is what
     * shows whether the BDUI template and bind data are worth deferring in turn.
     */
    private fun logLegTimings(
        productId: String,
        brief: Duration,
        detailed: Duration,
        template: Duration,
        total: Duration
    ) {
        if (!logger.isDebugEnabled) return
        val briefThenTemplate = brief + template
        val critical = listOf(
            "brief+template" to briefThenTemplate,
            "detailed" to detailed
        ).maxByOrNull { it.second }

        logger.debug(
            "PDP {} legs: brief={}ms detailed={}ms template={}ms " +
                    "| brief+template={}ms critical={} total={}ms",
            productId,
            brief.inWholeMilliseconds,
            detailed.inWholeMilliseconds,
            template.inWholeMilliseconds,
            briefThenTemplate.inWholeMilliseconds,
            critical?.first,
            total.inWholeMilliseconds
        )
    }

    override suspend fun getRecommendations(
        productId: String,
        cookies: Cookies
    ): Result<List<BriefProduct>, NetworkError> {
        return productDetailRepository.getRecommendations(productId, cookies)
    }

    override suspend fun addFavorite(
        productId: String,
        cookies: Cookies,
        interaction: InteractionContext
    ): Result<Unit, NetworkError> {
        return productDetailRepository.addFavorite(productId, cookies, interaction)
    }

    override suspend fun removeFavorite(
        productId: String,
        cookies: Cookies,
        interaction: InteractionContext
    ): Result<Unit, NetworkError> {
        return productDetailRepository.removeFavorite(productId, cookies, interaction)
    }

    override suspend fun checkFavorite(
        productId: String,
        cookies: Cookies
    ): Result<Boolean, NetworkError> {
        return productDetailRepository.checkFavorite(productId, cookies)
    }
}
