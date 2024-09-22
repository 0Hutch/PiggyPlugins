package com.spinplugins.IronBuddy;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.input.KeyListener;
import java.awt.event.KeyEvent;

@Slf4j
public class KeypressListener implements KeyListener {
    @Override
    public void keyTyped(KeyEvent e) {
        logkey("keyTyped", e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        logkey("keyPressed", e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        logkey("keyReleased", e);
    }

    private void logkey(String event, KeyEvent e){
        log.debug("{}; ID: {}; When: {}; Mods: {}; Code: {}; Char: {}; Loc: {}", event, e.getID(), e.getWhen(), e.getModifiersEx(), e.getKeyCode(), e.getKeyChar(), e.getKeyLocation());
    }
}

