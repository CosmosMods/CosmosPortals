package com.tcn.cosmosportals.client.container;

import com.tcn.cosmoslibrary.client.container.slot.SlotRestrictedAccess;
import com.tcn.cosmosportals.core.management.CoreModBusManager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContainerPortalDock extends Container {

	private final IWorldPosCallable access;

	private final World world;
	private final BlockPos pos;

	public ContainerPortalDock(int indexIn, PlayerInventory playerInventoryIn, PacketBuffer extraData) {
		this(indexIn, playerInventoryIn, new Inventory(1), IWorldPosCallable.NULL, extraData.readBlockPos());
	}

	public ContainerPortalDock(int indexIn, PlayerInventory playerInventoryIn, IInventory contentsIn, IWorldPosCallable callableIn, BlockPos pos) {
		super(CoreModBusManager.DOCK_CONTAINER_TYPE, indexIn);
		this.pos = pos;
		this.world = playerInventoryIn.player.level;
		
		this.access = callableIn;
		
		this.addSlot(new SlotRestrictedAccess(contentsIn, 0, 146, 111, false, false));
		
		//Player Inventory
		for (int k = 0; k < 3; ++k) {
			for (int i1 = 0; i1 < 9; ++i1) {
				this.addSlot(new Slot(playerInventoryIn, i1 + k * 9 + 9, 10 + i1 * 18, 176 + k * 18));
			}
		}

		//Player Hotbar
		for (int l = 0; l < 9; ++l) {
			this.addSlot(new Slot(playerInventoryIn, l, 10 + l * 18, 234));
		}
	}
	

	public void addSlotListener(IContainerListener listenerIn) {
		super.addSlotListener(listenerIn);
	}

	@OnlyIn(Dist.CLIENT)
	public void removeSlotListener(IContainerListener listenerIn) {
		super.removeSlotListener(listenerIn);
	}

	@Override
	public void slotsChanged(IInventory inventoryIn) {
		super.slotsChanged(inventoryIn);
		this.broadcastChanges();
	}
	
	@Override
	public void removed(PlayerEntity playerIn) {
		super.removed(playerIn);
	}

	@Override
	public boolean stillValid(PlayerEntity playerIn) {
		return stillValid(this.access, playerIn, CoreModBusManager.PORTAL_DOCK);
	}

	@Override
	public ItemStack quickMoveStack(PlayerEntity playerIn, int indexIn) {
		return ItemStack.EMPTY;
	}

	public World getWorld() {
		return world;
	}

	public BlockPos getBlockPos() {
		return pos;
	}
}
