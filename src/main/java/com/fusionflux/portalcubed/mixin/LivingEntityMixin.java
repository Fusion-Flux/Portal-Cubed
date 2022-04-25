package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.accessor.EntityPortalsAccess;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.entity.CustomPortalEntity;
import com.fusionflux.portalcubed.entity.EntityAttachments;
import com.fusionflux.portalcubed.entity.PortalPlaceholderEntity;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.google.common.collect.Lists;
import me.andrew.gravitychanger.api.GravityChangerAPI;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.teleportation.CollisionHelper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements EntityAttachments, EntityPortalsAccess {

    private static final TrackedData<Boolean> changeGravity = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "initDataTracker",at = @At("HEAD"))
    public void initDataTracker(CallbackInfo ci) {
        this.getDataTracker().startTracking(changeGravity, false);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if(this.world.getBlockState(this.getBlockPos()).getBlock() != PortalCubedBlocks.ADHESION_GEL && this.getDataTracker().get(changeGravity)){
            this.getDataTracker().set(changeGravity,false);
            GravityChangerAPI.setGravityDirection(((LivingEntity) (Object)this), Direction.DOWN);
        }
    }

    @Override
    public void setSwapGravity(boolean setValue) {
        this.getDataTracker().set(changeGravity,setValue);
    }

    @Override
    public boolean getSwapGravity() {
        return this.getDataTracker().get(changeGravity);
    }



}
