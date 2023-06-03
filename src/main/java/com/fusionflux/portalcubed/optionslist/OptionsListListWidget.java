package com.fusionflux.portalcubed.optionslist;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.network.chat.Component;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import java.util.List;
import java.util.Optional;

@ClientOnly
class OptionsListListWidget extends ContainerObjectSelectionList<ButtonEntry> {
    Font textRenderer;

    OptionsListListWidget(Minecraft minecraftClient, int i, int j, int k, int l, int m) {
        super(minecraftClient, i, j, k, l, m);
        this.centerListVertically = false;
        textRenderer = minecraftClient.font;
    }

    @Override
    public int getScrollbarPosition() {
        return this.width - 7;
    }

    public void addButton(List<AbstractWidget> buttons, Component text, EntryInfo info) {
        this.addEntry(new ButtonEntry(buttons, text, info));
    }

    @Override
    public int getRowWidth() {
        return 10000;
    }

    public Optional<AbstractWidget> getHoveredButton(double mouseX, double mouseY) {
        for (ButtonEntry buttonEntry : this.children()) {
            if (!buttonEntry.buttons.isEmpty() && buttonEntry.buttons.get(0).isMouseOver(mouseX, mouseY)) {
                return Optional.of(buttonEntry.buttons.get(0));
            }
        }
        return Optional.empty();
    }
}
