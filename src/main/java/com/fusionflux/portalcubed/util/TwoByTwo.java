package com.fusionflux.portalcubed.util;

import java.util.Iterator;
import java.util.List;

import com.fusionflux.portalcubed.fluids.ToxicGooFluid.Block;
import com.google.common.collect.Iterators;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public record TwoByTwo(BlockPos topRight, BlockPos topLeft, BlockPos bottomLeft, BlockPos bottomRight) implements Iterable<BlockPos> {
    public static TwoByTwo fromBottomRightCorner(BlockPos bottomRight, Direction left, Direction up) {
        return new TwoByTwo(
                bottomRight.relative(up), bottomRight.relative(up).relative(left), bottomRight.relative(left), bottomRight
        );
    }

    public static TwoByTwo fromTopLeftCorner(BlockPos topLeft, Direction right, Direction down) {
        return new TwoByTwo(
                topLeft.relative(right), topLeft, topLeft.relative(down), topLeft.relative(right).relative(down)
        );
    }

    @Nullable
    public static TwoByTwo fromNbt(CompoundTag tag) {
        BlockPos topRight = NbtHelper.readBlockPos(tag, "topRight");
        BlockPos topLeft = NbtHelper.readBlockPos(tag, "topLeft");
        BlockPos bottomLeft = NbtHelper.readBlockPos(tag, "bottomLeft");
        BlockPos bottomRight = NbtHelper.readBlockPos(tag, "bottomRight");
        TwoByTwo twoByTwo = new TwoByTwo(topRight, topLeft, bottomLeft, bottomRight);
        for (BlockPos pos : twoByTwo) {
            if (pos == null)
                return null;
        }
        return twoByTwo;
    }

    public BlockPos byQuadrantIndex(int index) {
        return byQuadrant(index + 1);
    }

    public BlockPos byQuadrant(int quadrant) {
        return switch (quadrant) {
            case 1 -> topRight;
            case 2 -> topLeft;
            case 3 -> bottomLeft;
            case 4 -> bottomRight;
            default -> throw new IllegalArgumentException("Invalid quadrant: " + quadrant);
        };
    }

    public Iterable<BlockPos> quadrants(int first, int second, int third, int fourth) {
        return List.of(byQuadrant(first), byQuadrant(second), byQuadrant(third), byQuadrant(fourth));
    }

    public Vec3 getCenter() {
        // average centers of each pos
        double totalX = 0, totalY = 0, totalZ = 0;
        for (BlockPos pos : this) {
            totalX += pos.getX() + 0.5;
            totalY += pos.getY() + 0.5;
            totalZ += pos.getZ() + 0.5;
        }
        return new Vec3(totalX / 4f, totalY / 4f, totalZ / 4f);
    }

    public AABB toBox(double inflate) {
        return new AABB(bottomLeft, topRight).inflate(inflate);
    }

    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.put("topRight", NbtUtils.writeBlockPos(topRight));
        tag.put("topLeft", NbtUtils.writeBlockPos(topLeft));
        tag.put("bottomLeft", NbtUtils.writeBlockPos(bottomLeft));
        tag.put("bottomRight", NbtUtils.writeBlockPos(bottomRight));
        return tag;
    }

    @NotNull
    @Override
    public Iterator<BlockPos> iterator() {
        return Iterators.forArray(topRight, topLeft, bottomLeft, bottomRight);
    }

    public boolean contains(BlockPos pos) {
        return pos.equals(topRight) || pos.equals(topLeft) || pos.equals(bottomLeft) || pos.equals(bottomRight);
    }
}
