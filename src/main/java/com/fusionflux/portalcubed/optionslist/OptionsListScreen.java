package com.fusionflux.portalcubed.optionslist;

import com.fusionflux.portalcubed.packet.PortalCubedServerPackets;
import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.ScreenTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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
public class OptionsListScreen extends HandledScreen<OptionsListScreenHandler> {
    public final String translationPrefix;
    public final OptionsListBlockEntity target;
    public OptionsListListWidget list;
    public boolean reload = false;

    public OptionsListScreen(OptionsListScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        assert MinecraftClient.getInstance().world != null;
        final var entity = MinecraftClient.getInstance().world.getBlockEntity(handler.getAt());
        if (!(entity instanceof OptionsListBlockEntity ola)) {
            closeScreen();
            target = null;
            translationPrefix = null;
            return;
        }
        target = ola;
        this.translationPrefix = "optionslist." + target.getClass().getSimpleName() + ".";
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
        backgroundWidth = width;
        backgroundHeight = height;
        super.init();

        this.addDrawableChild(new ButtonWidget(
            this.width / 2 - 154, this.height - 28, 150, 20, ScreenTexts.CANCEL,
            button -> closeScreen()
        ));

        ButtonWidget done = this.addDrawableChild(new ButtonWidget(this.width / 2 + 4, this.height - 28, 150, 20, ScreenTexts.DONE, (button) -> {
            for (EntryInfo info : OptionsListData.getEntries(target)) {
                try {
                    info.field.set(target, info.value);
                } catch (IllegalAccessException ignored) {
                }
            }
            final PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(handler.getAt());
            buf.writeString(OptionsListData.write(target));
            ClientPlayNetworking.send(PortalCubedServerPackets.OPTIONS_LIST_CONFIGURE, buf);
            closeScreen();
        }));

        this.list = new OptionsListListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
        if (this.client != null && this.client.world != null) this.list.setRenderBackground(false);
        this.addSelectableChild(this.list);
        for (EntryInfo info : OptionsListData.getEntries(target)) {
            Text name = Objects.requireNonNullElseGet(info.name, () -> Text.translatable(translationPrefix + info.field.getName()));

            if (info.widget instanceof Map.Entry) {
                Map.Entry<ButtonWidget.PressAction, Function<Object, Text>> widget = (Map.Entry<ButtonWidget.PressAction, Function<Object, Text>>)info.widget;
                if (info.field.getType().isEnum())
                    widget.setValue(value -> Text.translatable(translationPrefix + "enum." + info.field.getType().getSimpleName() + "." + info.value.toString()));
                this.list.addButton(List.of(new ButtonWidget(width - 160, 0, 150, 20, widget.getValue().apply(info.value), widget.getKey())), name, info);
            } else if (info.field.getType() == List.class) {
                if (!reload) info.index = 0;
                TextFieldWidget widget = new TextFieldWidget(textRenderer, width - 160, 0, 150, 20, Text.empty());
                widget.setMaxLength(info.width);
                if (info.index < ((List<String>)info.value).size())
                    widget.setText((String.valueOf(((List<String>)info.value).get(info.index))));
                Predicate<String> processor = ((BiFunction<TextFieldWidget, ButtonWidget, Predicate<String>>)info.widget).apply(widget, done);
                widget.setTextPredicate(processor);
                ButtonWidget cycleButton = new ButtonWidget(width - 185, 0, 20, 20, Text.literal(String.valueOf(info.index)).formatted(Formatting.GOLD), (button -> {
                    ((List<String>)info.value).remove("");
                    double scrollAmount = list.getScrollAmount();
                    this.reload = true;
                    info.index = info.index + 1;
                    if (info.index > ((List<String>)info.value).size()) info.index = 0;
                    Objects.requireNonNull(client).setScreen(this);
                    list.setScrollAmount(scrollAmount);
                }));
                this.list.addButton(List.of(widget, cycleButton), name, info);
            } else if (info.widget != null) {
                ClickableWidget widget;
                MidnightConfig.Entry e = info.field.getAnnotation(MidnightConfig.Entry.class);
                if (e.isSlider())
                    widget = new OptionsListSliderWidget(width - 160, 0, 150, 20, Text.of(info.tempValue), (Double.parseDouble(info.tempValue) - e.min()) / (e.max() - e.min()), info);
                else widget = new TextFieldWidget(textRenderer, width - 160, 0, 150, 20, null, Text.of(info.tempValue));
                if (widget instanceof TextFieldWidget textField) {
                    textField.setMaxLength(info.width);
                    textField.setText(info.tempValue);
                    Predicate<String> processor = ((BiFunction<TextFieldWidget, ButtonWidget, Predicate<String>>)info.widget).apply(textField, done);
                    textField.setTextPredicate(processor);
                }
                if (e.isColor()) {
                    ButtonWidget colorButton = new ButtonWidget(width - 185, 0, 20, 20, Text.literal("⬛"), (button -> {
                    }));
                    try {
                        colorButton.setMessage(Text.literal("⬛").setStyle(Style.EMPTY.withColor(Color.decode(info.tempValue).getRGB())));
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
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.list.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, textRenderer, title, width / 2, 15, 0xFFFFFF);

        for (EntryInfo info : OptionsListData.getEntries(target)) {
            if (list.getHoveredButton(mouseX, mouseY).isPresent()) {
                ClickableWidget buttonWidget = list.getHoveredButton(mouseX, mouseY).get();
                Text text = ButtonEntry.BUTTONS_WITH_TEXT.get(buttonWidget);
                Text name = Text.translatable(this.translationPrefix + info.field.getName());
                String key = translationPrefix + info.field.getName() + ".tooltip";

                if (info.error != null && text.equals(name))
                    renderTooltip(matrices, info.error.getValue(), mouseX, mouseY);
                else if (I18n.hasTranslation(key) && text.equals(name)) {
                    List<Text> list = new ArrayList<>();
                    for (String str : I18n.translate(key).split("\n"))
                        list.add(Text.literal(str));
                    renderTooltip(matrices, list, mouseX, mouseY);
                }
            }
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
    }
}
