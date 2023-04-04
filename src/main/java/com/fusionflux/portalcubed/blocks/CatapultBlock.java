package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.accessor.BlockCollisionTrigger;
import com.fusionflux.portalcubed.accessor.LivingEntityAccessor;
import com.fusionflux.portalcubed.blocks.blockentities.CatapultBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CatapultBlock extends SpecialHiddenBlockWithEntity implements BlockCollisionTrigger {
    private static final VoxelShape SHAPE = createCuboidShape(3, 3, 3, 13, 13, 13);

    public CatapultBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CatapultBlockEntity(pos, state);
    }

    @Override
    protected VoxelShape getVisibleOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getTriggerShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
    }

    @Override
    public void onEntityEnter(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntityAccessor livingEntity) {
            world.getBlockEntity(pos, PortalCubedBlocks.CATAPULT_BLOCK_ENTITY)
                .ifPresent(livingEntity::collidedWithCatapult);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.isCreative()) return ActionResult.PASS;
        if (!world.isClient) {
            final NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);

            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }
}
