package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.fusionflux.portalcubed.util.CustomProperties;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public class DualExcursionFunnelEmitterBlockEntity extends AbstractExcursionFunnelEmitterBlockEntity {

    public DualExcursionFunnelEmitterBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.DUAL_EXCURSION_FUNNEL_EMITTER_ENTITY, pos, state);
    }

    public static void tick2(World world, BlockPos pos, @SuppressWarnings("unused") BlockState state, DualExcursionFunnelEmitterBlockEntity blockEntity) {
        if (!world.isClient) {
            boolean redstonePowered = world.isReceivingRedstonePower(blockEntity.getPos());

            if (!redstonePowered) {

                if (world.getBlockState(pos).get(Properties.POWERED)) {
                    blockEntity.togglePowered(world.getBlockState(pos));
                    blockEntity.duelTogglePowered(world.getBlockState(pos));
                }

                BlockPos translatedPos = pos;
                BlockPos savedPos = pos;
                if (blockEntity.funnels != null) {
                    Set<BlockPos> modFunnels = new HashSet<>();
                    Set<BlockPos> portalFunnels = new HashSet<>();
                    boolean teleported = false;
                    Direction storedDirection = blockEntity.getCachedState().get(Properties.FACING);
                    for (int i = 0; i <= blockEntity.maxRange; i++) {
                        if (!teleported) {
                            translatedPos = translatedPos.offset(storedDirection);
                        } else {
                            teleported = false;
                        }

                        if (translatedPos.getY() < world.getTopY() && translatedPos.getY() > world.getBottomY() && (world.isAir(translatedPos) || (world.getBlockState(translatedPos).getHardness(world, translatedPos) <= 0.1F && world.getBlockState(translatedPos).getHardness(world, translatedPos) != -1F) || world.getBlockState(translatedPos).getBlock().equals(PortalCubedBlocks.EXCURSION_FUNNEL)) && !world.getBlockState(translatedPos).getBlock().equals(Blocks.BARRIER)) {

                            world.setBlockState(translatedPos, PortalCubedBlocks.EXCURSION_FUNNEL.getDefaultState());

                            ExcursionFunnelMainBlockEntity funnel = ((ExcursionFunnelMainBlockEntity) Objects.requireNonNull(world.getBlockEntity(translatedPos)));

                            modFunnels.add(funnel.getPos());
                            blockEntity.funnels.add(funnel.getPos());
                            if (!savedPos.equals(pos)) {
                                portalFunnels.add(funnel.getPos());
                                blockEntity.portalFunnels.add(funnel.getPos());
                            }

                            if (!funnel.facing.contains(storedDirection)) {
                                funnel.facing.add(storedDirection);
                                funnel.emitters.add(funnel.facing.indexOf(storedDirection), pos);
                                funnel.portalEmitters.add(funnel.facing.indexOf(storedDirection), savedPos);
                            }

                            funnel.updateState(world.getBlockState(translatedPos), world, translatedPos, funnel);

                            Box portalCheckBox = new Box(translatedPos);

                            List<ExperimentalPortal> list = world.getNonSpectatingEntities(ExperimentalPortal.class, portalCheckBox);


                            for (ExperimentalPortal portal : list) {
                                if (portal.getFacingDirection().getOpposite().equals(storedDirection)) {
                                    if (portal.getActive()) {
                                        Direction otherPortalVertFacing = Direction.fromVector(new BlockPos(portal.getOtherAxisH().x, portal.getOtherAxisH().y, portal.getOtherAxisH().z));
                                        int offset = (int)(((portal.getBlockPos().getX() - translatedPos.getX()) * Math.abs(portal.getAxisH().get().x)) + ((portal.getBlockPos().getY() - translatedPos.getY()) * Math.abs(portal.getAxisH().get().y)) + ((portal.getBlockPos().getZ() - translatedPos.getZ()) * Math.abs(portal.getAxisH().get().z)));
                                        Direction mainPortalVertFacing = Direction.fromVector(new BlockPos(portal.getAxisH().get().x, portal.getAxisH().get().y, portal.getAxisH().get().z));
                                        assert mainPortalVertFacing != null;
                                        if (mainPortalVertFacing.equals(Direction.SOUTH)) {
                                            offset = (Math.abs(offset) - 1) * -1;
                                        }
                                        if (mainPortalVertFacing.equals(Direction.EAST)) {
                                            offset = (Math.abs(offset) - 1) * -1;
                                        }

                                        translatedPos = new BlockPos(portal.getDestination().get().x, portal.getDestination().get().y, portal.getDestination().get().z).offset(otherPortalVertFacing, offset);
                                        savedPos = translatedPos;
                                        assert otherPortalVertFacing != null;
                                        if (otherPortalVertFacing.equals(Direction.SOUTH)) {
                                            translatedPos = translatedPos.offset(Direction.NORTH, 1);
                                        }
                                        if (otherPortalVertFacing.equals(Direction.EAST)) {
                                            translatedPos = translatedPos.offset(Direction.WEST, 1);
                                        }

                                        storedDirection = Direction.fromVector((int)portal.getOtherFacing().x, (int)portal.getOtherFacing().y, (int)portal.getOtherFacing().z);
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

            if (redstonePowered) {
                if (!world.getBlockState(pos).get(Properties.POWERED)) {
                    blockEntity.togglePowered(world.getBlockState(pos));
                    blockEntity.duelTogglePowered(world.getBlockState(pos));
                }


                BlockPos translatedPos = pos;
                BlockPos savedPos = pos;
                if (blockEntity.funnels != null) {
                    Set<BlockPos> modFunnels = new HashSet<>();
                    Set<BlockPos> portalFunnels = new HashSet<>();
                    boolean teleported = false;
                    Direction storedDirection = blockEntity.getCachedState().get(Properties.FACING);
                    for (int i = 0; i <= blockEntity.maxRange; i++) {
                        if (!teleported) {
                            translatedPos = translatedPos.offset(storedDirection);
                        } else {
                            teleported = false;
                        }

                        if (translatedPos.getY() < world.getTopY() && translatedPos.getY() > world.getBottomY() && (world.isAir(translatedPos) || world.getBlockState(translatedPos).getHardness(world, translatedPos) <= 0.1F || world.getBlockState(translatedPos).getBlock().equals(PortalCubedBlocks.EXCURSION_FUNNEL)) && !world.getBlockState(translatedPos).getBlock().equals(Blocks.BARRIER)) {

                            world.setBlockState(translatedPos, PortalCubedBlocks.EXCURSION_FUNNEL.getDefaultState());

                            ExcursionFunnelMainBlockEntity funnel = ((ExcursionFunnelMainBlockEntity) Objects.requireNonNull(world.getBlockEntity(translatedPos)));

                            modFunnels.add(funnel.getPos());
                            blockEntity.funnels.add(funnel.getPos());
                            if (!savedPos.equals(pos)) {
                                portalFunnels.add(funnel.getPos());
                                blockEntity.portalFunnels.add(funnel.getPos());
                            }
                            if (!funnel.facing.contains(storedDirection)) {
                                funnel.facing.add(storedDirection);
                                funnel.emitters.add(funnel.facing.indexOf(storedDirection), pos);
                                funnel.portalEmitters.add(funnel.facing.indexOf(storedDirection), savedPos);
                            }

                            funnel.updateState(world.getBlockState(translatedPos), world, translatedPos, funnel);

                            Box portalCheckBox = new Box(translatedPos);

                            List<ExperimentalPortal> list = world.getNonSpectatingEntities(ExperimentalPortal.class, portalCheckBox);


                            for (ExperimentalPortal portal : list) {
                                if (portal.getFacingDirection().getOpposite().equals(storedDirection)) {
                                    if (portal.getActive()) {
                                        Direction otherPortalVertFacing = Direction.fromVector(new BlockPos(portal.getOtherAxisH().x, portal.getOtherAxisH().y, portal.getOtherAxisH().z));
                                        int offset = (int)(((portal.getBlockPos().getX() - translatedPos.getX()) * Math.abs(portal.getAxisH().get().x)) + ((portal.getBlockPos().getY() - translatedPos.getY()) * Math.abs(portal.getAxisH().get().y)) + ((portal.getBlockPos().getZ() - translatedPos.getZ()) * Math.abs(portal.getAxisH().get().z)));
                                        Direction mainPortalVertFacing = Direction.fromVector(new BlockPos(portal.getAxisH().get().x, portal.getAxisH().get().y, portal.getAxisH().get().z));
                                        assert mainPortalVertFacing != null;
                                        if (mainPortalVertFacing.equals(Direction.SOUTH)) {
                                            offset = (Math.abs(offset) - 1) * -1;
                                        }
                                        if (mainPortalVertFacing.equals(Direction.EAST)) {
                                            offset = (Math.abs(offset) - 1) * -1;
                                        }

                                        translatedPos = new BlockPos(portal.getDestination().get().x, portal.getDestination().get().y, portal.getDestination().get().z).offset(otherPortalVertFacing, offset);
                                        savedPos = translatedPos;
                                        assert otherPortalVertFacing != null;
                                        if (otherPortalVertFacing.equals(Direction.SOUTH)) {
                                            translatedPos = translatedPos.offset(Direction.NORTH, 1);
                                        }
                                        if (otherPortalVertFacing.equals(Direction.EAST)) {
                                            translatedPos = translatedPos.offset(Direction.WEST, 1);
                                        }

                                        storedDirection = Direction.fromVector((int)portal.getOtherFacing().x, (int)portal.getOtherFacing().y, (int)portal.getOtherFacing().z);
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

        }


    }

    public void duelTogglePowered(BlockState state) {
        assert world != null;
        world.setBlockState(pos, state.cycle(CustomProperties.REVERSED));
    }
}
