package com.fusionflux.portalcubed.client.gui;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.VelocityHelperBlock;
import com.fusionflux.portalcubed.blocks.blockentities.VelocityHelperBlockEntity;
import com.fusionflux.portalcubed.gui.VelocityHelperScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.objecthunter.exp4j.Expression;

import java.util.function.Consumer;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class VelocityHelperScreen extends HandledScreen<VelocityHelperScreenHandler> {
    private static final Identifier TEXTURE = id("textures/gui/velocity_helper.png");

    private final VelocityHelperBlockEntity entity;

    private TextFieldWidget flightDurationWidget;
    private ExpressionFieldWidget conditionWidget, icWidget;
    private ButtonWidget doneButton;

    public VelocityHelperScreen(VelocityHelperScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 248;
        backgroundHeight = 166;
        assert MinecraftClient.getInstance().world != null;
        entity = MinecraftClient.getInstance().world.getBlockEntity(handler.getAt(), PortalCubedBlocks.VELOCITY_HELPER_BLOCK_ENTITY).orElse(null);
        if (entity == null) {
            closeScreen();
        }
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, TEXTURE);
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        textRenderer.draw(matrices, title, (float)titleX, (float)titleY, 0x404040);
        drawTextRightAligned(
            matrices, textRenderer,
            Text.translatable("portalcubed.velocity_helper.flight_duration"),
            139, 23, 0x404040
        );
        drawTextCentered(
            matrices, textRenderer,
            Text.translatable("portalcubed.velocity_helper.condition"),
            backgroundWidth / 2, 39, 0x404040
        );
        drawTextCentered(
            matrices, textRenderer,
            Text.translatable("portalcubed.velocity_helper.interpolation_curve"),
            backgroundWidth / 2, 71, 0x404040
        );
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);

        final Expression curve = icWidget.getExpression();
        if (curve != null) {
            final String flightDurationString = flightDurationWidget.getText();
            if (!flightDurationString.isEmpty()) {
                final int pointCount = Math.min(Integer.parseInt(flightDurationString), 217);
                final float spacing = 217f / pointCount;
                float last = 0;
                float x = this.x + 17 + spacing;
                try {
                    for (int i = 1; i <= pointCount; i++, x += spacing) {
                        curve.setVariable("x", 1.0 / pointCount * i);
                        final float calculation = (float)MathHelper.clamp(curve.evaluate(), 0, 1) * 44;
                        drawLine(matrices, x - spacing, this.y + 157 - last, x, this.y + 157 - calculation, 0xffffffff);
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
            w.setTextPredicate(s -> {
                if (s.isEmpty()) return true;
                try {
                    return Integer.parseInt(s) > 0;
                } catch (NumberFormatException e) {
                    return false;
                }
            });
            w.setText(Integer.toString(entity.getFlightDuration()));
        });
        conditionWidget = createExpressionField(37, 49, 174, w -> {
            w.setParser(s -> VelocityHelperBlockEntity.parseExpression(s, "x", "y", "z"));
            w.setExpression(entity.getConditionString());
        });
        icWidget = createExpressionField(37, 81, 174, w -> {
            w.setParser(s -> VelocityHelperBlockEntity.parseExpression(s, "x"));
            w.setExpression(entity.getInterpolationCurveString());
        });
        addDrawableChild(new ButtonWidget(
            width / 2 - 90, this.y + backgroundHeight + 5, 75, 20, ScreenTexts.CANCEL,
            w -> closeScreen()
        ));
        addDrawableChild(doneButton = new ButtonWidget(
            width / 2 + 15, this.y + backgroundHeight + 5, 75, 20, ScreenTexts.DONE,
            w -> {
                VelocityHelperBlock.sendConfigurePacket(handler.getAt(), VelocityHelperBlock.CONFIG_OTHER, buf -> {
                    buf.writeVarInt(Integer.parseInt(flightDurationWidget.getText()));
                    buf.writeString(conditionWidget.getText());
                    buf.writeString(icWidget.getText());
                });
                closeScreen();
            }
        ));
    }

    private TextFieldWidget createTextField(int x, int y, int width, Consumer<TextFieldWidget> consumer) {
        final TextFieldWidget widget = new TextFieldWidget(textRenderer, this.x + x, this.y + y, width, 20, Text.empty());
        consumer.accept(widget);
        addDrawableChild(widget);
        return widget;
    }

    private ExpressionFieldWidget createExpressionField(int x, int y, int width, Consumer<ExpressionFieldWidget> consumer) {
        final ExpressionFieldWidget widget = new ExpressionFieldWidget(textRenderer, this.x + x, this.y + y, width, 20, Text.empty());
        consumer.accept(widget);
        addDrawableChild(widget);
        return widget;
    }

    @Override
    protected void handledScreenTick() {
        flightDurationWidget.tick();
        conditionWidget.tick();
        icWidget.tick();
        doneButton.active =
            !flightDurationWidget.getText().isEmpty() &&
                !conditionWidget.getText().isEmpty() &&
                !icWidget.getText().isEmpty();
    }

    public static void drawLine(MatrixStack matrices, float x0, float y0, float x1, float y1, int color) {
        final Matrix4f model = matrices.peek().getModel();
        final Matrix3f normal = matrices.peek().getNormal();
        final float mag = MathHelper.fastInverseSqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0));
        final float normalX = Math.abs(x1 - x0) * mag;
        final float normalY = Math.abs(y1 - y0) * mag;

        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesShader);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBufferBuilder();
        RenderSystem.lineWidth(3f);
        bufferBuilder.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
        bufferBuilder.vertex(model, x0, y0, 0).color(color).normal(normal, normalX, normalY, 0).next();
        bufferBuilder.vertex(model, x1, y1, 0).color(color).normal(normal, normalX, normalY, 0).next();
        Tessellator.getInstance().draw();
    }

    public static void drawTextRightAligned(MatrixStack matrices, TextRenderer textRenderer, Text text, int x, int y, int color) {
        textRenderer.draw(matrices, text, x - textRenderer.getWidth(text), y, color);
    }

    public static void drawTextCentered(MatrixStack matrices, TextRenderer textRenderer, Text text, int x, int y, int color) {
        textRenderer.draw(matrices, text, x - textRenderer.getWidth(text) / 2f, y, color);
    }
}
