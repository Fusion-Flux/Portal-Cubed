package com.fusionflux.portalcubed;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.GravityChannel;
import com.fusionflux.gravity_api.util.RotationUtil;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.blocks.PortalBlocksLoader;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.TallButtonVariant;
import com.fusionflux.portalcubed.blocks.blockentities.BetaFaithPlateBlockEntity;
import com.fusionflux.portalcubed.blocks.blockentities.FaithPlateBlockEntity;
import com.fusionflux.portalcubed.client.AdhesionGravityVerifier;
import com.fusionflux.portalcubed.client.PortalCubedClient;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.commands.PortalCubedCommands;
import com.fusionflux.portalcubed.compat.create.CreateIntegration;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.entity.PortalCubedTrackedDataHandlers;
import com.fusionflux.portalcubed.fluids.PortalCubedFluids;
import com.fusionflux.portalcubed.gui.FaithPlateScreenHandler;
import com.fusionflux.portalcubed.gui.VelocityHelperScreenHandler;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.optionslist.OptionsListScreenHandler;
import com.fusionflux.portalcubed.packet.PortalCubedServerPackets;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.IPQuaternion;
import com.mojang.logging.LogUtils;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.block.content.registry.api.BlockContentRegistries;
import org.quiltmc.qsl.block.content.registry.api.FlammableBlockEntry;
import org.quiltmc.qsl.command.api.CommandRegistrationCallback;
import org.quiltmc.qsl.item.group.api.QuiltItemGroup;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayConnectionEvents;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
import org.slf4j.Logger;

import java.util.UUID;

public class PortalCubed implements ModInitializer {

    public static final String MOD_ID = "portalcubed";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ItemGroup TESTING_ELEMENTS_GROUP = QuiltItemGroup.createWithIcon(
            id("testing_elements"),
            () -> new ItemStack(PortalCubedItems.PORTAL_GUN));

    public static final ItemGroup PORTAL_BLOCKS_GROUP = QuiltItemGroup.createWithIcon(
            id("portal_blocks"),
            () -> new ItemStack(PortalCubedItems.BLOCK_ITEM_ICON));

    public static final ScreenHandlerType<FaithPlateScreenHandler> FAITH_PLATE_SCREEN_HANDLER = Registry.register(
        Registry.SCREEN_HANDLER, id("faith_plate_screen"),
        new ExtendedScreenHandlerType<>(FaithPlateScreenHandler::new)
    );
    public static final ScreenHandlerType<VelocityHelperScreenHandler> VELOCITY_HELPER_SCREEN_HANDLER = Registry.register(
        Registry.SCREEN_HANDLER, id("velocity_helper"),
        new ExtendedScreenHandlerType<>(VelocityHelperScreenHandler::new)
    );
    public static final ScreenHandlerType<OptionsListScreenHandler> OPTIONS_LIST_SCREEN_HANDLER = Registry.register(
        Registry.SCREEN_HANDLER, id("options_list"),
        new ExtendedScreenHandlerType<>(OptionsListScreenHandler::new)
    );

    @Override
    public void onInitialize(ModContainer mod) {
        ServerPlayNetworking.registerGlobalReceiver(id("use_portal"), (server, player, handler, buf, responseSender) -> {
            // read the velocity from the byte buf
            final int targetEntityId = buf.readVarInt();
            float yawSet = buf.readFloat();
            float pitchSet = buf.readFloat();
            final Vec3d entityVelocity = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
            double teleportXOffset = buf.readDouble();
            double teleportYOffset = buf.readDouble();
            double teleportZOffset = buf.readDouble();
            if (!Float.isFinite(yawSet)) {
                handler.disconnect(Text.translatable("multiplayer.disconnect.invalid_player_movement"));
                return;
            }
            server.execute(() -> {
                if (!(player.world.getEntityById(targetEntityId) instanceof ExperimentalPortal portal)) {
                    LOGGER.warn("{} tried to teleport through nonexistent portal", player);
                    handler.requestTeleport(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
                    CalledValues.setIsTeleporting(player, false);
                    GravityChangerAPI.clearGravity(player);
                    return;
                }
                if (portal.getPos().squaredDistanceTo(player.getPos()) > 10 * 10) {
                    LOGGER.warn("{} tried to teleport through distant portal ({})", player, portal.getPos().distanceTo(player.getPos()));
                    handler.requestTeleport(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
                    CalledValues.setIsTeleporting(player, false);
                    GravityChangerAPI.clearGravity(player);
                    return;
                }
                if (portal.getDestination().isEmpty()) {
                    LOGGER.warn("{} tried to teleport through an inactive portal ({}).", player, portal);
                    handler.requestTeleport(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
                    CalledValues.setIsTeleporting(player, false);
                    GravityChangerAPI.clearGravity(player);
                    return;
                }
                Direction portalFacing = portal.getFacingDirection();
                Direction otherDirec = Direction.fromVector((int) portal.getOtherFacing().getX(), (int) portal.getOtherFacing().getY(), (int) portal.getOtherFacing().getZ());
                Direction portalVertFacing = Direction.fromVector(new BlockPos(portal.getAxisH().orElseThrow().x, portal.getAxisH().orElseThrow().y, portal.getAxisH().orElseThrow().z));

                IPQuaternion rotationW = IPQuaternion.getRotationBetween(portal.getAxisW().orElseThrow().multiply(-1), portal.getOtherAxisW(), (portal.getAxisH().orElseThrow()));
                IPQuaternion rotationH = IPQuaternion.getRotationBetween((portal.getAxisH().orElseThrow()), (portal.getOtherAxisH()), portal.getAxisW().orElseThrow().multiply(-1));

                if (portalFacing == Direction.UP || portalFacing == Direction.DOWN) {
                    if (otherDirec.equals(portalFacing) || (portalVertFacing  != otherDirec && portalVertFacing != otherDirec.getOpposite())) {
                        rotationW = IPQuaternion.getRotationBetween(portal.getNormal().multiply(-1), portal.getOtherNormal(), (portal.getAxisH().orElseThrow()));
                        rotationH = IPQuaternion.getRotationBetween((portal.getAxisH().orElseThrow()), (portal.getOtherAxisH()), portal.getNormal().multiply(-1));
                    }
                }

                float modPitch = pitchSet;
                if (modPitch == 90) {
                    modPitch = 0;
                }
                Vec3d rotatedYaw = Vec3d.fromPolar(modPitch, yawSet);
                Vec3d rotatedPitch = Vec3d.fromPolar(pitchSet, yawSet);
                Vec3d rotatedVel = entityVelocity;
                Vec3d rotatedOffsets = new Vec3d(teleportXOffset, teleportYOffset, teleportZOffset);

                rotatedYaw = (rotationH.rotate(rotationW.rotate(rotatedYaw)));
                rotatedPitch = (rotationH.rotate(rotationW.rotate(rotatedPitch)));
                rotatedVel = (rotationH.rotate(rotationW.rotate(rotatedVel)));
                rotatedOffsets = (rotationH.rotate(rotationW.rotate(rotatedOffsets)));


                if (otherDirec == Direction.UP && rotatedVel.y < 0.48) {
                    rotatedVel = new Vec3d(rotatedVel.x, 0.48, rotatedVel.z);
                }

                rotatedOffsets = rotatedOffsets.subtract(0, player.getEyeY() - player.getY(), 0);

                if (otherDirec != Direction.UP && otherDirec != Direction.DOWN) {
                    if (rotatedOffsets.y < -0.95) {
                        rotatedOffsets = new Vec3d(rotatedOffsets.x, -0.95, rotatedOffsets.z);
                    } else if (rotatedOffsets.y > -0.85) {
                        rotatedOffsets = new Vec3d(rotatedOffsets.x, -0.85, rotatedOffsets.z);
                    }
                }

                Vec2f lookAnglePitch = new Vec2f(
                        (float)Math.toDegrees(-MathHelper.atan2(rotatedPitch.y, Math.sqrt(rotatedPitch.x * rotatedPitch.x + rotatedPitch.z * rotatedPitch.z))),
                        (float)Math.toDegrees(MathHelper.atan2(rotatedPitch.z, rotatedPitch.x))
                );

                Vec2f lookAngleYaw = new Vec2f(
                        (float)Math.toDegrees(-MathHelper.atan2(rotatedYaw.y, Math.sqrt(rotatedYaw.x * rotatedYaw.x + rotatedYaw.z * rotatedYaw.z))),
                        (float)Math.toDegrees(MathHelper.atan2(rotatedYaw.z, rotatedYaw.x))
                );
                CalledValues.setVelocityUpdateAfterTeleport(player, rotatedVel);
                player.setYaw(lookAngleYaw.y - 90);
                player.setPitch(lookAnglePitch.x);
                player.refreshPositionAfterTeleport(portal.getDestination().get().add(rotatedOffsets));
                CalledValues.setHasTeleportationHappened(player, true);
                GravityChangerAPI.clearGravity(player);
            });
        });




        ServerPlayNetworking.registerGlobalReceiver(id("configure_faith_plate"), (server, player, handler, buf, responseSender) -> {
            // read the velocity from the byte buf
            BlockPos target = buf.readBlockPos();
            double x =  buf.readDouble();
            double y =  buf.readDouble();
            double z =  buf.readDouble();
            server.execute(() -> {
                BlockEntity entity = player.world.getBlockEntity(target);
                if (entity instanceof FaithPlateBlockEntity plate) {
                    plate.setVelX(x);
                    plate.setVelY(y);
                    plate.setVelZ(z);
                }
                if (entity instanceof BetaFaithPlateBlockEntity plate) {
                    plate.setVelX(x);
                    plate.setVelY(y);
                    plate.setVelZ(z);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(id("client_teleport_update"), (server, player, handler, buf, responseSender) ->
            server.execute(() -> CalledValues.setHasTeleportationHappened(player, false))
        );

        ServerPlayNetworking.registerGlobalReceiver(id("request_velocity_for_gel"), (server, player, handler, buf, responseSender) -> {
            final Vec3d entityVelocity = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
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
            UUID cubeuuid =  buf.readUuid();
            server.execute(() -> {
                if (!(player.getWorld().getEntity(cubeuuid) instanceof CorePhysicsEntity cube)) {
                    LOGGER.warn("{} tried to drop nonexistent physics object", player);
                    return;
                }
                if (!player.getUuid().equals(cube.getHolderUUID().orElse(null))) {
                    LOGGER.warn("{} tried to drop another player's physics object (held by {})", player, cube.getHolderUUID());
                    return;
                }
                cube.setRotYaw(rotYaw);
                Vec3d cubePos = new Vec3d(x, y, z);
                Vec3d lastCubePos = new Vec3d(lastX, lastY, lastZ);
                if (cubePos.squaredDistanceTo(lastCubePos) > 10 * 10) {
                    LOGGER.warn("{} tried to throw a physics object really fast ({})", player, cubePos.distanceTo(lastCubePos));
                    return;
                }

                if (cubePos.squaredDistanceTo(player.getPos()) > 10 * 10) {
                    LOGGER.warn("{} tried to drop physics object far away ({})", player, cubePos.distanceTo(player.getPos()));
                    return;
                }
                cube.setPosition(cubePos);
                cube.setVelocity(RotationUtil.vecWorldToPlayer(cubePos.subtract(lastCubePos), GravityChangerAPI.getGravityDirection(cube)).multiply(.5));
            });
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (!handler.player.world.getGameRules().getBoolean(PortalCubedGameRules.ALLOW_CROUCH_FLY_GLITCH)) {
                // Default is true on the client, so we don't need to send in that case
                final PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(false);
                handler.sendPacket(ServerPlayNetworking.createS2CPacket(PortalCubedClientPackets.ENABLE_CFG, buf));
            }
            if (handler.player.world.getGameRules().getBoolean(PortalCubedGameRules.USE_PORTAL_HUD)) {
                // Same as above, but false
                final PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(true);
                handler.sendPacket(ServerPlayNetworking.createS2CPacket(PortalCubedClientPackets.ENABLE_PORTAL_HUD, buf));
            }
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) ->
            PortalCubedClient.isPortalHudMode() &&
                (!(world.getBlockState(hitResult.getBlockPos()).getBlock() instanceof TallButtonVariant) ||
                    hand != Hand.OFF_HAND)
                ? ActionResult.FAIL : ActionResult.PASS
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
        BlockContentRegistries.FLAMMABLE_BLOCK.put(PortalCubedBlocks.NEUROTOXIN_BLOCK, new FlammableBlockEntry(10000, 10000));
        GravityChannel.UPDATE_GRAVITY.getVerifierRegistry().register(AdhesionGravityVerifier.FIELD_GRAVITY_SOURCE, AdhesionGravityVerifier::check);

        CommandRegistrationCallback.EVENT.register(new PortalCubedCommands());

        if (QuiltLoader.isModLoaded("create")) {
            CreateIntegration.init();
        }
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

}
