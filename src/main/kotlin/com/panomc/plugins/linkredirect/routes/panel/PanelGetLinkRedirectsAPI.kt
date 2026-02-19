package com.panomc.plugins.linkredirect.routes.panel

import com.panomc.platform.annotation.Endpoint
import com.panomc.platform.auth.AuthProvider
import com.panomc.platform.db.DatabaseManager
import com.panomc.platform.model.*
import com.panomc.plugins.linkredirect.LinkRedirectPlugin
import com.panomc.plugins.linkredirect.db.dao.RedirectDao
import com.panomc.plugins.linkredirect.permission.ManageRedirectsPermission
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaRepository

@Endpoint
class PanelGetLinkRedirectsAPI(
    private val plugin: LinkRedirectPlugin,
    private val redirectDao: RedirectDao
) : PanelApi() {
    override val paths = listOf(Path("/api/panel/link-redirects", RouteType.GET))

    private val authProvider: AuthProvider by lazy {
        plugin.applicationContext.getBean(AuthProvider::class.java)
    }

    private val databaseManager: DatabaseManager by lazy {
        plugin.applicationContext.getBean(DatabaseManager::class.java)
    }

    override fun getValidationHandler(schemaRepository: SchemaRepository): ValidationHandler =
        ValidationHandlerBuilder.create(schemaRepository)
            .build()

    override suspend fun handle(context: RoutingContext): Result {
        authProvider.requirePermission(ManageRedirectsPermission(), context)

        val page = context.request().getParam("page")?.toIntOrNull() ?: 1
        val sqlClient = databaseManager.getSqlClient()
        
        val redirects = redirectDao.getAll(page, sqlClient)
        val totalCount = redirectDao.count(sqlClient)
        val totalPage = (totalCount + 9) / 10

        return Successful(
            mapOf(
                "redirects" to redirects,
                "totalCount" to totalCount,
                "totalPage" to totalPage,
                "page" to page
            )
        )
    }
}
