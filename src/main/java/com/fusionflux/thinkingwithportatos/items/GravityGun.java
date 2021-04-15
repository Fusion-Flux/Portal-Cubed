package com.fusionflux.thinkingwithportatos.items;

import com.fusionflux.thinkingwithportatos.ThinkingWithPortatos;
import com.fusionflux.thinkingwithportatos.physics.BodyGrabbingManager;
import com.fusionflux.thinkingwithportatos.physics.GrabUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class GravityGun extends Item {
    public static final float STRENGTH = 30f; // hard coding go brrrr

    public GravityGun(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            BodyGrabbingManager manager = ThinkingWithPortatos.getBodyGrabbingManager(false);

            if (manager.isPlayerGrabbing(user)) {
                manager.tryUngrab(user, 0.0f);
            } else {
                Entity entity = GrabUtil.getEntityToGrab(user);

                if (entity == null) {
                    Entity block = GrabUtil.getBlockToGrab(user);

                    if (block != null) {
                        manager.tryGrab(user, block);
                    }
                } else {
                    manager.tryGrab(user, entity);
                }
            }
        }

        return TypedActionResult.pass(user.getStackInHand(hand));
    }
}
