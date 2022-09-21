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

public class BlockItemContainerWorkbench extends BlockItem {

	public BlockItemContainerWorkbench(Block blockIn, Properties propertiesIn) {
		super(blockIn, propertiesIn);
	}

	@Override
	public void appendHoverText(ItemStack stackIn, @Nullable Level worldIn, List<Component> toolTipIn, TooltipFlag flagIn) {
		super.appendHoverText(stackIn, worldIn, toolTipIn, flagIn);
		
		if (!ComponentHelper.isShiftKeyDown(Minecraft.getInstance())) {
			toolTipIn.add(ComponentHelper.getTooltipInfo("cosmosportals.block_info.workbench"));
			
			if (ComponentHelper.displayShiftForDetail) {
				toolTipIn.add(ComponentHelper.shiftForMoreDetails());
			}
		} else {
			toolTipIn.add(ComponentHelper.getTooltipOne("cosmosportals.block_info.workbench_one"));
			toolTipIn.add(ComponentHelper.getTooltipTwo("cosmosportals.block_info.workbench_two"));
			toolTipIn.add(ComponentHelper.getTooltipLimit("cosmosportals.block_info.workbench_limit"));
			
			toolTipIn.add(ComponentHelper.shiftForLessDetails());
		}
	}
}