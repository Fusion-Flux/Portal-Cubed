package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.accessor.EntityPortalsAccess;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.q_misc_util.Helper;
import qouteall.q_misc_util.my_util.SignalArged;
import qouteall.q_misc_util.my_util.SignalBiArged;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class BridgeEntity extends Entity {

    public BridgeEntity(EntityType<? extends Entity> type, World world) {
        super(type, world);
        this.ignoreCameraFrustum = true;
    }
    private static final TrackedData<BlockPos> BLOCKPOS1 = DataTracker.registerData(BridgeEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    private static final TrackedData<BlockPos> BLOCKPOS2 = DataTracker.registerData(BridgeEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    private static final TrackedData<BlockPos> DIRECTION = DataTracker.registerData(BridgeEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    @Override
    public boolean collides() {
        return !this.isRemoved();
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());

        packet.writeVarInt(Registry.ENTITY_TYPE.getRawId(this.getType()))
                .writeUuid(this.getUuid())
                .writeVarInt(this.getId())
                .writeDouble(this.getX())
                .writeDouble(this.getY())
                .writeDouble(this.getZ())
                .writeByte(MathHelper.floor(this.getPitch() * 256.0F / 360.0F))
                .writeByte(MathHelper.floor(this.getYaw() * 256.0F / 360.0F));

        return ServerPlayNetworking.createS2CPacket(PortalCubedClientPackets.SPAWN_PACKET, packet);
    }



    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(BLOCKPOS1, new BlockPos(0,0,0));
        this.dataTracker.startTracking(BLOCKPOS2, new BlockPos(0,0,0));
        this.dataTracker.startTracking(DIRECTION, new BlockPos(0,0,0));
    }


    @Override
    public boolean isInvisible() {
        return false;
    }

    public void setBoundingBoxPos(BlockPos pos1,BlockPos pos2){
        this.dataTracker.set(BLOCKPOS1, pos1);
        this.dataTracker.set(BLOCKPOS2, pos2);
    }

    public void setDirection(BlockPos direction){
        this.dataTracker.set(DIRECTION, direction);
    }

    @Override
    public boolean shouldRender(double distance) {
        return true;
    }

    @Override
    protected Box calculateBoundingBox() {
        BlockPos pos1 = this.dataTracker.get(BLOCKPOS1);
        BlockPos pos2 = this.dataTracker.get(BLOCKPOS2);
        BlockPos facing = this.dataTracker.get(DIRECTION);

        Box blockBox = new Box(pos1,new BlockPos(pos1.getX()+1,pos1.getY()+1,pos1.getZ()+1));
        Vec3d startPos = new Vec3d(blockBox.getCenter().x-(.5 * facing.getX()),blockBox.getCenter().y-.5,blockBox.getCenter().z-(.5 * facing.getZ()));
        Vec3d invertDirection = Vec3d.ZERO;
        if(facing.getX() != 0){
            invertDirection = new Vec3d(0,1,1);
        }
        if(facing.getZ() != 0){
            invertDirection = new Vec3d(1,1,0);
        }

        startPos = startPos.add(new Vec3d(invertDirection.x*0.375,invertDirection.y*0.8125,invertDirection.z*0.375));


        Box blockPosEnd = new Box(pos2,new BlockPos(pos2.getX()+1,pos2.getY()+1,pos2.getZ()+1));
        Vec3d endPos = new Vec3d(blockPosEnd.getCenter().x-(.5 * facing.getX()),blockPosEnd.getCenter().y-.5,blockPosEnd.getCenter().z-(.5 * facing.getZ()));

        endPos = endPos.add(new Vec3d(invertDirection.x*-0.375,invertDirection.y*0.75,invertDirection.z*-0.375));

        return new Box(startPos,endPos);
    }


    @Override
    public void tick() {
        super.tick();
        if(this.world.isClient){
            this.setPosition(this.getPos());
        }
        /*if(!this.world.isClient){
            for(int i = -1; i < 4; ++i) {
                for(int j = -1; j < 4; ++j) {
                    int k = this.getChunkPos().x + i;
                    int l = this.getChunkPos().z + j;
                    ((ServerWorld)(this.world)).setChunkForced(k, l, true);
                }
            }
        }*/


    }



}
