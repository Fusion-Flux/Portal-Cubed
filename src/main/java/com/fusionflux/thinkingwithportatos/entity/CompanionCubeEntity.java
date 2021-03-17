package com.fusionflux.thinkingwithportatos.entity;

import com.fusionflux.thinkingwithportatos.ThinkingWithPortatos;
import com.fusionflux.thinkingwithportatos.items.ThinkingWithPortatosItems;
import com.fusionflux.thinkingwithportatos.sound.ThinkingWithPortatosSounds;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.Objects;

public class CompanionCubeEntity extends CubeEntity {
    public static final Identifier SPAWN_PACKET = new Identifier(ThinkingWithPortatos.MODID, "companion_cube");
    private float storedDamage = 0.0F;
    public CompanionCubeEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }


    private int t = 1760;
    @Override
    public void tick() {
        if(this.world.isClient) {
            if (t == 1760) {
                MinecraftClient.getInstance().getSoundManager().play(new EntityTrackingSoundInstance(ThinkingWithPortatosSounds.COMPANION_CUBE_AMBIANCE_EVENT, this.getSoundCategory(),.0008F,1F,this));
                //Objects.requireNonNull(this.getServer()).getPlayerManager().sendToAround(null,this.getPos().getX(),this.getPos().getY(),this.getPos().getZ(),.1,world.getRegistryKey(),new PlaySoundFromEntityS2CPacket(ThinkingWithPortatosSounds.COMPANION_CUBE_AMBIANCE_EVENT, this.getSoundCategory(),this,.00001F,1F));
                t--;

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
            boolean bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity)source.getAttacker()).abilities.creativeMode;
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
