package com.fusionflux.thinkingwithportatos.mixin;

import com.fusionflux.thinkingwithportatos.accessor.VelocityTransfer;
import com.fusionflux.thinkingwithportatos.blocks.ThinkingWithPortatosBlocks;
import com.fusionflux.thinkingwithportatos.entity.CustomPortalEntity;
import com.fusionflux.thinkingwithportatos.entity.EntityAttachments;
import com.fusionflux.thinkingwithportatos.sound.ThinkingWithPortatosSounds;
import com.qouteall.immersive_portals.teleportation.CollisionHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAttachments, VelocityTransfer {

    @Shadow
    public World world;

    private int timeinblock = 1;

    @Unique
    private double maxFallSpeed = 0;
    @Unique
    private double storeVelocity1 = 0;
    @Unique
    private double storeVelocity2 = 0;
    @Unique
    private double speedTransformApply = 0;

    @Override
    public double getMaxFallSpeed() {
        return maxFallSpeed;
    }

    @Override
    public void setMaxFallSpeed(double maxFallSpeed) {
        this.maxFallSpeed = maxFallSpeed;
    }

    @Shadow
    public abstract BlockPos getBlockPos();

    @Shadow
    public abstract Vec3d getVelocity();

    @Shadow
    public abstract void setVelocity(Vec3d velocity);

    @Shadow
    public abstract Box getBoundingBox();

    @Shadow
    public abstract boolean equals(Object o);

    @Shadow
    public abstract EntityType<?> getType();

    @Shadow public abstract Vec3d getPos();

    @Shadow public abstract double getX();

    @Shadow public abstract double getY();

    @Shadow public abstract double getZ();

    @Shadow public int age;

    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Shadow public abstract float getEyeHeight(EntityPose pose);

    @Shadow public abstract EntityPose getPose();

    private boolean recentlyTouchedPortal;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tick(CallbackInfo ci) {
        Vec3d expand = this.getVelocity().multiply(10);
        Box streachedBB = this.getBoundingBox().stretch(expand);

        List<Entity> globalPortals = this.world.getEntitiesByClass(CustomPortalEntity.class, streachedBB, null);
            for (Entity globalPortal : globalPortals) {
                if (streachedBB.intersects(globalPortal.getBoundingBox())) {
                    double offsetX=0;
                    double offsetZ=0;
                    double offsetY=0;
                    if(Math.abs(this.getVelocity().y) > Math.abs(this.getVelocity().x)||Math.abs(this.getVelocity().z) > Math.abs(this.getVelocity().x)) {
                        offsetX = (this.getBoundingBox().getCenter().x - globalPortal.getBoundingBox().getCenter().x)*.02;
                    }
                    if(Math.abs(this.getVelocity().y) > Math.abs(this.getVelocity().z)||Math.abs(this.getVelocity().x) > Math.abs(this.getVelocity().z)) {
                        offsetZ = (this.getBoundingBox().getCenter().z - globalPortal.getBoundingBox().getCenter().z)*.02;
                    }
                    if(Math.abs(this.getVelocity().z) > Math.abs(this.getVelocity().y)||Math.abs(this.getVelocity().x) > Math.abs(this.getVelocity().y)) {
                        offsetY = (this.getBoundingBox().getCenter().y - globalPortal.getBoundingBox().getCenter().y)*.02;
                    }
                    if(!this.getBoundingBox().intersects(globalPortal.getBoundingBox()))
                    this.setVelocity(this.getVelocity().add(-offsetX,-offsetY,-offsetZ));
                }
            }
        if (!world.isClient) {
if(world.getBlockState(new BlockPos(this.getX(),this.getY()+this.getEyeHeight(this.getPose()),this.getZ()))==ThinkingWithPortatosBlocks.NEUROTOXIN_BLOCK.getDefaultState()){
if(Math.abs(timeinblock-this.age)>20){
    this.damage(DamageSource.DROWN,2);
}
}else{
    timeinblock=this.age;
}
            List<Entity> portalSound = this.world.getEntitiesByClass(CustomPortalEntity.class, this.getBoundingBox().expand(2), null);
            for (Entity globalportal : portalSound) {
                CustomPortalEntity collidingportal = (CustomPortalEntity) globalportal;
                collidingportal.getActive();
                if (CollisionHelper.isCollidingWithAnyPortal(((Entity) (Object) this))&&collidingportal.getActive() && !recentlyTouchedPortal) {
                    world.playSound(null, this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), ThinkingWithPortatosSounds.ENTITY_ENTER_PORTAL, SoundCategory.NEUTRAL, .1F, 1F);
                    recentlyTouchedPortal = true;
                }
                if(!CollisionHelper.isCollidingWithAnyPortal(((Entity) (Object) this))&&collidingportal.getActive()&&recentlyTouchedPortal){
                    world.playSound(null, this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), ThinkingWithPortatosSounds.ENTITY_EXIT_PORTAL, SoundCategory.NEUTRAL, .1F, 1F);
                    recentlyTouchedPortal = false;
                }
            }
        }
        if (maxFallSpeed == 10 && world.getBlockState(this.getBlockPos()).getBlock() == ThinkingWithPortatosBlocks.PROPULSION_GEL) {
            maxFallSpeed = 10;
        } else {
            if (maxFallSpeed > 0) {
                maxFallSpeed = maxFallSpeed - 1;
            }
        }

        if (world.isClient) {
            storeVelocity2 = storeVelocity1;
            storeVelocity1 = this.getVelocity().length();

            /*-----------
            if (storeVelocity3 > storeVelocity1 && storeVelocity3 > storeVelocity2) {
                speedTransformApply=storeVelocity3;
            }
            -----------*/
        }

        speedTransformApply = Math.max(storeVelocity1, storeVelocity2);

    }

    @Override
    public double getVelocityTransfer() {
        return this.speedTransformApply;
    }

    @Override
    public void setVelocityTransfer(double speedValueTransferDuck) {
        this.speedTransformApply = speedValueTransferDuck;
    }

    /*----------
    @Inject(method = "calculateDimensions", at = @At("TAIL"))
    public void calculateDimensions(CallbackInfo ci) {
        EntityPose entityPose2 = this.getPose();
        EntityDimensions entityDimensions3 = this.getDimensions(entityPose2);
        this.standingEyeHeight = this.getEyeHeight(entityPose2, entityDimensions3) - 1;
    }
    ----------*/
}
