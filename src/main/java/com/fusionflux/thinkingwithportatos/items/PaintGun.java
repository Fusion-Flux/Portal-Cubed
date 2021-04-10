package com.fusionflux.thinkingwithportatos.items;


import com.fusionflux.thinkingwithportatos.ThinkingWithPortatos;
import com.fusionflux.thinkingwithportatos.entity.CustomPortalEntity;
import com.fusionflux.thinkingwithportatos.entity.GelOrbEntity;
import com.fusionflux.thinkingwithportatos.entity.PortalPlaceholderEntity;
import com.fusionflux.thinkingwithportatos.entity.ThinkingWithPortatosEntities;
import com.fusionflux.thinkingwithportatos.sound.ThinkingWithPortatosSounds;
import com.qouteall.immersive_portals.api.PortalAPI;
import com.qouteall.immersive_portals.my_util.DQuaternion;
import com.qouteall.immersive_portals.portal.PortalManipulation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;


public class PaintGun extends Item {

    public PaintGun(Settings settings) {
        super(settings);
    }

    protected LivingEntity player;
    protected boolean using = false;
    protected boolean usingLeft = false;

    public void useLeft(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        stack.getOrCreateTag().putBoolean("complementary", false);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (ThinkingWithPortatos.getBodyGrabbingManager(user.world.isClient).tryStopGrabbing(user)) {
            return new TypedActionResult<>(ActionResult.SUCCESS, user.getStackInHand(hand));
        }
        ItemStack stack = user.getStackInHand(hand);
        stack.getOrCreateTag().putBoolean("complementary", true);
        usingLeft = false;
        using = true;
        player = user;
        return TypedActionResult.pass(stack);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        using = false;
        return stack;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && selected && using) {

            throwGel(world);
        }
    }

    protected void throwGel(World world) {
        GelOrbEntity gelOrbEntity = new GelOrbEntity(world, player);
        gelOrbEntity.setItem(new ItemStack(ThinkingWithPortatosItems.GEL_ORB));
        gelOrbEntity.setProperties(player, player.pitch, player.yaw, 0.0F, 1.5F, 0F);
        world.spawnEntity(gelOrbEntity); // spawns entity
    }
}