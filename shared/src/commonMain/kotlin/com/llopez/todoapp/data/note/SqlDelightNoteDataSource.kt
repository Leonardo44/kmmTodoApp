package com.llopez.todoapp.data.note

import com.llopez.todoapp.database.NoteDatabase
import com.llopez.todoapp.domain.note.Note
import com.llopez.todoapp.domain.note.NoteDataSource
import com.llopez.todoapp.domain.time.DateTimeUtil

class SqlDelightNoteDataSource(db: NoteDatabase): NoteDataSource {
    private  val queries = db.noteQueries

    override suspend fun insertNote(note: Note) {
        queries.insertOrReplaceNote(
            note.id,
            note.title,
            note.content,
            note.colorHex,
            DateTimeUtil.toEpochMillis(note.created)
        )
    }

    override suspend fun getNoteById(id: Long): Note? {
       return queries
           .getNoteById(id)
           .executeAsOneOrNull()
           ?.toNote()
    }

    override suspend fun getAllNotes(): List<Note> {
        return queries
            .getAllNotes()
            .executeAsList()
            .map { it.toNote() }
    }

    override suspend fun deleteNoteById(id: Long) {
        queries.deleteNoteById(id)
    }

}