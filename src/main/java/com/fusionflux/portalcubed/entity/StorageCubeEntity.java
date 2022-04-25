package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.accessor.EntityPortalsAccess;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import me.andrew.gravitychanger.api.GravityChangerAPI;
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

    private float storedDamage = 0.0F;

    private int gravityTimer = 0;

    private boolean gravitySwap = false;

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
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!this.world.isClient && !this.isRemoved()) {
            this.storedDamage += amount;
            //this.scheduleVelocityUpdate();
            boolean bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity) source.getAttacker()).getAbilities().creativeMode;
            if (bl || this.storedDamage >= 20.0F) {
                if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                    // TODO
                    //this.dropItem(ThinkingWithPortatosItems.COMPANION_CUBE);
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
        this.setVelocity((double)((float)packet.getVelocityX() / 8000.0F), (double)((float)packet.getVelocityY() / 8000.0F), (double)((float)packet.getVelocityZ() / 8000.0F));
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
                ((EntityPortalsAccess) player).setCubeUUID(null);
            }
            this.getDataTracker().set(HOLDERUUID, Optional.of(uuid));
        }else {
            this.getDataTracker().set(HOLDERUUID, Optional.empty());
        }
    }


    public void tick() {
        super.tick();
        this.bodyYaw = 0;
        this.headYaw = 0;
        if(!world.isClient) {
            if (getUUIDPresent()) {
                PlayerEntity player = (PlayerEntity) ((ServerWorld) world).getEntity(getHolderUUID());
                if (player != null) {
                    Vec3d vec3d = player.getCameraPosVec(0);
                    double d = 2;
                    Vec3d vec3d2 = player.getRotationVec(1.0F);
                    Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
                    GravityChangerAPI.setGravityDirection(this, GravityChangerAPI.getGravityDirection(player));
                    gravityTimer = 10;
                    gravitySwap = true;
                    this.setVelocity(0, .03, 0);
                    this.fallDistance = 0;
                    this.setPosition(vec3d3);
                    this.velocityModified = true;
                }
            }

            if(gravityTimer > 0){
                gravityTimer--;
            }

            if(gravityTimer == 0 && gravitySwap){
                GravityChangerAPI.setGravityDirection(this, GravityChangerAPI.getDefaultGravityDirection(this));
                gravitySwap=false;
            }



        }

    }


    public void onRemoved() {
        if(!world.isClient) {
            PlayerEntity player = (PlayerEntity) ((ServerWorld) world).getEntity(getHolderUUID());
            if (player != null) {
                ((EntityPortalsAccess) player).setCubeUUID(null);
            }
        }
    }

    public void onKilledOther(ServerWorld world, LivingEntity other) {
        if(!world.isClient) {
            PlayerEntity player = (PlayerEntity) ((ServerWorld) world).getEntity(getHolderUUID());
            if (player != null) {
                ((EntityPortalsAccess) player).setCubeUUID(null);
            }
        }
    }

    protected void updatePostDeath() {
        if(!world.isClient) {
            PlayerEntity player = (PlayerEntity) ((ServerWorld) world).getEntity(getHolderUUID());
            if (player != null) {
                ((EntityPortalsAccess) player).setCubeUUID(null);
            }
        }
        super.updatePostDeath();
    }

    public void onDeath(DamageSource source) {
        if(!world.isClient) {
            PlayerEntity player = (PlayerEntity) ((ServerWorld) world).getEntity(getHolderUUID());
            if (player != null) {
                ((EntityPortalsAccess) player).setCubeUUID(null);
            }
        }
        super.onDeath(source);
    }


}
