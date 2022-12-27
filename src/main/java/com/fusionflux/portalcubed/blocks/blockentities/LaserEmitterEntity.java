package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.fusionflux.portalcubed.entity.RedirectionCubeEntity;
import com.fusionflux.portalcubed.entity.StorageCubeEntity;
import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TintedGlassBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
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
public class LaserEmitterEntity extends BlockEntity {

    public final int MAX_RANGE = PortalCubedConfig.maxBridgeLength;

    public List<BlockPos> funnels;
    public List<BlockPos> portalFunnels;


    public LaserEmitterEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.LASER_EMITTER_ENTITY,pos,state);
        this.funnels = new ArrayList<>();
        this.portalFunnels = new ArrayList<>();
    }

    public static void tick1(World world, BlockPos pos, BlockState state, LaserEmitterEntity blockEntity) {
        if (!world.isClient) {
            boolean redstonePowered = world.isReceivingRedstonePower(blockEntity.getPos());

            if (redstonePowered) {

                if (!world.getBlockState(pos).get(Properties.POWERED)) {
                    blockEntity.togglePowered(world.getBlockState(pos));
                }

                BlockPos translatedPos = pos;
                BlockPos savedPos = pos;
                if (blockEntity.funnels != null) {
                    List<BlockPos> modfunnels = new ArrayList<>();
                    List<BlockPos> portalfunnels = new ArrayList<>();
                    boolean teleported = false;
                    Direction storedDirection = blockEntity.getCachedState().get(Properties.FACING);
                    for (int i = 0; i <= blockEntity.MAX_RANGE; i++) {
                        if(!teleported) {
                            translatedPos = translatedPos.offset(storedDirection);
                        } else{
                            teleported = false;
                        }
                        if (translatedPos.getY() < world.getTopY() && translatedPos.getY() > world.getBottomY() && (world.isAir(translatedPos) || (world.getBlockState(translatedPos).getHardness(world, translatedPos) <= 0.1F && world.getBlockState(translatedPos).getHardness(world, translatedPos) != -1F) || world.getBlockState(translatedPos).getBlock().equals(PortalCubedBlocks.LASER)) && !world.getBlockState(translatedPos).getBlock().equals(Blocks.BARRIER)) {
                            world.setBlockState(translatedPos, PortalCubedBlocks.LASER.getDefaultState());

                            LaserBlockEntity funnel = ((LaserBlockEntity) Objects.requireNonNull(world.getBlockEntity(translatedPos)));

                            modfunnels.add(funnel.getPos());
                            blockEntity.funnels.add(funnel.getPos());
                            if(!savedPos.equals(pos)){
                                portalfunnels.add(funnel.getPos());
                                blockEntity.portalFunnels.add(funnel.getPos());
                            }

                            if(!funnel.facing.contains(storedDirection)){
                                funnel.facing.add(storedDirection);
                                funnel.emitters.add(funnel.facing.indexOf(storedDirection),pos);
                                funnel.portalEmitters.add(funnel.facing.indexOf(storedDirection),savedPos);
                            }

                            ///if(!funnel.emitters.contains(pos) ) {
                            ///    funnel.emitters.add(pos);
                            ///    funnel.facing.add(funnel.emitters.indexOf(pos),storedDirection);
                            ///}else {
                            ///    if (!funnel.facing.get(funnel.emitters.indexOf(pos)).equals(storedDirection)) {
                            ///        funnel.facing.set(funnel.emitters.indexOf(pos), storedDirection);
                            ///    }
                            ///}

                            funnel.updateState(world.getBlockState(translatedPos),world,translatedPos,funnel);

                            Box portalCheckBox = new Box(translatedPos).contract(.25);

                            List<RedirectionCubeEntity> reflectCubes = world.getNonSpectatingEntities(RedirectionCubeEntity.class, portalCheckBox);
                            boolean iscube= false;
                            for (RedirectionCubeEntity cubes : reflectCubes) {
                                if(cubes != null){
                                    savedPos = translatedPos;
                                    storedDirection = Direction.fromRotation((double)cubes.getRotYaw());
                                    iscube=true;
                                    cubes.setButtonTimer(1);
                                    break;
                                }
                            }

                            List<CorePhysicsEntity> blockCube = world.getNonSpectatingEntities(CorePhysicsEntity.class, portalCheckBox);
                            boolean blocked= false;
                            for (CorePhysicsEntity cubes : blockCube) {
                                if(!(cubes instanceof RedirectionCubeEntity)){
                                    blocked=true;
                                    break;
                                }
                            }

                            if(blocked){
                                blockEntity.funnels = modfunnels;
                                blockEntity.portalFunnels=portalfunnels;
                                break;
                            }
                            portalCheckBox = new Box(translatedPos);
                            List<ExperimentalPortal> list = world.getNonSpectatingEntities(ExperimentalPortal.class, portalCheckBox);

                            if(!iscube)
                            for (ExperimentalPortal portal : list) {
                                if(portal.getFacingDirection().getOpposite().equals(storedDirection)){
                                    if(portal.getActive()) {
                                        Direction otherPortalVertFacing = Direction.fromVector(new BlockPos(CalledValues.getOtherAxisH(portal).x, CalledValues.getOtherAxisH(portal).y, CalledValues.getOtherAxisH(portal).z));
                                        int offset = (int)(((portal.getBlockPos().getX()-translatedPos.getX()) * Math.abs(CalledValues.getAxisH(portal).x)) + ((portal.getBlockPos().getY()-translatedPos.getY()) * Math.abs(CalledValues.getAxisH(portal).y)) + ((portal.getBlockPos().getZ()-translatedPos.getZ()) * Math.abs(CalledValues.getAxisH(portal).z)));
                                        Direction mainPortalVertFacing = Direction.fromVector(new BlockPos(CalledValues.getAxisH(portal).x, CalledValues.getAxisH(portal).y, CalledValues.getAxisH(portal).z));
                                        if(mainPortalVertFacing.equals(Direction.SOUTH)){
                                            offset = (Math.abs(offset)-1)*-1;
                                        }
                                        if(mainPortalVertFacing.equals(Direction.EAST)){
                                            offset = (Math.abs(offset)-1)*-1;
                                        }

                                        translatedPos = new BlockPos(CalledValues.getDestination(portal).x,CalledValues.getDestination(portal).y,CalledValues.getDestination(portal).z).offset(otherPortalVertFacing,offset);
                                        savedPos = translatedPos;
                                        if(otherPortalVertFacing.equals(Direction.SOUTH)){
                                            translatedPos = translatedPos.offset(Direction.NORTH,1);
                                        }
                                        if(otherPortalVertFacing.equals(Direction.EAST)){
                                            translatedPos = translatedPos.offset(Direction.WEST,1);
                                        }

                                        storedDirection = Direction.fromVector((int)CalledValues.getOtherFacing(portal).x,(int)CalledValues.getOtherFacing(portal).y,(int)CalledValues.getOtherFacing(portal).z);
                                        teleported = true;
                                        blockEntity.funnels = modfunnels;
                                        blockEntity.portalFunnels=portalfunnels;
                                    }
                                }
                            }
                        } else if(world.getBlockState(translatedPos).getBlock() instanceof AbstractGlassBlock && !(world.getBlockState(translatedPos).getBlock() instanceof TintedGlassBlock)) {
                            blockEntity.funnels = modfunnels;
                            blockEntity.portalFunnels=portalfunnels;
                        }else{
                            blockEntity.funnels = modfunnels;
                            blockEntity.portalFunnels=portalfunnels;
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

    public void playSound(SoundEvent soundEvent) {
        this.world.playSound(null, this.pos, soundEvent, SoundCategory.BLOCKS, 0.1F, 3.0F);
    }


    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);

        List<Integer> posXList = new ArrayList<>();
        List<Integer> posYList = new ArrayList<>();
        List<Integer> posZList = new ArrayList<>();

        for(BlockPos pos : funnels){
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

        for(BlockPos pos : portalFunnels){
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

        if(!funnels.isEmpty())
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

        if(!portalFunnels.isEmpty())
            portalFunnels.clear();

        for (int i = 0; i < pSize; i++) {
            portalFunnels.add(new BlockPos.Mutable(portalXList.get(i), portalYList.get(i), portalZList.get(i)));
        }
    }

    public void togglePowered(BlockState state) {
        assert world != null;
        world.setBlockState(pos, state.cycle(Properties.POWERED));
        if (world.getBlockState(pos).get(Properties.POWERED)) {
            this.playSound(SoundEvents.BLOCK_BEACON_ACTIVATE);
        }
        if (!world.getBlockState(pos).get(Properties.POWERED)) {
            this.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE);
        }
    }
}