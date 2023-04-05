package com.fusionflux.portalcubed.entity;

import com.fusionflux.portalcubed.util.GeneralUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class TurretEntity extends CorePhysicsEntity {
    public static final float MODEL_SCALE = MathHelper.lerp(0.875f, 1 / 1.62f, 1f);
    private static final Box BASE_BOX = createFootBox(0.5f * MODEL_SCALE, 1.5f * MODEL_SCALE, MODEL_SCALE);

    public TurretEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected Box calculateBoundingBox() {
        return GeneralUtil.rotate(BASE_BOX, headYaw, Direction.Axis.Y).offset(getPos());
    }
}
