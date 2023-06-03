package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.accessor.Accessors;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.LevelEntityGetter;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(Level.class)
public abstract class LevelMixin implements Accessors {

    @Shadow
    protected abstract LevelEntityGetter<Entity> getEntities();

    @Override
    @Nullable
    public Entity getEntity(UUID uuid) {
        return this.getEntities().get(uuid);
    }
}
