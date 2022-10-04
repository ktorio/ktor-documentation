package com.example.dao

import com.example.models.*
import org.ehcache.config.builders.*
import org.ehcache.config.units.*
import org.ehcache.impl.config.persistence.*
import java.io.*

class DAOFacadeCacheImpl(
    private val delegate: DAOFacade,
    storagePath: File
) : DAOFacade {
    private val cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
        .with(CacheManagerPersistenceConfiguration(storagePath))
        .withCache(
            "articlesCache",
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                Int::class.javaObjectType,
                Article::class.java,
                ResourcePoolsBuilder.newResourcePoolsBuilder()
                    .heap(1000, EntryUnit.ENTRIES)
                    .offheap(10, MemoryUnit.MB)
                    .disk(100, MemoryUnit.MB, true)
            )
        )
        .build(true)

    private val articlesCache = cacheManager.getCache("articlesCache", Int::class.javaObjectType, Article::class.java)

    override suspend fun allArticles(): List<Article> =
        delegate.allArticles()

    override suspend fun article(id: Int): Article? =
        articlesCache[id]
            ?: delegate.article(id)
                .also { article -> articlesCache.put(id, article) }

    override suspend fun addNewArticle(title: String, body: String): Article? =
        delegate.addNewArticle(title, body)
            ?.also { article -> articlesCache.put(article.id, article) }

    override suspend fun editArticle(id: Int, title: String, body: String): Boolean {
        articlesCache.put(id, Article(id, title, body))
        return delegate.editArticle(id, title, body)
    }

    override suspend fun deleteArticle(id: Int): Boolean {
        articlesCache.remove(id)
        return delegate.deleteArticle(id)
    }
}
