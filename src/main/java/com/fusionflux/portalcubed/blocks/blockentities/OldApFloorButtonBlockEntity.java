package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.HardLightBridgeEmitterBlock;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.entity.*;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * @author sailKite
 * @author FusionFlux
 * <p>
 * Handles the operating logic for the {@link HardLightBridgeEmitterBlock} and their associated bridges.
 */
public class OldApFloorButtonBlockEntity extends BlockEntity {


    public OldApFloorButtonBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.OLD_AP_FLOOR_BUTTON_BLOCK_ENTITY, pos, state);

    }

    public static void tick1(Level world, BlockPos pos, BlockState state, OldApFloorButtonBlockEntity blockEntity) {
        if (!world.isClientSide) {
            Direction storedDirec = blockEntity.getBlockState().getValue(BlockStateProperties.FACING);
            Direction storedDirecOpp = storedDirec.getOpposite();
            BlockPos transPos = pos.relative(storedDirec);

            AABB portalCheckBox = new AABB(transPos);

            portalCheckBox = portalCheckBox.deflate(Math.abs(storedDirecOpp.getStepX()) * .75, Math.abs(storedDirecOpp.getStepY()) * .75, Math.abs(storedDirecOpp.getStepZ()) * .75).move(storedDirecOpp.getStepX() * .5, storedDirecOpp.getStepY() * .5, storedDirecOpp.getStepZ() * .5);
            List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, portalCheckBox);

            boolean isPowered = false;
            for (LivingEntity living : entities) {
                if (living instanceof Player || living instanceof StorageCubeEntity || living instanceof Portal1CompanionCubeEntity || living instanceof Portal1StorageCubeEntity || living instanceof OldApCubeEntity) {
                    if (living instanceof CorePhysicsEntity physicsEntity) {
                        if (physicsEntity.fizzling()) {
                            physicsEntity.push(0, 0.015, 0);
                        } else {
                            isPowered = true;
                            if (living instanceof StorageCubeEntity cube) {
                                cube.setButtonTimer(1);
                            }
                        }
                    } else {
                        isPowered = true;
                    }
                }
            }

            if (state.getValue(BlockStateProperties.ENABLED) != isPowered) {
                blockEntity.updateState(state, isPowered);
            }

        }


    }

    public void updateState(BlockState state, boolean toggle) {
        if (level != null) {
            level.setBlock(worldPosition, state.setValue(BlockStateProperties.ENABLED, toggle), 3);
            if (!level.isClientSide) {
                level.playSound(
                    null, worldPosition,
                    toggle ? PortalCubedSounds.OLD_AP_FLOOR_BUTTON_PRESS_EVENT : PortalCubedSounds.OLD_AP_FLOOR_BUTTON_RELEASE_EVENT,
                    SoundSource.BLOCKS, 0.8f, 1f
                );
            }
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
    }

}
