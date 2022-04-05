package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.accessor.CustomRaycastContext;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.google.common.collect.AbstractIterator;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.function.Predicate;

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