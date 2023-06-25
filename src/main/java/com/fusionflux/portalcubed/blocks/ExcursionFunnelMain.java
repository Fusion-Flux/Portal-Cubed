package com.fusionflux.portalcubed.blocks;

import com.fusionflux.gravity_api.api.GravityChangerAPI;
import com.fusionflux.gravity_api.util.RotationUtil;
import com.fusionflux.portalcubed.accessor.EntityExt;
import com.fusionflux.portalcubed.accessor.HasMovementInputAccessor;
import com.fusionflux.portalcubed.blocks.blockentities.ExcursionFunnelMainBlockEntity;
import com.fusionflux.portalcubed.blocks.properties.PortalCubedProperties;
import com.fusionflux.portalcubed.compat.rayon.RayonIntegration;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;


public class ExcursionFunnelMain extends BaseEntityBlock {

    public static final BooleanProperty NORTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    public static final BooleanProperty UP;
    public static final BooleanProperty DOWN;
    public static final BooleanProperty R_NORTH;
    public static final BooleanProperty R_EAST;
    public static final BooleanProperty R_SOUTH;
    public static final BooleanProperty R_WEST;
    public static final BooleanProperty R_UP;
    public static final BooleanProperty R_DOWN;

    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);

    public ExcursionFunnelMain(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, false).setValue(R_NORTH, false).setValue(R_EAST, false).setValue(R_SOUTH, false).setValue(R_WEST, false).setValue(R_UP, false).setValue(R_DOWN, false));
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }


    static {
        NORTH = BlockStateProperties.NORTH;
        EAST = BlockStateProperties.EAST;
        SOUTH = BlockStateProperties.SOUTH;
        WEST = BlockStateProperties.WEST;
        UP = BlockStateProperties.UP;
        DOWN = BlockStateProperties.DOWN;
        R_NORTH = PortalCubedProperties.RNORTH;
        R_EAST = PortalCubedProperties.REAST;
        R_SOUTH = PortalCubedProperties.RSOUTH;
        R_WEST = PortalCubedProperties.RWEST;
        R_UP = PortalCubedProperties.RUP;
        R_DOWN = PortalCubedProperties.RDOWN;
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH, UP, DOWN, R_NORTH, R_EAST, R_WEST, R_SOUTH, R_UP, R_DOWN);
    }


    @Override
    @ClientOnly
    @SuppressWarnings("deprecation")
    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return true;
    }


    @NotNull
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }



    @Override
    @SuppressWarnings("deprecation")
    public boolean skipRendering(BlockState state, BlockState stateFrom, Direction direction) {
        return stateFrom.is(this) || super.skipRendering(state, stateFrom, direction);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        this.addCollisionEffects(world, entity, pos, state);
    }

    public static Vec3 getPushDirection(BlockState state) {
        Vec3 result = Vec3.ZERO;
        boolean modifyX = false;
        boolean modifyY = false;
        boolean modifyZ = false;


        if (state.getValue(BlockStateProperties.NORTH)) {
            result = result.subtract(0, 0, 1);
        }
        if (state.getValue(BlockStateProperties.SOUTH)) {
            result = result.add(0, 0, 1);
        }
        if (state.getValue(BlockStateProperties.EAST)) {
            result = result.add(1, 0, 0);
        }
        if (state.getValue(BlockStateProperties.WEST)) {
            result = result.subtract(1, 0, 0);
        }
        if (state.getValue(BlockStateProperties.UP)) {
            result = result.add(0, 1, 0);
        }
        if (state.getValue(BlockStateProperties.DOWN)) {
            result = result.subtract(0, 1, 0);
        }

        if (state.getValue(BlockStateProperties.NORTH) && state.getValue(BlockStateProperties.SOUTH)) {
            modifyZ = true;
        }
        if (state.getValue(BlockStateProperties.EAST) && state.getValue(BlockStateProperties.WEST)) {
            modifyX = true;
        }
        if (state.getValue(BlockStateProperties.UP) && state.getValue(BlockStateProperties.DOWN)) {
            modifyY = true;
        }

        if (state.getValue(PortalCubedProperties.RSOUTH)) {
            result = result.subtract(0, 0, 1);
        }
        if (state.getValue(PortalCubedProperties.RNORTH)) {
            result = result.add(0, 0, 1);
        }
        if (state.getValue(PortalCubedProperties.RWEST)) {
            result = result.add(1, 0, 0);
        }
        if (state.getValue(PortalCubedProperties.REAST)) {
            result = result.subtract(1, 0, 0);
        }
        if (state.getValue(PortalCubedProperties.RUP)) {
            result = result.subtract(0, 1, 0);
        }
        if (state.getValue(PortalCubedProperties.RDOWN)) {
            result = result.add(0, 1, 0);
        }

        if (state.getValue(PortalCubedProperties.RNORTH) && state.getValue(PortalCubedProperties.RSOUTH)) {
            modifyZ = true;
        }
        if (state.getValue(PortalCubedProperties.REAST) && state.getValue(PortalCubedProperties.RWEST)) {
            modifyX = true;
        }
        if (state.getValue(PortalCubedProperties.RUP) && state.getValue(PortalCubedProperties.RDOWN)) {
            modifyY = true;
        }

        if (modifyX) {
            result = new Vec3(.00001, result.y(), result.z());
        }
        if (modifyY) {
            result = new Vec3(result.x(), .00001, result.z());
        }
        if (modifyZ) {
            result = new Vec3(result.x(), result.y(), .00001);
        }


        return result;
    }


    private void addCollisionEffects(Level world, Entity entity, BlockPos pos, BlockState state) {
        if (entity instanceof Player player) {
            if (world.isClientSide()) {
                Vec3 entityCenter = entity.getBoundingBox().getCenter();

                Vec3 direction = getPushDirection(state);

                direction = direction.scale(.125);


                direction = RotationUtil.vecWorldToPlayer(direction, GravityChangerAPI.getGravityDirection(entity));

                RayonIntegration.INSTANCE.setNoGravity(entity, true);

                if (!((EntityExt) entity).isInFunnel()) {
                    ((EntityExt) entity).setInFunnel(true);
                    entity.setDeltaMovement(0, 0, 0);
                    playEnterSound();
                }
                double xOffset = (entityCenter.x() - pos.getX() - .5) + entity.getDeltaMovement().x;
                double yOffset = (entityCenter.y() - pos.getY() - .5) + entity.getDeltaMovement().y;
                double zOffset = (entityCenter.z() - pos.getZ() - .5) + entity.getDeltaMovement().z;
                if (xOffset == 0) {
                    xOffset = .0001;
                }
                if (yOffset == 0) {
                    yOffset = .0001;
                }
                if (zOffset == 0) {
                    zOffset = .0001;
                }
                ((EntityExt) entity).setFunnelTimer(2);

                Vec3 gotVelocity = Vec3.ZERO;


                if (player.isLocalPlayer()) {
                    LocalPlayer alpha = (LocalPlayer) player;
                    if (direction.x != 0) {
                        gotVelocity = new Vec3(direction.x(), gotVelocity.y, gotVelocity.z);
                    } else {
                        if (!((HasMovementInputAccessor) alpha).hasMovementInputPublic()) {
                            gotVelocity = gotVelocity.add(RotationUtil.vecWorldToPlayer(new Vec3(-((xOffset / Math.abs(xOffset)) * Math.sqrt(Math.abs(xOffset))) / 20, 0, 0), GravityChangerAPI.getGravityDirection(entity)));
                        } else {
                            gotVelocity = gotVelocity.add(player.getDeltaMovement().x, 0, 0);
                        }
                    }
                    if (direction.y != 0) {
                        gotVelocity = new Vec3(gotVelocity.x, direction.y(), gotVelocity.z);
                    } else {
                        if (!((HasMovementInputAccessor) alpha).hasMovementInputPublic()) {
                            gotVelocity = gotVelocity.add(RotationUtil.vecWorldToPlayer(new Vec3(0, -((yOffset / Math.abs(yOffset)) * Math.sqrt(Math.abs(yOffset))) / 20, 0), GravityChangerAPI.getGravityDirection(entity)));
                        } else {
                            gotVelocity = gotVelocity.add(0, player.getDeltaMovement().y, 0);
                        }
                    }
                    if (direction.z != 0) {
                        gotVelocity = new Vec3(gotVelocity.x, gotVelocity.y, direction.z());
                    } else {
                        if (!((HasMovementInputAccessor) alpha).hasMovementInputPublic()) {
                            gotVelocity = gotVelocity.add(RotationUtil.vecWorldToPlayer(new Vec3(0, 0, -((zOffset / Math.abs(zOffset)) * Math.sqrt(Math.abs(zOffset))) / 20), GravityChangerAPI.getGravityDirection(entity)));
                        } else {
                            gotVelocity = gotVelocity.add(0, 0, player.getDeltaMovement().z);
                        }
                    }
                    entity.setDeltaMovement(gotVelocity);

                    if (entity.isShiftKeyDown() && gotVelocity.lengthSqr() < 0.15 * 0.15 && !entity.isFree(gotVelocity.x, gotVelocity.y, gotVelocity.z)) {
                        ((EntityExt) entity).setCFG();
                    }

                }
            }
        } else {
            if (!world.isClientSide()) {
                Vec3 entityCenter = entity.getBoundingBox().getCenter();

                Vec3 direction = getPushDirection(state);
                direction = direction.scale(.125);

                RayonIntegration.INSTANCE.setNoGravity(entity, true);

                if (!((EntityExt) entity).isInFunnel()) {
                    ((EntityExt) entity).setInFunnel(true);
                    entity.setDeltaMovement(0, 0, 0); //not this code
                }

                double xOffset = (entityCenter.x() - pos.getX() - .5) + entity.getDeltaMovement().x;
                double yOffset = (entityCenter.y() - pos.getY() - .5) + entity.getDeltaMovement().y;
                double zOffset = (entityCenter.z() - pos.getZ() - .5) + entity.getDeltaMovement().z;

                if (xOffset == 0) {
                    xOffset = .0001;
                }
                if (yOffset == 0) {
                    yOffset = .0001;
                }
                if (zOffset == 0) {
                    zOffset = .0001;
                }
                entity.fallDistance = 0;

                ((EntityExt) entity).setFunnelTimer(2);

                Vec3 gotVelocity = Vec3.ZERO;

                if (direction.x != 0) {
                    gotVelocity = new Vec3(direction.x(), gotVelocity.y, gotVelocity.z);
                } else {
                    gotVelocity = gotVelocity.add(RotationUtil.vecWorldToPlayer(new Vec3(-((xOffset / Math.abs(xOffset)) * Math.sqrt(Math.abs(xOffset))) / 20, 0, 0), GravityChangerAPI.getGravityDirection(entity)));
                }
                if (direction.y != 0) {
                    gotVelocity = new Vec3(gotVelocity.x, direction.y(), gotVelocity.z);
                } else {
                    gotVelocity = gotVelocity.add(RotationUtil.vecWorldToPlayer(new Vec3(0, -((yOffset / Math.abs(yOffset)) * Math.sqrt(Math.abs(yOffset))) / 20, 0), GravityChangerAPI.getGravityDirection(entity)));
                }
                if (direction.z != 0) {
                    gotVelocity = new Vec3(gotVelocity.x, gotVelocity.y, direction.z());
                } else {
                    gotVelocity = gotVelocity.add(RotationUtil.vecWorldToPlayer(new Vec3(0, 0, -((zOffset / Math.abs(zOffset)) * Math.sqrt(Math.abs(zOffset))) / 20), GravityChangerAPI.getGravityDirection(entity)));
                }
                if (!gotVelocity.equals(Vec3.ZERO))
                    entity.setDeltaMovement(gotVelocity);
            }
        }
    }

    @ClientOnly
    private static void playEnterSound() {
        Minecraft.getInstance().getSoundManager().play(
            new AbstractTickableSoundInstance(PortalCubedSounds.TBEAM_ENTER_EVENT, SoundSource.BLOCKS, SoundInstance.createUnseededRandom()) {
                int ticks;

                {
                    attenuation = Attenuation.NONE;
                }

                @Override
                public void tick() {
                    ticks++;
                    if (ticks > 80) {
                        volume = 1f - 0.05f * (ticks - 80);
                        if (volume <= 0) {
                            stop();
                        }
                    }
                }
            }
        );
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ExcursionFunnelMainBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, PortalCubedBlocks.EXCURSION_FUNNEL_ENTITY, ExcursionFunnelMainBlockEntity::tick);
    }

}
