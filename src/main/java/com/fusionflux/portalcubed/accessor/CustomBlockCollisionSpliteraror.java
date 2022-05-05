package com.fusionflux.portalcubed.accessor;

import com.fusionflux.portalcubed.accessor.CustomCollisionView;
import com.google.common.collect.AbstractIterator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class CustomBlockCollisionSpliteraror extends AbstractIterator<VoxelShape> {
    private final Box box;
    private final Vec3d omittedDirection;
    private final ShapeContext context;
    private final CuboidBlockIterator blockIterator;
    private final BlockPos.Mutable pos;
    private final VoxelShape boxShape;
    private final CustomCollisionView world;
    private final boolean forEntity;
    @Nullable
    private BlockView chunk;
    private long chunkPos;

    public CustomBlockCollisionSpliteraror(CustomCollisionView world, @Nullable Entity entity, Box box, Vec3d direction) {
        this(world, entity, box, direction,false);
    }

    public CustomBlockCollisionSpliteraror(CustomCollisionView world, @Nullable Entity entity, Box box,Vec3d direction, boolean forEntity) {
        this.context = entity == null ? ShapeContext.absent() : ShapeContext.of(entity);
        this.pos = new BlockPos.Mutable();
        this.boxShape = VoxelShapes.cuboid(box);
        this.world = world;
        this.box = box;
        this.omittedDirection = direction;
        this.forEntity = forEntity;
        int i = MathHelper.floor(box.minX - 1.0E-7) - 1;
        int j = MathHelper.floor(box.maxX + 1.0E-7) + 1;
        int k = MathHelper.floor(box.minY - 1.0E-7) - 1;
        int l = MathHelper.floor(box.maxY + 1.0E-7) + 1;
        int m = MathHelper.floor(box.minZ - 1.0E-7) - 1;
        int n = MathHelper.floor(box.maxZ + 1.0E-7) + 1;
        this.blockIterator = new CuboidBlockIterator(i, k, m, j, l, n);
    }

    @Nullable
    private BlockView getChunk(int x, int z) {
        int i = ChunkSectionPos.getSectionCoord(x);
        int j = ChunkSectionPos.getSectionCoord(z);
        long l = ChunkPos.toLong(i, j);
        if (this.chunk != null && this.chunkPos == l) {
            return this.chunk;
        } else {
            BlockView blockView = this.world.getChunkAsView(i, j);
            this.chunk = blockView;
            this.chunkPos = l;
            return blockView;
        }
    }

    protected VoxelShape computeNext() {
        while(this.blockIterator.step()) {
            int i = this.blockIterator.getX();
            int j = this.blockIterator.getY();
            int k = this.blockIterator.getZ();
            int l = this.blockIterator.getEdgeCoordinatesCount();
            if (l != 3) {
                BlockView blockView = this.getChunk(i, k);
                if (blockView != null) {
                    this.pos.set(i, j, k);
                    BlockState blockState = blockView.getBlockState(this.pos);
                    if ((!this.forEntity || blockState.shouldSuffocate(blockView, this.pos))
                            && (l != 1 || blockState.exceedsCube())
                            && (l != 2 || blockState.isOf(Blocks.MOVING_PISTON))) {
                        VoxelShape voxelShape = blockState.getCollisionShape(this.world, this.pos, this.context);
                        if (voxelShape == VoxelShapes.fullCube()) {
                            //this.box.intersects((double)i, (double)j, (double)k, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0)
                            //intersects(box,(double)i, (double)j, (double)k, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0)
                            if (this.intersects((double)i, (double)j, (double)k, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0)) {
                                return voxelShape.offset((double)i, (double)j, (double)k);
                            }
                        } else {
                            VoxelShape voxelShape2 = voxelShape.offset((double)i, (double)j, (double)k);
                            if (VoxelShapes.matchesAnywhere(voxelShape2, this.boxShape, BooleanBiFunction.AND)) {
                                return voxelShape2;
                            }
                        }
                    }
                }
            }
        }

        return this.endOfData();
    }

    public boolean intersects(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        if(omittedDirection != null) {
            if (omittedDirection.x == 1) {
                maxX = maxX - .1;
            }
            if (omittedDirection.x == -1) {
                minX = minX + .1;
            }
            if (omittedDirection.y == 1) {
                maxY = maxY - .1;
            }
            if (omittedDirection.y == -1) {
                minY = minY + .1;
            }
            if (omittedDirection.z == 1) {
                maxZ = maxZ - .1;
            }
            if (omittedDirection.z == -1) {
                minZ = minZ + .1;
            }
        }
        return this.box.minX < maxX && this.box.maxX > minX && this.box.minY < maxY && this.box.maxY > minY && this.box.minZ < maxZ && this.box.maxZ > minZ;
    }
}
