package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.blocks.blockentities.LaserNodeBlockEntity;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public abstract class AbstractLaserNodeBlock extends BlockWithEntity {
    public static final BooleanProperty ENABLED = Properties.ENABLED;

    protected AbstractLaserNodeBlock(Settings settings) {
        super(settings);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean emitsRedstonePower(BlockState state) {
        return state.get(Properties.ENABLED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (state.get(Properties.ENABLED)) {
            return 15;
        }
        return 0;
    }

    @Override
    @ClientOnly
    @SuppressWarnings("deprecation")
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LaserNodeBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, PortalCubedBlocks.LASER_NODE_BLOCK_ENTITY, LaserNodeBlockEntity::tick);
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.getStackInHand(hand).isOf(PortalCubedItems.HAMMER)) {
            return ActionResult.PASS;
        }
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        world.getBlockEntity(pos, PortalCubedBlocks.LASER_NODE_BLOCK_ENTITY).ifPresent(entity -> entity.setSound(
            switch (entity.getSound().toString()) {
                case "portalcubed:laser/node_music" -> PortalCubedSounds.LASER_TRIPLE_LASER_SOUND_DEMO_1;
                case "portalcubed:laser/triple_laser_sound_demo_1" -> PortalCubedSounds.LASER_TRIPLE_LASER_SOUND_DEMO_2;
                case "portalcubed:laser/triple_laser_sound_demo_2" -> PortalCubedSounds.LASER_TRIPLE_LASER_SOUND_DEMO_3;
                default -> PortalCubedSounds.LASER_NODE_MUSIC;
            }
        ));
        return ActionResult.CONSUME;
    }
}
