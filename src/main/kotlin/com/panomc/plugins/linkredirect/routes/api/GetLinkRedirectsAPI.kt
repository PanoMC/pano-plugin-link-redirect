package com.panomc.plugins.linkredirect.routes.api

import com.panomc.platform.annotation.Endpoint
import com.panomc.platform.db.DatabaseManager
import com.panomc.platform.model.*
import com.panomc.plugins.linkredirect.LinkRedirectPlugin
import com.panomc.plugins.linkredirect.db.dao.RedirectDao
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.json.schema.SchemaRepository

@Endpoint
class GetLinkRedirectsAPI(
    private val plugin: LinkRedirectPlugin,
    private val redirectDao: RedirectDao
) : Api() {
    override val paths = listOf(Path("/api/link-redirects", RouteType.GET))

    private val databaseManager: DatabaseManager by lazy {
        plugin.applicationContext.getBean(DatabaseManager::class.java)
    }

    override fun getValidationHandler(schemaRepository: SchemaRepository): ValidationHandler? = null

    override suspend fun handle(context: RoutingContext): Result {
        val sqlClient = databaseManager.getSqlClient()
        val redirects = redirectDao.getList(sqlClient)

        val response = redirects.map { redirect ->
            mapOf(
                "title" to redirect.title,
                "path" to redirect.path,
                "targetUrl" to redirect.targetUrl,
                "delay" to redirect.delay,
                "showIntermediatePage" to redirect.showIntermediatePage,
                "intermediatePageDesign" to redirect.intermediatePageDesign,
                "openInNewTab" to redirect.openInNewTab,
                "showInNavigation" to redirect.showInNavigation,
                "requireLogin" to redirect.requireLogin,
                "requirePermission" to redirect.requirePermission,
                "permissionNode" to redirect.permissionNode
            )
        }

        return Successful(mapOf("redirects" to response))
    }
}
