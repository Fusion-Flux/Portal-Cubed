package com.fusionflux.portalcubed.optionslist;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.text.Text;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import java.util.List;
import java.util.Optional;

@ClientOnly
class OptionsListListWidget extends ElementListWidget<ButtonEntry> {
    TextRenderer textRenderer;

    OptionsListListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
        super(minecraftClient, i, j, k, l, m);
        this.centerListVertically = false;
        textRenderer = minecraftClient.textRenderer;
    }

    @Override
    public int getScrollbarPositionX() {
        return this.width - 7;
    }

    public void addButton(List<ClickableWidget> buttons, Text text, EntryInfo info) {
        this.addEntry(new ButtonEntry(buttons, text, info));
    }

    @Override
    public int getRowWidth() {
        return 10000;
    }

    public Optional<ClickableWidget> getHoveredButton(double mouseX, double mouseY) {
        for (ButtonEntry buttonEntry : this.children()) {
            if (!buttonEntry.buttons.isEmpty() && buttonEntry.buttons.get(0).isMouseOver(mouseX, mouseY)) {
                return Optional.of(buttonEntry.buttons.get(0));
            }
        }
        return Optional.empty();
    }
}
