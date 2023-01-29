package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
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

/**
 * @author sailKite
 * @author FusionFlux
 * <p>
 * Handles the operating logic for the {@link HardLightBridgeEmitterBlock} and their associated bridges.
 */
public class HardLightBridgeEmitterBlockEntity extends ExcursionFunnelEmitterEntityAbstract {

    public final int MAX_RANGE = PortalCubedConfig.maxBridgeLength;
    public List<BlockPos> bridges;
    public List<BlockPos> portalBridges;

    public HardLightBridgeEmitterBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.HLB_EMITTER_ENTITY,pos,state);
        this.bridges = new ArrayList<>();
        this.portalBridges = new ArrayList<>();
    }
    public static void tick(World world, BlockPos pos, @SuppressWarnings("unused") BlockState state, HardLightBridgeEmitterBlockEntity blockEntity) {
        if (!world.isClient) {
            boolean redstonePowered = world.isReceivingRedstonePower(blockEntity.getPos());

            if (redstonePowered) {

                if (!world.getBlockState(pos).get(Properties.POWERED)) {
                    blockEntity.togglePowered(world.getBlockState(pos));
                }

                BlockPos translatedPos = pos;
                BlockPos savedPos = pos;
                if (blockEntity.bridges != null) {
                    List<BlockPos> modFunnels = new ArrayList<>();
                    List<BlockPos> portalFunnels = new ArrayList<>();
                    boolean teleported = false;
                    Direction storedDirection = blockEntity.getCachedState().get(Properties.FACING);
                    Direction vertDirection = Direction.NORTH;
                    for (int i = 0; i <= blockEntity.MAX_RANGE; i++) {
                        if(!teleported) {
                            translatedPos = translatedPos.offset(storedDirection);
                        } else{
                            teleported = false;
                        }
                        if (translatedPos.getY() < world.getTopY() && translatedPos.getY() > world.getBottomY() && (world.isAir(translatedPos) || (world.getBlockState(translatedPos).getHardness(world, translatedPos) <= 0.1F && world.getBlockState(translatedPos).getHardness(world, translatedPos) != -1F) || world.getBlockState(translatedPos).getBlock().equals(PortalCubedBlocks.HLB_BLOCK)) && !world.getBlockState(translatedPos).getBlock().equals(Blocks.BARRIER)) {

                            if(!world.getBlockState(translatedPos).getBlock().equals(PortalCubedBlocks.HLB_BLOCK)) {
                                world.setBlockState(translatedPos, PortalCubedBlocks.HLB_BLOCK.getDefaultState());
                            }

                            HardLightBridgeBlockEntity bridge = ((HardLightBridgeBlockEntity) Objects.requireNonNull(world.getBlockEntity(translatedPos)));

                            modFunnels.add(bridge.getPos());
                            blockEntity.bridges.add(bridge.getPos());
                            if(!savedPos.equals(pos)){
                                portalFunnels.add(bridge.getPos());
                                blockEntity.portalBridges.add(bridge.getPos());
                            }

                            if(!bridge.facing.contains(storedDirection)){
                                bridge.facing.add(storedDirection);
                                bridge.facingVert.add(bridge.facing.indexOf(storedDirection),vertDirection);
                                bridge.emitters.add(bridge.facing.indexOf(storedDirection),pos);
                                bridge.portalEmitters.add(bridge.facing.indexOf(storedDirection),savedPos);
                            }

                            bridge.updateState(world.getBlockState(translatedPos),world,translatedPos,bridge);

                            Box portalCheckBox = new Box(translatedPos);

                            List<ExperimentalPortal> list = world.getNonSpectatingEntities(ExperimentalPortal.class, portalCheckBox);


                            for (ExperimentalPortal portal : list) {
                                if(portal.getFacingDirection().getOpposite().equals(storedDirection)){
                                    if(portal.getActive()) {
                                        Direction otherPortalFacing = Direction.fromVector(new BlockPos(CalledValues.getOtherFacing(portal).x, CalledValues.getOtherFacing(portal).y, CalledValues.getOtherFacing(portal).z));
                                        Direction otherPortalVertFacing = Direction.fromVector(new BlockPos(CalledValues.getOtherAxisH(portal).x, CalledValues.getOtherAxisH(portal).y, CalledValues.getOtherAxisH(portal).z));
                                        int offset = (int)(((portal.getBlockPos().getX()-translatedPos.getX()) * Math.abs(CalledValues.getAxisH(portal).x)) + ((portal.getBlockPos().getY()-translatedPos.getY()) * Math.abs(CalledValues.getAxisH(portal).y)) + ((portal.getBlockPos().getZ()-translatedPos.getZ()) * Math.abs(CalledValues.getAxisH(portal).z)));
                                        Direction mainPortalVertFacing = Direction.fromVector(new BlockPos(CalledValues.getAxisH(portal).x, CalledValues.getAxisH(portal).y, CalledValues.getAxisH(portal).z));
                                        assert mainPortalVertFacing != null;
                                        if(mainPortalVertFacing.equals(Direction.SOUTH)){
                                            offset = (Math.abs(offset)-1)*-1;
                                        }
                                        if(mainPortalVertFacing.equals(Direction.EAST)){
                                            offset = (Math.abs(offset)-1)*-1;
                                        }

                                        translatedPos = new BlockPos(CalledValues.getDestination(portal).x,CalledValues.getDestination(portal).y,CalledValues.getDestination(portal).z).offset(otherPortalVertFacing,offset);
                                        savedPos = translatedPos;
                                        assert otherPortalVertFacing != null;
                                        if(otherPortalVertFacing.equals(Direction.SOUTH)){
                                            translatedPos = translatedPos.offset(Direction.NORTH,1);
                                        }
                                        if(otherPortalVertFacing.equals(Direction.EAST)){
                                            translatedPos = translatedPos.offset(Direction.WEST,1);
                                        }

                                        assert otherPortalFacing != null;
                                        if(otherPortalFacing.equals(Direction.UP) || otherPortalFacing.equals(Direction.DOWN)){
                                            vertDirection = otherPortalVertFacing;
                                        }

                                        storedDirection = Direction.fromVector((int)CalledValues.getOtherFacing(portal).x,(int)CalledValues.getOtherFacing(portal).y,(int)CalledValues.getOtherFacing(portal).z);
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
                if (world.getBlockState(pos).get(Properties.POWERED)) {
                    blockEntity.togglePowered(world.getBlockState(pos));
                }
            }

        }


    }

    @Override
    public void playSound(SoundEvent soundEvent) {
        assert this.world != null;
        this.world.playSound(null, this.pos, soundEvent, SoundCategory.BLOCKS, 0.1F, 3.0F);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        List<Integer> posXList = new ArrayList<>();
        List<Integer> posYList = new ArrayList<>();
        List<Integer> posZList = new ArrayList<>();

        for(BlockPos pos : bridges){
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

        for(BlockPos pos : portalBridges){
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
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        List<Integer> posXList;
        List<Integer> posYList;
        List<Integer> posZList;

        posXList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("xList")));
        posYList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("yList")));
        posZList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("zList")));

        int size = tag.getInt("size");

        if(!bridges.isEmpty())
            bridges.clear();

        for (int i = 0; i < size; i++) {
            bridges.add(new BlockPos.Mutable(posXList.get(i), posYList.get(i), posZList.get(i)));
        }

        List<Integer> portalXList;
        List<Integer> portalYList;
        List<Integer> portalZList;

        portalXList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("portalxList")));
        portalYList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("portalyList")));
        portalZList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("portalzList")));

        int pSize = tag.getInt("pSize");

        if(!portalBridges.isEmpty())
            portalBridges.clear();

        for (int i = 0; i < pSize; i++) {
            portalBridges.add(new BlockPos.Mutable(portalXList.get(i), portalYList.get(i), portalZList.get(i)));
        }
    }


}