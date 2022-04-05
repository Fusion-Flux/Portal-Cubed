package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author sailKite
 * @author FusionFlux
 * <p>
 * Handles the operating logic for the {@link HardLightBridgeEmitterBlock} and their associated bridges.
 */
public class ReversedExcursionFunnelEmitterEntity extends ExcursionFunnelEmitterEntityAbstract {


    public ReversedExcursionFunnelEmitterEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.REVERSED_EXCURSION_FUNNEL_EMMITER_ENTITY,pos,state);
    }

    public static void tick3(World world, BlockPos pos, BlockState state, ReversedExcursionFunnelEmitterEntity blockEntity) {
        if (!world.isClient) {
            boolean redstonePowered = world.isReceivingRedstonePower(blockEntity.getPos());

            if (redstonePowered) {

                if (!world.getBlockState(pos).get(Properties.POWERED)) {
                    blockEntity.togglePowered(world.getBlockState(pos));
                }
                Direction facing = state.get(Properties.FACING);

                BlockPos.Mutable translatedPos = pos.mutableCopy();

                if (blockEntity.funnels != null) {
                    List<BlockPos.Mutable> modfunnels = new ArrayList<>();


                    for (int i = 0; i <= blockEntity.MAX_RANGE; i++) {
                        translatedPos.move(blockEntity.getCachedState().get(Properties.FACING));
                        if (world.isAir(translatedPos) || world.getBlockState(translatedPos).getHardness(world, translatedPos) <= 0.1F || world.getBlockState(translatedPos).getBlock().equals(PortalCubedBlocks.EXCURSION_FUNNEL)) {

                            world.setBlockState(translatedPos, PortalCubedBlocks.EXCURSION_FUNNEL.getDefaultState().with(Properties.FACING, facing));

                            ExcursionFunnelEntityMain funnel = ((ExcursionFunnelEntityMain) Objects.requireNonNull(world.getBlockEntity(translatedPos)));

                            modfunnels.add(funnel.getPos().mutableCopy());
                            blockEntity.funnels.add(funnel.getPos().mutableCopy());

                            if(!funnel.emitters.contains(pos.mutableCopy()) ) {
                                funnel.emitters.add(pos.mutableCopy());
                            }
                            funnel.updateState(world.getBlockState(translatedPos),world,translatedPos,funnel);
                        } else {
                            blockEntity.funnels = modfunnels;
                            break;
                        }
                    }
                }

            }

            if (!redstonePowered) {
                if (world.getBlockState(pos).get(Properties.POWERED)) {
                    blockEntity.togglePowered(world.getBlockState(pos));
                }
            }

        }


    }

}