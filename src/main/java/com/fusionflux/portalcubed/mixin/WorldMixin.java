package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.accessor.Accessors;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLookup;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(World.class)
public abstract class WorldMixin implements Accessors {

    @Shadow
    protected abstract EntityLookup<Entity> getEntityLookup();

    @Override
    @Nullable
    public Entity getEntity(UUID uuid) {
        return this.getEntityLookup().get(uuid);
    }
}