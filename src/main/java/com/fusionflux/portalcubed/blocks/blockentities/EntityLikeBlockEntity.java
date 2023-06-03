package com.fusionflux.portalcubed.blocks.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class EntityLikeBlockEntity extends BlockEntity {
    private int age;
    public float prevYaw, prevPitch;
    public float yaw, pitch;

    public EntityLikeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.putFloat("Yaw", yaw);
        nbt.putFloat("Pitch", pitch);
    }

    @Override
    public void load(CompoundTag nbt) {
        yaw = nbt.getFloat("Yaw");
        pitch = nbt.getFloat("Pitch");
    }

    public void tick(Level world, BlockPos pos, BlockState state) {
        age++;
        prevYaw = yaw;
        prevPitch = pitch;
    }

    public int getAge() {
        return age;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = Mth.wrapDegrees(yaw);
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = Mth.wrapDegrees(pitch);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }
}
