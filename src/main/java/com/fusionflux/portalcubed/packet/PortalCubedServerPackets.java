package com.fusionflux.portalcubed.packet;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.TallButtonVariant;
import com.fusionflux.portalcubed.blocks.VelocityHelperBlock;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.compat.pehkui.PehkuiScaleTypes;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.entity.TurretEntity;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.AdvancedEntityRaycast;
import com.fusionflux.portalcubed.util.ClickHandlingItem;
import com.fusionflux.portalcubed.util.PortalCubedComponents;
import com.fusionflux.portalcubed.util.PortalDirectionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Triple;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.PacketSender;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class PortalCubedServerPackets {
    public static final ResourceLocation GRAB_KEY_PRESSED = id("grab_key_pressed");
    public static final ResourceLocation REMOVE_PORTALS = id("remove_portals");
    public static final ResourceLocation VELOCITY_HELPER_CONFIGURE = id("velocity_helper_configure");
    public static final ResourceLocation OPTIONS_LIST_CONFIGURE = id("options_list_configure");
    public static final ResourceLocation PLAY_BOUNCE_SOUND = id("play_bounce_sound");
    public static final ResourceLocation CROWBAR_ATTACK = id("crowbar_attack");
    public static final ResourceLocation LEFT_CLICK = id("left_click");
    public static final ResourceLocation RIGHT_CLICK = id("right_click");
    public static final ResourceLocation SYNC_SHOOTER_ROT = id("sync_shooter_rot");

    public static void onGrabKeyPressed(MinecraftServer server, ServerPlayer player, @SuppressWarnings("unused") ServerGamePacketListenerImpl handler, @SuppressWarnings("unused") FriendlyByteBuf buf, @SuppressWarnings("unused") PacketSender sender) {

        Vec3 vec3d = player.getEyePosition(0);
        double d = 3 * PehkuiScaleTypes.ENTITY_REACH.getScaleData(player).getScale();

        Vec3 vec3d2 = player.getViewVector(1.0F);
        Vec3 vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);

        server.execute(() -> {
            final AdvancedEntityRaycast.Result advancedCast = PortalDirectionUtils.raycast(player.level(), new ClipContext(
                vec3d, vec3d3, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player
            ));
            EntityHitResult entityHitResult = advancedCast.entityRaycast(player, (entity) -> !entity.isSpectator() && entity.isPickable());
            if (entityHitResult != null) {
                if (entityHitResult.getEntity() instanceof CorePhysicsEntity entity && !PortalCubedComponents.HOLDER_COMPONENT.get(player).hold(entity)) {
                    PortalCubedComponents.HOLDER_COMPONENT.get(player).stopHolding();
                }
            } else if (!PortalCubedComponents.HOLDER_COMPONENT.get(player).stopHolding()) {
                if (advancedCast.finalHit().getType() == HitResult.Type.BLOCK) {
                    final BlockHitResult hit = (BlockHitResult)advancedCast.finalHit();
                    final BlockState state = player.level().getBlockState(hit.getBlockPos());
                    if (
                        state.getBlock() instanceof TallButtonVariant button &&
                            player.gameMode.useItemOn(player, player.level(), ItemStack.EMPTY, InteractionHand.OFF_HAND, hit) != InteractionResult.PASS
                    ) {
                        player.level().playSound(null, hit.getBlockPos(), button.getClickSound(true), SoundSource.BLOCKS, 0.8f, 1f);
                        return;
                    }
                }
                player.playNotifySound(PortalCubedSounds.HOLD_FAIL_EVENT, SoundSource.NEUTRAL, 0.3f, 1f);
                ServerPlayNetworking.send(player, PortalCubedClientPackets.HAND_SHAKE_PACKET, PacketByteBufs.create());
            }
        });
    }

    public static void onRemovePortalKeyPressed(MinecraftServer server, ServerPlayer player, @SuppressWarnings("unused") ServerGamePacketListenerImpl handler, @SuppressWarnings("unused") FriendlyByteBuf buf, @SuppressWarnings("unused") PacketSender sender) {
        server.execute(() -> {
            boolean foundPortal = false;
            for (final UUID portal : List.copyOf(CalledValues.getPortals(player))) {
                final Entity checkPortal = player.serverLevel().getEntity(portal);
                if (checkPortal != null) {
                    foundPortal = true;
                    checkPortal.kill();
                }
            }
            if (foundPortal) {
                ServerPlayNetworking.send(player, PortalCubedClientPackets.HAND_SHAKE_PACKET, PacketByteBufs.create());
                player.playNotifySound(PortalCubedSounds.ENTITY_PORTAL_FIZZLE, SoundSource.NEUTRAL, 0.5f, 1f);
                ServerPlayNetworking.send(player, PortalCubedClientPackets.HAND_SHAKE_PACKET, PacketByteBufs.create());
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static void onVelocityHelperConfigure(MinecraftServer server, ServerPlayer player, @SuppressWarnings("unused") ServerGamePacketListenerImpl handler, @SuppressWarnings("unused") FriendlyByteBuf buf, @SuppressWarnings("unused") PacketSender sender) {
        final BlockPos origin = buf.readBlockPos();
        final int mode = buf.readByte();
        final Object arg = switch (mode) {
            case VelocityHelperBlock.CONFIG_DEST -> buf.readOptional(FriendlyByteBuf::readBlockPos);
            case VelocityHelperBlock.CONFIG_OTHER -> Triple.of(buf.readVarInt(), buf.readUtf(), buf.readUtf());
            default -> {
                PortalCubed.LOGGER.error("Malformed velocity_helper_configure packet. Unknown mode {}.", mode);
                yield null;
            }
        };
        if (arg == null) return;
        server.execute(() -> player.level().getBlockEntity(origin, PortalCubedBlocks.VELOCITY_HELPER_BLOCK_ENTITY).ifPresentOrElse(
            entity -> {
                switch (mode) {
                    case VelocityHelperBlock.CONFIG_DEST -> {
                        if (!player.isHolding(s -> s.is(PortalCubedItems.WRENCHES))) {
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

    public static void onOptionsListConfigure(MinecraftServer server, ServerPlayer player, @SuppressWarnings("unused") ServerGamePacketListenerImpl handler, @SuppressWarnings("unused") FriendlyByteBuf buf, @SuppressWarnings("unused") PacketSender sender) {
//        final BlockPos origin = buf.readBlockPos();
//        final String json = buf.readUtf();
//        server.execute(() -> {
//            if (!(player.level().getBlockEntity(origin) instanceof OptionsListBlockEntity optionsListBlockEntity) || !player.isCreative()) {
//                return;
//            }
//            OptionsListData.read(json, optionsListBlockEntity);
//            optionsListBlockEntity.updateListeners();
//        });
    }

    public static void registerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(GRAB_KEY_PRESSED, PortalCubedServerPackets::onGrabKeyPressed);
        ServerPlayNetworking.registerGlobalReceiver(REMOVE_PORTALS, PortalCubedServerPackets::onRemovePortalKeyPressed);
        ServerPlayNetworking.registerGlobalReceiver(VELOCITY_HELPER_CONFIGURE, PortalCubedServerPackets::onVelocityHelperConfigure);
        ServerPlayNetworking.registerGlobalReceiver(OPTIONS_LIST_CONFIGURE, PortalCubedServerPackets::onOptionsListConfigure);
        ServerPlayNetworking.registerGlobalReceiver(
            PLAY_BOUNCE_SOUND, (server, player, handler, buf, responseSender) -> PortalCubed.playBounceSound(player)
        );
        ServerPlayNetworking.registerGlobalReceiver(CROWBAR_ATTACK, (server, player, handler, buf, responseSender) -> {
            final BlockHitResult hit = buf.readBlockHitResult();
            if (hit.getLocation().distanceToSqr(player.position()) > 100) {
                PortalCubed.LOGGER.warn(
                    "Player {} tried to use a crowbar to attack a distant block ({})",
                    player, hit.getLocation().distanceTo(player.position())
                );
            }
            server.execute(() -> {
                player.level().playSound(
                    player,
                    player.getX(), player.getY(), player.getZ(),
                    PortalCubedSounds.CROWBAR_SWOOSH_EVENT, SoundSource.PLAYERS,
                    1f, 1f
                );
                TurretEntity.makeBulletHole(player.serverLevel(), hit, SoundSource.PLAYERS);
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(LEFT_CLICK, (server, player, handler, buf, responseSender) -> server.execute(() -> {
            if (player.getMainHandItem().getItem() instanceof ClickHandlingItem chi) {
                if (chi.onLeftClick(player, InteractionHand.MAIN_HAND).shouldSwing()) player.swing(InteractionHand.MAIN_HAND, true);
            }
        }));
        ServerPlayNetworking.registerGlobalReceiver(RIGHT_CLICK, (server, player, handler, buf, responseSender) -> server.execute(() -> {
            if (player.getMainHandItem().getItem() instanceof ClickHandlingItem chi) {
                if (chi.onRightClick(player, InteractionHand.MAIN_HAND).shouldSwing()) player.swing(InteractionHand.MAIN_HAND, true);
            }
        }));
        ServerPlayNetworking.registerGlobalReceiver(SYNC_SHOOTER_ROT, (server, player, handler, buf, responseSender) -> {
            final float xRot = buf.readFloat();
            final float yRot = buf.readFloat();
            server.execute(() -> {
                player.setXRot(xRot);
                player.setYRot(yRot);
                player.setYHeadRot(yRot);
                // The O's are the ones that are actually used for getViewVector!
                player.xRotO = xRot;
                player.yHeadRotO = yRot;
            });
        });
    }
}
