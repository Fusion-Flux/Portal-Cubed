package com.fusionflux.thinkingwithportatos.entity;

import com.fusionflux.thinkingwithportatos.sound.ThinkingWithPortatosSounds;
import com.qouteall.immersive_portals.Helper;
import com.qouteall.immersive_portals.api.PortalAPI;
import com.qouteall.immersive_portals.my_util.SignalArged;
import com.qouteall.immersive_portals.my_util.SignalBiArged;
import com.qouteall.immersive_portals.portal.Portal;
import com.qouteall.immersive_portals.portal.PortalManipulation;
import com.qouteall.immersive_portals.teleportation.CollisionHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.UUID;

public class CustomPortalEntity extends Portal {
    public static final SignalArged<CustomPortalEntity> clientPortalTickSignal;
    public static final SignalArged<CustomPortalEntity> serverPortalTickSignal;
    public static final SignalArged<CustomPortalEntity> portalCacheUpdateSignal;
    public static final SignalArged<CustomPortalEntity> portalDisposeSignal;
    public static final SignalBiArged<CustomPortalEntity, CompoundTag> readPortalDataSignal;
    public static final SignalBiArged<CustomPortalEntity, CompoundTag> writePortalDataSignal;
    public static final TrackedData<String> STOREDSTRING = DataTracker.registerData(CustomPortalEntity.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Boolean> ISACTIVE = DataTracker.registerData(CustomPortalEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<String> STOREDOUTLINE = DataTracker.registerData(CustomPortalEntity.class, TrackedDataHandlerRegistry.STRING);

    public CustomPortalEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
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
                this.remove();
                return;
            }

            serverPortalTickSignal.emit(this);
        }

        CollisionHelper.notifyCollidingPortals(this);
        if (!this.world.isClient) {
           /* if(!this.getString().equals("null")) {
                CustomPortalEntity lightAtOtherPortal;
                lightAtOtherPortal = (CustomPortalEntity) ((ServerWorld) world).getEntity(UUID.fromString(this.getString()));
                if (this.world.getLuminance(this.getBlockPos()) > lightAtOtherPortal.world.getLuminance(lightAtOtherPortal.getBlockPos())) {
                    lightAtOtherPortal.setLuminance(this.world.getLuminance(this.getBlockPos()));
                }
                if (this.world.getLuminance(this.getBlockPos()) < lightAtOtherPortal.world.getLuminance(lightAtOtherPortal.getBlockPos())) {
                    this.setLuminance(lightAtOtherPortal.world.getLuminance(this.getBlockPos()));
                }
            }*/

            if ((!this.world.getBlockState(this.getBlockPos()).isAir())||(!this.world.getBlockState(new BlockPos(
                    this.getPos().getX()-Math.abs(this.axisH.getX()),
                    this.getPos().getY()+this.axisH.getY(),
                    this.getPos().getZ()-Math.abs(this.axisH.getZ()))).isAir())) {

                if(!this.getOutline().equals("null")) {
                    PortalPlaceholderEntity portalOutline;
                    portalOutline = (PortalPlaceholderEntity) ((ServerWorld) world).getEntity(UUID.fromString(this.getOutline()));
                    assert portalOutline != null;
                    portalOutline.kill();
                }

                this.kill();
                world.playSound(null, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), ThinkingWithPortatosSounds.ENTITY_PORTAL_CLOSE, SoundCategory.NEUTRAL, .1F, 1F);
                System.out.println("killed");
                if(!this.getString().equals("null")){
                    CustomPortalEntity otherPortal;
                    otherPortal = (CustomPortalEntity) ((ServerWorld) world).getEntity(UUID.fromString(this.getString()));
                    assert otherPortal != null;
                    if(otherPortal!=null) {
                        otherPortal.setDestination(otherPortal.getOriginPos());
                        PortalManipulation.adjustRotationToConnect(PortalAPI.createFlippedPortal(otherPortal), otherPortal);
                        otherPortal.setActive(false);
                        otherPortal.reloadAndSyncToClient();
                    }
                }
            }
            BlockPos alteredPos = new BlockPos(
                    this.getPos().getX()-this.axisW.crossProduct(this.axisH).getX(),
                    this.getPos().getY()-this.axisW.crossProduct(this.axisH).getY(),
                    this.getPos().getZ()-this.axisW.crossProduct(this.axisH).getZ());
            BlockPos lowerPos = new BlockPos(
                    this.getPos().getX()-this.axisW.crossProduct(this.axisH).getX()-Math.abs(this.axisH.getX()),
                    this.getPos().getY()-this.axisW.crossProduct(this.axisH).getY()+this.axisH.getY(),
                    this.getPos().getZ()-this.axisW.crossProduct(this.axisH).getZ()-Math.abs(this.axisH.getZ()));

            Direction portalFacing = Direction.fromVector((int)this.getNormal().getX(),(int)this.getNormal().getY(),(int)this.getNormal().getZ());

            if ((!this.world.getBlockState(alteredPos).isSideSolidFullSquare(world,alteredPos,portalFacing))||
                    (!this.world.getBlockState(lowerPos).isSideSolidFullSquare(world,lowerPos,portalFacing)
                    )) {
                if(!this.getOutline().equals("null")) {
                    PortalPlaceholderEntity portalOutline;
                    portalOutline = (PortalPlaceholderEntity) ((ServerWorld) world).getEntity(UUID.fromString(this.getOutline()));
                    assert portalOutline != null;
                    portalOutline.kill();
                }
                this.kill();
                world.playSound(null, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), ThinkingWithPortatosSounds.ENTITY_PORTAL_CLOSE, SoundCategory.NEUTRAL, .1F, 1F);
                System.out.println("killed");
                if(!this.getString().equals("null")){
                    CustomPortalEntity otherPortal;
                    otherPortal = (CustomPortalEntity) ((ServerWorld) world).getEntity(UUID.fromString(this.getString()));
                    assert otherPortal != null;
                    if(otherPortal!=null) {
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

    static {
        clientPortalTickSignal = new SignalArged();
        serverPortalTickSignal = new SignalArged();
        portalCacheUpdateSignal = new SignalArged();
        portalDisposeSignal = new SignalArged();
        readPortalDataSignal = new SignalBiArged();
        writePortalDataSignal = new SignalBiArged();
    }
}
