package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.items.PortalCubedItems;
import net.minecraft.client.render.entity.CatEntityRenderer;
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

public class MugEntity extends StorageCubeEntity  {

    public MugEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }
    Random rand = new Random();

    //int mugType = 20;

    @Override
    public void writeCustomDataToNbt(NbtCompound compoundTag) {
        //compoundTag.putInt("mugtype",mugType);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound compoundTag) {
        //mugType = compoundTag.getInt("mugtype");
    }

    //public int getMugType(){
    //    return mugType;
    //}
    //public void genMugType(){
    //        mugType = rand.nextInt(4);
    //    }
    public int getMugType(){
        return getDataTracker().get(MUGTYPE);
    }

    public void genMugType(){
        setMugType(rand.nextInt(4));
    }

    public void setMugType(Integer type) {
        this.getDataTracker().set(MUGTYPE, type);
    }

    public static final TrackedData<Integer> MUGTYPE = DataTracker.registerData(MugEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(MUGTYPE, 20);
    }
    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.world.isClient && !this.isRemoved()) {
            boolean bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity) source.getAttacker()).getAbilities().creativeMode;
            if (source.getAttacker() instanceof PlayerEntity || source == DamageSource.OUT_OF_WORLD) {
                if(source.getAttacker() instanceof PlayerEntity && ((PlayerEntity) source.getAttacker()).getAbilities().allowModifyWorld){
                    if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS) && !bl) {
                        this.dropItem(PortalCubedItems.RADIO);
                    }
                    this.discard();
                }
                if(!(source.getAttacker() instanceof PlayerEntity)) {
                    if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS) && !bl) {
                        this.dropItem(PortalCubedItems.RADIO);
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
