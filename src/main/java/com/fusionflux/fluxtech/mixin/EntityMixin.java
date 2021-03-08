package com.fusionflux.fluxtech.mixin;

import com.fusionflux.fluxtech.blocks.FluxTechBlocks;
import com.fusionflux.fluxtech.config.FluxTechConfig2;
import com.fusionflux.fluxtech.entity.EntityAttachments;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAttachments {

    private static final TrackedData<Boolean> IS_ROLLING = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Direction> DIRECTION = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.FACING);


    public double maxFallSpeed = 0;

    @Override
    public double getMaxFallSpeed() {
        return maxFallSpeed;
    }

    @Override
    public void setMaxFallSpeed(double maxFallSpeed) {
        this.maxFallSpeed = maxFallSpeed;
    }

    @Shadow public World world;

    @Shadow public abstract BlockPos getBlockPos();

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tick(CallbackInfo ci) {
            if(maxFallSpeed == 10 && world.getBlockState(this.getBlockPos()).getBlock() == FluxTechBlocks.PROPULSION_GEL){
                maxFallSpeed = 10;
            }else{
                if(maxFallSpeed>0){
                    maxFallSpeed=maxFallSpeed-1;
                }
            }

    }





    /*@Inject(method = "calculateDimensions", at = @At("TAIL"))
public void calculateDimensions(CallbackInfo ci){
        EntityPose entityPose2 = this.getPose();
        EntityDimensions entityDimensions3 = this.getDimensions(entityPose2);
        this.standingEyeHeight = this.getEyeHeight(entityPose2, entityDimensions3)-1;
    }*/
}
