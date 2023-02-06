package com.fusionflux.portalcubed.client.gui;

import com.fusionflux.portalcubed.blocks.blockentities.BetaFaithPlateBlockEntity;
import com.fusionflux.portalcubed.blocks.blockentities.FaithPlateBlockEntity;
import com.fusionflux.portalcubed.gui.FaithPlateScreenHandler;
import com.fusionflux.portalcubed.packet.NetworkingSafetyWrapper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.quiltmc.qsl.networking.api.PacketByteBufs;

public class FaithPlateScreen extends HandledScreen<ScreenHandler> {

    private static final Identifier TEXTURE = new Identifier("portalcubed", "textures/gui/container/faith_plate.png");

    private final BlockPos pos;
    private final double x;
    private final double y;
    private final double z;

    public FaithPlateScreen(ScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
        pos = getBlockPos(screenHandler);
        x = getXVar(screenHandler);
        y = getYVar(screenHandler);
        z = getZVar(screenHandler);
    }

    private static BlockPos getBlockPos(ScreenHandler handler) {
        if (handler instanceof FaithPlateScreenHandler) {
            return ((FaithPlateScreenHandler) handler).getPos();
        } else {
            return BlockPos.ORIGIN;
        }
    }
    private static double getXVar(ScreenHandler handler) {
        if (handler instanceof FaithPlateScreenHandler) {
            return ((FaithPlateScreenHandler) handler).getX();
        } else {
            return 0;
        }
    }
    private static double getYVar(ScreenHandler handler) {
        if (handler instanceof FaithPlateScreenHandler) {
            return ((FaithPlateScreenHandler) handler).getY();
        } else {
            return 0;
        }
    }
    private static double getZVar(ScreenHandler handler) {
        if (handler instanceof FaithPlateScreenHandler) {
            return ((FaithPlateScreenHandler) handler).getZ();
        } else {
            return 0;
        }
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }
    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        }

    @Override
    protected void init() {
        super.init();
        var field1 = addDrawableChild(new TextFieldWidget(
                textRenderer,
                (this.width/2)- 80, // x ( aligned top-left )
                (this.height/2) - 30, // y
                50, // width
                20, // height
                Text.of(String.valueOf(x)) // default text??? not sure
        ));
        field1.setText("X: " + x);
        var field2 = addDrawableChild(new TextFieldWidget(
                textRenderer,
                (this.width/2)-25, // x ( aligned top-left )
                (this.height/2) - 30, // y
                50, // width
                20, // height
                Text.of(String.valueOf(y)) // default text??? not sure
        ));
        field2.setText("Y: " + y);
        var field3 = addDrawableChild(new TextFieldWidget(
                textRenderer,
                (this.width/2)+ 30, // x ( aligned top-left )
                (this.height/2) - 30, // y
                50, // width
                20, // height
                Text.of(String.valueOf(z)) // default text??? not sure
        ));
        field3.setText("Z: " + z);

        int x = this.width/2;
        int y = this.height/2;
        addDrawableChild(new ButtonWidget(x - 50, y, 100, 20, Text.of("Done"), (button) -> {

            PacketByteBuf buf = PacketByteBufs.create();

            buf.writeBlockPos(pos);

            String xString = field1.getText().replaceAll("[^\\d.-]", "").replaceFirst("[.]","d").replaceAll("[.]", "").replaceAll("d", ".").replaceFirst("-","m").replaceAll("-", "").replaceAll("m", "-");
            String yString = field2.getText().replaceAll("[^\\d.-]", "").replaceFirst("[.]","d").replaceAll("[.]", "").replaceAll("d", ".").replaceFirst("-","m").replaceAll("-", "").replaceAll("m", "-");
            String zString = field3.getText().replaceAll("[^\\d.-]", "").replaceFirst("[.]","d").replaceAll("[.]", "").replaceAll("d", ".").replaceFirst("-","m").replaceAll("-", "").replaceAll("m", "-");

            double sendX = 0;
            double sendY = 0;
            double sendZ = 0;

            if(!xString.equals("")){
                sendX = Double.parseDouble(xString);
            }
            if(!yString.equals("")){
                sendY = Double.parseDouble(yString);
            }
            if(!zString.equals("")){
                sendZ = Double.parseDouble(zString);
            }

            if(sendX >4){
                sendX = 4;
            }
            if(sendY >4){
                sendY = 4;
            }
            if(sendZ >4){
                sendZ = 4;
            }

            World world = MinecraftClient.getInstance().world;

            if(world != null){
                BlockEntity entity = world.getBlockEntity(pos);
                if(entity instanceof FaithPlateBlockEntity faith){
                    faith.setVelX(sendX);
                    faith.setVelY(sendY);
                    faith.setVelZ(sendZ);
                }
                if(entity instanceof BetaFaithPlateBlockEntity faith){
                    faith.setVelX(sendX);
                    faith.setVelY(sendY);
                    faith.setVelZ(sendZ);
                }
            }

            buf.writeDouble(sendX);
            buf.writeDouble(sendY);
            buf.writeDouble(sendZ);
            NetworkingSafetyWrapper.sendFromClient("faithplatepacket", buf);
            this.closeScreen();
           // ClientPlayNetworking.send("a", buf);
        }));
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }


}
