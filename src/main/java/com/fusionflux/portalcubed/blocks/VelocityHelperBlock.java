package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.accessor.BlockCollisionTrigger;
import com.fusionflux.portalcubed.accessor.EntityExt;
import com.fusionflux.portalcubed.blocks.blockentities.VelocityHelperBlockEntity;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.packet.PortalCubedServerPackets;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import java.util.Optional;
import java.util.function.Consumer;

public class VelocityHelperBlock extends SpecialHiddenBlockWithEntity implements BlockCollisionTrigger {
    public static final int CONFIG_DEST = 0;
    public static final int CONFIG_OTHER = 1;

    public static final VoxelShape SHAPE = box(4, 4, 4, 12, 12, 12);

    public VelocityHelperBlock(Properties settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VelocityHelperBlockEntity(pos, state);
    }

    @Override
    protected VoxelShape getVisibleOutlineShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getTriggerShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    public void onEntityEnter(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (entity instanceof EntityExt ext) {
            world.getBlockEntity(pos, PortalCubedBlocks.VELOCITY_HELPER_BLOCK_ENTITY)
                .ifPresent(ext::collidedWithVelocityHelper);
        }
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!player.isCreative()) return InteractionResult.PASS;
        final ItemStack stack = player.getItemInHand(hand);
        if (stack.is(PortalCubedItems.HAMMER)) {
            final VelocityHelperBlockEntity entity = world.getBlockEntity(pos, PortalCubedBlocks.VELOCITY_HELPER_BLOCK_ENTITY).orElse(null);
            if (entity != null) {
                if (!world.isClientSide) {
                    return InteractionResult.PASS;
                }
                if (PortalCubedClient.velocityHelperDragStart == null) {
                    if (entity.getDestination() == null) {
                        PortalCubedClient.velocityHelperDragStart = pos;
                    } else {
                        sendLinkPacket(pos, null);
                    }
                } else if (PortalCubedClient.velocityHelperDragStart.equals(pos)) {
                    PortalCubedClient.velocityHelperDragStart = null;
                } else {
                    sendLinkPacket(PortalCubedClient.velocityHelperDragStart, pos);
                    PortalCubedClient.velocityHelperDragStart = null;
                }
                return InteractionResult.SUCCESS;
            }
        } else {
            if (!world.isClientSide) {
                final MenuProvider screenHandlerFactory = state.getMenuProvider(world, pos);

                if (screenHandlerFactory != null) {
                    player.openMenu(screenHandlerFactory);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @ClientOnly
    private static void sendLinkPacket(BlockPos origin, @Nullable BlockPos dest) {
        sendConfigurePacket(origin, CONFIG_DEST, buf -> buf.writeOptional(Optional.ofNullable(dest), FriendlyByteBuf::writeBlockPos));
    }

    @ClientOnly
    public static void sendConfigurePacket(BlockPos origin, int mode, Consumer<FriendlyByteBuf> writer) {
        final FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(origin);
        buf.writeByte(mode);
        writer.accept(buf);
        ClientPlayNetworking.send(PortalCubedServerPackets.VELOCITY_HELPER_CONFIGURE, buf);
    }
}
