package com.fusionflux.portalcubed.blocks.fizzler;

import net.minecraft.world.entity.Entity;

public class MatterInquisitionField extends AbstractFizzlerBlock {
    public MatterInquisitionField(Properties settings) {
        super(settings);
    }

    @Override
    public void applyEffectsTo(Entity entity) {
        fizzlePhysicsEntity(entity);
    }
}
