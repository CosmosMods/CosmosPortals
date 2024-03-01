package com.tcn.cosmosportals.core.blockentity;

import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmosportals.client.container.ContainerPortalDock;
import com.tcn.cosmosportals.core.management.ModObjectHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityPortalDock extends AbstractBlockEntityPortalDock {

	public BlockEntityPortalDock(BlockPos posIn, BlockState stateIn) {
		super(ModObjectHolder.tile_portal_dock, posIn, stateIn, 1);
	}

	@Override
	public Component getDisplayName() {
		return ComponentHelper.title("cosmosportals.gui.dock");
	}
	
	@Override
	public AbstractContainerMenu createMenu(int idIn, Inventory playerInventoryIn, Player playerIn) {
		return new ContainerPortalDock(idIn, playerInventoryIn, this, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()), this.getBlockPos());
	}

	@Override
	public Component getName() {
		return ComponentHelper.title("cosmosportals.gui.dock");
	}
}