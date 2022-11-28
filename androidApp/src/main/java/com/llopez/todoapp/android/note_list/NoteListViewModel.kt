package com.llopez.todoapp.android.note_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.llopez.todoapp.domain.note.Note
import com.llopez.todoapp.domain.note.NoteDataSource
import com.llopez.todoapp.domain.note.SearchNotes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private  val noteDataSource: NoteDataSource,
    private  val savedStateHandler: SavedStateHandle
): ViewModel() {
    private val searchNotes = SearchNotes()
    private val notes = savedStateHandler.getStateFlow("notes", emptyList<Note>())
    private val searchText = savedStateHandler.getStateFlow("searchText", "")
    private val isSearchActive = savedStateHandler.getStateFlow("isSearchActive", false)
    val state = combine(notes, searchText, isSearchActive) { notes, searchText, isSearchActive ->
        NoteListState(
            notes = searchNotes.execute(notes, searchText),
            isSearchActivate = isSearchActive
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NoteListState())


    init {
    }

    fun loadNotes() {
        viewModelScope.launch {
            savedStateHandler["notes"] = noteDataSource.getAllNotes()
        }
    }

    fun onSearchTextChange(text: String) {
        savedStateHandler["searchText"] = text
    }

    fun onToggleSearch() {
        savedStateHandler["isSearchActive"] = !isSearchActive.value

        if (!isSearchActive.value) {
            savedStateHandler["searchText"] = ""
        }
    }

    fun deleteNoteById(id: Long) {
        viewModelScope.launch {
            noteDataSource.deleteNoteById(id)
            loadNotes()
        }
    }


}