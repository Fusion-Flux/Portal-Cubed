package com.fusionflux.portalcubed.accessor;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;

public class CustomRaycastContext {

    private final Vec3d start;
    private final Vec3d end;
    private final RaycastContext.FluidHandling fluid;
    private final ShapeContext entityPosition;


    public CustomRaycastContext(Vec3d start, Vec3d end, RaycastContext.FluidHandling fluidHandling) {
        this.start = start;
        this.end = end;
        this.fluid = fluidHandling;
        this.entityPosition = ShapeContext.absent();
    }

    public Vec3d getEnd() {
        return this.end;
    }

    public Vec3d getStart() {
        return this.start;
    }

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



    public interface ShapeProvider {
        VoxelShape get(BlockState state, BlockView world, BlockPos pos, ShapeContext context);
    }
}
