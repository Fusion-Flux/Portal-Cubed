package com.fusionflux.portalcubed.blocks;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.Gravity;
import com.fusionflux.portalcubed.client.AdhesionGravityVerifier;
import com.fusionflux.portalcubed.entity.EntityAttachments;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;

public class AdhesionGel extends BaseGel {
    public AdhesionGel(Settings settings) {
        super(settings);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        Box block = new Box(pos);
        Box player = getPlayerBox(entity.getBoundingBox(), GravityChangerAPI.getGravityDirection(entity), -(entity.getHeight() - 1));
        if (block.intersects(player)) {
            this.addCollisionEffects(world, entity, pos, state);
        }
    }

    private void addCollisionEffects(World world, Entity entity, BlockPos pos, BlockState state) {
        PacketByteBuf info = AdhesionGravityVerifier.packInfo(pos);
        if ((entity.isOnGround() && entity.horizontalCollision) || (!entity.isOnGround() && entity.horizontalCollision) || (!entity.isOnGround() && !entity.horizontalCollision)) {
            if (((EntityAttachments) entity).getGelTimer() == 0) {
                Direction current = GravityChangerAPI.getGravityDirection(entity);

                double delta = -.9;
                ArrayList<Gravity> gravList = GravityChangerAPI.getGravityList(entity);
                for (Gravity grav : gravList) {
                    if (grav.source().equals("portalcubed:adhesion_gel")) {
                        delta = -.1;
                        break;
                    }
                }
                for (Direction direc : getOpenFaces(state)) {
                    if (direc != current) {
                        Box gravbox = getGravityEffectBox(pos, direc, delta);
                        if (gravbox.intersects(entity.getBoundingBox())) {
                            if (world.isClient && entity instanceof PlayerEntity) {
                                GravityChangerAPI.addGravityClient((ClientPlayerEntity) entity, AdhesionGravityVerifier.newFieldGravity(direc), AdhesionGravityVerifier.FIELD_GRAVITY_SOURCE, info);
                                ((EntityAttachments) entity).setGelTimer(10);
                                break;
                            } else {
                                if (!(entity instanceof PlayerEntity) && !world.isClient) {
                                    GravityChangerAPI.addGravity(entity, new Gravity(direc, 10, 2, "adhesion_gel"));
                                    ((EntityAttachments) entity).setGelTimer(10);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (world.isClient && entity instanceof PlayerEntity) {
            GravityChangerAPI.addGravityClient((ClientPlayerEntity) entity, AdhesionGravityVerifier.newFieldGravity(GravityChangerAPI.getGravityDirection(entity)), AdhesionGravityVerifier.FIELD_GRAVITY_SOURCE, info);
        } else {
            if (!(entity instanceof PlayerEntity) && !world.isClient)
                GravityChangerAPI.addGravity(entity, new Gravity(GravityChangerAPI.getGravityDirection(entity), 10, 2, "adhesion_gel"));
        }
    }

    public Box getGravityEffectBox(BlockPos blockPos, Direction direction, double delta) {
        double minX = blockPos.getX();
        double minY = blockPos.getY();
        double minZ = blockPos.getZ();
        double maxX = blockPos.getX() + 1;
        double maxY = blockPos.getY() + 1;
        double maxZ = blockPos.getZ() + 1;
        switch (direction) {
            case DOWN -> maxY += delta;
            case UP -> minY -= delta;
            case NORTH -> maxZ += delta;
            case SOUTH -> minZ -= delta;
            case WEST -> maxX += delta;
            case EAST -> minX -= delta;
        }
        return new Box(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public Box getPlayerBox(Box playerBox, Direction direction, double delta) {
        double minX = playerBox.minX;
        double minY = playerBox.minY;
        double minZ = playerBox.minZ;
        double maxX = playerBox.maxX;
        double maxY = playerBox.maxY;
        double maxZ = playerBox.maxZ;
        switch (direction) {
            case DOWN -> maxY += delta;
            case UP -> minY -= delta;
            case NORTH -> maxZ += delta;
            case SOUTH -> minZ -= delta;
            case WEST -> maxX += delta;
            case EAST -> minX -= delta;
        }
        return new Box(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
