package com.fusionflux.fluxtech.items;

import com.qouteall.immersive_portals.ClientWorldLoader;
import com.qouteall.immersive_portals.block_manipulation.BlockManipulationServer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class MinecraftClientMethods {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static HitResult remoteHitResult;
    public static RegistryKey<World> remotePointedDim;
    public static boolean isContextSwitched = false;

    public static boolean isPointingToPortal() {
        return remotePointedDim != null;
    }

    /*public static void myItemUse(World world, PlayerEntity user, Hand hand) {
        ClientWorld targetWorld = ClientWorldLoader.getWorld(remotePointedDim);
        ItemStack itemStack = client.player.getStackInHand(hand);
        BlockHitResult blockHitResult = (BlockHitResult) remoteHitResult;
        Pair<BlockHitResult, RegistryKey<World>> result = BlockManipulationServer.getHitResultForPlacing(targetWorld, blockHitResult);
        blockHitResult = (BlockHitResult) result.getLeft();
        targetWorld = ClientWorldLoader.getWorld((RegistryKey) result.getRight());
        remoteHitResult = blockHitResult;
        remotePointedDim = (RegistryKey) result.getRight();
        int i = itemStack.getCount();
        ActionResult actionResult2 = myInteractBlock(hand, targetWorld, blockHitResult);
        if (!actionResult2.isAccepted()) {
            if (actionResult2 != ActionResult.FAIL) {
                if (!itemStack.isEmpty()) {
                    ActionResult actionResult3 = client.interactionManager.interactItem(client.player, targetWorld, hand);
                    if (actionResult3.isAccepted()) {


                        client.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                        return;
                    }
                }

            }
        }

        }*/
    /*public static ActionResult myInteractBlock(Hand hand, ClientWorld targetWorld, BlockHitResult blockHitResult) {
        ClientWorld oldWorld = client.world;

        ActionResult var4;
        try {
            client.player.world = targetWorld;
            client.world = targetWorld;
            isContextSwitched = true;
            var4 = client.interactionManager.interactBlock(client.player, targetWorld, hand, blockHitResult);
        } finally {
            client.player.world = oldWorld;
            client.world = oldWorld;
            isContextSwitched = false;
        }

        return var4;
    }*/

    }

