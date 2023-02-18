package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.PortalCubed;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.quiltmc.loader.api.minecraft.ClientOnly;

public class DecalEntity extends Entity {
    private static final TrackedData<Identifier> TEXTURE = DataTracker.registerData(DecalEntity.class, PortalCubedTrackedDataHandlers.IDENTIFIER);

    public DecalEntity(EntityType<?> type, World world) {
        super(type, world);
        ignoreCameraFrustum = true;
    }

    public static void spawn(ServerWorld world, Vec3d origin, Direction.Axis axis, Identifier texture) {
        final DecalEntity decal = PortalCubedEntities.DECAL.create(null);
        if (decal == null) return;
        decal.setPosition(origin);
        if (axis == Direction.Axis.Y) {
            decal.setPitch(90);
        } else if (axis == Direction.Axis.Z) {
            decal.setYaw(90);
        }
        decal.setTexture(texture);
        final Packet<?> packet1 = decal.createSpawnPacket();
        final Packet<?> packet2 = new EntityTrackerUpdateS2CPacket(decal.getId(), decal.getDataTracker(), true);
        for (final ServerPlayerEntity player : world.getPlayers()) {
            world.sendToPlayerIfNearby(player, true, origin.x, origin.y, origin.z, packet1);
            world.sendToPlayerIfNearby(player, true, origin.x, origin.y, origin.z, packet2);
        }
    }

    @Override
    protected void initDataTracker() {
        dataTracker.startTracking(TEXTURE, new Identifier(""));
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    public Identifier getTexture() {
        return dataTracker.get(TEXTURE);
    }

    private void setTexture(Identifier path) {
        dataTracker.set(TEXTURE, path);
    }

    @Override
    public void tick() {
        super.tick();
        if (!world.isClient) {
            PortalCubed.LOGGER.warn("DecalEntity {} was loaded on the server. Please use DecalEntity.spawn, as opposed to ServerWorld.spawnEntity.", this);
            kill();
            return;
        }
        clientTick();
    }

    @ClientOnly
    private void clientTick() {
    }

    @Override
    public boolean shouldRender(double distance) {
        return true;
    }
}
