package com.fusionflux.portalcubed;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.GravityChannel;
import com.fusionflux.gravity_api.util.RotationUtil;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.accessor.QuaternionHandler;
import com.fusionflux.portalcubed.blocks.PortalBlocksLoader;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.blockentities.BetaFaithPlateBlockEntity;
import com.fusionflux.portalcubed.blocks.blockentities.FaithPlateBlockEntity;
import com.fusionflux.portalcubed.client.AdhesionGravityVerifier;
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
import org.quiltmc.qsl.entity.networking.api.tracked_data.QuiltTrackedDataHandlerRegistry;
import org.quiltmc.qsl.item.group.api.QuiltItemGroup;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
import org.slf4j.Logger;

import java.util.UUID;

public class PortalCubed implements ModInitializer {

    public static final String MOD_ID = "portalcubed";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ItemGroup TestingElementsGroup = QuiltItemGroup.createWithIcon(
            id("testing_elements"),
            () -> new ItemStack(PortalCubedItems.PORTAL_GUN));

    public static final ItemGroup PortalBlocksGroup = QuiltItemGroup.createWithIcon(
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
        ServerPlayNetworking.registerGlobalReceiver(id("portalpacket"), (server, player, handler, buf, responseSender) -> {
            // read the velocity from the byte buf
            final int targetEntityId = buf.readVarInt();
            final Vec3d offset = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
            float yawSet = buf.readFloat();
            float pitchSet = buf.readFloat();
            final Vec3d entityVelocity = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
            final boolean wasInfiniteFall = buf.readBoolean();
            if (Double.isNaN(offset.x) || Double.isNaN(offset.y) || Double.isNaN(offset.z) || !Float.isFinite(yawSet)) {
                handler.disconnect(Text.translatable("multiplayer.disconnect.invalid_player_movement"));
                return;
            }
            server.execute(() -> {
                if (offset.lengthSquared() > 10 * 10) {
                    LOGGER.warn("{} tried to teleport with a high offset ({})", player, offset.length());
                    handler.requestTeleport(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
                    return;
                }
                if (!(player.world.getEntityById(targetEntityId) instanceof ExperimentalPortal portal)) {
                    LOGGER.warn("{} tried to teleport through nonexistent portal", player);
                    handler.requestTeleport(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
                    return;
                }
                if (portal.getPos().squaredDistanceTo(player.getPos()) > 10 * 10) {
                    LOGGER.warn("{} tried to teleport through distant portal ({})", player, portal.getPos().distanceTo(player.getPos()));
                    handler.requestTeleport(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
                    return;
                }
                if (portal.getDestination().isEmpty()) {
                    LOGGER.warn("{} tried to teleport through an inactive portal ({}).", player, portal);
                    handler.requestTeleport(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
                    return;
                }

                Direction otherDirec = Direction.fromVector((int) portal.getOtherFacing().getX(), (int) portal.getOtherFacing().getY(), (int) portal.getOtherFacing().getZ());

                CalledValues.setVelocityUpdateAfterTeleport(player,PortalVelocityHelper.rotateVelocity(entityVelocity, portal.getFacingDirection(), otherDirec));

                if(otherDirec != Direction.DOWN && wasInfiniteFall){
                    CalledValues.setWasInfiniteFalling(player,false);
                }

                float yawValue = yawSet + PortalVelocityHelper.yawAddition(portal.getFacingDirection(), otherDirec);
                player.setYaw(yawValue);
                player.setPitch(pitchSet);
                player.refreshPositionAfterTeleport(portal.getDestination().get().subtract(offset));
                CalledValues.setHasTeleportationHappened(player,true);
                GravityChangerAPI.clearGravity(player);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(id("faithplatepacket"), (server, player, handler, buf, responseSender) -> {
            // read the velocity from the byte buf
            BlockPos target = buf.readBlockPos();
            double x =  buf.readDouble();
            double y =  buf.readDouble();
            double z =  buf.readDouble();
            server.execute(() -> {
                BlockEntity entity = player.world.getBlockEntity(target);
                if(entity instanceof FaithPlateBlockEntity plate){
                    plate.setVelX(x);
                    plate.setVelY(y);
                    plate.setVelZ(z);
                }
                if(entity instanceof BetaFaithPlateBlockEntity plate){
                    plate.setVelX(x);
                    plate.setVelY(y);
                    plate.setVelZ(z);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(id("clientteleportupdate"), (server, player, handler, buf, responseSender) ->
            server.execute(() -> CalledValues.setHasTeleportationHappened(player, false))
        );

        ServerPlayNetworking.registerGlobalReceiver(id("requestvelocityforgel"), (server, player, handler, buf, responseSender) ->{
                    final Vec3d entityVelocity = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
                    final boolean fireGel = buf.readBoolean();
                server.execute(() -> {
                    CalledValues.setServerVelForGel(player,entityVelocity);
                    CalledValues.setCanFireGel(player,fireGel);

                });
    }
        );

        ServerPlayNetworking.registerGlobalReceiver(id("cubeposupdate"), (server, player, handler, buf, responseSender) -> {
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
                Vec3d cubePos = new Vec3d(x,y,z);
                Vec3d lastCubePos = new Vec3d(lastX,lastY,lastZ);
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

        QuiltTrackedDataHandlerRegistry.register(id("quaternion"), QuaternionHandler.QUATERNION_HANDLER);
        MidnightConfig.init("portalcubed", PortalCubedConfig.class);
        PortalBlocksLoader.init(mod);
        PortalCubedBlocks.registerBlocks();
        PortalCubedFluids.registerFluids();
        PortalCubedItems.registerItems();
        PortalCubedEntities.registerEntities();
        PortalCubedTrackedDataHandlers.register();
        PortalCubedServerPackets.registerPackets();
        PortalCubedSounds.registerSounds();
        BlockContentRegistries.FLAMMABLE_BLOCK.put(PortalCubedBlocks.NEUROTOXIN_BLOCK, new FlammableBlockEntry(10000, 10000));
        GravityChannel.UPDATE_GRAVITY.getVerifierRegistry().register(AdhesionGravityVerifier.FIELD_GRAVITY_SOURCE, AdhesionGravityVerifier::check);

        if (QuiltLoader.isModLoaded("create")) {
            CreateIntegration.init();
        }
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

}
