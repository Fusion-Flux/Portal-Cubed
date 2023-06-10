package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class CompanionCubeEntity extends StorageCubeEntity {

    public CompanionCubeEntity(EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
    }

    private int t = 1500;

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level.isClientSide && !this.isRemoved()) {
            boolean bl = source.getEntity() instanceof Player && ((Player) source.getEntity()).getAbilities().instabuild;
            if (source.getEntity() instanceof Player || source == damageSources().outOfWorld()) {
                if (source.getEntity() instanceof Player && ((Player) source.getEntity()).getAbilities().mayBuild) {
                    if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS) && !bl) {
                        this.spawnAtLocation(PortalCubedItems.COMPANION_CUBE);
                    }
                    this.discard();
                }
                if (!(source.getEntity() instanceof Player)) {
                    if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS) && !bl) {
                        this.spawnAtLocation(PortalCubedItems.COMPANION_CUBE);
                    }
                    this.discard();
                }
            }

        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            if (t == 1500) {
                level.playSound(null, this, PortalCubedSounds.COMPANION_CUBE_AMBIANCE_EVENT, this.getSoundSource(), 1f, 1f);
            }
            t--;
            if (t == 0) {
                t = 1500;
            }

        }
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        t = 40;
        super.recreateFromPacket(packet);
    }

}
