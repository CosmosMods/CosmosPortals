package com.tcn.cosmosportals.core.item;

import java.util.List;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.common.comp.CosmosCompHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class BlockItemPortalDock extends BlockItem {

	public BlockItemPortalDock(Block blockIn, Properties propertiesIn) {
		super(blockIn, propertiesIn);
	}

	@Override
	public void appendHoverText(ItemStack stackIn, @Nullable Level worldIn, List<Component> toolTipIn, TooltipFlag flagIn) {
		super.appendHoverText(stackIn, worldIn, toolTipIn, flagIn);
		
		if (!CosmosCompHelper.isShiftKeyDown(Minecraft.getInstance())) {
			toolTipIn.add(CosmosCompHelper.getTooltipInfo("cosmosportals.block_info.dock"));
			
			if (CosmosCompHelper.displayShiftForDetail) {
				toolTipIn.add(CosmosCompHelper.shiftForMoreDetails());
			}
		} else {
			toolTipIn.add(CosmosCompHelper.getTooltipOne("cosmosportals.block_info.dock_one"));
			toolTipIn.add(CosmosCompHelper.getTooltipTwo("cosmosportals.block_info.dock_two"));
			toolTipIn.add(CosmosCompHelper.getTooltipThree("cosmosportals.block_info.dock_three"));
			
			toolTipIn.add(CosmosCompHelper.shiftForLessDetails());
		}
	}
}