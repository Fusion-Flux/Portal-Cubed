package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.accessor.CalledValues;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.fusionflux.portalcubed.entity.CorePhysicsEntity;
import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import com.fusionflux.portalcubed.entity.RedirectionCubeEntity;
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
public class GrillEmitterEntity extends BlockEntity {

    public final int MAX_RANGE = PortalCubedConfig.maxBridgeLength;

    public List<BlockPos> funnels;
    public List<BlockPos> portalFunnels;


    public GrillEmitterEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.GRILL_EMITTER_ENTITY,pos,state);
        this.funnels = new ArrayList<>();
        this.portalFunnels = new ArrayList<>();
    }

    public static void tick1(World world, BlockPos pos, BlockState state, GrillEmitterEntity blockEntity) {
        if (!world.isClient) {
            boolean redstonePowered = world.isReceivingRedstonePower(blockEntity.getPos());

            if (redstonePowered) {

                if (!world.getBlockState(pos).get(Properties.POWERED)) {
                    blockEntity.togglePowered(world.getBlockState(pos));
                }

                BlockPos translatedPos = pos;
                while(world.getBlockState(translatedPos.offset(Direction.UP)).getBlock() == PortalCubedBlocks.GRILL_EMITTER){
                    translatedPos = translatedPos.offset(Direction.UP);
                }
                if (blockEntity.funnels != null) {
                    List<BlockPos> modfunnels = new ArrayList<>();
                    Direction storedDirection = blockEntity.getCachedState().get(Properties.FACING);
                    BlockPos verticalAdjustment = translatedPos;
                    while (world.getBlockState(translatedPos).getBlock() == PortalCubedBlocks.GRILL_EMITTER){
                        BlockPos checker = translatedPos;
                    boolean isValid = false;
                    for (int i = 0; i <= blockEntity.MAX_RANGE; i++) {
                        checker = checker.offset(storedDirection);
                        if (world.getBlockState(checker).getBlock() == PortalCubedBlocks.GRILL_EMITTER) {
                            isValid = true;
                            break;
                        }
                    }
                    if (isValid)
                        for (int i = 0; i <= blockEntity.MAX_RANGE; i++) {
                            translatedPos = translatedPos.offset(storedDirection);
                            if (translatedPos.getY() < world.getTopY() && translatedPos.getY() > world.getBottomY() && (world.isAir(translatedPos) || (world.getBlockState(translatedPos).getHardness(world, translatedPos) <= 0.1F && world.getBlockState(translatedPos).getHardness(world, translatedPos) != -1F) || world.getBlockState(translatedPos).getBlock().equals(PortalCubedBlocks.GRILL)) && !world.getBlockState(translatedPos).getBlock().equals(Blocks.BARRIER)) {
                                world.setBlockState(translatedPos, PortalCubedBlocks.GRILL.getDefaultState());

                                GrillBlockEntity funnel = ((GrillBlockEntity) Objects.requireNonNull(world.getBlockEntity(translatedPos)));

                                modfunnels.add(funnel.getPos());
                                blockEntity.funnels.add(funnel.getPos());

                                if (!funnel.facing.contains(storedDirection)) {
                                    funnel.facing.add(storedDirection);
                                    funnel.emitters.add(funnel.facing.indexOf(storedDirection), pos);
                                }

                                funnel.updateState(world.getBlockState(translatedPos), world, translatedPos, funnel);

                            } else if (world.getBlockState(translatedPos).getBlock() instanceof AbstractGlassBlock && !(world.getBlockState(translatedPos).getBlock() instanceof TintedGlassBlock)) {
                                blockEntity.funnels = modfunnels;
                            } else {
                                blockEntity.funnels = modfunnels;
                                break;
                            }
                        }
                        translatedPos = verticalAdjustment.offset(Direction.DOWN);
                        verticalAdjustment = verticalAdjustment.offset(Direction.DOWN);
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