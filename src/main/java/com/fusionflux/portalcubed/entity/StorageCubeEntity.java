package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.accessor.EntityPortalsAccess;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import me.andrew.gravitychanger.api.GravityChangerAPI;
import me.andrew.gravitychanger.util.Gravity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class StorageCubeEntity extends PathAwareEntity  {



    public StorageCubeEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    private boolean canUsePortals = true;

    @Override
    public boolean collides() {
        return !this.isRemoved();
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return  damageSource != DamageSource.OUT_OF_WORLD && !damageSource.isSourceCreativePlayer() ;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source) && !(source.getAttacker() instanceof PlayerEntity)) {
            return false;
        } else if (!this.world.isClient && !this.isRemoved()) {
            boolean bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity) source.getAttacker()).getAbilities().creativeMode;
            if (source.getAttacker() instanceof PlayerEntity) {
                if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS) && !bl) {
                    this.dropItem(PortalCubedItems.STORAGE_CUBE);
                }
                this.discard();
            }

            return true;
        } else {
            return true;
        }
    }

    @Override
    public boolean shouldRenderName() {
        return false;
    }
    @Override
    public boolean isCustomNameVisible() {
        return false;
    }

    @Override
    @Nullable
    public Text getCustomName() {
        return null;
    }

    @Override
    public void readFromPacket(MobSpawnS2CPacket packet) {
        double d = packet.getX();
        double e = packet.getY();
        double f = packet.getZ();
        float g = (float)(packet.getYaw() * 360) / 256.0F;
        float h = (float)(packet.getPitch() * 360) / 256.0F;
        this.updateTrackedPosition(d, e, f);
        this.bodyYaw = 0;
        this.headYaw = 0;
        this.prevBodyYaw = this.bodyYaw;
        this.prevHeadYaw = this.headYaw;
        this.setNoDrag(true);
        this.setId(packet.getId());
        this.setUuid(packet.getUuid());
        this.updatePositionAndAngles(d, e, f, g, h);
        this.setVelocity((double)((float)packet.getVelocityX() ), (double)((float)packet.getVelocityY() ), (double)((float)packet.getVelocityZ() ));
    }


    public static final TrackedData<Optional<UUID>> HOLDERUUID = DataTracker.registerData(StorageCubeEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);


    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(HOLDERUUID, Optional.empty());
    }

    public Boolean getUUIDPresent() {
        return getDataTracker().get(HOLDERUUID).isPresent();
    }

    public UUID getHolderUUID() {
        if (getDataTracker().get(HOLDERUUID).isPresent()) {
            return getDataTracker().get(HOLDERUUID).get();
        }
        return null;
    }


    public void setHolderUUID(UUID uuid) {
        if(uuid != null) {
            if (getDataTracker().get(HOLDERUUID).isPresent()) {
                PlayerEntity player = (PlayerEntity) ((ServerWorld) world).getEntity(getHolderUUID());
                if(player != null)
                CalledValues.setCubeUUID(player,null);
            }
            this.getDataTracker().set(HOLDERUUID, Optional.of(uuid));
        }else {
            this.getDataTracker().set(HOLDERUUID, Optional.empty());
        }
    }

    @Override
    public boolean canUsePortals() {
        return canUsePortals;
    }

    public void tick() {
        super.tick();
        this.bodyYaw = 0;
        this.headYaw = 0;
        canUsePortals = !getUUIDPresent();
        if(!world.isClient) {
            if (getUUIDPresent()) {
                PlayerEntity player = (PlayerEntity) ((ServerWorld) world).getEntity(getHolderUUID());
                if (player != null && player.isAlive()) {
                    Vec3d vec3d = player.getCameraPosVec(0);
                    double d = 2;
                    canUsePortals = false;
                    Vec3d vec3d2 = player.getRotationVec(1.0F);
                    Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
                    GravityChangerAPI.addGravity( this, new Gravity(GravityChangerAPI.getGravityDirection(player),10,10,"player_interaction"));
                    this.fallDistance = 0;
                    this.setPosition(vec3d3);
                    this.setVelocity(player.getVelocity().x, player.getVelocity().y, player.getVelocity().z);
                    this.velocityModified = true;
                }else{
                    if(player != null ){
                        setHolderUUID(null);
                    }
                    canUsePortals = true;
                }
            }
        }

    }


    public void onRemoved() {
        if(!world.isClient) {
            PlayerEntity player = (PlayerEntity) ((ServerWorld) world).getEntity(getHolderUUID());
            if (player != null) {
                CalledValues.setCubeUUID(player,null);
            }
        }
    }

    public void onKilledOther(ServerWorld world, LivingEntity other) {
        if(!world.isClient) {
            PlayerEntity player = (PlayerEntity) ((ServerWorld) world).getEntity(getHolderUUID());
            if (player != null) {
                CalledValues.setCubeUUID(player,null);
            }
        }
    }

    protected void updatePostDeath() {
        if(!world.isClient) {
            PlayerEntity player = (PlayerEntity) ((ServerWorld) world).getEntity(getHolderUUID());
            if (player != null) {
                CalledValues.setCubeUUID(player,null);
            }
        }
        super.updatePostDeath();
    }

    public void onDeath(DamageSource source) {
        if(!world.isClient) {
            PlayerEntity player = (PlayerEntity) ((ServerWorld) world).getEntity(getHolderUUID());
            if (player != null) {
                CalledValues.setCubeUUID(player,null);
            }
        }
        super.onDeath(source);
    }


}
