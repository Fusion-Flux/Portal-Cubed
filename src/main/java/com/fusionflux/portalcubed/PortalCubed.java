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
import com.fusionflux.portalcubed.config.MidnightConfig;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.fluids.PortalCubedFluids;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.packet.PortalCubedServerPackets;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.fusionflux.portalcubed.util.FaithPlateScreenHandler;
import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.item.group.api.QuiltItemGroup;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
import org.slf4j.Logger;

import java.util.UUID;

public class PortalCubed implements ModInitializer {
    public static final PortalCubedConfig CONFIG = new PortalCubedConfig();

    public static final String MODID = "portalcubed";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ItemGroup TestingElementsGroup = QuiltItemGroup.createWithIcon(
            id("testing_elements"),
            () -> new ItemStack(PortalCubedItems.PORTAL_GUN));

    public static final ItemGroup PortalBlocksGroup = QuiltItemGroup.createWithIcon(
            id("portal_blocks"),
            () -> new ItemStack(PortalCubedItems.BLOCK_ITEM_ICON));

    public static Identifier id(String path) {
        return new Identifier(MODID, path);
    }
    public static ScreenHandlerType<FaithPlateScreenHandler> FAITH_PLATE_SCREEN_HANDLER = new ExtendedScreenHandlerType<>(FaithPlateScreenHandler::new);
    static {
        FAITH_PLATE_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(id("faith_plate_screen"), FaithPlateScreenHandler::new);
    }

    @Override
    public void onInitialize(ModContainer mod) {
        ServerPlayNetworking.registerGlobalReceiver(id("portalpacket"), (server, player, handler, buf, responseSender) -> {
            // read the velocity from the bytebuf
            final int targetEntityId = buf.readVarInt();
            final Vec3d offset = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
            float yawSet = buf.readFloat();
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
                player.setYaw(yawSet);
                player.refreshPositionAfterTeleport(CalledValues.getDestination(portal).subtract(offset));
                CalledValues.setHasTeleportationHappened(player,true);
                GravityChangerAPI.clearGravity(player);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(id("faithplatepacket"), (server, player, handler, buf, responseSender) -> {
            // read the velocity from the bytebuf
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

        ServerPlayNetworking.registerGlobalReceiver(id("clientteleportupdate"), (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                CalledValues.setHasTeleportationHappened(player,false);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(id("cubeposupdate"), (server, player, handler, buf, responseSender) -> {
            // read the velocity from the bytebuf
            double x =  buf.readDouble();
            double y =  buf.readDouble();
            double z =  buf.readDouble();
            double lastx =  buf.readDouble();
            double lasty =  buf.readDouble();
            double lastz =  buf.readDouble();
            float rotyaw = buf.readFloat();
            UUID cubeuuid =  buf.readUuid();
            server.execute(() -> {
                if (!(player.getWorld().getEntity(cubeuuid) instanceof CorePhysicsEntity cube)) {
                    LOGGER.warn("{} tried to drop nonexistent physics object", player);
                    return;
                }
                if (!player.getUuid().equals(cube.getHolderUUID())) {
                    LOGGER.warn("{} tried to drop another player's physics object (held by {})", player, cube.getHolderUUID());
                    return;
                }
                cube.setHolderUUID(null);
                cube.setRotYaw(rotyaw);
                Vec3d cubePos = new Vec3d(x,y,z);
                Vec3d lastcubePos = new Vec3d(lastx,lasty,lastz);
                if (cubePos.squaredDistanceTo(lastcubePos) > 10 * 10) {
                    LOGGER.warn("{} tried to throw a physics object really fast ({})", player, cubePos.distanceTo(lastcubePos));
                    return;
                }

                if (cubePos.squaredDistanceTo(player.getPos()) > 10 * 10) {
                    LOGGER.warn("{} tried to drop physics object far away ({})", player, cubePos.distanceTo(player.getPos()));
                    return;
                }
                cube.setPosition(cubePos);
                cube.setVelocity(RotationUtil.vecWorldToPlayer(cubePos.subtract(lastcubePos), GravityChangerAPI.getGravityDirection(cube)).multiply(.5));
            });
        });

        QuaternionHandler.QUATERNION_HANDLER.getClass();
        MidnightConfig.init("portalcubed", PortalCubedConfig.class);
        PortalBlocksLoader.init(mod);
        PortalCubedBlocks.registerBlocks();
        PortalCubedFluids.registerFluids();
        PortalCubedItems.registerItems();
        PortalCubedEntities.registerEntities();
        PortalCubedServerPackets.registerPackets();
        PortalCubedSounds.registerSounds();
        FlammableBlockRegistry.getDefaultInstance().add(PortalCubedBlocks.NEUROTOXIN_BLOCK,10000,10000);
        GravityChannel.UPDATE_GRAVITY.getVerifierRegistry().register(AdhesionGravityVerifier.FIELD_GRAVITY_SOURCE, AdhesionGravityVerifier::check);
    }
}
