package com.fusionflux.portalcubed.optionslist;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.util.Map;

class EntryInfo {
    Field field;
    Object widget;
    int width;
    int max;
    boolean centered;
    Map.Entry<EditBox, Component> error;
    Object value;
    String tempValue;
    boolean inLimits = true;
    Class<? extends OptionsListBlockEntity> owner;
    Component name;
    int index;
    AbstractWidget colorButton;
}
