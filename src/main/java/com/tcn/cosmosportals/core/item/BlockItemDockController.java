package com.tcn.cosmosportals.core.item;

import java.util.List;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class BlockItemDockController extends BlockItem {

	public BlockItemDockController(Block blockIn, Properties propertiesIn) {
		super(blockIn, propertiesIn);
	}

	@Override
	public void appendHoverText(ItemStack stackIn, @Nullable Level worldIn, List<Component> toolTipIn, TooltipFlag flagIn) {
		super.appendHoverText(stackIn, worldIn, toolTipIn, flagIn);
		
		if (!ComponentHelper.isShiftKeyDown(Minecraft.getInstance())) {
			toolTipIn.add(ComponentHelper.getTooltipInfo("cosmosportals.block_info.dock_controller"));
			
			if (ComponentHelper.displayShiftForDetail) {
				toolTipIn.add(ComponentHelper.shiftForMoreDetails());
			}
		} else {
			toolTipIn.add(ComponentHelper.getTooltipOne("cosmosportals.block_info.dock_controller_one"));
			toolTipIn.add(ComponentHelper.getTooltipTwo("cosmosportals.block_info.dock_controller_two"));
			toolTipIn.add(ComponentHelper.getTooltipFour("cosmosportals.block_info.dock_controller_three"));
			
			toolTipIn.add(ComponentHelper.shiftForLessDetails());
		}
	}
}