package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.accessor.Accessors;
import com.fusionflux.portalcubed.accessor.LevelExt;
import com.fusionflux.portalcubed.mechanics.PortalCubedDamageSources;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.storage.WritableLevelData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;
import java.util.function.Supplier;

@Mixin(Level.class)
public abstract class LevelMixin implements Accessors, LevelExt {
    @Unique
    PortalCubedDamageSources pc$damageSources;

    @Shadow
    protected abstract LevelEntityGetter<Entity> getEntities();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void createDamageSources(
        WritableLevelData writableLevelData,
        ResourceKey<Level> resourceKey,
        RegistryAccess registryAccess,
        Holder<DimensionType> holder,
        Supplier<ProfilerFiller> supplier,
        boolean bl,
        boolean bl2,
        long l,
        int i,
        CallbackInfo ci
    ) {
        pc$damageSources = new PortalCubedDamageSources(registryAccess);
    }

    @Override
    @Nullable
    public Entity getEntity(UUID uuid) {
        return this.getEntities().get(uuid);
    }

    @Override
    public PortalCubedDamageSources pcDamageSources() {
        return pc$damageSources;
    }
}
