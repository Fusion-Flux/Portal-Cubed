package com.fusionflux.portalcubed.optionslist;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ClientOnly
public class ButtonEntry extends ContainerObjectSelectionList.Entry<ButtonEntry> {
    private static final Font TEXT_RENDERER = Minecraft.getInstance().font;
    public final List<AbstractWidget> buttons;
    private final Component text;
    public final EntryInfo info;
    private final List<AbstractWidget> children = new ArrayList<>();
    public static final Map<AbstractWidget, Component> BUTTONS_WITH_TEXT = new HashMap<>();

    ButtonEntry(List<AbstractWidget> buttons, Component text, EntryInfo info) {
        if (!buttons.isEmpty()) BUTTONS_WITH_TEXT.put(buttons.get(0), text);
        this.buttons = buttons;
        this.text = text;
        this.info = info;
        children.addAll(buttons);
    }

    @Override
    public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        buttons.forEach(b -> {
            b.y = y;
            b.render(matrices, mouseX, mouseY, tickDelta);
        });
        if (text != null && (!text.getString().contains("spacer") || !buttons.isEmpty())) {
            if (info.centered)
                TEXT_RENDERER.drawShadow(matrices, text, Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2f - (TEXT_RENDERER.width(text) / 2f), y + 5, 0xFFFFFF);
            else GuiComponent.drawString(matrices, TEXT_RENDERER, text, 12, y + 5, 0xFFFFFF);
        }
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return children;
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        return children;
    }
}
