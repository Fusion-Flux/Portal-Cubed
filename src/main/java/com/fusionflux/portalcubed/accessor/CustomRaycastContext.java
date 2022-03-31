package com.fusionflux.portalcubed.accessor;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;

import java.util.function.Predicate;

public class CustomRaycastContext {

    private final Vec3d start;
    private final Vec3d end;
    //private final RaycastContext.ShapeType shapeType;
    private final RaycastContext.FluidHandling fluid;
    private final ShapeContext entityPosition;


    public CustomRaycastContext(Vec3d start, Vec3d end, RaycastContext.FluidHandling fluidHandling) {
        this.start = start;
        this.end = end;
        //this.shapeType = shapeType;
        this.fluid = fluidHandling;
        this.entityPosition = ShapeContext.absent();
    }

    public Vec3d getEnd() {
        return this.end;
    }

    public Vec3d getStart() {
        return this.start;
    }

   // public VoxelShape getBlockShape(BlockState state, BlockView world, BlockPos pos) {
   //     return this.shapeType.get(state, world, pos, this.entityPosition);
   // }

    public VoxelShape getFluidShape(FluidState state, BlockView world, BlockPos pos) {
        return this.fluid.handled(state) ? state.getShape(world, pos) : VoxelShapes.empty();
    }

    public static enum ShapeType implements RaycastContext.ShapeProvider {
        COLLIDER(AbstractBlock.AbstractBlockState::getCollisionShape),
        OUTLINE(AbstractBlock.AbstractBlockState::getOutlineShape),
        VISUAL(AbstractBlock.AbstractBlockState::getCameraCollisionShape),
        FALLDAMAGE_RESETTING((state, world, pos, context) -> {
            return state.isIn(BlockTags.FALL_DAMAGE_RESETTING) ? VoxelShapes.fullCube() : VoxelShapes.empty();
        });

        private final RaycastContext.ShapeProvider provider;

        private ShapeType(RaycastContext.ShapeProvider provider) {
            this.provider = provider;
        }

        public VoxelShape get(BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext shapeContext) {
            return this.provider.get(blockState, blockView, blockPos, shapeContext);
        }
    }

    public static enum FluidHandling {
        NONE((state) -> {
            return false;
        }),
        SOURCE_ONLY(FluidState::isStill),
        ANY((state) -> {
            return !state.isEmpty();
        }),
        WATER((state) -> {
            return state.isIn(FluidTags.WATER);
        });

        private final Predicate<FluidState> predicate;

        private FluidHandling(Predicate<FluidState> predicate) {
            this.predicate = predicate;
        }

        public boolean handled(FluidState state) {
            return this.predicate.test(state);
        }
    }

    public interface ShapeProvider {
        VoxelShape get(BlockState state, BlockView world, BlockPos pos, ShapeContext context);
    }
}
