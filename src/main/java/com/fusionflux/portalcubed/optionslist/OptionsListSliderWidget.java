package com.fusionflux.portalcubed.optionslist;

import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import org.quiltmc.loader.api.minecraft.ClientOnly;

@ClientOnly
class OptionsListSliderWidget extends AbstractSliderButton {
    private final EntryInfo info;
    private final MidnightConfig.Entry e;

    OptionsListSliderWidget(int x, int y, int width, int height, Component text, double value, EntryInfo info) {
        super(x, y, width, height, text, value);
        this.e = info.field.getAnnotation(MidnightConfig.Entry.class);
        this.info = info;
    }

    @Override
    protected void updateMessage() {
        this.setMessage(Component.nullToEmpty(info.tempValue));
    }

    @Override
    protected void applyValue() {
        if (info.field.getType() == int.class)
            info.value = ((Number)(e.min() + value * (e.max() - e.min()))).intValue();
        else if (info.field.getType() == double.class)
            info.value = Math.round((e.min() + value * (e.max() - e.min())) * (double)e.precision()) / (double)e.precision();
        else if (info.field.getType() == float.class)
            info.value = Math.round((e.min() + value * (e.max() - e.min())) * (float)e.precision()) / (float)e.precision();
        info.tempValue = String.valueOf(info.value);
    }
}
