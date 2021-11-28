package com.tcn.cosmosportals.core.item;

import java.util.List;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.common.comp.CosmosCompHelper;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class BlockItemPortalFrame extends BlockItem {

	public BlockItemPortalFrame(Block blockIn, Properties propertiesIn) {
		super(blockIn, propertiesIn);
	}

	@Override
	public void appendHoverText(ItemStack stackIn, @Nullable World worldIn, List<ITextComponent> toolTipIn, ITooltipFlag flagIn) {
		super.appendHoverText(stackIn, worldIn, toolTipIn, flagIn);
		
		toolTipIn.add(CosmosCompHelper.getTooltipInfo("cosmosportals.block_info.frame"));
		toolTipIn.add(CosmosCompHelper.getTooltipOne("cosmosportals.block_info.frame_one"));
	}
}