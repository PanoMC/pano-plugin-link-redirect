package com.panomc.plugins.linkredirect.db.migration

import com.panomc.platform.annotation.Migration
import com.panomc.platform.db.DatabaseMigration
import com.panomc.plugins.linkredirect.db.dao.LinkRedirectDao
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.sqlclient.SqlClient

@Migration
class LinkRedirectMigration1to2(
    private val linkRedirectDao: LinkRedirectDao
) : DatabaseMigration(1, 2, "Rename redirect_model table to link_redirect") {
    override val handlers: List<suspend (SqlClient) -> Unit> = listOf(
        renameTable()
    )

    private fun renameTable(): suspend (sqlClient: SqlClient) -> Unit =
        { sqlClient: SqlClient ->
            val oldTableName = "${linkRedirectDao.getTablePrefix()}redirect_model"
            val newTableName = "${linkRedirectDao.getTablePrefix()}link_redirect"
            
            try {
                // Determine if old table exists by attempting to rename it. 
                // We drop the potentially auto-created empty new table first.
                sqlClient.query("DROP TABLE IF EXISTS `$newTableName`").execute().coAwait()
                sqlClient.query("RENAME TABLE `$oldTableName` TO `$newTableName`").execute().coAwait()
            } catch (e: Exception) {
                // If old table doesn't exist, or any error occurs, we handle it gracefully.
                e.printStackTrace()
            }
            
            try {
                val schemeTableName = "${linkRedirectDao.getTablePrefix()}scheme_version"
                sqlClient.query("DELETE FROM `$schemeTableName` WHERE `pluginId` = 'pano-plugin-link-redirect'").execute().coAwait()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
}
