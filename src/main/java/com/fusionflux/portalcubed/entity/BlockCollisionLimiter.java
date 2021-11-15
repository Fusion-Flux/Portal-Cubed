package com.fusionflux.portalcubed.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

import java.util.UUID;

public class BlockCollisionLimiter {
    private final ThreadLocal<Pair<Long, UUID>> lastCollidingEntity = new ThreadLocal<>();

    /**
     * Writen by chylex and onehalf
     * Prevents handling collision for an entity multiple times if the entity is touching 2 or more blocks.
     * <p>
     * Because onEntityCollision is always called in succession for all blocks colliding with an entity,
     * it is enough to compare if either the world time or the entity has changed since last call (on the same thread).
     * <p>
     * Returns true if the collision should be handled.
     */
    public boolean check(World world, Entity entity) {
        long currentWorldTime = world.getTime();
        Pair<Long, UUID> last = this.lastCollidingEntity.get();
        if (last == null || last.getLeft() != currentWorldTime || !last.getRight().equals(entity.getUuid())) {
            this.lastCollidingEntity.set(new Pair(currentWorldTime, entity.getUuid()));
            return true;
        }
        return false;
    }
}