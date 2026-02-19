package com.panomc.plugins.linkredirect.db.model

import com.panomc.platform.db.DBEntity

data class RedirectModel(
    val id: Long = -1L,
    val title: String,
    val path: String,
    val targetUrl: String,
    val delay: Int = 0,
    val showIntermediatePage: Boolean = false,
    val intermediatePageDesign: String? = null,
    val useCustomPage: Boolean = false,
    val openInNewTab: Boolean = false,
    val showInNavigation: Boolean = false,
    val requireLogin: Boolean = false,
    val requirePermission: Boolean = false,
    val permissionNode: String? = null,
    val htmlContent: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
): DBEntity()
