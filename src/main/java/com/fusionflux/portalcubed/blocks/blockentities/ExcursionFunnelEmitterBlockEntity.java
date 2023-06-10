package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.HardLightBridgeEmitterBlock;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author sailKite
 * @author FusionFlux
 * <p>
 * Handles the operating logic for the {@link HardLightBridgeEmitterBlock} and their associated bridges.
 */
public class ExcursionFunnelEmitterBlockEntity extends AbstractExcursionFunnelEmitterBlockEntity {

    public ExcursionFunnelEmitterBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.EXCURSION_FUNNEL_EMITTER_ENTITY, pos, state);
    }

    public static void tick1(Level world, BlockPos pos, @SuppressWarnings("unused") BlockState state, ExcursionFunnelEmitterBlockEntity blockEntity) {
        if (!world.isClientSide) {
            boolean redstonePowered = world.hasNeighborSignal(blockEntity.getBlockPos());

            if (redstonePowered) {

                if (!world.getBlockState(pos).getValue(BlockStateProperties.POWERED)) {
                    blockEntity.togglePowered(world.getBlockState(pos));
                }

                BlockPos translatedPos = pos;
                BlockPos savedPos = pos;
                if (blockEntity.funnels != null) {
                    Set<BlockPos> modFunnels = new HashSet<>();
                    Set<BlockPos> portalFunnels = new HashSet<>();
                    boolean teleported = false;
                    Direction storedDirection = blockEntity.getBlockState().getValue(BlockStateProperties.FACING);
                    for (int i = 0; i <= blockEntity.maxRange; i++) {
                        if (!teleported) {
                            translatedPos = translatedPos.relative(storedDirection);
                        } else {
                            teleported = false;
                        }
                        if (translatedPos.getY() < world.getMaxBuildHeight() && translatedPos.getY() > world.getMinBuildHeight() && (world.isEmptyBlock(translatedPos) || (world.getBlockState(translatedPos).getDestroySpeed(world, translatedPos) <= 0.1F && world.getBlockState(translatedPos).getDestroySpeed(world, translatedPos) != -1F) || world.getBlockState(translatedPos).getBlock().equals(PortalCubedBlocks.EXCURSION_FUNNEL)) && !world.getBlockState(translatedPos).getBlock().equals(Blocks.BARRIER)) {
                            world.setBlockAndUpdate(translatedPos, PortalCubedBlocks.EXCURSION_FUNNEL.defaultBlockState());

                            ExcursionFunnelMainBlockEntity funnel = ((ExcursionFunnelMainBlockEntity) Objects.requireNonNull(world.getBlockEntity(translatedPos)));

                            modFunnels.add(funnel.getBlockPos());
                            blockEntity.funnels.add(funnel.getBlockPos());
                            if (!savedPos.equals(pos)) {
                                portalFunnels.add(funnel.getBlockPos());
                                blockEntity.portalFunnels.add(funnel.getBlockPos());
                            }

                            if (!funnel.facing.contains(storedDirection)) {
                                funnel.facing.add(storedDirection);
                                funnel.emitters.add(funnel.facing.indexOf(storedDirection), pos);
                                funnel.portalEmitters.add(funnel.facing.indexOf(storedDirection), savedPos);
                            }

                            funnel.updateState(world.getBlockState(translatedPos), world, translatedPos, funnel);

                            AABB portalCheckBox = new AABB(translatedPos);

                            List<ExperimentalPortal> list = world.getEntitiesOfClass(ExperimentalPortal.class, portalCheckBox);


                            for (ExperimentalPortal portal : list) {
                                if (portal.getFacingDirection().getOpposite().equals(storedDirection)) {
                                    if (portal.getActive()) {
                                        Direction otherPortalVertFacing = Direction.fromNormal(BlockPos.containing(portal.getOtherAxisH().x, portal.getOtherAxisH().y, portal.getOtherAxisH().z));
                                        int offset = (int)(((portal.blockPosition().getX() - translatedPos.getX()) * Math.abs(portal.getAxisH().get().x)) + ((portal.blockPosition().getY() - translatedPos.getY()) * Math.abs(portal.getAxisH().get().y)) + ((portal.blockPosition().getZ() - translatedPos.getZ()) * Math.abs(portal.getAxisH().get().z)));
                                        Direction mainPortalVertFacing = Direction.fromNormal(BlockPos.containing(portal.getAxisH().get().x, portal.getAxisH().get().y, portal.getAxisH().get().z));
                                        assert mainPortalVertFacing != null;
                                        if (mainPortalVertFacing.equals(Direction.SOUTH)) {
                                            offset = (Math.abs(offset) - 1) * -1;
                                        }
                                        if (mainPortalVertFacing.equals(Direction.EAST)) {
                                            offset = (Math.abs(offset) - 1) * -1;
                                        }

                                        translatedPos = BlockPos.containing(portal.getDestination().get().x, portal.getDestination().get().y, portal.getDestination().get().z).relative(otherPortalVertFacing, offset);
                                        savedPos = translatedPos;
                                        assert otherPortalVertFacing != null;
                                        if (otherPortalVertFacing.equals(Direction.SOUTH)) {
                                            translatedPos = translatedPos.relative(Direction.NORTH, 1);
                                        }
                                        if (otherPortalVertFacing.equals(Direction.EAST)) {
                                            translatedPos = translatedPos.relative(Direction.WEST, 1);
                                        }

                                        storedDirection = Direction.fromNormal((int)portal.getOtherFacing().x, (int)portal.getOtherFacing().y, (int)portal.getOtherFacing().z);
                                        teleported = true;
                                        blockEntity.funnels = modFunnels;
                                        blockEntity.portalFunnels = portalFunnels;
                                    }
                                }
                            }
                        } else {
                            blockEntity.funnels = modFunnels;
                            blockEntity.portalFunnels = portalFunnels;
                            break;
                        }
                    }
                }

            }

            if (!redstonePowered) {
                if (world.getBlockState(pos).getValue(BlockStateProperties.POWERED)) {
                    blockEntity.togglePowered(world.getBlockState(pos));
                }
            }

        }


    }

}
