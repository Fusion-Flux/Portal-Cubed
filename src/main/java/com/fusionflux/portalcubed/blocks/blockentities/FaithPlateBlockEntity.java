package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.entity.*;
import com.fusionflux.portalcubed.util.FaithPlateScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author sailKite
 * @author FusionFlux
 * <p>
 * Handles the operating logic for the {@link HardLightBridgeEmitterBlock} and their associated bridges.
 */
public class FaithPlateBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {

    public final int MAX_RANGE = PortalCubedConfig.maxBridgeLength;

    private double velX=0;
    private double velY=0;
    private double velZ=0;

    private  double timer = 0;
    private  double animationtimer = 0;

    public FaithPlateBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.FAITH_PLATE_ENTITY,pos,state);

    }

    public static void tick1(World world, BlockPos pos, BlockState state, FaithPlateBlockEntity blockEntity) {
        //if(!world.isClient)
        //blockEntity.toUpdatePacket();
        Box checkBox = new Box(pos).offset(state.get(Properties.FACING).getOffsetX(),state.get(Properties.FACING).getOffsetY(),state.get(Properties.FACING).getOffsetZ());

        List<Entity> list = world.getNonSpectatingEntities(Entity.class, checkBox);
        //System.out.println(blockEntity.velX);

        if(blockEntity.animationtimer>0) {
            blockEntity.animationtimer -= 1;
            world.setBlockState(pos,state.with(Properties.ENABLED,true),3);
        }else{
            world.setBlockState(pos,state.with(Properties.ENABLED,false),3);
        }

        for(Entity liver : list){
            if(blockEntity.timer <= 0) {
                liver.setVelocity(blockEntity.velX, blockEntity.velY, blockEntity.velZ);
                blockEntity.timer = 5;
                blockEntity.animationtimer = 7;
                world.setBlockState(pos,state.with(Properties.ENABLED,false),3);
            }
        }
        if(blockEntity.timer>0)
            blockEntity.timer -= 1;

    }

    public void playSound(SoundEvent soundEvent) {
        this.world.playSound(null, this.pos, soundEvent, SoundCategory.BLOCKS, 0.1F, 3.0F);
    }


    public void setVelX(double velX) {
        this.velX = velX;
    }
    public void setVelY(double velY) {
        this.velY = velY;
    }
    public void setVelZ(double velZ) {
        this.velZ = velZ;
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putDouble("velX",velX);
        tag.putDouble("velY",velY);
        tag.putDouble("velZ",velZ);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        velX = tag.getDouble("velX");
        velY = tag.getDouble("velY");
        velZ = tag.getDouble("velZ");
    }


    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return null;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeDouble(velX);
        buf.writeDouble(velY);
        buf.writeDouble(velZ);
    }

    @Override
    public Text getDisplayName() {
        return Text.empty();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new FaithPlateScreenHandler(i,playerInventory);
    }
}