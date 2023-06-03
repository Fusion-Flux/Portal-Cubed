package com.fusionflux.portalcubed.optionslist;

import com.fusionflux.portalcubed.packet.PortalCubedServerPackets;
import com.mojang.blaze3d.vertex.PoseStack;
import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

@ClientOnly
public class OptionsListScreen extends AbstractContainerScreen<OptionsListScreenHandler> {
    public final String translationPrefix;
    public final OptionsListBlockEntity target;
    public OptionsListListWidget list;
    public boolean reload = false;

    public OptionsListScreen(OptionsListScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        assert Minecraft.getInstance().level != null;
        final var entity = Minecraft.getInstance().level.getBlockEntity(handler.getAt());
        if (!(entity instanceof OptionsListBlockEntity ola)) {
            onClose();
            target = null;
            translationPrefix = null;
            return;
        }
        target = ola;
        //noinspection DataFlowIssue
        this.translationPrefix = "optionslist." + Registry.BLOCK_ENTITY_TYPE.getKey(ola.getType()).toString().replace(':', '.') + ".";
        for (final EntryInfo entry : OptionsListData.getEntries(target)) {
            try {
                entry.value = entry.field.get(target);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        imageWidth = width;
        imageHeight = height;
        super.init();

        Button done = this.addRenderableWidget(new Button(this.width / 2 - 75, this.height - 28, 150, 20, CommonComponents.GUI_DONE, (button) -> {
            for (EntryInfo info : OptionsListData.getEntries(target)) {
                try {
                    info.field.set(target, info.value);
                } catch (IllegalAccessException ignored) {
                }
            }
            onClose();
        }));

        this.list = new OptionsListListWidget(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        if (this.minecraft != null && this.minecraft.level != null) this.list.setRenderBackground(false);
        this.addWidget(this.list);
        for (EntryInfo info : OptionsListData.getEntries(target)) {
            Component name = Objects.requireNonNullElseGet(info.name, () -> Component.translatable(translationPrefix + info.field.getName()));

            if (info.widget instanceof Map.Entry) {
                Map.Entry<Button.OnPress, Function<Object, Component>> widget = (Map.Entry<Button.OnPress, Function<Object, Component>>)info.widget;
                if (info.field.getType().isEnum())
                    widget.setValue(value -> Component.translatable(translationPrefix + "enum." + info.field.getType().getSimpleName() + "." + info.value.toString()));
                this.list.addButton(List.of(new Button(width - 160, 0, 150, 20, widget.getValue().apply(info.value), widget.getKey())), name, info);
            } else if (info.field.getType() == List.class) {
                if (!reload) info.index = 0;
                EditBox widget = new EditBox(font, width - 160, 0, 150, 20, Component.empty());
                widget.setMaxLength(info.width);
                if (info.index < ((List<String>)info.value).size())
                    widget.setValue((String.valueOf(((List<String>)info.value).get(info.index))));
                Predicate<String> processor = ((BiFunction<EditBox, Button, Predicate<String>>)info.widget).apply(widget, done);
                widget.setFilter(processor);
                Button cycleButton = new Button(width - 185, 0, 20, 20, Component.literal(String.valueOf(info.index)).withStyle(ChatFormatting.GOLD), (button -> {
                    ((List<String>)info.value).remove("");
                    double scrollAmount = list.getScrollAmount();
                    this.reload = true;
                    info.index = info.index + 1;
                    if (info.index > ((List<String>)info.value).size()) info.index = 0;
                    Objects.requireNonNull(minecraft).setScreen(this);
                    list.setScrollAmount(scrollAmount);
                }));
                this.list.addButton(List.of(widget, cycleButton), name, info);
            } else if (info.widget != null) {
                AbstractWidget widget;
                MidnightConfig.Entry e = info.field.getAnnotation(MidnightConfig.Entry.class);
                if (e.isSlider())
                    widget = new OptionsListSliderWidget(width - 160, 0, 150, 20, Component.nullToEmpty(info.tempValue), (Double.parseDouble(info.tempValue) - e.min()) / (e.max() - e.min()), info);
                else widget = new EditBox(font, width - 160, 0, 150, 20, null, Component.nullToEmpty(info.tempValue));
                if (widget instanceof EditBox textField) {
                    textField.setMaxLength(info.width);
                    textField.setValue(info.tempValue);
                    Predicate<String> processor = ((BiFunction<EditBox, Button, Predicate<String>>)info.widget).apply(textField, done);
                    textField.setFilter(processor);
                }
                if (e.isColor()) {
                    Button colorButton = new Button(width - 185, 0, 20, 20, Component.literal("⬛"), (button -> {
                    }));
                    try {
                        colorButton.setMessage(Component.literal("⬛").setStyle(Style.EMPTY.withColor(Color.decode(info.tempValue).getRGB())));
                    } catch (Exception ignored) {
                    }
                    info.colorButton = colorButton;
                    colorButton.active = false;
                    this.list.addButton(List.of(widget, colorButton), name, info);
                } else {
                    this.list.addButton(List.of(widget), name, info);
                }
            } else {
                this.list.addButton(List.of(), name, info);
            }
        }

    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.list.render(matrices, mouseX, mouseY, delta);
        drawCenteredString(matrices, font, title, width / 2, 15, 0xFFFFFF);

        for (EntryInfo info : OptionsListData.getEntries(target)) {
            if (list.getHoveredButton(mouseX, mouseY).isPresent()) {
                AbstractWidget buttonWidget = list.getHoveredButton(mouseX, mouseY).get();
                Component text = ButtonEntry.BUTTONS_WITH_TEXT.get(buttonWidget);
                Component name = Component.translatable(this.translationPrefix + info.field.getName());
                String key = translationPrefix + info.field.getName() + ".tooltip";

                if (info.error != null && text.equals(name))
                    renderTooltip(matrices, info.error.getValue(), mouseX, mouseY);
                else if (I18n.exists(key) && text.equals(name)) {
                    List<Component> list = new ArrayList<>();
                    for (String str : I18n.get(key).split("\n"))
                        list.add(Component.literal(str));
                    renderComponentTooltip(matrices, list, mouseX, mouseY);
                }
            }
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        final FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(menu.getAt());
        buf.writeUtf(OptionsListData.write(target));
        ClientPlayNetworking.send(PortalCubedServerPackets.OPTIONS_LIST_CONFIGURE, buf);
        super.onClose();
    }

    @Override
    protected void renderLabels(PoseStack matrices, int mouseX, int mouseY) {
    }

    @Override
    protected void renderBg(PoseStack matrices, float delta, int mouseX, int mouseY) {
    }
}
