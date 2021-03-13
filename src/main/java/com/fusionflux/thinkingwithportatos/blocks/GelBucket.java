package com.fusionflux.thinkingwithportatos.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public class GelBucket extends BlockItem {


    public GelBucket(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public ActionResult place(ItemPlacementContext context) {
        ItemStack itemStack = this.getPlacementContext(context).getStack();
        int count = itemStack.getCount();
        ActionResult result = super.place(context);
        itemStack.setCount(count);
        return result;
    }

}
