package com.example.chessapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class PlayerProfile(
    val id: String,
    val username: String,
    val createdAt: Long,
    val updatedAt: Long
)

class PlayerProfileDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_PLAYERS (
                $COLUMN_ID TEXT PRIMARY KEY,
                $COLUMN_USERNAME TEXT NOT NULL,
                $COLUMN_CREATED_AT INTEGER NOT NULL,
                $COLUMN_UPDATED_AT INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL(
                """
                CREATE TABLE ${TABLE_PLAYERS}_new (
                    $COLUMN_ID TEXT PRIMARY KEY,
                    $COLUMN_USERNAME TEXT NOT NULL,
                    $COLUMN_CREATED_AT INTEGER NOT NULL,
                    $COLUMN_UPDATED_AT INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO ${TABLE_PLAYERS}_new (
                    $COLUMN_ID,
                    $COLUMN_USERNAME,
                    $COLUMN_CREATED_AT,
                    $COLUMN_UPDATED_AT
                )
                SELECT
                    $COLUMN_ID,
                    $COLUMN_USERNAME,
                    $COLUMN_CREATED_AT,
                    $COLUMN_UPDATED_AT
                FROM $TABLE_PLAYERS
                """.trimIndent()
            )
            db.execSQL("DROP TABLE $TABLE_PLAYERS")
            db.execSQL("ALTER TABLE ${TABLE_PLAYERS}_new RENAME TO $TABLE_PLAYERS")
        } else {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_PLAYERS")
            onCreate(db)
        }
    }

    companion object {
        const val DATABASE_NAME = "pawn_blade_players.db"
        const val DATABASE_VERSION = 2

        const val TABLE_PLAYERS = "players"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_UPDATED_AT = "updated_at"
    }
}

class PlayerProfileRepository(context: Context) {
    private val databaseHelper = PlayerProfileDatabaseHelper(context.applicationContext)

    fun getLocalPlayerProfile(): PlayerProfile? {
        val db = databaseHelper.readableDatabase
        val cursor = db.query(
            PlayerProfileDatabaseHelper.TABLE_PLAYERS,
            null,
            null,
            null,
            null,
            null,
            "${PlayerProfileDatabaseHelper.COLUMN_UPDATED_AT} DESC",
            "1"
        )

        return cursor.use {
            if (!it.moveToFirst()) {
                null
            } else {
                PlayerProfile(
                    id = it.getString(it.getColumnIndexOrThrow(PlayerProfileDatabaseHelper.COLUMN_ID)),
                    username = it.getString(it.getColumnIndexOrThrow(PlayerProfileDatabaseHelper.COLUMN_USERNAME)),
                    createdAt = it.getLong(it.getColumnIndexOrThrow(PlayerProfileDatabaseHelper.COLUMN_CREATED_AT)),
                    updatedAt = it.getLong(it.getColumnIndexOrThrow(PlayerProfileDatabaseHelper.COLUMN_UPDATED_AT))
                )
            }
        }
    }

    fun saveLocalPlayerProfile(profile: PlayerProfile) {
        val values = ContentValues().apply {
            put(PlayerProfileDatabaseHelper.COLUMN_ID, profile.id)
            put(PlayerProfileDatabaseHelper.COLUMN_USERNAME, profile.username)
            put(PlayerProfileDatabaseHelper.COLUMN_CREATED_AT, profile.createdAt)
            put(PlayerProfileDatabaseHelper.COLUMN_UPDATED_AT, profile.updatedAt)
        }

        databaseHelper.writableDatabase.insertWithOnConflict(
            PlayerProfileDatabaseHelper.TABLE_PLAYERS,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }
}
