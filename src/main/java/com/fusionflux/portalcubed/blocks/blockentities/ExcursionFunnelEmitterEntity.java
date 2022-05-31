package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

/**
 * @author sailKite
 * @author FusionFlux
 * <p>
 * Handles the operating logic for the {@link HardLightBridgeEmitterBlock} and their associated bridges.
 */
public class ExcursionFunnelEmitterEntity extends ExcursionFunnelEmitterEntityAbstract {

    public ExcursionFunnelEmitterEntity( BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.EXCURSION_FUNNEL_EMMITER_ENTITY,pos,state);
    }

    public static void tick1(World world, BlockPos pos, BlockState state, ExcursionFunnelEmitterEntity blockEntity) {
        if (!world.isClient) {
            boolean redstonePowered = world.isReceivingRedstonePower(blockEntity.getPos());

            if (redstonePowered) {

                if (!world.getBlockState(pos).get(Properties.POWERED)) {
                    blockEntity.togglePowered(world.getBlockState(pos));
                }

                BlockPos translatedPos = pos;

                if (blockEntity.funnels != null) {
                    List<BlockPos> modfunnels = new ArrayList<>();

                    for (int i = 0; i <= blockEntity.MAX_RANGE; i++) {
                        translatedPos = translatedPos.offset(blockEntity.getCachedState().get(Properties.FACING));
                        if (world.isAir(translatedPos) || world.getBlockState(translatedPos).getHardness(world, translatedPos) <= 0.1F || world.getBlockState(translatedPos).getBlock().equals(PortalCubedBlocks.EXCURSION_FUNNEL)) {
                            world.setBlockState(translatedPos, PortalCubedBlocks.EXCURSION_FUNNEL.getDefaultState());

                            ExcursionFunnelEntityMain funnel = ((ExcursionFunnelEntityMain) Objects.requireNonNull(world.getBlockEntity(translatedPos)));

                            modfunnels.add(funnel.getPos());
                            blockEntity.funnels.add(funnel.getPos());

                            if(!funnel.emitters.contains(pos) ) {
                                funnel.emitters.add(pos);
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