package com.fusionflux.portalcubed;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.GravityChannel;
import com.fusionflux.gravity_api.util.RotationUtil;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.blocks.PortalBlocksLoader;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.TallButtonVariant;
import com.fusionflux.portalcubed.blocks.blockentities.FaithPlateBlockEntity;
import com.fusionflux.portalcubed.client.AdhesionGravityVerifier;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.commands.PortalCubedCommands;
import com.fusionflux.portalcubed.compat.rayon.RayonIntegration;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.entity.Portal;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.entity.PortalCubedTrackedDataHandlers;
import com.fusionflux.portalcubed.fluids.PortalCubedFluids;
import com.fusionflux.portalcubed.fog.FogPersistentState;
import com.fusionflux.portalcubed.fog.FogSettings;
import com.fusionflux.portalcubed.gui.FaithPlateScreenHandler;
import com.fusionflux.portalcubed.gui.VelocityHelperScreenHandler;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.optionslist.OptionsListScreenHandler;
import com.fusionflux.portalcubed.packet.PortalCubedServerPackets;
import com.fusionflux.portalcubed.particle.PortalCubedParticleTypes;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.IPQuaternion;
import com.mojang.logging.LogUtils;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.block.content.registry.api.BlockContentRegistries;
import org.quiltmc.qsl.block.content.registry.api.FlammableBlockEntry;
import org.quiltmc.qsl.command.api.CommandRegistrationCallback;
import org.quiltmc.qsl.entity.event.api.EntityWorldChangeEvents;
import org.quiltmc.qsl.entity.event.api.ServerPlayerEntityCopyCallback;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayConnectionEvents;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.UUID;

public class PortalCubed implements ModInitializer {

    public static final String MOD_ID = "portalcubed";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final MenuType<FaithPlateScreenHandler> FAITH_PLATE_SCREEN_HANDLER = Registry.register(
        BuiltInRegistries.MENU, id("faith_plate_screen"),
        new ExtendedScreenHandlerType<>(FaithPlateScreenHandler::new)
    );
    public static final MenuType<VelocityHelperScreenHandler> VELOCITY_HELPER_SCREEN_HANDLER = Registry.register(
        BuiltInRegistries.MENU, id("velocity_helper"),
        new ExtendedScreenHandlerType<>(VelocityHelperScreenHandler::new)
    );
    public static final MenuType<OptionsListScreenHandler> OPTIONS_LIST_SCREEN_HANDLER = Registry.register(
        BuiltInRegistries.MENU, id("options_list"),
        new ExtendedScreenHandlerType<>(OptionsListScreenHandler::new)
    );

    public static final double MAX_SPEED = 2225 / 64.0 / 20.0, MAX_SPEED_SQR = MAX_SPEED * MAX_SPEED;

    @Override
    public void onInitialize(ModContainer mod) {
        ServerPlayNetworking.registerGlobalReceiver(id("use_portal"), (server, player, handler, buf, responseSender) -> {
            // read the velocity from the byte buf
            final int targetEntityId = buf.readVarInt();
            float yawSet = buf.readFloat();
            float pitchSet = buf.readFloat();
            Optional<IPQuaternion> currentAnimationDelta = buf.readOptional(b -> new IPQuaternion(
                b.readDouble(),
                b.readDouble(),
                b.readDouble(),
                b.readDouble()
            ));
            final Vec3 entityVelocity = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
            double teleportXOffset = buf.readDouble();
            double teleportYOffset = buf.readDouble();
            double teleportZOffset = buf.readDouble();
            if (!Float.isFinite(yawSet)) {
                handler.disconnect(Component.translatable("multiplayer.disconnect.invalid_player_movement"));
                return;
            }
            server.execute(() -> {
                if (!(player.level.getEntity(targetEntityId) instanceof Portal portal)) {
                    LOGGER.warn("{} tried to teleport through nonexistent portal", player);
                    handler.teleport(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
                    CalledValues.setIsTeleporting(player, false);
                    GravityChangerAPI.clearGravity(player);
                    return;
                }
                if (portal.position().distanceToSqr(player.position()) > 10 * 10) {
                    LOGGER.warn("{} tried to teleport through distant portal ({})", player, portal.position().distanceTo(player.position()));
                    handler.teleport(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
                    CalledValues.setIsTeleporting(player, false);
                    GravityChangerAPI.clearGravity(player);
                    return;
                }
                if (portal.getDestination().isEmpty()) {
                    LOGGER.warn("{} tried to teleport through an inactive portal ({}).", player, portal);
                    handler.teleport(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
                    CalledValues.setIsTeleporting(player, false);
                    GravityChangerAPI.clearGravity(player);
                    return;
                }

                final TeleportResult result = commonTeleport(
                    portal,
                    entityVelocity,
                    new Vec3(teleportXOffset, teleportYOffset, teleportZOffset),
                    player,
                    currentAnimationDelta,
                    pitchSet, yawSet
                );
                CalledValues.setVelocityUpdateAfterTeleport(player, result.velocity());

                final Vec3 dest = result.dest();
                final IPQuaternion cameraAnimation = IPQuaternion.getCameraRotation(result.pitch(), result.yaw())
                    .getConjugated()
                    .hamiltonProduct(result.immediateFinalRot());
                player.connection.teleport(dest.x, dest.y, dest.z, result.yaw(), result.pitch());
                final FriendlyByteBuf buf2 = PacketByteBufs.create();
                buf2.writeDouble(cameraAnimation.x);
                buf2.writeDouble(cameraAnimation.y);
                buf2.writeDouble(cameraAnimation.z);
                buf2.writeDouble(cameraAnimation.w);
                ServerPlayNetworking.send(player, PortalCubedClientPackets.SET_CAMERA_INTERPOLATE, buf2);

                CalledValues.setHasTeleportationHappened(player, true);
                GravityChangerAPI.clearGravity(player);

            });
        });

        ServerPlayNetworking.registerGlobalReceiver(id("configure_faith_plate"), (server, player, handler, buf, responseSender) -> {
            // read the velocity from the byte buf
            BlockPos target = buf.readBlockPos();
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            server.execute(() -> {
                final BlockEntity entity = player.level.getBlockEntity(target);
                if (entity instanceof FaithPlateBlockEntity faithPlateBlockEntity) {
                    faithPlateBlockEntity.setVelX(x);
                    faithPlateBlockEntity.setVelY(y);
                    faithPlateBlockEntity.setVelZ(z);
                    faithPlateBlockEntity.updateListeners();
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(id("client_teleport_update"), (server, player, handler, buf, responseSender) ->
            server.execute(() -> CalledValues.setHasTeleportationHappened(player, false))
        );

        ServerPlayNetworking.registerGlobalReceiver(id("request_velocity_for_gel"), (server, player, handler, buf, responseSender) -> {
            final Vec3 entityVelocity = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
            final boolean fireGel = buf.readBoolean();
            server.execute(() -> {
                CalledValues.setServerVelForGel(player, entityVelocity);
                CalledValues.setCanFireGel(player, fireGel);

            });
        });

        ServerPlayNetworking.registerGlobalReceiver(id("cube_pos_update"), (server, player, handler, buf, responseSender) -> {
            // read the velocity from the byte buf
            double x =  buf.readDouble();
            double y =  buf.readDouble();
            double z =  buf.readDouble();
            double lastX =  buf.readDouble();
            double lastY =  buf.readDouble();
            double lastZ =  buf.readDouble();
            float rotYaw = buf.readFloat();
            UUID cubeuuid =  buf.readUUID();
            server.execute(() -> {
                if (!(player.getLevel().getEntity(cubeuuid) instanceof CorePhysicsEntity cube)) {
                    LOGGER.warn("{} tried to drop nonexistent physics object", player);
                    return;
                }
                if (!player.getUUID().equals(cube.getHolderUUID().orElse(null))) {
                    LOGGER.warn("{} tried to drop another player's physics object (held by {})", player, cube.getHolderUUID());
                    return;
                }
                cube.setRotYaw(rotYaw);
                Vec3 cubePos = new Vec3(x, y, z);
                Vec3 lastCubePos = new Vec3(lastX, lastY, lastZ);
                if (cubePos.distanceToSqr(lastCubePos) > 10 * 10) {
                    LOGGER.warn("{} tried to throw a physics object really fast ({})", player, cubePos.distanceTo(lastCubePos));
                    return;
                }

                if (cubePos.distanceToSqr(player.position()) > 10 * 10) {
                    LOGGER.warn("{} tried to drop physics object far away ({})", player, cubePos.distanceTo(player.position()));
                    return;
                }
                cube.setPos(cubePos);
                cube.setDeltaMovement(RotationUtil.vecWorldToPlayer(cubePos.subtract(lastCubePos), GravityChangerAPI.getGravityDirection(cube)).scale(.5));
            });
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (!handler.player.level.getGameRules().getBoolean(PortalCubedGameRules.ALLOW_CROUCH_FLY_GLITCH)) {
                // Default is true on the client, so we don't need to send in that case
                final FriendlyByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(false);
                handler.send(ServerPlayNetworking.createS2CPacket(PortalCubedClientPackets.ENABLE_CFG, buf));
            }
            if (handler.player.level.getGameRules().getBoolean(PortalCubedGameRules.USE_PORTAL_HUD)) {
                // Same as above, but false
                final FriendlyByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(true);
                handler.send(ServerPlayNetworking.createS2CPacket(PortalCubedClientPackets.ENABLE_PORTAL_HUD, buf));
            }
            syncFog(handler.player);
        });

        EntityWorldChangeEvents.AFTER_PLAYER_WORLD_CHANGE.register((player, origin, destination) -> syncFog(player));

        ServerPlayerEntityCopyCallback.EVENT.register((copy, original, wasDeath) -> syncFog(copy));

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) ->
            PortalCubedClient.isPortalHudMode() &&
                (!(world.getBlockState(hitResult.getBlockPos()).getBlock() instanceof TallButtonVariant) ||
                    hand != InteractionHand.OFF_HAND)
                ? InteractionResult.FAIL : InteractionResult.PASS
        );

        MidnightConfig.init("portalcubed", PortalCubedConfig.class);
        PortalBlocksLoader.init(mod);
        PortalCubedBlocks.registerBlocks();
        PortalCubedFluids.registerFluids();
        PortalCubedItems.registerItems();
        PortalCubedEntities.registerEntities();
        PortalCubedTrackedDataHandlers.register();
        PortalCubedServerPackets.registerPackets();
        PortalCubedSounds.registerSounds();
        PortalCubedGameRules.register();
        PortalCubedParticleTypes.register();
        PortalTabsLoader.load(mod);
        BlockContentRegistries.FLAMMABLE.put(PortalCubedBlocks.NEUROTOXIN_BLOCK, new FlammableBlockEntry(10000, 10000));
        GravityChannel.UPDATE_GRAVITY.getVerifierRegistry().register(AdhesionGravityVerifier.FIELD_GRAVITY_SOURCE, AdhesionGravityVerifier::check);

        CommandRegistrationCallback.EVENT.register(new PortalCubedCommands());

        if (QuiltLoader.isModLoaded("create")) {
            LOGGER.warn("Create is out for this game version! Go poke the Portal Cubed developers on Discord to re-enable this integration.");
//            CreateIntegration.init();
        }

        RayonIntegration.INSTANCE.init();
    }

    public static void syncFog(ServerPlayer player) {
        final FriendlyByteBuf buf = PacketByteBufs.create();
        FogSettings.encodeOptional(FogPersistentState.getOrCreate((ServerLevel)player.level).getSettings(), buf);
        ServerPlayNetworking.send(player, PortalCubedClientPackets.SET_CUSTOM_FOG, buf);
    }

    public static void syncFog(ServerLevel world) {
        final FriendlyByteBuf buf = PacketByteBufs.create();
        FogSettings.encodeOptional(FogPersistentState.getOrCreate(world).getSettings(), buf);
        world.getServer().getPlayerList().broadcastAll(
            ServerPlayNetworking.createS2CPacket(PortalCubedClientPackets.SET_CUSTOM_FOG, buf),
            world.dimension()
        );
    }

    public static void playBounceSound(Entity entity) {
        entity.level.playSound(
            null,
            entity.position().x(), entity.position().y(), entity.position().z(),
            PortalCubedSounds.GEL_BOUNCE_EVENT, SoundSource.BLOCKS,
            1f, 0.95f + entity.level.random.nextFloat() * 0.1f
        );
    }

    @ClientOnly
    public static void playBounceSoundRemotely() {
        ClientPlayNetworking.send(PortalCubedServerPackets.PLAY_BOUNCE_SOUND, PacketByteBufs.empty());
    }

    public static TeleportResult commonTeleport(
        Portal portal,
        Vec3 entityVelocity,
        Vec3 teleportOffset,
        Entity entity,
        Optional<IPQuaternion> currentAnimationDelta,
        float pitchSet, float yawSet
    ) {
        assert portal.getDestination().isPresent();
        assert portal.getOtherNormal().isPresent();

        final Vec3 otherNormal = portal.getOtherNormal().get();
        Direction otherDirec = Direction.fromNormal((int) otherNormal.x(), (int) otherNormal.y(), (int) otherNormal.z());
        final IPQuaternion portalTransform = portal.getTransformQuat();

        Vec3 rotatedVel = entityVelocity;
        Vec3 rotatedOffsets = teleportOffset;

        rotatedVel = portalTransform.rotate(rotatedVel, false);
        rotatedOffsets = portalTransform.rotate(rotatedOffsets, false);

        if (otherDirec == Direction.UP && rotatedVel.y < 0.48) {
            rotatedVel = new Vec3(rotatedVel.x, 0.48, rotatedVel.z);
        }

        rotatedOffsets = rotatedOffsets.subtract(0, entity.getEyeY() - entity.getY(), 0);

        if (otherDirec != Direction.UP && otherDirec != Direction.DOWN) {
            if (rotatedOffsets.y < -0.95) {
                rotatedOffsets = new Vec3(rotatedOffsets.x, -0.95, rotatedOffsets.z);
            } else if (rotatedOffsets.y > -0.95 + (1.9 - entity.getBbHeight())) {
                rotatedOffsets = new Vec3(rotatedOffsets.x, -0.95 + (1.9 - entity.getBbHeight()), rotatedOffsets.z);
            }
        }

        if (rotatedVel.lengthSqr() > PortalCubed.MAX_SPEED_SQR) {
            rotatedVel = rotatedVel.scale(PortalCubed.MAX_SPEED / rotatedVel.length());
        }

        IPQuaternion oldCameraRotation = IPQuaternion.getCameraRotation(pitchSet, yawSet);
        if (currentAnimationDelta.isPresent()) {
            oldCameraRotation = oldCameraRotation.hamiltonProduct(currentAnimationDelta.get());
        }
        final IPQuaternion immediateFinalRot = oldCameraRotation.hamiltonProduct(portalTransform.getConjugated());
        final var pitchYaw = IPQuaternion.getPitchYawFromRotation(immediateFinalRot);
        float finalYaw = (float)(double)pitchYaw.getB();
        float finalPitch = (float)(double)pitchYaw.getA();
        if (finalPitch > 90) {
            finalPitch = 90 - (finalPitch - 90);
        } else if (finalPitch < -90) {
            finalPitch = -90 + (-90 - finalPitch);
        }

        final Vec3 dest = portal.getDestination().get().add(rotatedOffsets);

        return new TeleportResult(
            dest,
            finalYaw,
            finalPitch,
            rotatedVel,
            immediateFinalRot
        );
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

}
