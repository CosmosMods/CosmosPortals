package com.tcn.cosmosportals.client.colour;

import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmosportals.core.block.BlockDockController;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortal;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortalDock;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortalDockUpgraded;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockColour implements BlockColor {

	@Override
	public int getColor(BlockState stateIn, BlockAndTintGetter displayReaderIn, BlockPos posIn, int tintIndexIn) {
		BlockEntity tile = displayReaderIn.getBlockEntity(posIn);
		
		if (stateIn.getBlock() instanceof BlockDockController) {
			if (tintIndexIn == 0) {
				return ComponentColour.GRAY.dec();
			} else if (tintIndexIn == 1) {
				 return ComponentColour.WHITE.dec();
			}
		}
		
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

		else if (tile instanceof BlockEntityPortalDockUpgraded) {
			if (tintIndexIn == 1) {
				BlockEntityPortalDockUpgraded portal_tile = (BlockEntityPortalDockUpgraded) tile;
				
				return portal_tile.getDisplayColour();
			} else if (tintIndexIn == 2) {
				 return ComponentColour.WHITE.dec();
			} else {
				return ComponentColour.GRAY.dec();
			}
		}
		
		return ComponentColour.GRAY.dec();
	}

}