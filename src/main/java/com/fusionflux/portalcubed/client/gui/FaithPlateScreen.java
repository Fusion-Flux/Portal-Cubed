package com.fusionflux.portalcubed.client.gui;

import com.fusionflux.portalcubed.gui.FaithPlateScreenHandler;
import com.fusionflux.portalcubed.packet.NetworkingSafetyWrapper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.quiltmc.qsl.networking.api.PacketByteBufs;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class FaithPlateScreen extends AbstractContainerScreen<AbstractContainerMenu> {

    private static final ResourceLocation TEXTURE = id("textures/gui/container/faith_plate.png");

    private final BlockPos pos;
    private final double x;
    private final double y;
    private final double z;

    public FaithPlateScreen(AbstractContainerMenu screenHandler, Inventory playerInventory, Component text) {
        super(screenHandler, playerInventory, text);
        pos = getBlockPos(screenHandler);
        x = getXVar(screenHandler);
        y = getYVar(screenHandler);
        z = getZVar(screenHandler);
    }

    private static BlockPos getBlockPos(AbstractContainerMenu handler) {
        if (handler instanceof FaithPlateScreenHandler) {
            return ((FaithPlateScreenHandler) handler).getPos();
        } else {
            return BlockPos.ZERO;
        }
    }
    private static double getXVar(AbstractContainerMenu handler) {
        if (handler instanceof FaithPlateScreenHandler) {
            return ((FaithPlateScreenHandler) handler).getX();
        } else {
            return 0;
        }
    }
    private static double getYVar(AbstractContainerMenu handler) {
        if (handler instanceof FaithPlateScreenHandler) {
            return ((FaithPlateScreenHandler) handler).getY();
        } else {
            return 0;
        }
    }
    private static double getZVar(AbstractContainerMenu handler) {
        if (handler instanceof FaithPlateScreenHandler) {
            return ((FaithPlateScreenHandler) handler).getZ();
        } else {
            return 0;
        }
    }

    @Override
    protected void renderBg(PoseStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        blit(matrices, x, y, 0, 0, imageWidth, imageHeight);
    }
    @Override
    protected void renderLabels(PoseStack matrices, int mouseX, int mouseY) {
    }

    @Override
    protected void init() {
        super.init();
        var field1 = addRenderableWidget(new EditBox(
                font,
                (this.width / 2) - 80, // x ( aligned top-left )
                (this.height / 2) - 30, // y
                50, // width
                20, // height
                Component.nullToEmpty(String.valueOf(x)) // default text??? not sure
        ));
        field1.setValue("X: " + x);
        var field2 = addRenderableWidget(new EditBox(
                font,
                (this.width / 2) - 25, // x ( aligned top-left )
                (this.height / 2) - 30, // y
                50, // width
                20, // height
                Component.nullToEmpty(String.valueOf(y)) // default text??? not sure
        ));
        field2.setValue("Y: " + y);
        var field3 = addRenderableWidget(new EditBox(
                font,
                (this.width / 2) + 30, // x ( aligned top-left )
                (this.height / 2) - 30, // y
                50, // width
                20, // height
                Component.nullToEmpty(String.valueOf(z)) // default text??? not sure
        ));
        field3.setValue("Z: " + z);

        int x = this.width / 2;
        int y = this.height / 2;
        addRenderableWidget(new Button(x - 50, y, 100, 20, Component.nullToEmpty("Done"), (button) -> {

            FriendlyByteBuf buf = PacketByteBufs.create();

            buf.writeBlockPos(pos);

            String xString = field1.getValue().replaceAll("[^\\d.-]", "").replaceFirst("[.]", "d").replaceAll("[.]", "").replaceAll("d", ".").replaceFirst("-", "m").replaceAll("-", "").replaceAll("m", "-");
            String yString = field2.getValue().replaceAll("[^\\d.-]", "").replaceFirst("[.]", "d").replaceAll("[.]", "").replaceAll("d", ".").replaceFirst("-", "m").replaceAll("-", "").replaceAll("m", "-");
            String zString = field3.getValue().replaceAll("[^\\d.-]", "").replaceFirst("[.]", "d").replaceAll("[.]", "").replaceAll("d", ".").replaceFirst("-", "m").replaceAll("-", "").replaceAll("m", "-");

            double sendX = 0;
            double sendY = 0;
            double sendZ = 0;

            if (!xString.equals("")) {
                sendX = Double.parseDouble(xString);
            }
            if (!yString.equals("")) {
                sendY = Double.parseDouble(yString);
            }
            if (!zString.equals("")) {
                sendZ = Double.parseDouble(zString);
            }

            if (sendX > 4) {
                sendX = 4;
            }
            if (sendY > 4) {
                sendY = 4;
            }
            if (sendZ > 4) {
                sendZ = 4;
            }

            buf.writeDouble(sendX);
            buf.writeDouble(sendY);
            buf.writeDouble(sendZ);
            NetworkingSafetyWrapper.sendFromClient("configure_faith_plate", buf);
            this.onClose();
           // ClientPlayNetworking.send("a", buf);
        }));
        // Center the title
        titleLabelX = (imageWidth - font.width(title)) / 2;
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        renderTooltip(matrices, mouseX, mouseY);
    }


}
