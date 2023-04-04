package com.fusionflux.portalcubed.compat.create;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.blocks.AutoPortalBlock;
import com.fusionflux.portalcubed.blocks.blockentities.AutoPortalBlockEntity;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.simibubi.create.foundation.ponder.*;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.utility.Pointing;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class PCPonder {
    private static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(PortalCubed.MOD_ID);

    public static final PonderTag PORTAL_CUBED = new PonderTag(id("portal_cubed"))
        .item(PortalCubedItems.BLOCK_ITEM_ICON, true, false)
        .addToIndex();

    private static final boolean GENERATE_TRANSLATIONS = false;

    public static void register() {
        HELPER.addStoryBoard(id("auto_portal"), "auto_portal", PCPonder::autoPortal, PORTAL_CUBED);

        if (GENERATE_TRANSLATIONS) {
            PonderLocalization.generateSceneLang();
            final JsonObject translation = new JsonObject();
            PonderLocalization.record("portalcubed", translation);
            try (Writer writer = new FileWriter("create_ponder_portalcubed.json", StandardCharsets.UTF_8)) {
                new GsonBuilder().setPrettyPrinting().create().toJson(translation, writer);
            } catch (IOException e) {
                PortalCubed.LOGGER.error("Failed to save file", e);
            }
        }
    }

    public static void autoPortal(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("auto_portal", "Using the Auto Portal");
        scene.configureBasePlate(0, 0, 4);
        scene.showBasePlate();
        scene.idle(5);

        scene.world.showSection(util.select.position(2, 1, 2), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(2, 2, 2), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(2, 1, 1, 2, 2, 1), Direction.SOUTH);
        scene.idle(5);
        scene.world.showSection(util.select.position(1, 2, 2), Direction.EAST);
        scene.idle(5);

        scene.overlay.showText(80)
            .text("Power an Auto Portal with redstone to open it.")
            .placeNearTarget()
            .pointAt(util.vector.centerOf(1, 2, 2));
        scene.idle(60);

        scene.world.toggleRedstonePower(util.select.position(1, 2, 2));
        var portalLink = scene.world.createEntity(world -> AutoPortalBlock.openOrClosePortal(
            world, new BlockPos(2, 1, 1), Direction.NORTH, false, false, false
        ));

        scene.idle(20);
        scene.world.toggleRedstonePower(util.select.position(1, 2, 2));
        scene.idle(20);

        scene.overlay.showText(80)
            .text("Power an Auto Portal with redstone again to close it.")
            .placeNearTarget()
            .pointAt(util.vector.centerOf(1, 2, 2));
        scene.idle(60);

        scene.world.toggleRedstonePower(util.select.position(1, 2, 2));
        scene.world.modifyEntity(portalLink, Entity::kill);

        scene.idle(20);
        scene.world.toggleRedstonePower(util.select.position(1, 2, 2));

        scene.overlay.showControls(
            new InputWindowElement(util.vector.centerOf(2, 2, 1), Pointing.RIGHT)
                .rightClick()
                .withItem(new ItemStack(PortalCubedItems.HAMMER)),
            70
        );
        scene.idle(10);
        scene.overlay.showText(60)
            .text("Right-click with a hammer to switch between primary and secondary modes.")
            .attachKeyFrame()
            .placeNearTarget()
            .pointAt(util.vector.centerOf(2, 1, 1));
        scene.world.cycleBlockProperty(new BlockPos(2, 1, 1), AutoPortalBlock.TYPE);
        scene.world.cycleBlockProperty(new BlockPos(2, 2, 1), AutoPortalBlock.TYPE);
        scene.idle(90);

        scene.world.toggleRedstonePower(util.select.position(1, 2, 2));
        portalLink = scene.world.createEntity(world -> AutoPortalBlock.openOrClosePortal(
            world, new BlockPos(2, 1, 1), Direction.NORTH, false, false, false
        ));
        scene.idle(20);
        scene.world.toggleRedstonePower(util.select.position(1, 2, 2));
        scene.idle(30);
        scene.world.toggleRedstonePower(util.select.position(1, 2, 2));
        scene.world.modifyEntity(portalLink, Entity::kill);
        scene.idle(20);
        scene.world.toggleRedstonePower(util.select.position(1, 2, 2));

        scene.idle(10);
        scene.overlay.showControls(
            new InputWindowElement(util.vector.centerOf(2, 2, 1), Pointing.RIGHT)
                .rightClick()
                .withItem(new ItemStack(Items.PURPLE_DYE)),
            70
        );
        scene.idle(10);
        scene.overlay.showText(60)
            .text("Right-click with dye to change the portal's base color.")
            .attachKeyFrame()
            .placeNearTarget()
            .pointAt(util.vector.centerOf(2, 1, 1));
        scene.world.modifyTileNBT(
            util.select.position(2, 1, 1),
            AutoPortalBlockEntity.class,
            nbt -> nbt.putInt("Color", 0x8932b8)
        );
        scene.world.modifyTileNBT(
            util.select.position(2, 2, 1),
            AutoPortalBlockEntity.class,
            nbt -> nbt.putInt("Color", 0x8932b8)
        );
        scene.idle(90);

        scene.world.toggleRedstonePower(util.select.position(1, 2, 2));
        portalLink = scene.world.createEntity(world -> AutoPortalBlock.openOrClosePortal(
            world, new BlockPos(2, 1, 1), Direction.NORTH, false, false, false
        ));
        scene.idle(20);
        scene.world.toggleRedstonePower(util.select.position(1, 2, 2));

        scene.overlay.showText(60)
            .text("This portal is green because the Auto Portal is still set to secondary.")
            .placeNearTarget()
            .pointAt(util.vector.centerOf(2, 2, 1));
        scene.idle(80);

        scene.world.toggleRedstonePower(util.select.position(1, 2, 2));
        scene.world.modifyEntity(portalLink, Entity::kill);
        scene.idle(20);
        scene.world.toggleRedstonePower(util.select.position(1, 2, 2));

        scene.idle(10);
        scene.overlay.showControls(
            new InputWindowElement(util.vector.centerOf(2, 2, 1), Pointing.RIGHT)
                .rightClick()
                .withItem(new ItemStack(PortalCubedItems.HAMMER))
                .whileSneaking(),
            70
        );
        scene.idle(10);
        scene.overlay.showText(60)
            .text("Sneak right-click with a hammer to reset the portal's base color.")
            .attachKeyFrame()
            .placeNearTarget()
            .pointAt(util.vector.centerOf(2, 1, 1));
        scene.world.modifyTileNBT(
            util.select.position(2, 1, 1),
            AutoPortalBlockEntity.class,
            nbt -> nbt.putInt("Color", 0x1d86db)
        );
        scene.world.modifyTileNBT(
            util.select.position(2, 2, 1),
            AutoPortalBlockEntity.class,
            nbt -> nbt.putInt("Color", 0x1d86db)
        );
        scene.idle(90);

        scene.world.toggleRedstonePower(util.select.position(1, 2, 2));
        portalLink = scene.world.createEntity(world -> AutoPortalBlock.openOrClosePortal(
            world, new BlockPos(2, 1, 1), Direction.NORTH, false, false, false
        ));
        scene.idle(20);
        scene.world.toggleRedstonePower(util.select.position(1, 2, 2));
        scene.idle(30);
        scene.world.toggleRedstonePower(util.select.position(1, 2, 2));
        scene.world.modifyEntity(portalLink, Entity::kill);
        scene.idle(20);
        scene.world.toggleRedstonePower(util.select.position(1, 2, 2));
    }
}
