package com.fusionflux.portalcubed;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.GravityChannel;
import com.fusionflux.gravity_api.util.RotationUtil;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.blocks.PortalBlocksLoader;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.blockentities.BetaFaithPlateBlockEntity;
import com.fusionflux.portalcubed.blocks.blockentities.FaithPlateBlockEntity;
import com.fusionflux.portalcubed.client.AdhesionGravityVerifier;
import com.fusionflux.portalcubed.client.packet.PortalCubedClientPackets;
import com.fusionflux.portalcubed.commands.PortalCubedCommands;
import com.fusionflux.portalcubed.compatability.create.CreateIntegration;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.entity.PortalCubedTrackedDataHandlers;
import com.fusionflux.portalcubed.fluids.PortalCubedFluids;
import com.fusionflux.portalcubed.gui.FaithPlateScreenHandler;
import com.fusionflux.portalcubed.gui.VelocityHelperScreenHandler;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.packet.PortalCubedServerPackets;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.PortalVelocityHelper;
import com.mojang.logging.LogUtils;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
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
                Direction portalVertFacing = Direction.fromVector(new BlockPos(portal.getAxisH().get().x, portal.getAxisH().get().y, portal.getAxisH().get().z));

                Direction otherDirec = Direction.fromVector((int) portal.getOtherFacing().getX(), (int) portal.getOtherFacing().getY(), (int) portal.getOtherFacing().getZ());
                Direction otherPortalVertFacing = Direction.fromVector(new BlockPos(portal.getOtherAxisH().x, portal.getOtherAxisH().y, portal.getOtherAxisH().z));

                Vec3d rotatedOffsets = new Vec3d(teleportXOffset, teleportYOffset, teleportZOffset);

                double heightOffset = (player.getEyeY() - player.getY()) / 2;

                //if (portalFacing != Direction.UP && portalFacing != Direction.DOWN) {
                //    if (otherDirec != Direction.UP && otherDirec != Direction.DOWN) {
                //        rotatedOffsets = rotatedOffsets.multiply(1,-1,1);
                //    }
                //}
//

                if (portalFacing == Direction.UP || portalFacing == Direction.DOWN) {
                    if (otherDirec != Direction.UP && otherDirec != Direction.DOWN) {
                        rotatedOffsets = PortalVelocityHelper.rotatePosition(rotatedOffsets, heightOffset, portalVertFacing, otherDirec);
                    }
                }

                if (otherDirec == Direction.UP || otherDirec == Direction.DOWN) {
                    if (portalFacing != Direction.UP && portalFacing != Direction.DOWN) {
                        rotatedOffsets = PortalVelocityHelper.rotatePosition(rotatedOffsets, heightOffset, portalFacing, otherPortalVertFacing);
                    }
                }

                System.out.println(portalVertFacing);


                if (portalFacing == Direction.UP || portalFacing == Direction.DOWN) {
                    if (otherDirec == Direction.UP || otherDirec == Direction.DOWN) {
                        if (portalVertFacing != otherPortalVertFacing)
                            rotatedOffsets = PortalVelocityHelper.rotatePosition(rotatedOffsets, heightOffset, portalVertFacing, otherPortalVertFacing);
                    }
                }

                rotatedOffsets = PortalVelocityHelper.rotatePosition(rotatedOffsets, heightOffset, portalFacing, otherDirec);

                //System.out.println(rotatedOffsets);

                Vec3d rotatedVel = entityVelocity;


                if (portalFacing == Direction.UP || portalFacing == Direction.DOWN) {
                    if (otherDirec == Direction.UP || otherDirec == Direction.DOWN) {
                        if (portalFacing.getOpposite() != otherDirec)
                            rotatedVel = PortalVelocityHelper.rotateVelocity(rotatedVel, portalVertFacing, otherPortalVertFacing);
                    }
                }

                if (portalFacing == Direction.UP || portalFacing == Direction.DOWN) {
                    if (otherDirec != Direction.UP && otherDirec != Direction.DOWN) {
                        rotatedVel = PortalVelocityHelper.rotateVelocity(rotatedVel, portalVertFacing, otherDirec);
                    }
                }

                rotatedVel = PortalVelocityHelper.rotateVelocity(rotatedVel, portalFacing, otherDirec);

                if (otherDirec == Direction.UP || otherDirec == Direction.DOWN) {
                    if (portalFacing != Direction.UP && portalFacing != Direction.DOWN) {
                        rotatedVel = PortalVelocityHelper.rotateVelocity(rotatedVel, portalFacing, otherPortalVertFacing);
                    }
                }


                CalledValues.setVelocityUpdateAfterTeleport(player, rotatedVel);

                float yawValue = yawSet + PortalVelocityHelper.yawAddition(portal.getFacingDirection(), otherDirec);
                player.setYaw(yawValue);
                player.setPitch(pitchSet);
                player.refreshPositionAfterTeleport(portal.getDestination().get().add(rotatedOffsets).subtract(0, player.getEyeY() - player.getY(), 0));
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
            final PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(handler.player.world.getGameRules().getBoolean(PortalCubedGameRules.ALLOW_CROUCH_FLY_GLITCH));
            handler.sendPacket(ServerPlayNetworking.createS2CPacket(PortalCubedClientPackets.ENABLE_CFG, buf));
        });

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
