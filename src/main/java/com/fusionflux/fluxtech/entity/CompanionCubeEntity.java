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
    }
}
