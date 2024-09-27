package com.bankmemory.util;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class OnAnyChangeDocumentListener implements DocumentListener {
    @Override
    public void insertUpdate(DocumentEvent e) {
        onChange(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        onChange(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        onChange(e);
    }

    public abstract void onChange(DocumentEvent e);
}
