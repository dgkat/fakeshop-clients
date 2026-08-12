package org.example.fakeshop_clients.features.productDetailPage.domain

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result

import org.example.fakeshop_clients.core.presentation.models.UiBriefProduct
import org.example.fakeshop_clients.features.bdui.data.SSRBduiTemplateDatasource
import org.example.fakeshop_clients.features.bdui.data.mappers.DataToDomainBduiTemplateMapper
import org.example.fakeshop_clients.features.bdui.domain.buildPdpBindData
import org.example.fakeshop_clients.features.core.models.Cookies
import org.example.fakeshop_clients.features.productDetailPage.domain.models.PdpData

class ProductDetailServiceImpl(
    private val productDetailRepository: ProductDetailRepository,
    private val bduiTemplateDatasource: SSRBduiTemplateDatasource,
    private val bduiTemplateMapper: DataToDomainBduiTemplateMapper
) : ProductDetailService {

    override suspend fun getPdpData(
        id: String,
        cookies: Cookies
    ): Result<PdpData, NetworkError> = coroutineScope {
        val briefDef = async { productDetailRepository.getBriefProductById(id, cookies) }
        val detailedDef = async { productDetailRepository.getDetailedProductById(id, cookies) }

        val briefRes = briefDef.await()
        if (briefRes is Result.Error) return@coroutineScope Result.Error(briefRes.error)
        val brief = (briefRes as Result.Success).data

        val templateDef = async { bduiTemplateDatasource.getPdpTemplate(brief.category, cookies) }

        val detailedRes = detailedDef.await()
        if (detailedRes is Result.Error) return@coroutineScope Result.Error(detailedRes.error)
        val detailed = (detailedRes as Result.Success).data

        when (val templateRes = templateDef.await()) {
            is Result.Error -> Result.Error(templateRes.error)
            is Result.Success -> {
                val template = bduiTemplateMapper.map(templateRes.data)
                val uiBrief = UiBriefProduct(
                    id = brief.id,
                    name = brief.name,
                    price = brief.price,
                    imageUrl = brief.imageUrl,
                    category = brief.category
                )
                val bindData = buildPdpBindData(uiBrief, detailed)
                Result.Success(
                    PdpData(
                        brief = brief,
                        galleryUrls = detailed.galleryUrls,
                        template = template,
                        bindData = bindData
                    )
                )
            }
        }
    }

    override suspend fun addFavorite(
        productId: String,
        cookies: Cookies
    ): Result<Unit, NetworkError> {
        return productDetailRepository.addFavorite(productId, cookies)
    }

    override suspend fun removeFavorite(
        productId: String,
        cookies: Cookies
    ): Result<Unit, NetworkError> {
        return productDetailRepository.removeFavorite(productId, cookies)
    }

    override suspend fun checkFavorite(
        productId: String,
        cookies: Cookies
    ): Result<Boolean, NetworkError> {
        return productDetailRepository.checkFavorite(productId, cookies)
    }
}
