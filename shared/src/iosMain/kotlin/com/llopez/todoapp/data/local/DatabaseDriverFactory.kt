package com.llopez.todoapp.data.local

import com.llopez.todoapp.database.NoteDatabase
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual class DatabaseDriverFactory {
    actual fun createDriver() : SqlDriver {
        return NativeSqliteDriver(NoteDatabase.Schema, "name.db")
    }
}