package com.fusionflux.portalcubed.client.render.entity;

import com.fusionflux.portalcubed.client.render.entity.model.LilPineappleModel;
import com.fusionflux.portalcubed.entity.LilPineappleEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import java.util.Locale;
import java.util.Objects;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class LilPineappleRenderer extends CorePhysicsRenderer<LilPineappleEntity, LilPineappleModel> {
    private static final Identifier BASE_TEXTURE = id("textures/entity/lil_pineapple.png");
    private static final Identifier PROUD_TEXTURE = id("textures/entity/lil_prideapple_proud.png");
    private static final Identifier ACE_TEXTURE = id("textures/entity/lil_prideapple_ace.png");
    private static final Identifier AGENDER_TEXTURE = id("textures/entity/lil_prideapple_agender.png");
    private static final Identifier ARO_TEXTURE = id("textures/entity/lil_prideapple_aro.png");
    private static final Identifier BI_TEXTURE = id("textures/entity/lil_prideapple_bi.png");
    private static final Identifier GENDERFLUID_TEXTURE = id("textures/entity/lil_prideapple_genderfluid.png");
    private static final Identifier LESBIAN_TEXTURE = id("textures/entity/lil_prideapple_lesbian.png");
    private static final Identifier NONBINARY_TEXTURE = id("textures/entity/lil_prideapple_nonbinary.png");
    private static final Identifier PAN_TEXTURE = id("textures/entity/lil_prideapple_pan.png");
    private static final Identifier TRANS_TEXTURE = id("textures/entity/lil_prideapple_trans.png");

    public LilPineappleRenderer(EntityRendererFactory.Context context) {
        super(context, new LilPineappleModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(LilPineappleModel.LIL_PINEAPPLE)), 0.5f);
    }




    @Override
    public Identifier getTexture(LilPineappleEntity entity) {
        if (entity.getCustomName() != null) {
            if (Objects.equals(entity.getCustomName().getString().toLowerCase(Locale.ROOT), "proud")) {
                return PROUD_TEXTURE;
            }
            if (Objects.equals(entity.getCustomName().getString().toLowerCase(Locale.ROOT), "ace")) {
                return ACE_TEXTURE;
            }
            if (Objects.equals(entity.getCustomName().getString().toLowerCase(Locale.ROOT), "aro")) {
                return ARO_TEXTURE;
            }
            if (Objects.equals(entity.getCustomName().getString().toLowerCase(Locale.ROOT), "agender")) {
                return AGENDER_TEXTURE;
            }
            if (Objects.equals(entity.getCustomName().getString().toLowerCase(Locale.ROOT), "bi")) {
                return BI_TEXTURE;
            }
            if (Objects.equals(entity.getCustomName().getString().toLowerCase(Locale.ROOT), "genderfluid")) {
                return GENDERFLUID_TEXTURE;
            }
            if (Objects.equals(entity.getCustomName().getString().toLowerCase(Locale.ROOT), "lesbian")) {
                return LESBIAN_TEXTURE;
            }
            if (Objects.equals(entity.getCustomName().getString().toLowerCase(Locale.ROOT), "nonbinary")) {
                return NONBINARY_TEXTURE;
            }
            if (Objects.equals(entity.getCustomName().getString().toLowerCase(Locale.ROOT), "pan")) {
                return PAN_TEXTURE;
            }
            if (Objects.equals(entity.getCustomName().getString().toLowerCase(Locale.ROOT), "trans")) {
                return TRANS_TEXTURE;
            }
        }
        return BASE_TEXTURE;
    }
}
