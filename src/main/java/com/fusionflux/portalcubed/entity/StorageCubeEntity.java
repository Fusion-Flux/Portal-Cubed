package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.accessor.Accessors;
import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.accessor.EntityPortalsAccess;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import me.andrew.gravitychanger.api.GravityChangerAPI;
import me.andrew.gravitychanger.util.Gravity;
import me.andrew.gravitychanger.util.RotationUtil;
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
import net.minecraft.util.math.MathHelper;
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

    private Vec3d lastPos = this.getPos();

    private final Vec3d offset = new Vec3d(0,this.getWidth()/2,0);

    @Override
    public boolean collides() {
        return !this.isRemoved();
    }

    @Override
    public boolean isCollidable() {
        return canUsePortals;
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
        Vec3d rotatedOffset = RotationUtil.vecPlayerToWorld(offset, GravityChangerAPI.getGravityDirection(this));
        if(!world.isClient) {
            this.setNoDrag(!this.isOnGround());
            if (getUUIDPresent()) {
                PlayerEntity player = (PlayerEntity) ((ServerWorld) world).getEntity(getHolderUUID());
                if (player != null && player.isAlive()) {
                    Vec3d vec3d = player.getCameraPosVec(0);
                    double d = 2;
                    canUsePortals = false;
                    Vec3d vec3d2 = this.getPlayerRotationVector(player.getPitch(),player.getYaw());
                    Vec3d vec3d3 = vec3d.add((vec3d2.x * d) - rotatedOffset.x, (vec3d2.y * d) - rotatedOffset.y, (vec3d2.z * d) - rotatedOffset.z);
                    GravityChangerAPI.addGravity( this, new Gravity(GravityChangerAPI.getGravityDirection(player),10,1,"player_interaction"));
                    this.fallDistance = 0;
                    this.setPosition(vec3d3);
                    this.setVelocity(RotationUtil.vecWorldToPlayer(this.getPos().subtract(lastPos), GravityChangerAPI.getGravityDirection(this)));
                    //this.velocityModified = true;
                }else{
                    if(player != null ){
                        setHolderUUID(null);
                    }
                    canUsePortals = true;
                }
            }
        }else{
            if (getUUIDPresent()) {
                PlayerEntity player = (PlayerEntity)((Accessors) world).getEntity(getHolderUUID());
                if (player != null && player.isAlive()) {
                    Vec3d vec3d = player.getCameraPosVec(0);
                    double d = 2;
                    Vec3d vec3d2 = player.getRotationVec(1.0F);
                    Vec3d vec3d3 = vec3d.add((vec3d2.x * d) - rotatedOffset.x, (vec3d2.y * d) - rotatedOffset.y, (vec3d2.z * d) - rotatedOffset.z);
                    this.setPosition(vec3d3);
                }
            }
        }
        if(this.getVelocity().y < -3.92){
            this.setVelocity(this.getVelocity().add(0,.81d,0));
        }
        this.lastPos = this.getPos();
    }

    protected final Vec3d getPlayerRotationVector(float pitch, float yaw) {
        float f = pitch * (float) (Math.PI / 180.0);
        float g = -yaw * (float) (Math.PI / 180.0);
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return RotationUtil.vecPlayerToWorld(new Vec3d((double)(i * j), (double)(-k), (double)(h * j)), GravityChangerAPI.getGravityDirection(this));
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
