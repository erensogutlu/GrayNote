package com.javal.graynote.listeners;

import com.javal.graynote.entities.Note;

public interface NotesListener {
    void OnNoteClicked(Note note, int position);
    // https://github.com/erensogutlu
}
