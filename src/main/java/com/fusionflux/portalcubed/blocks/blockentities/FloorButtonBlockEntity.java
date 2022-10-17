package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.entity.RedirectionCubeEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
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
public class FloorButtonBlockEntity extends BlockEntity {

    public final int MAX_RANGE = PortalCubedConfig.maxBridgeLength;


    public FloorButtonBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.FLOOR_BUTTON_BLOCK_ENTITY,pos,state);

    }

    public static void tick1(World world, BlockPos pos, BlockState state, FloorButtonBlockEntity blockEntity) {
        if (!world.isClient) {
            Direction storedDirec = blockEntity.getCachedState().get(Properties.FACING);
            Direction storedDirecOpp = storedDirec.getOpposite();
            BlockPos transPos = pos.offset(storedDirec);

            Box portalCheckBox = new Box(transPos);

            portalCheckBox = portalCheckBox.contract(Math.abs(storedDirecOpp.getOffsetX())*.75,Math.abs(storedDirecOpp.getOffsetY())*.75,Math.abs(storedDirecOpp.getOffsetZ())*.75).offset(storedDirecOpp.getOffsetX()*.5,storedDirecOpp.getOffsetY()*.5,storedDirecOpp.getOffsetZ()*.5);
            List<LivingEntity> entities = world.getNonSpectatingEntities(LivingEntity.class, portalCheckBox);

            boolean isPowered = false;
            for (LivingEntity living : entities) {
                if(living != null){
                    isPowered = true;
                }
            }

            blockEntity.updateState(state,isPowered);

        }


    }

    public void playSound(SoundEvent soundEvent) {
        this.world.playSound(null, this.pos, soundEvent, SoundCategory.BLOCKS, 0.1F, 3.0F);
    }

    public void updateState(BlockState state, boolean toggle) {
        if(world != null)
        world.setBlockState(pos,state.with(Properties.ENABLED,toggle),3);
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