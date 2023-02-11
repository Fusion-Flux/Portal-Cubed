package com.fusionflux.portalcubed.blocks;

import com.fusionflux.portalcubed.accessor.BlockCollisionTrigger;
import com.fusionflux.portalcubed.accessor.LivingEntityAccessor;
import com.fusionflux.portalcubed.blocks.blockentities.VelocityHelperBlockEntity;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.packet.PortalCubedServerPackets;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
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
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import java.util.Optional;
import java.util.function.Consumer;

public class VelocityHelperBlock extends SpecialHiddenBlockWithEntity implements BlockCollisionTrigger {
    public static final int CONFIG_DEST = 0;
    public static final int CONFIG_OTHER = 1;

    public static final VoxelShape SHAPE = createCuboidShape(4, 4, 4, 12, 12, 12);

    public VelocityHelperBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new VelocityHelperBlockEntity(pos, state);
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
            world.getBlockEntity(pos, PortalCubedBlocks.VELOCITY_HELPER_BLOCK_ENTITY)
                .ifPresent(livingEntity::collidedWithVelocityHelper);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.isCreative()) return ActionResult.PASS;
        final ItemStack stack = player.getStackInHand(hand);
        if (stack.isOf(PortalCubedItems.HAMMER)) {
            final VelocityHelperBlockEntity entity = world.getBlockEntity(pos, PortalCubedBlocks.VELOCITY_HELPER_BLOCK_ENTITY).orElse(null);
            if (entity != null) {
                if (!world.isClient) {
                    return ActionResult.PASS;
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
                return ActionResult.SUCCESS;
            }
        } else {
            if (!world.isClient) {
                final NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);

                if (screenHandlerFactory != null) {
                    player.openHandledScreen(screenHandlerFactory);
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @ClientOnly
    private static void sendLinkPacket(BlockPos origin, @Nullable BlockPos dest) {
        sendConfigurePacket(origin, CONFIG_DEST, buf -> buf.writeOptional(Optional.ofNullable(dest), PacketByteBuf::writeBlockPos));
    }

    @ClientOnly
    public static void sendConfigurePacket(BlockPos origin, int mode, Consumer<PacketByteBuf> writer) {
        final PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(origin);
        buf.writeByte(mode);
        writer.accept(buf);
        ClientPlayNetworking.send(PortalCubedServerPackets.VELOCITY_HELPER_CONFIGURE, buf);
    }
}
