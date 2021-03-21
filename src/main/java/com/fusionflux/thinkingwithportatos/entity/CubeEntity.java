package com.fusionflux.thinkingwithportatos.entity;

import com.fusionflux.thinkingwithportatos.items.ThinkingWithPortatosItems;
import com.fusionflux.thinkingwithportatos.sound.ThinkingWithPortatosSounds;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.bullet.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.body.shape.BoundingBoxShape;
import dev.lazurite.rayon.impl.bullet.world.MinecraftSpace;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import static com.fusionflux.thinkingwithportatos.ThinkingWithPortatos.id;

public class CubeEntity extends Entity implements PhysicsElement {
    public static final Identifier SPAWN_PACKET = id("spawn_cube");
    protected final ElementRigidBody RIGID_BODY = new ElementRigidBody(this);
    private float storedDamage = 0.0F;

    public CubeEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
        Rayon.SPACE.get(world).getThread().execute(() -> {
            this.RIGID_BODY.setCollisionShape(new BoundingBoxShape(this.getBoundingBox()));
            this.RIGID_BODY.setMass(1.0f);                 // 0.0f - ? kg
            this.RIGID_BODY.setFriction(0.8f);             // 0.0f - 1.0f
            this.RIGID_BODY.setRestitution(0.3f);          // 0.0f - 1.0f
            this.RIGID_BODY.setDragCoefficient(0.0f);     // 0.0f - ?
            this.RIGID_BODY.setEnvironmentLoadDistance(1); // 1 - ? (affects performance extremely)
            this.RIGID_BODY.setDoFluidResistance(true);
        });
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        world.playSound(null, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), ThinkingWithPortatosSounds.CUBE_HIT_EVENT, SoundCategory.NEUTRAL, .3F, 1F);
    }


    @Override
    public void tick() {
        super.tick();
        doDamage();
    }

    private void doDamage() {
        float velocity = getRigidBody().getLinearVelocity(new Vector3f()).length();

        float momentum = velocity * getRigidBody().getMass();

        if (velocity >= 15) {
            for (Entity entity : this.getEntityWorld().getOtherEntities(this, this.getBoundingBox(), (entity) -> entity instanceof LivingEntity)) {
                entity.damage(DamageSource.GENERIC, momentum / 20.0f);

                /* Loses 90% of its speed */
                Rayon.SPACE.get(world).getThread().execute(() ->
                        getRigidBody().applyCentralImpulse(getRigidBody().getLinearVelocity(new Vector3f()).multLocal(0.1f).multLocal(getRigidBody().getMass()))
                );
            }
        }
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean collides() {
        return !this.removed;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    protected void initDataTracker() {

    }

    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!this.world.isClient && !this.removed) {
            this.storedDamage += amount;
            this.scheduleVelocityUpdate();
            boolean bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity) source.getAttacker()).abilities.creativeMode;
            if (bl || this.storedDamage >= 20.0F) {
                if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                    // TODO
                    this.dropItem(ThinkingWithPortatosItems.CUBE);
                }

                this.remove();
            }

            return true;
        } else {
            return true;
        }
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {

    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {

    }

    @Override
    public Packet<?> createSpawnPacket() {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());

        packet.writeVarInt(Registry.ENTITY_TYPE.getRawId(this.getType()))
                .writeUuid(this.getUuid())
                .writeVarInt(this.getEntityId())
                .writeDouble(this.getX())
                .writeDouble(this.getY())
                .writeDouble(this.getZ())
                .writeByte(MathHelper.floor(this.pitch * 256.0F / 360.0F))
                .writeByte(MathHelper.floor(this.yaw * 256.0F / 360.0F));

        return ServerPlayNetworking.createS2CPacket(SPAWN_PACKET, packet);
    }

    @Override
    public void step(MinecraftSpace space) {

    }

    @Override
    public ElementRigidBody getRigidBody() {
        return this.RIGID_BODY;
    }
}
