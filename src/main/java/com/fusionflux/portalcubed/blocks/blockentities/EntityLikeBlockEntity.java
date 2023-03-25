package com.fusionflux.portalcubed.blocks.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class EntityLikeBlockEntity extends BlockEntity {
    private int age;
    public float prevYaw, prevPitch;
    public float yaw, pitch;

    public EntityLikeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("Age", age);
        nbt.putFloat("Yaw", yaw);
        nbt.putFloat("Pitch", pitch);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        age = nbt.getInt("Age");
        yaw = nbt.getFloat("Yaw");
        pitch = nbt.getFloat("Pitch");
    }

    public void tick(World world, BlockPos pos, BlockState state) {
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
        this.yaw = MathHelper.wrapDegrees(yaw);
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = MathHelper.wrapDegrees(pitch);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return toNbt();
    }
}
