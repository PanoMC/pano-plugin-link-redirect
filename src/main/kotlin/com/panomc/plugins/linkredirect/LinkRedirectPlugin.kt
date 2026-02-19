package com.panomc.plugins.linkredirect

import com.panomc.platform.api.PanoPlugin

class LinkRedirectPlugin : PanoPlugin() {
    private val pluginDatabaseManager by lazy {
        applicationContext.getBean(com.panomc.platform.api.PluginDatabaseManager::class.java)
    }

    private val setupManager by lazy {
        applicationContext.getBean(com.panomc.platform.setup.SetupManager::class.java)
    }

    private var isInitialized = false

    override suspend fun onStart() {
        logger.info("Starting...")
        startPlugin()
    }

    internal suspend fun startPlugin() {
        if (isInitialized) return
        
        if (!setupManager.isSetupDone()) {
            logger.info("Setup is not finished, waiting for setup completion...")
            return
        }

        pluginDatabaseManager.initialize(this)
        isInitialized = true
        logger.info("Started!")
    }

    override suspend fun onEnable() {
        logger.info("Enabled!")
    }

    override suspend fun onDisable() {
        isInitialized = false
    }

    override suspend fun onUninstall() {
        pluginDatabaseManager.uninstall(this)
    }
}

