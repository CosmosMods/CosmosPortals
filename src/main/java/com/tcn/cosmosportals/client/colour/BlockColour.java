package com.tcn.cosmosportals.client.colour;

import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortal;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortalDock;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockColour implements BlockColor {

	@Override
	public int getColor(BlockState stateIn, BlockAndTintGetter displayReaderIn, BlockPos posIn, int tintIndexIn) {
		BlockEntity tile = displayReaderIn.getBlockEntity(posIn);
		
		if (tile instanceof BlockEntityPortal) {
			BlockEntityPortal portal_tile = (BlockEntityPortal) tile;
			return portal_tile.getDisplayColour();
		} 
		
		else if (tile instanceof BlockEntityPortalDock) {
			if (tintIndexIn == 1) {
				BlockEntityPortalDock portal_tile = (BlockEntityPortalDock) tile;
				return portal_tile.getDisplayColour();
			} else {
				return ComponentColour.GRAY.dec();
			}
		}
		
		return ComponentColour.GRAY.dec();
	}

}