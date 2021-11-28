package com.tcn.cosmosportals.client.colour;

import com.tcn.cosmoslibrary.common.comp.CosmosColour;
import com.tcn.cosmosportals.core.tileentity.TileEntityPortal;
import com.tcn.cosmosportals.core.tileentity.TileEntityPortalDock;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;

public class BlockColour implements IBlockColor {

	@Override
	public int getColor(BlockState stateIn, IBlockDisplayReader displayReaderIn, BlockPos posIn, int tintIndexIn) {
		TileEntity tile = displayReaderIn.getBlockEntity(posIn);
		
		if (tile instanceof TileEntityPortal) {
			TileEntityPortal portal_tile = (TileEntityPortal) tile;
			return portal_tile.getDisplayColour();
		} 
		
		else if (tile instanceof TileEntityPortalDock) {
			if (tintIndexIn == 1) {
				TileEntityPortalDock portal_tile = (TileEntityPortalDock) tile;
				return portal_tile.getDisplayColour();
			} else {
				return CosmosColour.GRAY.dec();
			}
		}
		
		return CosmosColour.GRAY.dec();
	}
}