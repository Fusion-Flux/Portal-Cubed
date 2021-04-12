package com.fusionflux.thinkingwithportatos.entity;

import com.fusionflux.thinkingwithportatos.blocks.GelFlat;
import com.fusionflux.thinkingwithportatos.blocks.ThinkingWithPortatosBlocks;
import com.fusionflux.thinkingwithportatos.packet.EntitySpawnPacket;
import com.fusionflux.thinkingwithportatos.packet.ThinkingWithPortatosPackets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.HashMap;

public class RepulsionGelOrbEntity extends ThrownItemEntity {
    private static final HashMap<Vec3i, BooleanProperty> DIRECTIONS;

    static {
        DIRECTIONS = new HashMap<Vec3i, BooleanProperty>() {{
            put(new Vec3i(0, 1, 0), GelFlat.UP);
            put(new Vec3i(0, -1, 0), GelFlat.DOWN);
            put(new Vec3i(1, 0, 0), GelFlat.EAST);
            put(new Vec3i(-1, 0, 0), GelFlat.WEST);
            put(new Vec3i(0, 0, -1), GelFlat.NORTH);
            put(new Vec3i(0, 0, 1), GelFlat.SOUTH);
        }};
    }

    public RepulsionGelOrbEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }
    public RepulsionGelOrbEntity(World world) {
        super(ThinkingWithPortatosEntities.REPULSION_GEL_ORB, 0, 0, 0, world);
    }
    public RepulsionGelOrbEntity(World world, LivingEntity owner) {
        super(ThinkingWithPortatosEntities.REPULSION_GEL_ORB, owner, world);
    }
    public RepulsionGelOrbEntity(World world, double x, double y, double z) {
        super(ThinkingWithPortatosEntities.REPULSION_GEL_ORB, x, y, z, world);
    }

    @Override
    protected Item getDefaultItem() {
        return null; // We will configure this later, once we have created the ProjectileItem.
    }

    @Environment(EnvType.CLIENT)
    private ParticleEffect getParticleParameters() { // Not entirely sure, but probably has do to with the snowball's particles. (OPTIONAL)
        ItemStack itemStack = this.getItem();
        return (ParticleEffect)(itemStack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack));
    }

    @Environment(EnvType.CLIENT)
    public void handleStatus(byte status) { // Also not entirely sure, but probably also has to do with the particles. This method (as well as the previous one) are optional, so if you don't understand, don't include this one.
        if (status == 3) {
            ParticleEffect particleEffect = this.getParticleParameters();

            for(int i = 0; i < 8; ++i) {
                this.world.addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    protected void onEntityHit(EntityHitResult entityHitResult) { // called on entity hit.
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity(); // sets a new Entity instance as the EntityHitResult (victim)
    }

    protected void onCollision(HitResult hitResult) { // called on collision with a block
        super.onCollision(hitResult);

        Vec3d nudgeVector = getVelocity().normalize().multiply(0.01);
        BlockPos hitPos = new BlockPos(hitResult.getPos().subtract(nudgeVector)); // The block that the orb was in, where we put the gel
        BlockPos hitBlock = new BlockPos(hitResult.getPos().add(nudgeVector)); // Get the position of the block that was just hit
        BlockState blockState = this.world.getBlockState(hitPos);
        if(blockState.isAir() || blockState.isOf(ThinkingWithPortatosBlocks.PROPULSION_GEL)) {
            if(this.world.getBlockState(hitBlock).isFullCube(this.world, getBlockPos())) {
                Vec3i cardinalDir = hitBlock.subtract(hitPos);

                if(!this.world.getBlockState(hitPos.add(cardinalDir)).isAir()) {
                    BlockState initialState;
                    if (blockState.isAir()) {
                        initialState = ThinkingWithPortatosBlocks.GEL.getDefaultState();
                    } else {
                        initialState = this.world.getBlockState(hitPos);
                    }
                    if(DIRECTIONS.containsKey(cardinalDir)) {
                        this.world.setBlockState(hitPos, initialState.with(DIRECTIONS.get(cardinalDir), true));
                    }
                }
            }
        }

        if (!this.world.isClient) { // checks if the world is client
            this.world.sendEntityStatus(this, (byte)3); // particle?
            this.remove(); // kills the projectile
        }
    }

    @Override
    public Packet createSpawnPacket() {
        return EntitySpawnPacket.create(this, ThinkingWithPortatosPackets.SPAWN_PACKET);
    }
}
