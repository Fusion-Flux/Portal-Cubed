package com.fusionflux.thinkingwithportatos.physics;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.physics.space.body.shape.BoundingBoxShape;
import dev.lazurite.rayon.core.impl.physics.space.body.type.Debuggable;
import dev.lazurite.rayon.core.impl.physics.space.body.type.TerrainLoading;
import dev.lazurite.rayon.core.impl.physics.space.util.Clump;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
import net.minecraft.entity.Entity;

public class EntityRigidBody extends PhysicsRigidBody implements Debuggable, TerrainLoading {
    private final int envLoadDistance;
    private final Entity entity;
    private Clump clump;

    public EntityRigidBody(Entity entity) {
        super(new BoundingBoxShape(entity.getBoundingBox()));
        this.entity = entity;
        this.envLoadDistance = (int) boundingBox(new BoundingBox()).getExtent(new Vector3f()).length() + 1;
        this.setPhysicsLocation(VectorHelper.vec3dToVector3f(entity.getPos()));
    }

    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public void setDoTerrainLoading(boolean terrainLoading) {
    }

    @Override
    public boolean shouldDoTerrainLoading() {
        return true;
    }

    @Override
    public int getEnvironmentLoadDistance() {
        return this.envLoadDistance;
    }

    @Override
    public void setEnvironmentLoadDistance(int environmentLoadDistance) {
    }

    @Override
    public Clump getClump() {
        return this.clump;
    }

    @Override
    public void setClump(Clump clump) {
        this.clump = clump;
    }
}
