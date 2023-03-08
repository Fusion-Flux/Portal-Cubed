package com.fusionflux.portalcubed.optionslist;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ClientOnly
public class ButtonEntry extends ElementListWidget.Entry<ButtonEntry> {
    private static final TextRenderer TEXT_RENDERER = MinecraftClient.getInstance().textRenderer;
    public final List<ClickableWidget> buttons;
    private final Text text;
    public final EntryInfo info;
    private final List<ClickableWidget> children = new ArrayList<>();
    public static final Map<ClickableWidget, Text> BUTTONS_WITH_TEXT = new HashMap<>();

    ButtonEntry(List<ClickableWidget> buttons, Text text, EntryInfo info) {
        if (!buttons.isEmpty()) BUTTONS_WITH_TEXT.put(buttons.get(0), text);
        this.buttons = buttons;
        this.text = text;
        this.info = info;
        children.addAll(buttons);
    }

    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        buttons.forEach(b -> {
            b.y = y;
            b.render(matrices, mouseX, mouseY, tickDelta);
        });
        if (text != null && (!text.getString().contains("spacer") || !buttons.isEmpty())) {
            if (info.centered)
                TEXT_RENDERER.drawWithShadow(matrices, text, MinecraftClient.getInstance().getWindow().getScaledWidth() / 2f - (TEXT_RENDERER.getWidth(text) / 2f), y + 5, 0xFFFFFF);
            else DrawableHelper.drawTextWithShadow(matrices, TEXT_RENDERER, text, 12, y + 5, 0xFFFFFF);
        }
    }

    @Override
    public List<? extends Element> children() {
        return children;
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        return children;
    }
}
