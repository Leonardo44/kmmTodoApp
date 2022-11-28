package com.llopez.todoapp.android.note_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.llopez.todoapp.android.note_list.NoteListState
import com.llopez.todoapp.domain.note.Note
import com.llopez.todoapp.domain.note.NoteDataSource
import com.llopez.todoapp.domain.time.DateTimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val noteDataSource: NoteDataSource,
    private  val savedStateHandler: SavedStateHandle
): ViewModel() {
    private val noteTitle = savedStateHandler.getStateFlow("noteTitle", "")
    private val isNoteTitleTextFocused = savedStateHandler.getStateFlow("isNoteTitleTextFocused", false)
    private val noteContent = savedStateHandler.getStateFlow("noteContent", "")
    private val isNoteContentFocused = savedStateHandler.getStateFlow("isNoteContentFocused", false)
    private val noteColor = savedStateHandler.getStateFlow("noteColor", Note.generateRandomColors())
    val state = combine(noteTitle, isNoteTitleTextFocused, noteContent, isNoteContentFocused, noteColor) {noteTitle, isNoteTitleTextFocused, noteContent, isNoteContentFocused, noteColor ->
        NoteDetailState(
            noteTitle = noteTitle,
            isNoteTitleHintVisible = noteTitle.isEmpty() && !isNoteTitleTextFocused,
            noteContent = noteContent,
            isNoteContentHintVisible = noteContent.isEmpty() && !isNoteContentFocused,
            noteColor = noteColor
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NoteDetailState())
    private val _hasNoteBeenSaved = MutableStateFlow(false)
    val hasNoteBeenSaved = _hasNoteBeenSaved.asStateFlow()
    private var existingNoteId: Long? = null

    init {
        savedStateHandler.get<Long>("noteId")?.let { existingNoteId ->
            if (existingNoteId == -1L) {
                return@let
            }
            this.existingNoteId = existingNoteId

            viewModelScope.launch {
                noteDataSource.getNoteById(existingNoteId)?.let { note ->
                    savedStateHandler["noteTitle"] = note.title
                    savedStateHandler["noteContent"] = note.content
                    savedStateHandler["noteColor"] = note.colorHex
                }
            }
        }
    }

    fun onNoteTitleChanged(text: String) {
        savedStateHandler["noteTitle"] = text
    }

    fun onNoteContentChanged(text: String) {
        savedStateHandler["noteContent"] = text
    }

    fun onNoteTitleFocusChanged(isFocused: Boolean) {
        savedStateHandler["isNoteTitleFocused"] = isFocused
    }

    fun onNoteContentFocusChanged(isFocused: Boolean) {
        savedStateHandler["isNoteContentFocused"] = isFocused
    }

    fun saveNote() {
        viewModelScope.launch {
            noteDataSource.insertNote(
                Note(
                    id = existingNoteId,
                    title = noteTitle.value,
                    content = noteContent.value,
                    colorHex =  noteColor.value,
                    created = DateTimeUtil.now()
                )
            )
            _hasNoteBeenSaved.value = true
        }
    }
}