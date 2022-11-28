package com.llopez.todoapp.android.note_list

import com.llopez.todoapp.domain.note.Note

data class NoteListState(
    val notes: List<Note> = emptyList(),
    val searchText: String = "",
    val isSearchActivate: Boolean = false
);