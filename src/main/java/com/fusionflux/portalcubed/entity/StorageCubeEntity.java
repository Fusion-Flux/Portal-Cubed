package com.fusionflux.portalcubed.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class StorageCubeEntity extends PathAwareEntity  {
    public StorageCubeEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    private float storedDamage = 0.0F;

    @Override
    public boolean collides() {
        return !this.isRemoved();
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!this.world.isClient && !this.isRemoved()) {
            this.storedDamage += amount;
            //this.scheduleVelocityUpdate();
            boolean bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity) source.getAttacker()).getAbilities().creativeMode;
            if (bl || this.storedDamage >= 20.0F) {
                if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                    // TODO
                    //this.dropItem(ThinkingWithPortatosItems.COMPANION_CUBE);
                }

                this.discard();
            }

            return true;
        } else {
            return true;
        }
    }

    @Override
    public boolean shouldRenderName() {
        return false;
    }
    @Override
    public boolean isCustomNameVisible() {
        return false;
    }

    @Override
    @Nullable
    public Text getCustomName() {
        return null;
    }

}
