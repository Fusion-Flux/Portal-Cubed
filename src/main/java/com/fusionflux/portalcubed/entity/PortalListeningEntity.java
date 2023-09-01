package com.fusionflux.portalcubed.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;

/**
 * An entity that listens for placement and removal of portals near it.
 */
public abstract class PortalListeningEntity extends Entity {
	public static final EntityTypeTest<Entity, PortalListeningEntity> TYPE_TEST = EntityTypeTest.forClass(PortalListeningEntity.class);

	public PortalListeningEntity(EntityType<?> variant, Level world) {
		super(variant, world);
	}

	/**
	 * Called when a portal is created anywhere in the world. Add listeners here.
	 */
	public void onPortalCreate(Portal portal) {
	}

	// these methods are only fired for listeners registered to the portal.
	// don't add listeners to the portal here, that should be queued for later.

	public void onPortalRemove(Portal portal) {
	}

	public void onLinkedPortalCreate(Portal portal, Portal linked) {
	}
}
