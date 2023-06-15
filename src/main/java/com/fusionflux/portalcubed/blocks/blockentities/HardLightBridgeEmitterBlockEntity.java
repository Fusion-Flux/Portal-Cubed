package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.HardLightBridgeEmitterBlock;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.entity.Portal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

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
public class HardLightBridgeEmitterBlockEntity extends AbstractExcursionFunnelEmitterBlockEntity {

    public List<BlockPos> bridges;
    public List<BlockPos> portalBridges;

    public HardLightBridgeEmitterBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.HLB_EMITTER_ENTITY, pos, state);
        this.bridges = new ArrayList<>();
        this.portalBridges = new ArrayList<>();
    }
    public static void tick(Level world, BlockPos pos, @SuppressWarnings("unused") BlockState state, HardLightBridgeEmitterBlockEntity blockEntity) {
        if (!world.isClientSide) {
            boolean redstonePowered = world.hasNeighborSignal(blockEntity.getBlockPos());

            if (redstonePowered) {

                if (!world.getBlockState(pos).getValue(BlockStateProperties.POWERED)) {
                    blockEntity.togglePowered(world.getBlockState(pos));
                }

                BlockPos translatedPos = pos;
                BlockPos savedPos = pos;
                if (blockEntity.bridges != null) {
                    List<BlockPos> modFunnels = new ArrayList<>();
                    List<BlockPos> portalFunnels = new ArrayList<>();
                    boolean teleported = false;
                    Direction storedDirection = blockEntity.getBlockState().getValue(BlockStateProperties.FACING);
                    Direction vertDirection = Direction.NORTH;
                    for (int i = 0; i <= blockEntity.maxRange; i++) {
                        if (!teleported) {
                            translatedPos = translatedPos.relative(storedDirection);
                        } else {
                            teleported = false;
                        }
                        if (translatedPos.getY() < world.getMaxBuildHeight() && translatedPos.getY() > world.getMinBuildHeight() && (world.isEmptyBlock(translatedPos) || (world.getBlockState(translatedPos).getDestroySpeed(world, translatedPos) <= 0.1F && world.getBlockState(translatedPos).getDestroySpeed(world, translatedPos) != -1F) || world.getBlockState(translatedPos).getBlock().equals(PortalCubedBlocks.HLB_BLOCK)) && !world.getBlockState(translatedPos).getBlock().equals(Blocks.BARRIER)) {

                            if (!world.getBlockState(translatedPos).getBlock().equals(PortalCubedBlocks.HLB_BLOCK)) {
                                world.setBlockAndUpdate(translatedPos, PortalCubedBlocks.HLB_BLOCK.defaultBlockState());
                            }

                            HardLightBridgeBlockEntity bridge = ((HardLightBridgeBlockEntity) Objects.requireNonNull(world.getBlockEntity(translatedPos)));

                            modFunnels.add(bridge.getBlockPos());
                            blockEntity.bridges.add(bridge.getBlockPos());
                            if (!savedPos.equals(pos)) {
                                portalFunnels.add(bridge.getBlockPos());
                                blockEntity.portalBridges.add(bridge.getBlockPos());
                            }

                            if (!bridge.facing.contains(storedDirection)) {
                                bridge.facing.add(storedDirection);
                                bridge.facingVert.add(bridge.facing.indexOf(storedDirection), vertDirection);
                                bridge.emitters.add(bridge.facing.indexOf(storedDirection), pos);
                                bridge.portalEmitters.add(bridge.facing.indexOf(storedDirection), savedPos);
                            }

                            bridge.updateState(world.getBlockState(translatedPos), world, translatedPos, bridge);

                            AABB portalCheckBox = new AABB(translatedPos);

                            List<Portal> list = world.getEntitiesOfClass(Portal.class, portalCheckBox);


                            for (Portal portal : list) {
                                if (portal.getFacingDirection().getOpposite().equals(storedDirection)) {
                                    if (portal.getActive()) {
                                        final Vec3 otherNormal = portal.getOtherNormal().get();
                                        Direction otherPortalFacing = Direction.fromNormal(BlockPos.containing(otherNormal.x, otherNormal.y, otherNormal.z));
                                        Direction otherPortalVertFacing = Direction.fromNormal(BlockPos.containing(portal.getOtherAxisH().get().x, portal.getOtherAxisH().get().y, portal.getOtherAxisH().get().z));
                                        int offset = (int)(((portal.blockPosition().getX() - translatedPos.getX()) * Math.abs(portal.getAxisH().x)) + ((portal.blockPosition().getY() - translatedPos.getY()) * Math.abs(portal.getAxisH().y)) + ((portal.blockPosition().getZ() - translatedPos.getZ()) * Math.abs(portal.getAxisH().z)));
                                        Direction mainPortalVertFacing = Direction.fromNormal(BlockPos.containing(portal.getAxisH().x, portal.getAxisH().y, portal.getAxisH().z));
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

                                        assert otherPortalFacing != null;
                                        if (otherPortalFacing.equals(Direction.UP) || otherPortalFacing.equals(Direction.DOWN)) {
                                            vertDirection = otherPortalVertFacing;
                                        }

                                        storedDirection = Direction.fromNormal((int)otherNormal.x, (int)otherNormal.y, (int)otherNormal.z);
                                        teleported = true;
                                        blockEntity.bridges = modFunnels;
                                        blockEntity.portalBridges = portalFunnels;
                                    }
                                }
                            }
                        } else {
                            blockEntity.bridges = modFunnels;
                            blockEntity.portalBridges = portalFunnels;
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

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        List<Integer> posXList = new ArrayList<>();
        List<Integer> posYList = new ArrayList<>();
        List<Integer> posZList = new ArrayList<>();

        for (BlockPos pos : bridges) {
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

        for (BlockPos pos : portalBridges) {
            portalXList.add(pos.getX());
            portalYList.add(pos.getY());
            portalZList.add(pos.getZ());
        }

        tag.putIntArray("portalxList", portalXList);
        tag.putIntArray("portalyList", portalYList);
        tag.putIntArray("portalzList", portalZList);

        tag.putInt("size", bridges.size());
        tag.putInt("pSize", portalBridges.size());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        List<Integer> posXList;
        List<Integer> posYList;
        List<Integer> posZList;

        posXList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("xList")));
        posYList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("yList")));
        posZList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("zList")));

        int size = tag.getInt("size");

        if (!bridges.isEmpty())
            bridges.clear();

        for (int i = 0; i < size; i++) {
            bridges.add(new BlockPos.MutableBlockPos(posXList.get(i), posYList.get(i), posZList.get(i)));
        }

        List<Integer> portalXList;
        List<Integer> portalYList;
        List<Integer> portalZList;

        portalXList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("portalxList")));
        portalYList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("portalyList")));
        portalZList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("portalzList")));

        int pSize = tag.getInt("pSize");

        if (!portalBridges.isEmpty())
            portalBridges.clear();

        for (int i = 0; i < pSize; i++) {
            portalBridges.add(new BlockPos.MutableBlockPos(portalXList.get(i), portalYList.get(i), portalZList.get(i)));
        }
    }


}
