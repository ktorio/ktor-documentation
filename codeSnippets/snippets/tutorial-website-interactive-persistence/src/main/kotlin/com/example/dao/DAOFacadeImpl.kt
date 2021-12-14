package com.example.dao

import com.example.dao.DatabaseFactory.dbQuery
import com.example.models.*
import org.jetbrains.exposed.sql.*

class DAOFacadeImpl : DAOFacade {
    override suspend fun allArticles(): List<Article> = dbQuery {
        Articles.selectAll().map(::toArticle)
    }

    override suspend fun article(id: Int): Article? = dbQuery {
        Articles
            .select { Articles.id eq id }
            .map(::toArticle)
            .singleOrNull()
    }

    override suspend fun addNewArticle(title: String, body: String): Article? = dbQuery {
        val insertStatement = Articles.insert {
            it[Articles.title] = title
            it[Articles.body] = body
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::toArticle)
    }

    override suspend fun editArticle(id: Int, title: String, body: String): Boolean = dbQuery {
        Articles.update({ Articles.id eq id }) {
            it[Articles.title] = title
            it[Articles.body] = body
        } > 0
    }

    override suspend fun deleteArticle(id: Int): Boolean = dbQuery {
        Articles.deleteWhere { Articles.id eq id } > 0
    }

    private fun toArticle(row: ResultRow) = Article(
        id = row[Articles.id],
        title = row[Articles.title],
        body = row[Articles.body],
    )
}
