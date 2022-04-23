package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.blocks.GelFlat;
import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import qouteall.imm_ptl.core.api.PortalAPI;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.PortalAnimation;
import qouteall.imm_ptl.core.portal.PortalManipulation;
import qouteall.imm_ptl.core.teleportation.CollisionHelper;
import qouteall.q_misc_util.Helper;
import qouteall.q_misc_util.my_util.SignalArged;
import qouteall.q_misc_util.my_util.SignalBiArged;

import java.util.List;
import java.util.UUID;

public class CustomPortalEntity extends Portal {
    public static final SignalArged<CustomPortalEntity> clientPortalTickSignal;
    public static final SignalArged<CustomPortalEntity> serverPortalTickSignal;
    public static final SignalArged<CustomPortalEntity> portalCacheUpdateSignal;
    public static final SignalArged<CustomPortalEntity> portalDisposeSignal;
    public static final SignalBiArged<CustomPortalEntity, NbtCompound> readPortalDataSignal;
    public static final SignalBiArged<CustomPortalEntity, NbtCompound> writePortalDataSignal;
    public static final TrackedData<String> STOREDSTRING = DataTracker.registerData(CustomPortalEntity.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Boolean> ISACTIVE = DataTracker.registerData(CustomPortalEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<String> STOREDOUTLINE = DataTracker.registerData(CustomPortalEntity.class, TrackedDataHandlerRegistry.STRING);

    static {
        clientPortalTickSignal = new SignalArged();
        serverPortalTickSignal = new SignalArged();
        portalCacheUpdateSignal = new SignalArged();
        portalDisposeSignal = new SignalArged();
        readPortalDataSignal = new SignalBiArged();
        writePortalDataSignal = new SignalBiArged();
    }

    public CustomPortalEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound compoundTag) {
        super.writeCustomDataToNbt(compoundTag);
        compoundTag.put("animation", new PortalAnimation(PortalAnimation.Curve.linear, 0, false).toNbt());
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(STOREDSTRING, "null");
        this.getDataTracker().startTracking(STOREDOUTLINE, "null");
        this.getDataTracker().startTracking(ISACTIVE, false);
    }

    public String getString() {
        return getDataTracker().get(STOREDSTRING);
    }

    public void setString(String string) {
        this.getDataTracker().set(STOREDSTRING, string);
    }

    public String getOutline() {
        return getDataTracker().get(STOREDOUTLINE);
    }

    public void setOutline(String outline) {
        this.getDataTracker().set(STOREDOUTLINE, outline);
    }

    public Boolean getActive() {
        return getDataTracker().get(ISACTIVE);
    }

    public void setActive(Boolean active) {
        this.getDataTracker().set(ISACTIVE, active);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.world.isClient) {
            clientPortalTickSignal.emit(this);
        } else {
            if (!this.isPortalValid()) {
                Helper.log("removed invalid portal" + this);
                this.kill();
                return;
            }

            serverPortalTickSignal.emit(this);
        }

        CollisionHelper.notifyCollidingPortals(this);
        if (!this.world.isClient) {

       /*     List<CustomPortalEntity> globalPortals = this.world.getEntitiesByClass(CustomPortalEntity.class, this.getBoundingBox(), null);

            for (CustomPortalEntity globalPortal : globalPortals) {

            }*/

            /*if ((!this.world.getBlockState(this.getBlockPos()).isAir()) || (!this.world.getBlockState(new BlockPos(
                    this.getPos().getX() - Math.abs(this.axisH.getX()),
                    this.getPos().getY() + this.axisH.getY(),
                    this.getPos().getZ() - Math.abs(this.axisH.getZ()))).isAir())) {

                if (!this.getOutline().equals("null")) {
                    PortalPlaceholderEntity portalOutline;
                    portalOutline = (PortalPlaceholderEntity) ((ServerWorld) world).getEntity(UUID.fromString(this.getOutline()));
                    assert portalOutline != null;
                    if (portalOutline != null) {
                        portalOutline.kill();
                    }
                }

                this.kill();
                world.playSound(null, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), PortalCubedSounds.ENTITY_PORTAL_CLOSE, SoundCategory.NEUTRAL, .1F, 1F);
                System.out.println("killed");
                if (!this.getString().equals("null")) {
                    CustomPortalEntity otherPortal;
                    otherPortal = (CustomPortalEntity) ((ServerWorld) world).getEntity(UUID.fromString(this.getString()));
                    assert otherPortal != null;
                    if (otherPortal != null) {
                        otherPortal.setDestination(otherPortal.getOriginPos());
                        PortalManipulation.adjustRotationToConnect(PortalAPI.createFlippedPortal(otherPortal), otherPortal);
                        otherPortal.setActive(false);
                        otherPortal.reloadAndSyncToClient();
                    }
                }
            }*/
            BlockPos topBehind = new BlockPos(
                    this.getPos().getX() - this.axisW.crossProduct(this.axisH).getX(),
                    this.getPos().getY() - this.axisW.crossProduct(this.axisH).getY(),
                    this.getPos().getZ() - this.axisW.crossProduct(this.axisH).getZ());
            BlockPos bottomBehind = new BlockPos(
                    this.getPos().getX() - this.axisW.crossProduct(this.axisH).getX() - Math.abs(this.axisH.getX()),
                    this.getPos().getY() - this.axisW.crossProduct(this.axisH).getY() + this.axisH.getY(),
                    this.getPos().getZ() - this.axisW.crossProduct(this.axisH).getZ() - Math.abs(this.axisH.getZ()));
            BlockPos bottom = new BlockPos(
                    this.getPos().getX() - Math.abs(this.axisH.getX()),
                    this.getPos().getY() + this.axisH.getY(),
                    this.getPos().getZ() - Math.abs(this.axisH.getZ()));


            Direction portalFacing = Direction.fromVector((int) this.getNormal().getX(), (int) this.getNormal().getY(), (int) this.getNormal().getZ());
            boolean topValidBlock=false;
            if(this.world.getBlockState(this.getBlockPos()).isIn(PortalCubedBlocks.GELCHECKTAG)&&this.world.getBlockState(topBehind).isIn(PortalCubedBlocks.CANT_PLACE_PORTAL_ON)){
                BooleanProperty booleanProperty = GelFlat.getFacingProperty(portalFacing.getOpposite());

                topValidBlock = this.world.getBlockState(this.getBlockPos()).get(booleanProperty);
            }else if (!this.world.getBlockState(topBehind).isIn(PortalCubedBlocks.CANT_PLACE_PORTAL_ON)){
                topValidBlock=true;
            }
            boolean bottomValidBlock=false;
            if(this.world.getBlockState(bottom).isIn(PortalCubedBlocks.GELCHECKTAG)&&this.world.getBlockState(bottomBehind).isIn(PortalCubedBlocks.CANT_PLACE_PORTAL_ON)){
                BooleanProperty booleanProperty = GelFlat.getFacingProperty(portalFacing.getOpposite());
                bottomValidBlock = this.world.getBlockState(bottom).get(booleanProperty);
            }else if (!this.world.getBlockState(bottomBehind).isIn(PortalCubedBlocks.CANT_PLACE_PORTAL_ON)){
                bottomValidBlock=true;
            }

            if ((!this.world.getBlockState(topBehind).isSideSolidFullSquare(world, topBehind, portalFacing)) ||
                    (!this.world.getBlockState(bottomBehind).isSideSolidFullSquare(world, bottomBehind, portalFacing) ||
                            !topValidBlock ||
                            !bottomValidBlock)||
                    ((!this.world.getBlockState(this.getBlockPos()).isAir())&& !this.world.getBlockState(this.getBlockPos()).isIn(PortalCubedBlocks.ALLOW_PORTAL_IN) )|| (!this.world.getBlockState(bottom).isAir() && !this.world.getBlockState(bottom).isIn(PortalCubedBlocks.ALLOW_PORTAL_IN))) {
                if (!this.getOutline().equals("null")) {
                    PortalPlaceholderEntity portalOutline;
                    portalOutline = (PortalPlaceholderEntity) ((ServerWorld) world).getEntity(UUID.fromString(this.getOutline()));
                    assert portalOutline != null;
                    if (portalOutline != null) {
                        portalOutline.kill();
                    }
                }
                this.kill();
                world.playSound(null, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), PortalCubedSounds.ENTITY_PORTAL_CLOSE, SoundCategory.NEUTRAL, .1F, 1F);
                //System.out.println("killed");
                //System.out.println(this.world.getBlockState(this.getBlockPos()));
                //System.out.println(portalFacing);
                if (!this.getString().equals("null")) {
                    CustomPortalEntity otherPortal;
                    otherPortal = (CustomPortalEntity) ((ServerWorld) world).getEntity(UUID.fromString(this.getString()));
                    assert otherPortal != null;
                    if (otherPortal != null) {
                        otherPortal.setDestination(otherPortal.getOriginPos());
                        PortalManipulation.adjustRotationToConnect(PortalAPI.createFlippedPortal(otherPortal), otherPortal);
                        otherPortal.setActive(false);
                        otherPortal.reloadAndSyncToClient();
                    }
                }
            }
        }
        super.tick();
    }
}
