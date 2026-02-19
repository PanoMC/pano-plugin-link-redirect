package com.panomc.plugins.linkredirect.db.dao

import com.panomc.platform.db.Dao
import com.panomc.plugins.linkredirect.db.model.RedirectModel
import io.vertx.sqlclient.SqlClient

abstract class RedirectDao : Dao<RedirectModel>(RedirectModel::class.java) {
    abstract suspend fun add(redirect: RedirectModel, sqlClient: SqlClient): Long
    abstract suspend fun update(redirect: RedirectModel, sqlClient: SqlClient)
    abstract suspend fun deleteById(id: Long, sqlClient: SqlClient)
    abstract suspend fun getById(id: Long, sqlClient: SqlClient): RedirectModel?
    abstract suspend fun getByPath(path: String, sqlClient: SqlClient): RedirectModel?
    abstract suspend fun getAll(page: Int, sqlClient: SqlClient): List<RedirectModel>
    abstract suspend fun getList(sqlClient: SqlClient): List<RedirectModel>
    abstract suspend fun count(sqlClient: SqlClient): Long
}
