package com.mauquoi.money.cache

import org.ehcache.event.CacheEvent
import org.ehcache.event.CacheEventListener
import org.slf4j.LoggerFactory

class SimpleCacheListener : CacheEventListener<Any, Any> {
    override fun onEvent(cacheEvent: CacheEvent<out Any, out Any>) {
        LOGGER.info("${cacheEvent.key}: Replaced value ${cacheEvent.oldValue} with ${cacheEvent.newValue} because " +
                "of event ${cacheEvent.type}")
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SimpleCacheListener::class.java)
    }
}