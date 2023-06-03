package com.fusionflux.portalcubed.accessor;

import com.fusionflux.portalcubed.listeners.CustomCollisionView;
import com.google.common.collect.AbstractIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CustomBlockCollisionSpliterator extends AbstractIterator<VoxelShape> {
    private final AABB box;
    private final VoxelShape portalBox;
    private final CollisionContext context;
    private final Cursor3D blockIterator;
    private final BlockPos.MutableBlockPos pos;
    private final VoxelShape boxShape;
    private final CustomCollisionView world;
    private final boolean forEntity;
    @Nullable
    private BlockGetter chunk;
    private long chunkPos;

    public CustomBlockCollisionSpliterator(CustomCollisionView world, @Nullable Entity entity, AABB box, VoxelShape portalBox) {
        this(world, entity, box, portalBox, false);
    }

    public CustomBlockCollisionSpliterator(CustomCollisionView world, @Nullable Entity entity, AABB box, VoxelShape portalBox, boolean forEntity) {
        this.context = entity == null ? CollisionContext.empty() : CollisionContext.of(entity);
        this.pos = new BlockPos.MutableBlockPos();
        this.boxShape = Shapes.create(box);
        this.world = world;
        this.box = box;
        this.portalBox = portalBox;
        this.forEntity = forEntity;
        int i = Mth.floor(box.minX - 1.0E-7) - 1;
        int j = Mth.floor(box.maxX + 1.0E-7) + 1;
        int k = Mth.floor(box.minY - 1.0E-7) - 1;
        int l = Mth.floor(box.maxY + 1.0E-7) + 1;
        int m = Mth.floor(box.minZ - 1.0E-7) - 1;
        int n = Mth.floor(box.maxZ + 1.0E-7) + 1;
        this.blockIterator = new Cursor3D(i, k, m, j, l, n);
    }

    @Nullable
    private BlockGetter getChunk(int x, int z) {
        int i = SectionPos.blockToSectionCoord(x);
        int j = SectionPos.blockToSectionCoord(z);
        long l = ChunkPos.asLong(i, j);
        if (this.chunk != null && this.chunkPos == l) {
            return this.chunk;
        } else {
            BlockGetter blockView = ((this.world)).getChunkForCollisions(i, j);
            this.chunk = blockView;
            this.chunkPos = l;
            return blockView;
        }
    }

    @Override
    protected VoxelShape computeNext() {
        while (this.blockIterator.advance()) {
            int i = this.blockIterator.nextX();
            int j = this.blockIterator.nextY();
            int k = this.blockIterator.nextZ();
            int l = this.blockIterator.getNextType();
            if (l != 3) {
                BlockGetter blockView = this.getChunk(i, k);
                if (blockView != null) {
                    this.pos.set(i, j, k);
                    BlockState blockState = blockView.getBlockState(this.pos);
                    if ((!this.forEntity || blockState.isSuffocating(blockView, this.pos))
                            && (l != 1 || blockState.hasLargeCollisionShape())
                            && (l != 2 || blockState.is(Blocks.MOVING_PISTON))) {
                        VoxelShape voxelShape = blockState.getCollisionShape(this.world, this.pos, this.context);
                        VoxelShape cutout = portalBox.move(-i, -j, -k);
                        voxelShape = Shapes.joinUnoptimized(voxelShape, cutout, BooleanOp.ONLY_FIRST);

                        if (voxelShape == Shapes.block()) {
                            if (this.box.intersects(i, j, k, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0)) {
                                return voxelShape.move(i, j, k);
                            }
                        } else {
                            VoxelShape voxelShape2 = voxelShape.move(i, j, k);
                            if (Shapes.joinIsNotEmpty(voxelShape2, this.boxShape, BooleanOp.AND)) {
                                return voxelShape2;
                            }
                        }
                    }
                }
            }
        }

        return this.endOfData();
    }
}
