package com.tcn.cosmosportals.client.container;

import com.tcn.cosmoslibrary.client.container.CosmosContainerMenuBlockEntity;
import com.tcn.cosmoslibrary.client.container.slot.SlotRestrictedAccess;
import com.tcn.cosmoslibrary.client.container.slot.SlotSpecifiedItem;
import com.tcn.cosmosportals.core.management.ModObjectHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContainerContainerWorkbench extends CosmosContainerMenuBlockEntity {
	
	protected Container copy;
	
	protected final ResultContainer resultSlot = new ResultContainer();
	
	public ContainerContainerWorkbench(int indexIn, Inventory playerInventoryIn, FriendlyByteBuf extraData) {
		this(indexIn, playerInventoryIn, new SimpleContainer(2), ContainerLevelAccess.NULL, extraData.readBlockPos());
	}

	public ContainerContainerWorkbench(int indexIn, Inventory playerInventoryIn, Container contentsIn, ContainerLevelAccess accessIn, BlockPos posIn) {
		super(ModObjectHolder.container_container_workbench, indexIn, playerInventoryIn, accessIn, posIn);
		this.copy = contentsIn;
		
		this.addSlot(new SlotSpecifiedItem(contentsIn, 0, 30, 15, ModObjectHolder.item_dimension_container_unlinked, 16) {
			@Override
			public void setChanged() {
				super.setChanged();
				ContainerContainerWorkbench.this.slotsChanged(this.container);
				this.container.setChanged();
				contentsIn.setChanged();
			}
		});
		
		this.addSlot(new SlotSpecifiedItem(contentsIn, 1, 74, 15, ModObjectHolder.item_dimension_container, 1) {
			@Override
			public void setChanged() {
				super.setChanged();
				ContainerContainerWorkbench.this.slotsChanged(this.container);
				this.container.setChanged();
				contentsIn.setChanged();
			}
		});
		
		this.addSlot(new SlotRestrictedAccess(resultSlot, 0, 126, 15, false, true) {
			@Override
			public void onTake(Player playerIn, ItemStack stackIn) {
				ContainerContainerWorkbench.this.onTake(playerIn, stackIn);
			}
		});
		
		/** @Inventory */
		for (int k = 0; k < 3; ++k) {
			for (int i1 = 0; i1 < 9; ++i1) {
				this.addSlot(new Slot(playerInventoryIn, i1 + k * 9 + 9, 6 + i1 * 18, 46 + k * 18));
			}
		}

		/** @Actionbar */
		for (int l = 0; l < 9; ++l) {
			this.addSlot(new Slot(playerInventoryIn, l, 6 + l * 18, 104));
		}
		contentsIn.setChanged();
		this.slotsChanged(this.copy);
	}
	
	@Override
	public void slotsChanged(Container containerIn) {
		super.slotsChanged(containerIn);
		
		if (containerIn == this.copy) {
			this.createResult();
		}
	}

	protected void onTake(Player playerIn, ItemStack stackIn) {
		stackIn.onCraftedBy(playerIn.level, playerIn, stackIn.getCount());
		
		this.resultSlot.awardUsedRecipes(playerIn);
		if (!this.copy.getItem(1).isEmpty()) {
			this.shrinkStackInSlot(1);
		}
		this.slotsChanged(copy);
	}

	private void shrinkStackInSlot(int slotIndex) {
		this.copy.getItem(0).shrink(1);
	}

	public void createResult() {
		if (!this.copy.getItem(0).isEmpty()) {
			ItemStack inputStack = this.copy.getItem(1).copy();
			
			if (inputStack.isEmpty()) {
				this.resultSlot.setItem(0, ItemStack.EMPTY);
			} else {
				this.resultSlot.setItem(0, inputStack);
			}
		} else {
			this.resultSlot.setItem(0, ItemStack.EMPTY);
		}
	}
	
	@Override
	public void addSlotListener(ContainerListener listenerIn) {
		super.addSlotListener(listenerIn);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void removeSlotListener(ContainerListener listenerIn) {
		super.removeSlotListener(listenerIn);
	}

	@Override
	public void removed(Player playerIn) {
		super.removed(playerIn);
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return stillValid(this.access, playerIn, ModObjectHolder.block_container_workbench);
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int indexIn) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(indexIn);

		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			
			if (indexIn >= 0 && indexIn <= 2) {
				if (!this.moveItemStackTo(itemstack1, 3, this.slots.size() - 9, false)) {
					if (!this.moveItemStackTo(itemstack1, this.slots.size() - 9, this.slots.size(), false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (indexIn > 2 && indexIn <= this.slots.size() - 9) {
				if (itemstack.getItem().equals(ModObjectHolder.item_dimension_container_unlinked)) {
					if (!this.moveItemStackTo(itemstack1, 0, 1, true)) {
						if (!this.moveItemStackTo(itemstack1, this.slots.size() - 9, this.slots.size(), false)) {
							return ItemStack.EMPTY;
						}
					}
				} else if (itemstack.getItem().equals(ModObjectHolder.item_dimension_container)) {
					if (!this.moveItemStackTo(itemstack1, 1, 2, true)) {
						if (!this.moveItemStackTo(itemstack1, this.slots.size() - 9, this.slots.size(), false)) {
							return ItemStack.EMPTY;
						}
					}
				} else {
					if (!this.moveItemStackTo(itemstack1, this.slots.size() - 9, this.slots.size(), false)) {
						return ItemStack.EMPTY;
					}
				}
			} else {
				if (itemstack.getItem().equals(ModObjectHolder.item_dimension_container_unlinked)) {
					if (!this.moveItemStackTo(itemstack1, 0, 1, true)) {
						if (!this.moveItemStackTo(itemstack1, 3, this.slots.size() - 9, false)) {
							return ItemStack.EMPTY;
						}
					}
				} else if (itemstack.getItem().equals(ModObjectHolder.item_dimension_container)) {
					if (!this.moveItemStackTo(itemstack1, 1, 2, true)) {
						if (!this.moveItemStackTo(itemstack1, 3, this.slots.size() - 9, false)) {
							return ItemStack.EMPTY;
						}
					}
				} else {
					if (!this.moveItemStackTo(itemstack1, 3, this.slots.size() - 9, false)) {
						return ItemStack.EMPTY;
					}
				}
			}
			
			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(playerIn, itemstack1);
		}

		return !itemstack.isEmpty() ? itemstack : ItemStack.EMPTY;
	}
}