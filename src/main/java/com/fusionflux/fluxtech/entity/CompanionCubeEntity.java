package com.fusionflux.fluxtech.entity;

import com.fusionflux.fluxtech.FluxTech;
import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.bullet.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.body.shape.BoundingBoxShape;
import dev.lazurite.rayon.impl.bullet.world.MinecraftSpace;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class CompanionCubeEntity extends CubeEntity {
    public static final Identifier SPAWN_PACKET = new Identifier(FluxTech.MOD_ID, "companion_cube");

    public CompanionCubeEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
        Rayon.THREAD.get(world).execute(space -> {
            this.RIGID_BODY.setCollisionShape(new BoundingBoxShape(this.getBoundingBox()));
            this.RIGID_BODY.setMass(1.0f);              // 0.0f - ? kg
            this.RIGID_BODY.setFriction(0.5f);          // 0.0f - 1.0f
            this.RIGID_BODY.setRestitution(0.5f);       // 0.0f - 1.0f
            this.RIGID_BODY.setDragCoefficient(0.05f);  // 0.0f - ?
            this.RIGID_BODY.setBlockLoadDistance(1);    // 1 - ? (affects performance extremely)
            this.RIGID_BODY.setDoFluidResistance(true);
        });
    }
}
