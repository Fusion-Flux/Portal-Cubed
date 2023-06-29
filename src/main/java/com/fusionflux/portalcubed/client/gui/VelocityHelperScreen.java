package com.fusionflux.portalcubed.client.gui;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.VelocityHelperBlock;
import com.fusionflux.portalcubed.blocks.blockentities.VelocityHelperBlockEntity;
import com.fusionflux.portalcubed.gui.VelocityHelperScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.objecthunter.exp4j.Expression;
import org.joml.Math;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.function.Consumer;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class VelocityHelperScreen extends AbstractContainerScreen<VelocityHelperScreenHandler> {
    private static final ResourceLocation TEXTURE = id("textures/gui/velocity_helper.png");

    private final VelocityHelperBlockEntity entity;

    private EditBox flightDurationWidget;
    private ExpressionFieldWidget conditionWidget, icWidget;
    private Button doneButton;

    public VelocityHelperScreen(VelocityHelperScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        imageWidth = 248;
        imageHeight = 166;
        assert Minecraft.getInstance().level != null;
        entity = Minecraft.getInstance().level.getBlockEntity(handler.getAt(), PortalCubedBlocks.VELOCITY_HELPER_BLOCK_ENTITY).orElse(null);
        if (entity == null) {
            onClose();
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, 0x404040);
        drawTextRightAligned(
            graphics, font,
            Component.translatable("portalcubed.velocity_helper.flight_duration"),
            139, 23, 0x404040
        );
        drawTextCentered(
            graphics, font,
            Component.translatable("portalcubed.velocity_helper.condition"),
            imageWidth / 2, 39, 0x404040
        );
        drawTextCentered(
            graphics, font,
            Component.translatable("portalcubed.velocity_helper.interpolation_curve"),
            imageWidth / 2, 71, 0x404040
        );
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);

        final Expression curve = icWidget.getExpression();
        if (curve != null) {
            final String flightDurationString = flightDurationWidget.getValue();
            if (!flightDurationString.isEmpty()) {
                final int pointCount = Math.min(Integer.parseInt(flightDurationString), 217);
                final float spacing = 217f / pointCount;
                float last = 0;
                float x = this.leftPos + 17 + spacing;
                try {
                    for (int i = 1; i <= pointCount; i++, x += spacing) {
                        curve.setVariable("x", 1.0 / pointCount * i);
                        final float calculation = (float)Mth.clamp(curve.evaluate(), 0, 1) * 44;
                        drawLine(graphics, x - spacing, this.topPos + 157 - last, x, this.topPos + 157 - calculation, 0xffffffff);
                        last = calculation;
                    }
                } catch (RuntimeException e) {
                    icWidget.setError(ExpressionFieldWidget.cleanError(e));
                }
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        flightDurationWidget = createTextField(141, 17, 42, w -> {
            w.setFilter(s -> {
                if (s.isEmpty()) return true;
                try {
                    return Integer.parseInt(s) > 0;
                } catch (NumberFormatException e) {
                    return false;
                }
            });
            w.setValue(Integer.toString(entity.getFlightDuration()));
        });
        conditionWidget = createExpressionField(37, 49, 174, w -> {
            w.setParser(s -> VelocityHelperBlockEntity.parseExpression(s, "x", "y", "z"));
            w.setExpression(entity.getConditionString());
        });
        icWidget = createExpressionField(37, 81, 174, w -> {
            w.setParser(s -> VelocityHelperBlockEntity.parseExpression(s, "x"));
            w.setExpression(entity.getInterpolationCurveString());
        });
        addRenderableWidget(
            Button.builder(CommonComponents.GUI_CANCEL, w -> onClose())
                .width(width / 2 - 90)
                .pos(this.topPos + imageHeight + 5, 75)
                .build()
        );
        doneButton = addRenderableWidget(
            Button.builder(CommonComponents.GUI_DONE, w -> {
                VelocityHelperBlock.sendConfigurePacket(menu.getAt(), VelocityHelperBlock.CONFIG_OTHER, buf -> {
                    buf.writeVarInt(Integer.parseInt(flightDurationWidget.getValue()));
                    buf.writeUtf(conditionWidget.getValue());
                    buf.writeUtf(icWidget.getValue());
                });
                onClose();
            }).width(width / 2 + 15)
                .pos(this.topPos + imageHeight + 5, 75)
                .build()
        );
    }

    private EditBox createTextField(int x, int y, int width, Consumer<EditBox> consumer) {
        final EditBox widget = new EditBox(font, this.leftPos + x, this.topPos + y, width, 20, Component.empty());
        consumer.accept(widget);
        addRenderableWidget(widget);
        return widget;
    }

    private ExpressionFieldWidget createExpressionField(int x, int y, int width, Consumer<ExpressionFieldWidget> consumer) {
        final ExpressionFieldWidget widget = new ExpressionFieldWidget(font, this.leftPos + x, this.topPos + y, width, 20, Component.empty());
        consumer.accept(widget);
        addRenderableWidget(widget);
        return widget;
    }

    @Override
    protected void containerTick() {
        flightDurationWidget.tick();
        conditionWidget.tick();
        icWidget.tick();
        doneButton.active =
            !flightDurationWidget.getValue().isEmpty() &&
                !conditionWidget.getValue().isEmpty() &&
                !icWidget.getValue().isEmpty();
    }

    public static void drawLine(GuiGraphics graphics, float x0, float y0, float x1, float y1, int color) {
        final Matrix4f model = graphics.pose().last().pose();
        final Matrix3f normal = graphics.pose().last().normal();
        final float mag = Math.invsqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0));
        final float normalX = Math.abs(x1 - x0) * mag;
        final float normalY = Math.abs(y1 - y0) * mag;

        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.lineWidth(3f);
        bufferBuilder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
        bufferBuilder.vertex(model, x0, y0, 0).color(color).normal(normal, normalX, normalY, 0).endVertex();
        bufferBuilder.vertex(model, x1, y1, 0).color(color).normal(normal, normalX, normalY, 0).endVertex();
        Tesselator.getInstance().end();
    }

    public static void drawTextRightAligned(GuiGraphics graphics, Font font, Component text, int x, int y, int color) {
        graphics.drawString(font, text, x - font.width(text), y, color);
    }

    public static void drawTextCentered(GuiGraphics graphics, Font font, Component text, int x, int y, int color) {
        graphics.drawString(font, text, x - font.width(text) / 2, y, color);
    }
}
