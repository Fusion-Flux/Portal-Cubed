package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.fusionflux.portalcubed.entity.RedirectionCubeEntity;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LaserEmitterBlockEntity extends BlockEntity {

    public final int maxRange = PortalCubedConfig.maxBridgeLength;

    public List<BlockPos> funnels;
    public List<BlockPos> portalFunnels;


    public LaserEmitterBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.LASER_EMITTER_ENTITY, pos, state);
        this.funnels = new ArrayList<>();
        this.portalFunnels = new ArrayList<>();
    }

    public static void tick1(World world, BlockPos pos, @SuppressWarnings("unused") BlockState state, LaserEmitterBlockEntity blockEntity) {
        if (!world.isClient) {
            boolean redstonePowered = world.isReceivingRedstonePower(blockEntity.getPos());

            if (redstonePowered) {

                if (!world.getBlockState(pos).get(Properties.POWERED)) {
                    blockEntity.togglePowered(world.getBlockState(pos));
                }

                BlockPos translatedPos = pos;
                BlockPos savedPos = pos;
                if (blockEntity.funnels != null) {
                    List<BlockPos> modFunnels = new ArrayList<>();
                    List<BlockPos> portalFunnels = new ArrayList<>();
                    boolean teleported = false;
                    Direction storedDirection = blockEntity.getCachedState().get(Properties.FACING);
                    for (int i = 0; i <= blockEntity.maxRange; i++) {
                        if (!teleported) {
                            translatedPos = translatedPos.offset(storedDirection);
                        } else {
                            teleported = false;
                        }
                        if (translatedPos.getY() < world.getTopY() && translatedPos.getY() > world.getBottomY() && (world.isAir(translatedPos) || (world.getBlockState(translatedPos).getHardness(world, translatedPos) <= 0.1F && world.getBlockState(translatedPos).getHardness(world, translatedPos) != -1F) || world.getBlockState(translatedPos).getBlock().equals(PortalCubedBlocks.LASER)) && !world.getBlockState(translatedPos).getBlock().equals(Blocks.BARRIER)) {
                            world.setBlockState(translatedPos, PortalCubedBlocks.LASER.getDefaultState());

                            LaserBlockEntity funnel = ((LaserBlockEntity) Objects.requireNonNull(world.getBlockEntity(translatedPos)));

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

                            Box portalCheckBox = new Box(translatedPos).contract(.25);

                            List<RedirectionCubeEntity> reflectCubes = world.getNonSpectatingEntities(RedirectionCubeEntity.class, portalCheckBox);
                            boolean isCube = false;
                            for (RedirectionCubeEntity cubes : reflectCubes) {
                                if (cubes != null) {
                                    savedPos = translatedPos;
                                    storedDirection = Direction.fromRotation(cubes.getRotYaw());
                                    isCube = true;
                                    cubes.setButtonTimer(1);
                                    break;
                                }
                            }

                            List<CorePhysicsEntity> blockCube = world.getNonSpectatingEntities(CorePhysicsEntity.class, portalCheckBox);
                            boolean blocked = false;
                            for (CorePhysicsEntity cubes : blockCube) {
                                if (!(cubes instanceof RedirectionCubeEntity)) {
                                    blocked = true;
                                    break;
                                }
                            }

                            if (blocked) {
                                blockEntity.funnels = modFunnels;
                                blockEntity.portalFunnels = portalFunnels;
                                break;
                            }
                            portalCheckBox = new Box(translatedPos);
                            List<ExperimentalPortal> list = world.getNonSpectatingEntities(ExperimentalPortal.class, portalCheckBox);

                            if (!isCube) {
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
                            }
                        } else if ((world.getBlockState(translatedPos).isIn(ConventionalBlockTags.GLASS_BLOCKS) || world.getBlockState(translatedPos).isIn(ConventionalBlockTags.GLASS_PANES)) && world.getBlockState(translatedPos).getOpacity(world, translatedPos) <= 0.0) {
                            blockEntity.funnels = modFunnels;
                            blockEntity.portalFunnels = portalFunnels;
                        } else {
                            blockEntity.funnels = modFunnels;
                            blockEntity.portalFunnels = portalFunnels;
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

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);

        List<Integer> posXList = new ArrayList<>();
        List<Integer> posYList = new ArrayList<>();
        List<Integer> posZList = new ArrayList<>();

        for (BlockPos pos : funnels) {
            posXList.add(pos.getX());
            posYList.add(pos.getY());
            posZList.add(pos.getZ());
        }

        tag.putIntArray("xList", posXList);
        tag.putIntArray("yList", posYList);
        tag.putIntArray("zList", posZList);

        List<Integer> portalXList = new ArrayList<>();
        List<Integer> portalYList = new ArrayList<>();
        List<Integer> portalZList = new ArrayList<>();

        for (BlockPos pos : portalFunnels) {
            portalXList.add(pos.getX());
            portalYList.add(pos.getY());
            portalZList.add(pos.getZ());
        }

        tag.putIntArray("portalxList", portalXList);
        tag.putIntArray("portalyList", portalYList);
        tag.putIntArray("portalzList", portalZList);

        tag.putInt("pSize", portalFunnels.size());
        tag.putInt("size", funnels.size());
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        List<Integer> posXList;
        List<Integer> posYList;
        List<Integer> posZList;

        posXList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("xList")));
        posYList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("yList")));
        posZList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("zList")));

        int size = tag.getInt("size");

        if (!funnels.isEmpty())
            funnels.clear();

        for (int i = 0; i < size; i++) {
            funnels.add(new BlockPos.Mutable(posXList.get(i), posYList.get(i), posZList.get(i)));
        }

        List<Integer> portalXList;
        List<Integer> portalYList;
        List<Integer> portalZList;

        portalXList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("portalxList")));
        portalYList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("portalyList")));
        portalZList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("portalzList")));

        int pSize = tag.getInt("pSize");

        if (!portalFunnels.isEmpty())
            portalFunnels.clear();

        for (int i = 0; i < pSize; i++) {
            portalFunnels.add(new BlockPos.Mutable(portalXList.get(i), portalYList.get(i), portalZList.get(i)));
        }
    }

    public void togglePowered(BlockState state) {
        assert world != null;
        world.setBlockState(pos, state.cycle(Properties.POWERED));
        if (world.getBlockState(pos).get(Properties.POWERED)) {
            world.playSound(null, this.pos, PortalCubedSounds.LASER_EMITTER_ACTIVATE_EVENT, SoundCategory.BLOCKS, 0.25f, 1f);
        }
    }
}
