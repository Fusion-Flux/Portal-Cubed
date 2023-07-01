package com.fusionflux.portalcubed.util;

import java.util.function.Consumer;

import org.joml.Quaternionf;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

public class LerpedQuaternion {
    private Quaternionf start;
    private Quaternionf end;
    private float duration;
    private Consumer<LerpedQuaternion> updateCallback;

    private int ticks;

    public LerpedQuaternion(Quaternionf start) {
        this.start = start;
    }

    public void tick() {
        if (isActive()) {
            ticks++;
            if (ticks > duration) {
                finish();
            }
        }
    }

    public LerpedQuaternion withUpdateCallback(Consumer<LerpedQuaternion> callback) {
        this.updateCallback = callback;
        return this;
    }

    public LerpedQuaternion lerpTo(Quaternionf end) {
        return lerpTo(end, 20);
    }

    public LerpedQuaternion lerpTo(Quaternionf end, float duration) {
        this.end = end;
        this.duration = duration;
        onUpdate();
        return this;
    }

    public void set(Quaternionf start) {
        this.end = start; // finish will set start to end
        finish();
    }

    public boolean isActive() {
        return end != null;
    }

    public Quaternionf get() {
        float progress = Math.min(1, ticks / duration);
        return getInternal(progress);
    }

    public Quaternionf get(float partialTicks) {
        float progress = Math.min(1, (ticks + partialTicks) / duration);
        return getInternal(progress);
    }

    private Quaternionf getInternal(float progress) {
        if (!isActive())
            return start;
        return start.slerp(end, progress, new Quaternionf());
    }

    private void onUpdate() {
        if (updateCallback != null)
            updateCallback.accept(this);
    }

    private void finish() {
        this.start = this.end;
        this.end = null;
        this.ticks = 0;
        this.duration = 0;
        onUpdate();
    }

    public LerpedQuaternion copy() {
        LerpedQuaternion copy = new LerpedQuaternion(start).withUpdateCallback(this.updateCallback);
        if (isActive()) {
            copy.end = this.end;
            copy.ticks = this.ticks;
            copy.duration = this.duration;
        }
        return copy;
    }

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeQuaternion(start);
        if (isActive()) {
            buf.writeBoolean(true);
            buf.writeQuaternion(end);
            buf.writeVarInt(ticks);
            buf.writeFloat(duration);
        } else {
            buf.writeBoolean(false);
        }
    }

    public static LerpedQuaternion fromNetwork(FriendlyByteBuf buf) {
        Quaternionf start = buf.readQuaternion();
        LerpedQuaternion quaternion = new LerpedQuaternion(start);
        if (!buf.readBoolean())
            return quaternion;
        quaternion.end = buf.readQuaternion();
        quaternion.ticks = buf.readVarInt();
        quaternion.duration = buf.readFloat();
        return quaternion;
    }

    public CompoundTag toNbt() {
        CompoundTag nbt = new CompoundTag();
        NbtHelper.putQuaternion(nbt, "start", start);
        if (isActive()) {
            NbtHelper.putQuaternion(nbt, "end", end);
            nbt.putInt("ticks", ticks);
            nbt.putFloat("duration", duration);
        }
        return nbt;
    }

    public static LerpedQuaternion fromNbt(CompoundTag tag) {
        Quaternionf start = NbtHelper.getQuaternion(tag, "start");
        LerpedQuaternion quaternion = new LerpedQuaternion(start);
        if (tag.contains("end", Tag.TAG_COMPOUND) && tag.contains("ticks", Tag.TAG_INT) && tag.contains("duration", Tag.TAG_FLOAT)) {
            quaternion.end = NbtHelper.getQuaternion(tag, "end");
            quaternion.ticks = tag.getInt("ticks");
            quaternion.duration = tag.getFloat("duration");
        }
        return quaternion;
    }
}
