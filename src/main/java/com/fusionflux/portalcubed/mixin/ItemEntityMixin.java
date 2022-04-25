package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.accessor.EntityPortalsAccess;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.entity.EntityAttachments;
import me.andrew.gravitychanger.api.GravityChangerAPI;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements EntityAttachments, EntityPortalsAccess {

    private static final TrackedData<Boolean> changeGravity = DataTracker.registerData(ItemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public ItemEntityMixin(EntityType<?> type, World world) {
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
            GravityChangerAPI.setGravityDirection(((ItemEntity) (Object)this), Direction.DOWN);
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
