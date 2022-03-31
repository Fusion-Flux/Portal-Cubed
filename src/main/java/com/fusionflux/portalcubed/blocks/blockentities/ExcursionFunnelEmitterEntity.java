package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.accessor.CustomRaycastContext;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author sailKite
 * @author FusionFlux
 * <p>
 * Handles the operating logic for the {@link HardLightBridgeEmitterBlock} and their associated bridges.
 */
public class ExcursionFunnelEmitterEntity extends BlockEntity {

    public final int MAX_RANGE = PortalCubedConfig.get().numbersblock.maxBridgeLength;

    private BlockPos.Mutable obstructorPos;

    public ExcursionFunnelEmitterEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.EXCURSION_FUNNEL_EMMITER_ENTITY,pos,state);
        this.obstructorPos = pos.mutableCopy();
    }

    public static void tick(World world, BlockPos pos, BlockState state, ExcursionFunnelEmitterEntity blockEntity) {

        Direction facing = state.get(Properties.FACING);

        double dirX = (state.get(Properties.FACING).getOffsetX());
        double dirY = (state.get(Properties.FACING).getOffsetY());
        double dirZ = (state.get(Properties.FACING).getOffsetZ());

        //System.out.println(dirX);
        //System.out.println(dirY);
        //System.out.println(dirZ);

        //Vec3d endPos = new Vec3d((pos.getX() +.5)  + dirX* blockEntity.MAX_RANGE , (pos.getY() +.5) + dirY* blockEntity.MAX_RANGE,(pos.getZ() +.5) + dirZ* blockEntity.MAX_RANGE);
        //Vec3d blockFacePos = new Vec3d((pos.getX() +.5) +dirX/2  , (pos.getY() +.5) +dirY/2,(pos.getZ() +.5) +dirZ/2);
        //Vec3d startPos = new Vec3d((pos.getX() ) +dirX  , (pos.getY() ) +dirY,(pos.getZ() ) +dirZ);
        Vec3d endPos = new Vec3d((pos.getX())  + dirX* blockEntity.MAX_RANGE , (pos.getY()) + dirY* blockEntity.MAX_RANGE,(pos.getZ()) + dirZ* blockEntity.MAX_RANGE);


        //System.out.println(blockFacePos);
        //Vec3d endPos = new Vec3d((blockEntity.pos.getX()-.5) + dirX + blockEntity.getCachedState().get(Properties.FACING).getOffsetX()*blockEntity.MAX_RANGE,(blockEntity.pos.getY()-.5)+dirY+ blockEntity.getCachedState().get(Properties.FACING).getOffsetX()*blockEntity.MAX_RANGE,(blockEntity.pos.getZ()-.5) +dirZ+ blockEntity.getCachedState().get(Properties.FACING).getOffsetX()*blockEntity.MAX_RANGE);
        //Vec3d blockFacePos = new Vec3d((blockEntity.pos.getX()-.5) + dirX,(blockEntity.pos.getY()-.5)+dirY,(blockEntity.pos.getZ()-.5) +dirZ);
        //blockFacePos.add(blockEntity.getCachedState().get(Properties.FACING));



       // BlockHitResult hitResult = static_raycastBlock(world, blockFacePos, endPos, poss -> false);
        //System.out.println(hitResult.getPos());

        BlockPos startpos = new BlockPos((pos.getX() ) +dirX  , (pos.getY() ) +dirY,(pos.getZ() ) +dirZ);

        //Iterator var9 = BlockPos.iterate(startpos,hitResult.getBlockPos()).iterator();

        for (BlockPos blocks : BlockPos.iterate(startpos,new BlockPos(endPos.x,endPos.y,endPos.z))) {
            //world.setBlockState(blocks, PortalCubedBlocks.EXCURSION_FUNNEL.getDefaultState().with(Properties.FACING, facing) );

        }

       /* for (BlockPos blocks : BlockPos.iterate(startpos,new BlockPos(hitResult.getBlockPos().getX()-dirX/2,hitResult.getBlockPos().getY()-dirY/2,hitResult.getBlockPos().getZ()-dirZ/2))) {
            world.setBlockState(blocks, PortalCubedBlocks.EXCURSION_FUNNEL.getDefaultState().with(Properties.FACING, facing) );
        }*/

        //System.out.println(hitResult.getPos());
    }

    public void playSound(SoundEvent soundEvent) {
        this.world.playSound(null, this.pos, soundEvent, SoundCategory.BLOCKS, 0.1F, 3.0F);
    }

    public void playSound2(SoundEvent soundEvent) {
        this.world.playSound(null, this.pos, soundEvent, SoundCategory.BLOCKS, 0.05F, 3.0F);
    }




    /**
     * Used to correct a bug with the initial assignment and manipulation of {@link #obstructorPos}
     * during its first {@link}. Without this method or some other solution, the position will
     * always be [ 0, 0, 0 ].
     *
     * @param ownerPos the {@link BlockPos} of the owning {@link HardLightBridgeEmitterBlock}.
     */
    public void spookyUpdateObstructor(BlockPos ownerPos) {
        this.obstructorPos.set(ownerPos);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);

        // Due to limitations of CompoundTag, we have to separately write each part of any BlockPos
        tag.putInt("obsx", obstructorPos.getX());
        tag.putInt("obsy", obstructorPos.getY());
        tag.putInt("obsz", obstructorPos.getZ());

    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);


        // Due to limitations of CompoundTag, we have to separately read each part of any BlockPos
        obstructorPos = new BlockPos.Mutable(
                tag.getInt("obsx"),
                tag.getInt("obsy"),
                tag.getInt("obsz")
        );
    }

    private void togglePowered(BlockState state) {
        assert world != null;
        world.setBlockState(pos, state.cycle(Properties.POWERED));
        if (world.getBlockState(pos).get(Properties.POWERED)) {
            this.playSound(SoundEvents.BLOCK_BEACON_ACTIVATE);
        }
        if (!world.getBlockState(pos).get(Properties.POWERED)) {
            this.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE);
        }
    }

    public static BlockHitResult static_raycastBlock(World world, Vec3d start, Vec3d end, Predicate<BlockPos> shouldSkip) {
        return BlockView.raycast(start, end, new CustomRaycastContext(start, end, RaycastContext.FluidHandling.NONE), (ctx, poss) -> {
            if (shouldSkip.test(poss)) return null;
            BlockState state = world.getBlockState(poss);
            VoxelShape shape =  Block.createCuboidShape(0.0D, 0.0D, 0.0D, 0.0D, 16.0D, 0.0D);
            return world.raycastBlock(start, end, poss, shape, state);
        }, (ctx)-> {
            Vec3d vec3d = ctx.getStart().subtract(ctx.getEnd());
            return BlockHitResult.createMissed(ctx.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), new BlockPos(ctx.getEnd()));
        });
    }

}