package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.HardLightBridgeEmitterBlock;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.entity.OldApCubeEntity;
import com.fusionflux.portalcubed.entity.Portal1CompanionCubeEntity;
import com.fusionflux.portalcubed.entity.Portal1StorageCubeEntity;
import com.fusionflux.portalcubed.entity.StorageCubeEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

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

    public static void tick1(World world, BlockPos pos, BlockState state, OldApFloorButtonBlockEntity blockEntity) {
        if (!world.isClient) {
            Direction storedDirec = blockEntity.getCachedState().get(Properties.FACING);
            Direction storedDirecOpp = storedDirec.getOpposite();
            BlockPos transPos = pos.offset(storedDirec);

            Box portalCheckBox = new Box(transPos);

            portalCheckBox = portalCheckBox.contract(Math.abs(storedDirecOpp.getOffsetX()) * .75, Math.abs(storedDirecOpp.getOffsetY()) * .75, Math.abs(storedDirecOpp.getOffsetZ()) * .75).offset(storedDirecOpp.getOffsetX() * .5, storedDirecOpp.getOffsetY() * .5, storedDirecOpp.getOffsetZ() * .5);
            List<LivingEntity> entities = world.getNonSpectatingEntities(LivingEntity.class, portalCheckBox);

            boolean isPowered = false;
            for (LivingEntity living : entities) {
                if (living instanceof PlayerEntity || living instanceof StorageCubeEntity || living instanceof Portal1CompanionCubeEntity || living instanceof Portal1StorageCubeEntity || living instanceof OldApCubeEntity) {
                    isPowered = true;
                    break;
                }
            }

            blockEntity.updateState(state, isPowered);

        }


    }

    public void updateState(BlockState state, boolean toggle) {
        if (world != null) {
            world.setBlockState(pos, state.with(Properties.ENABLED, toggle), 3);
        }
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
    }

}
