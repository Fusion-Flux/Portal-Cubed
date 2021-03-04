package com.fusionflux.fluxtech.blocks;

import com.fusionflux.fluxtech.entity.BlockCollisionLimiter;
import com.fusionflux.fluxtech.entity.EntityAttachments;
import com.fusionflux.fluxtech.sound.FluxTechSounds;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PropulsionGel extends Gel {

    private final BlockCollisionLimiter limiter = new BlockCollisionLimiter();


    public PropulsionGel(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false));
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        this.addCollisionEffects(world, entity);
    }


    private void addCollisionEffects(World world, Entity entity) {
        if (entity.getType().equals(EntityType.BOAT)) {
            entity.damage(DamageSource.MAGIC, 200);
        } else {
            if (entity.isOnGround()) {
                if (!entity.isSneaking()) {
                    if (limiter.check(world, entity)) {
                        if (entity.getVelocity().x < 5 && entity.getVelocity().x > -2 && entity.getVelocity().y < 2 && entity.getVelocity().y > -2) {
                            entity.setVelocity(entity.getVelocity().multiply(1.7, 1.0D, 1.7));
                        } else if (entity.getVelocity().x > 2 && entity.getVelocity().x < -2 && entity.getVelocity().y > 2 && entity.getVelocity().y < -2) {
                            entity.setVelocity(entity.getVelocity().multiply(1.01, 1.0D, 1.01));
                        }
                        if(((EntityAttachments) entity).getMaxFallSpeed()==0) {
                            entity.playSound(FluxTechSounds.GEL_RUN_EVENT, .3F, 1F);
                        }
                        ((EntityAttachments) entity).setMaxFallSpeed(10);
                    }
                }
            }
        }
    }
}
