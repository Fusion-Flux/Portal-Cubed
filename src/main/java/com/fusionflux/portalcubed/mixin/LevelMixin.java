package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.accessor.LevelExt;
import com.fusionflux.portalcubed.entity.beams.EmittedEntity;
import com.fusionflux.portalcubed.mechanics.PortalCubedDamageSources;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.storage.WritableLevelData;

import com.google.common.collect.AbstractIterator;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Mixin(Level.class)
public abstract class LevelMixin implements LevelAccessor, LevelExt {
	@Unique
	private PortalCubedDamageSources pc$damageSources;
	@Unique
	private Long2ObjectMap<List<EmittedEntity>> pc$blockChangeListeners = new Long2ObjectOpenHashMap<>();

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

	@Inject(method = "setBlocksDirty", at = @At("HEAD"))
	private void updateEmittedEntities(BlockPos pos, BlockState old, BlockState updated, CallbackInfo ci) {
		if ((Object) this instanceof ServerLevel) {
			getListeners(pos).forEachRemaining(EmittedEntity::reEmit);
		}
	}

	@Unique
	private Iterator<EmittedEntity> getListeners(BlockPos pos) {
		long sectionPos = SectionPos.asLong(pos);
		List<EmittedEntity> listeners = pc$blockChangeListeners.get(sectionPos);
		return listeners == null || listeners.isEmpty() ? Collections.emptyIterator() : new AbstractIterator<>() {
			private final Iterator<EmittedEntity> entities = List.copyOf(listeners).iterator(); // copy to avoid CMEs

			@Override
			protected EmittedEntity computeNext() {
				if (!entities.hasNext())
					return endOfData();
				EmittedEntity next = entities.next();
				return next.listensTo(pos) ? next : computeNext();
			}
		};
	}

	@Override
	public void pc$addBlockChangeListener(long sectionPos, EmittedEntity entity) {
		pc$blockChangeListeners.computeIfAbsent(sectionPos, $ -> new ArrayList<>()).add(entity);
	}

	@Override
	public void pc$removeBlockChangeListener(long sectionPos, EmittedEntity entity) {
		List<EmittedEntity> listeners = pc$blockChangeListeners.get(sectionPos);
		if (listeners != null) {
			listeners.remove(entity);
		}
	}

	@Override
	@Nullable
	public Entity getEntityByUuid(UUID uuid) {
		return this.getEntities().get(uuid);
	}

	@Override
	public PortalCubedDamageSources pcDamageSources() {
		return pc$damageSources;
	}
}
