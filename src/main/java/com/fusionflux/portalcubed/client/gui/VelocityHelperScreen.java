package com.fusionflux.portalcubed.client.gui;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.VelocityHelperBlock;
import com.fusionflux.portalcubed.blocks.blockentities.VelocityHelperBlockEntity;
import com.fusionflux.portalcubed.gui.VelocityHelperScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
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
    protected void renderBg(PoseStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, TEXTURE);
        blit(matrices, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(PoseStack matrices, int mouseX, int mouseY) {
        font.draw(matrices, title, (float)titleLabelX, (float)titleLabelY, 0x404040);
        drawTextRightAligned(
            matrices, font,
            Component.translatable("portalcubed.velocity_helper.flight_duration"),
            139, 23, 0x404040
        );
        drawTextCentered(
            matrices, font,
            Component.translatable("portalcubed.velocity_helper.condition"),
            imageWidth / 2, 39, 0x404040
        );
        drawTextCentered(
            matrices, font,
            Component.translatable("portalcubed.velocity_helper.interpolation_curve"),
            imageWidth / 2, 71, 0x404040
        );
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        renderTooltip(matrices, mouseX, mouseY);

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
                        drawLine(matrices, x - spacing, this.topPos + 157 - last, x, this.topPos + 157 - calculation, 0xffffffff);
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
        addRenderableWidget(new Button(
            width / 2 - 90, this.topPos + imageHeight + 5, 75, 20, CommonComponents.GUI_CANCEL,
            w -> onClose()
        ));
        addRenderableWidget(doneButton = new Button(
            width / 2 + 15, this.topPos + imageHeight + 5, 75, 20, CommonComponents.GUI_DONE,
            w -> {
                VelocityHelperBlock.sendConfigurePacket(menu.getAt(), VelocityHelperBlock.CONFIG_OTHER, buf -> {
                    buf.writeVarInt(Integer.parseInt(flightDurationWidget.getValue()));
                    buf.writeUtf(conditionWidget.getValue());
                    buf.writeUtf(icWidget.getValue());
                });
                onClose();
            }
        ));
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

    public static void drawLine(PoseStack matrices, float x0, float y0, float x1, float y1, int color) {
        final Matrix4f model = matrices.last().pose();
        final Matrix3f normal = matrices.last().normal();
        final float mag = Mth.fastInvSqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0));
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

    public static void drawTextRightAligned(PoseStack matrices, Font textRenderer, Component text, int x, int y, int color) {
        textRenderer.draw(matrices, text, x - textRenderer.width(text), y, color);
    }

    public static void drawTextCentered(PoseStack matrices, Font textRenderer, Component text, int x, int y, int color) {
        textRenderer.draw(matrices, text, x - textRenderer.width(text) / 2f, y, color);
    }
}
