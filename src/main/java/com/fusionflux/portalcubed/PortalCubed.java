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
import com.fusionflux.portalcubed.compat.create.CreateIntegration;
import com.fusionflux.portalcubed.compat.rayon.RayonIntegration;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.block.content.registry.api.BlockContentRegistries;
import org.quiltmc.qsl.block.content.registry.api.FlammableBlockEntry;
import org.quiltmc.qsl.command.api.CommandRegistrationCallback;
import org.quiltmc.qsl.entity_events.api.EntityWorldChangeEvents;
import org.quiltmc.qsl.entity_events.api.ServerPlayerEntityCopyCallback;
import org.quiltmc.qsl.item.group.api.QuiltItemGroup;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayConnectionEvents;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;
import org.slf4j.Logger;

import java.util.UUID;

public class PortalCubed implements ModInitializer {

    public static final String MOD_ID = "portalcubed";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final CreativeModeTab TESTING_ELEMENTS_GROUP = QuiltItemGroup.createWithIcon(
            id("testing_elements"),
            () -> new ItemStack(PortalCubedItems.PORTAL_GUN));

    public static final MenuType<FaithPlateScreenHandler> FAITH_PLATE_SCREEN_HANDLER = Registry.register(
        Registry.MENU, id("faith_plate_screen"),
        new ExtendedScreenHandlerType<>(FaithPlateScreenHandler::new)
    );
    public static final MenuType<VelocityHelperScreenHandler> VELOCITY_HELPER_SCREEN_HANDLER = Registry.register(
        Registry.MENU, id("velocity_helper"),
        new ExtendedScreenHandlerType<>(VelocityHelperScreenHandler::new)
    );
    public static final MenuType<OptionsListScreenHandler> OPTIONS_LIST_SCREEN_HANDLER = Registry.register(
        Registry.MENU, id("options_list"),
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
            final Vec3 entityVelocity = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
            double teleportXOffset = buf.readDouble();
            double teleportYOffset = buf.readDouble();
            double teleportZOffset = buf.readDouble();
            if (!Float.isFinite(yawSet)) {
                handler.disconnect(Component.translatable("multiplayer.disconnect.invalid_player_movement"));
                return;
            }
            server.execute(() -> {
                if (!(player.level.getEntity(targetEntityId) instanceof ExperimentalPortal portal)) {
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
                Direction portalFacing = portal.getFacingDirection();
                Direction otherDirec = Direction.fromNormal((int) portal.getOtherFacing().x(), (int) portal.getOtherFacing().y(), (int) portal.getOtherFacing().z());
                Direction portalVertFacing = Direction.fromNormal(new BlockPos(portal.getAxisH().orElseThrow().x, portal.getAxisH().orElseThrow().y, portal.getAxisH().orElseThrow().z));

                IPQuaternion rotationW = IPQuaternion.getRotationBetween(portal.getAxisW().orElseThrow().scale(-1), portal.getOtherAxisW(), (portal.getAxisH().orElseThrow()));
                IPQuaternion rotationH = IPQuaternion.getRotationBetween((portal.getAxisH().orElseThrow()), (portal.getOtherAxisH()), portal.getAxisW().orElseThrow().scale(-1));

                if (portalFacing == Direction.UP || portalFacing == Direction.DOWN) {
                    if (otherDirec.equals(portalFacing) || (portalVertFacing  != otherDirec && portalVertFacing != otherDirec.getOpposite())) {
                        rotationW = IPQuaternion.getRotationBetween(portal.getNormal().scale(-1), portal.getOtherNormal(), (portal.getAxisH().orElseThrow()));
                        rotationH = IPQuaternion.getRotationBetween((portal.getAxisH().orElseThrow()), (portal.getOtherAxisH()), portal.getNormal().scale(-1));
                    }
                }

                Vec3 rotatedYaw = Vec3.directionFromRotation(pitchSet, yawSet);
                Vec3 rotatedPitch = Vec3.directionFromRotation(pitchSet, yawSet);
                Vec3 rotatedVel = entityVelocity;
                Vec3 rotatedOffsets = new Vec3(teleportXOffset, teleportYOffset, teleportZOffset);

                rotatedYaw = (rotationH.rotate(rotationW.rotate(rotatedYaw)));
                rotatedPitch = (rotationH.rotate(rotationW.rotate(rotatedPitch)));
                rotatedVel = (rotationH.rotate(rotationW.rotate(rotatedVel)));
                rotatedOffsets = (rotationH.rotate(rotationW.rotate(rotatedOffsets)));


                if (otherDirec == Direction.UP && rotatedVel.y < 0.48) {
                    rotatedVel = new Vec3(rotatedVel.x, 0.48, rotatedVel.z);
                }

                rotatedOffsets = rotatedOffsets.subtract(0, player.getEyeY() - player.getY(), 0);

                if (otherDirec != Direction.UP && otherDirec != Direction.DOWN) {
                    if (rotatedOffsets.y < -0.95) {
                        rotatedOffsets = new Vec3(rotatedOffsets.x, -0.95, rotatedOffsets.z);
                    } else if (rotatedOffsets.y > -0.85) {
                        rotatedOffsets = new Vec3(rotatedOffsets.x, -0.85, rotatedOffsets.z);
                    }
                }

                Vec2 lookAnglePitch = new Vec2(
                        (float)Math.toDegrees(-Mth.atan2(rotatedPitch.y, Math.sqrt(rotatedPitch.x * rotatedPitch.x + rotatedPitch.z * rotatedPitch.z))),
                        (float)Math.toDegrees(Mth.atan2(rotatedPitch.z, rotatedPitch.x))
                );

                Vec2 lookAngleYaw = new Vec2(
                        (float)Math.toDegrees(-Mth.atan2(rotatedYaw.y, Math.sqrt(rotatedYaw.x * rotatedYaw.x + rotatedYaw.z * rotatedYaw.z))),
                        (float)Math.toDegrees(Mth.atan2(rotatedYaw.z, rotatedYaw.x))
                );
                if (rotatedVel.lengthSqr() > PortalCubed.MAX_SPEED_SQR) {
                    rotatedVel = rotatedVel.scale(PortalCubed.MAX_SPEED / rotatedVel.length());
                }
                CalledValues.setVelocityUpdateAfterTeleport(player, rotatedVel);
                player.setYRot(lookAngleYaw.y - 90);
                player.setXRot(lookAnglePitch.x);
                player.moveTo(portal.getDestination().get().add(rotatedOffsets));
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
        BlockContentRegistries.FLAMMABLE_BLOCK.put(PortalCubedBlocks.NEUROTOXIN_BLOCK, new FlammableBlockEntry(10000, 10000));
        GravityChannel.UPDATE_GRAVITY.getVerifierRegistry().register(AdhesionGravityVerifier.FIELD_GRAVITY_SOURCE, AdhesionGravityVerifier::check);

        CommandRegistrationCallback.EVENT.register(new PortalCubedCommands());

        if (QuiltLoader.isModLoaded("create")) {
            CreateIntegration.init();
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

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

}
