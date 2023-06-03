package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.blocks.blockentities.LaserNodeBlockEntity;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public abstract class AbstractLaserNodeBlock extends BaseEntityBlock {
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

    protected AbstractLaserNodeBlock(Properties settings) {
        super(settings);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSignalSource(BlockState state) {
        return state.getValue(BlockStateProperties.ENABLED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
        if (state.getValue(BlockStateProperties.ENABLED)) {
            return 15;
        }
        return 0;
    }

    @Override
    @ClientOnly
    @SuppressWarnings("deprecation")
    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return true;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LaserNodeBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, PortalCubedBlocks.LASER_NODE_BLOCK_ENTITY, LaserNodeBlockEntity::tick);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!player.getItemInHand(hand).is(PortalCubedItems.HAMMER)) {
            return InteractionResult.PASS;
        }
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        world.getBlockEntity(pos, PortalCubedBlocks.LASER_NODE_BLOCK_ENTITY).ifPresent(entity -> entity.setSound(
            switch (entity.getSound().toString()) {
                case "portalcubed:laser/node_music" -> PortalCubedSounds.LASER_TRIPLE_LASER_SOUND_DEMO_1;
                case "portalcubed:laser/triple_laser_sound_demo_1" -> PortalCubedSounds.LASER_TRIPLE_LASER_SOUND_DEMO_2;
                case "portalcubed:laser/triple_laser_sound_demo_2" -> PortalCubedSounds.LASER_TRIPLE_LASER_SOUND_DEMO_3;
                default -> PortalCubedSounds.LASER_NODE_MUSIC;
            }
        ));
        return InteractionResult.CONSUME;
    }
}
