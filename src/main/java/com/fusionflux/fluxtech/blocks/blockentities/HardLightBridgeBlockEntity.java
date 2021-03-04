package com.fusionflux.fluxtech.blocks.blockentities;

import com.fusionflux.fluxtech.blocks.FluxTechBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HardLightBridgeBlockEntity extends BlockEntity implements Tickable {

    public List<BlockPos.Mutable> emitters = new ArrayList<>();

    public HardLightBridgeBlockEntity() {
        super( FluxTechBlocks.HLB_BLOCK_ENTITY );
    }

    @Override
    public void tick() {
        assert world != null;
        if( !world.isClient ) {
            // Iterate over supporting emitters to guarantee support
            ArrayList<BlockPos.Mutable> emittersToRemove = new ArrayList<>();
            BlockPos.Mutable emitter;

            for ( BlockPos.Mutable mutable : emitters ) {
                emitter = mutable;
                if ( !( world.getBlockEntity( emitter ) instanceof HardLightBridgeEmitterBlockEntity && world.isReceivingRedstonePower( mutable ) ) ) {
                    emittersToRemove.add( mutable );
                }
            }

// Clean up emitters
            for ( BlockPos.Mutable pos : emittersToRemove ) {
                emitters.remove( pos );
            }

            // If no remaining emitters or no power, replace with AIR
            if( emitters.stream().noneMatch( (emit) -> world.isReceivingRedstonePower( emit ))) {
                world.setBlockState( pos, Blocks.AIR.getDefaultState(), 3 );
            }else if( !world.getBlockState( pos ).get( Properties.FACING )
                    .equals( world.getBlockState( emitters.get( emitters.size() - 1 )).get( Properties.FACING ))) {
                world.setBlockState(
                        pos,
                        FluxTechBlocks.HLB_BLOCK.getDefaultState().with( Properties.FACING,
                                world.getBlockState( emitters.get( emitters.size() - 1 )).get( Properties.FACING )),
                        3); // here, and with the comma in the line above
            }
        }
    }

    @Override
    public CompoundTag toTag( CompoundTag tag ) {
        super.toTag( tag );

        // Because CompoundTags only support a few types, we have to decompose the emitter BlockPos' into ints
        tag.putInt( "size", emitters.size() );
        for( int i = 0; i < emitters.size(); i++ ) {
            tag.putInt( i+"x", emitters.get( i ).getX() );
            tag.putInt( i+"y", emitters.get( i ).getY() );
            tag.putInt( i+"z", emitters.get( i ).getZ() );
        }
        return tag;
    }

    @Override
    public void fromTag( BlockState state, CompoundTag tag ) {
        super.fromTag( state, tag );

        // Because CompoundTags only support a few types, we have to recompose the emitter BlockPos' from ints
        int size = tag.getInt( "size" );
        for( int i = 0; i < size; i++ ) {
            emitters.add( new BlockPos.Mutable(
                    tag.getInt( i+"x" ),
                    tag.getInt( i+"y" ),
                    tag.getInt( i+"z" )
            ));
        }
    }

    @Override
    public void markRemoved() {
        assert world != null;
        if( !world.isClient ) {
            if( !emitters.isEmpty() ) {
                // Repair callbacks
                emitters.forEach( (emitter) -> {
                    ((HardLightBridgeEmitterBlockEntity)Objects.requireNonNull( world.getBlockEntity( emitter ))).repairUpdate( pos );
                } );
            }
        }
        super.markRemoved();
    }
}