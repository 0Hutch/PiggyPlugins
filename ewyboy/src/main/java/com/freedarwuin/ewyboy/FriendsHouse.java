package com.freedarwuin.ewyboy;

import net.runelite.api.Client;
import net.runelite.api.FontID;
import net.runelite.api.VarClientStr;
import net.runelite.api.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class FriendsHouse {

    private static final Logger log = LoggerFactory.getLogger(FriendsHouse.class);
    private final Client client;
    private Widget fill;

    public FriendsHouse(Widget parent, Client client, boolean hasLastInput) {
        this.client = client;

        if (parent == null) {
            return;
        }

        fill = parent.createChild(WidgetType.TEXT);
        prep(fill, (parent.getWidth() / 2), hasLastInput);
    }

    private void prep(Widget widget, int parentWidth, boolean hasLastInput) {
        widget.setTextColor(0x800000);
        widget.setFontId(FontID.VERDANA_11);

        // Set the original dimensions of the widget
        widget.setOriginalHeight(20);
        widget.setOriginalWidth(100);  // Ensure this matches your desired width

        // Calculate the x position to center the widget
        int x = hasLastInput
                ? 10
                : parentWidth - (widget.getOriginalWidth() / 2);

        var y = hasLastInput
                ? 110
                : 5;

        // Set the widget position
        widget.setOriginalX(x);
        widget.setOriginalY(y);

        // Set the width and height modes
        widget.setWidthMode(WidgetSizeMode.ABSOLUTE);
        widget.setYPositionMode(WidgetPositionMode.ABSOLUTE_TOP);

        // Center the text
        widget.setXTextAlignment(WidgetTextAlignment.CENTER);

        widget.setHasListener(true);
        widget.setOnMouseRepeatListener((JavaScriptCallback) ev -> widget.setTextColor(0xFFFFFF));
        widget.setOnMouseLeaveListener((JavaScriptCallback) ev -> widget.setTextColor(0x800000));

        widget.revalidate();
    }

    public void setInputText(String name) {
        fill.setText("<col=000000>Name:</col> " + name);
        fill.setAction(0, name);
        fill.setOnOpListener((JavaScriptCallback) ev -> {
            Objects.requireNonNull(client.getWidget(ComponentID.CHATBOX_FULL_INPUT)).setText(name);
            client.setVarcStrValue(VarClientStr.INPUT_TEXT, name);
        });
        fill.revalidate();
    }

}
