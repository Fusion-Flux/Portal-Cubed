package com.fusionflux.thinkingwithportatos.entity;

import com.fusionflux.thinkingwithportatos.ThinkingWithPortatos;
import com.fusionflux.thinkingwithportatos.items.ThinkingWithPortatosItems;
import com.fusionflux.thinkingwithportatos.sound.ThinkingWithPortatosSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class CompanionCubeEntity extends CubeEntity {
    public static final Identifier SPAWN_PACKET = new Identifier(ThinkingWithPortatos.MODID, "companion_cube");
    private float storedDamage = 0.0F;
    private int t = 1760;


    public CompanionCubeEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        if (this.world.isClient) {
            if (t == 1760) {
                MinecraftClient.getInstance().getSoundManager().play(new EntityTrackingSoundInstance(ThinkingWithPortatosSounds.COMPANION_CUBE_AMBIANCE_EVENT, this.getSoundCategory(), .0008F, 1F, this));
            }
            t--;
            if (t == 0) {
                t = 1760;
            }

        }
        super.tick();
    }

    @Override
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
                    this.dropItem(ThinkingWithPortatosItems.COMPANION_CUBE);
                }

                this.remove();
            }

            return true;
        } else {
            return true;
        }
    }
}
