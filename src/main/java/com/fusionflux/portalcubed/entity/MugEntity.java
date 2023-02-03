package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.items.PortalCubedItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.Random;

public class MugEntity extends CorePhysicsEntity  {

    public MugEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }
    final Random rand = new Random();

    @Override
    public void writeCustomDataToNbt(NbtCompound compoundTag) {
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound compoundTag) {
    }

    public int getMugType(){
        return getDataTracker().get(MUG_TYPE);
    }

    public void genMugType(){
        setMugType(rand.nextInt(4));
    }

    public void setMugType(Integer type) {
        this.getDataTracker().set(MUG_TYPE, type);
    }

    public static final TrackedData<Integer> MUG_TYPE = DataTracker.registerData(MugEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(MUG_TYPE, 20);
    }
    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.world.isClient && !this.isRemoved()) {
            boolean bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity) source.getAttacker()).getAbilities().creativeMode;
            if (source.getAttacker() instanceof PlayerEntity || source == DamageSource.OUT_OF_WORLD) {
                if(source.getAttacker() instanceof PlayerEntity && ((PlayerEntity) source.getAttacker()).getAbilities().allowModifyWorld){
                    if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS) && !bl) {
                        this.dropItem(PortalCubedItems.MUG);
                    }
                    this.discard();
                }
                if(!(source.getAttacker() instanceof PlayerEntity)) {
                    if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS) && !bl) {
                        this.dropItem(PortalCubedItems.MUG);
                    }
                    this.discard();
                }
            }

        }
        return false;
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        if(this.getMugType() == 20){
            setMugType(rand.nextInt(4));
        }
        super.onSpawnPacket(packet);
    }
}
