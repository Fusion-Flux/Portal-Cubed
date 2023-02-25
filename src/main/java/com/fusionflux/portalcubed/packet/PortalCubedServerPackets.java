package com.fusionflux.portalcubed.packet;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.TallButtonVariant;
import com.fusionflux.portalcubed.blocks.VelocityHelperBlock;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.AdvancedEntityRaycast;
import com.fusionflux.portalcubed.util.PortalCubedComponents;
import com.fusionflux.portalcubed.util.PortalDirectionUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.apache.commons.lang3.tuple.Triple;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.PacketSender;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class PortalCubedServerPackets {
    public static final Identifier GRAB_KEY_PRESSED = id("grab_key_pressed");
    public static final Identifier REMOVE_PORTALS = id("remove_portals");
    public static final Identifier VELOCITY_HELPER_CONFIGURE = id("velocity_helper_configure");

    public static void onGrabKeyPressed(MinecraftServer server, ServerPlayerEntity player, @SuppressWarnings("unused") ServerPlayNetworkHandler handler, @SuppressWarnings("unused") PacketByteBuf buf, @SuppressWarnings("unused") PacketSender sender) {

        Vec3d vec3d = player.getCameraPosVec(0);
        double d = 3;

        Vec3d vec3d2 = player.getRotationVec(1.0F);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);

        server.execute(() -> {
            final AdvancedEntityRaycast.Result advancedCast = PortalDirectionUtils.raycast(player.world, new RaycastContext(
                vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player
            ));
            EntityHitResult entityHitResult = advancedCast.entityRaycast(player, (entity) -> !entity.isSpectator() && entity.collides());
            if (entityHitResult != null) {
                if (entityHitResult.getEntity() instanceof CorePhysicsEntity entity && !PortalCubedComponents.HOLDER_COMPONENT.get(player).hold(entity)) {
                    PortalCubedComponents.HOLDER_COMPONENT.get(player).stopHolding();
                }
            } else if (!PortalCubedComponents.HOLDER_COMPONENT.get(player).stopHolding()) {
                final BlockHitResult hit = advancedCast.finalHit();
                if (hit.getType() != HitResult.Type.MISS) {
                    final BlockState state = player.world.getBlockState(hit.getBlockPos());
                    if (
                        state.getBlock() instanceof TallButtonVariant button &&
                            player.interactionManager.interactBlock(player, player.world, ItemStack.EMPTY, Hand.MAIN_HAND, hit) != ActionResult.PASS
                    ) {
                        player.world.playSound(null, hit.getBlockPos(), button.getClickSound(true), SoundCategory.BLOCKS, 0.8f, 1f);
                        return;
                    }
                }
                player.playSound(PortalCubedSounds.NOTHING_TO_GRAB_EVENT, SoundCategory.NEUTRAL, 0.3f, 1f);
                ServerPlayNetworking.send(player, PortalCubedClientPackets.HAND_SHAKE_PACKET, PacketByteBufs.create());
            }
        });
    }

    public static void onRemovePortalKeyPressed(MinecraftServer server, ServerPlayerEntity player, @SuppressWarnings("unused") ServerPlayNetworkHandler handler, @SuppressWarnings("unused") PacketByteBuf buf, @SuppressWarnings("unused") PacketSender sender) {
        server.execute(() -> {
            boolean foundPortal = false;
            for (final UUID portal : List.copyOf(CalledValues.getPortals(player))) {
                final Entity checkPortal = ((ServerWorld)player.world).getEntity(portal);
                if (checkPortal != null) {
                    foundPortal = true;
                    checkPortal.kill();
                }
            }
            if (foundPortal) {
                ServerPlayNetworking.send(player, PortalCubedClientPackets.HAND_SHAKE_PACKET, PacketByteBufs.create());
                player.playSound(PortalCubedSounds.ENTITY_PORTAL_FIZZLE, SoundCategory.NEUTRAL, 0.5f, 1f);
                ServerPlayNetworking.send(player, PortalCubedClientPackets.HAND_SHAKE_PACKET, PacketByteBufs.create());
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static void onVelocityHelperConfigure(MinecraftServer server, ServerPlayerEntity player, @SuppressWarnings("unused") ServerPlayNetworkHandler handler, @SuppressWarnings("unused") PacketByteBuf buf, @SuppressWarnings("unused") PacketSender sender) {
        final BlockPos origin = buf.readBlockPos();
        final int mode = buf.readByte();
        final Object arg = switch (mode) {
            case VelocityHelperBlock.CONFIG_DEST -> buf.readOptional(PacketByteBuf::readBlockPos);
            case VelocityHelperBlock.CONFIG_OTHER -> Triple.of(buf.readVarInt(), buf.readString(), buf.readString());
            default -> {
                PortalCubed.LOGGER.error("Malformed velocity_helper_configure packet. Unknown mode {}.", mode);
                yield null;
            }
        };
        if (arg == null) return;
        server.execute(() -> player.getWorld().getBlockEntity(origin, PortalCubedBlocks.VELOCITY_HELPER_BLOCK_ENTITY).ifPresentOrElse(
            entity -> {
                switch (mode) {
                    case VelocityHelperBlock.CONFIG_DEST -> {
                        if (!player.isHolding(PortalCubedItems.HAMMER)) {
                            PortalCubed.LOGGER.warn("Received velocity_helper_configure from {}, who's not holding a hammer.", player);
                            return;
                        }
                        entity.setDestination(((Optional<BlockPos>)arg).orElse(null));
                    }
                    case VelocityHelperBlock.CONFIG_OTHER -> {
                        final var triple = (Triple<Integer, String, String>)arg;
                        entity.setFlightDuration(triple.getLeft());
                        entity.setCondition(triple.getMiddle());
                        entity.setInterpolationCurve(triple.getRight());
                    }
                }
                entity.updateListeners();
            },
            () -> PortalCubed.LOGGER.warn("Received velocity_helper_configure for unloaded velocity helper at {}.", origin)
        ));
    }

    public static void registerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(GRAB_KEY_PRESSED, PortalCubedServerPackets::onGrabKeyPressed);
        ServerPlayNetworking.registerGlobalReceiver(REMOVE_PORTALS, PortalCubedServerPackets::onRemovePortalKeyPressed);
        ServerPlayNetworking.registerGlobalReceiver(VELOCITY_HELPER_CONFIGURE, PortalCubedServerPackets::onVelocityHelperConfigure);
    }
}
