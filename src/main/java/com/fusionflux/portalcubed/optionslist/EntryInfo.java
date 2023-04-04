package com.fusionflux.portalcubed.optionslist;

import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.util.Map;

class EntryInfo {
    Field field;
    Object widget;
    int width;
    int max;
    boolean centered;
    Map.Entry<TextFieldWidget, Text> error;
    Object value;
    String tempValue;
    boolean inLimits = true;
    Class<? extends OptionsListBlockEntity> owner;
    Text name;
    int index;
    ClickableWidget colorButton;
}
