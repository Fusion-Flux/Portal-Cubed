package com.fusionflux.portalcubed.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.UUID;

public class EntityReference<T extends Entity> {
	public final ServerLevel level;
	public final UUID uuid;
	private final Class<T> entityClass;

	private WeakReference<T> entity;

	public EntityReference(ServerLevel level, T entity) {
		this.level = level;
		this.uuid = entity.getUUID();
		//noinspection unchecked
		this.entityClass = (Class<T>) entity.getClass();
		this.entity = new WeakReference<>(entity);
	}

	public EntityReference(ServerLevel level, UUID uuid, Class<T> entityClass) {
		this.level = level;
		this.uuid = uuid;
		this.entityClass = entityClass;
		this.entity = new WeakReference<>(null);
	}

	@Nullable
	public T get() {
		tryResolve();
		return entity.get();
	}

	public boolean isLoaded() {
		tryResolve();
		return get() != null;
	}

	public boolean isUnloaded() {
		return !isLoaded();
	}

	private void tryResolve() {
		if (entity.get() == null) {
			Entity entity = level.getEntity(uuid);
			if (entityClass.isInstance(entity))
				//noinspection unchecked
				this.entity = new WeakReference<>((T) entity);
		}
	}
}
