package com.tcn.cosmosportals.core.blockentity;

import com.tcn.cosmoslibrary.common.enums.EnumUIHelp;
import com.tcn.cosmoslibrary.common.enums.EnumUILock;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockNotifier;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBlockEntityUIMode;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmosportals.client.container.ContainerContainerWorkbench;
import com.tcn.cosmosportals.core.block.BlockContainerWorkbench;
import com.tcn.cosmosportals.core.item.ItemPortalContainer;
import com.tcn.cosmosportals.core.management.ModObjectHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

@SuppressWarnings({ "unused" })
public class BlockEntityContainerWorkbench extends BlockEntity implements IBlockNotifier, IBlockInteract, Container, MenuProvider, IBlockEntityUIMode {

	NonNullList<ItemStack> inventoryItems = NonNullList.<ItemStack>withSize(2, ItemStack.EMPTY);

	public ComponentColour customColour = ComponentColour.EMPTY;
	public String display_name = "";
	
	private EnumUIMode uiMode = EnumUIMode.DARK;
	private EnumUIHelp uiHelp = EnumUIHelp.HIDDEN;
	private EnumUILock uiLock = EnumUILock.PRIVATE;

	public BlockEntityContainerWorkbench(BlockPos posIn, BlockState stateIn) {
		super(ModObjectHolder.tile_container_workbench, posIn, stateIn);
	}

	public void sendUpdates(boolean update) {
		if (level != null) {
			this.setChanged();
			BlockState state = this.getBlockState();
			BlockContainerWorkbench block = (BlockContainerWorkbench) state.getBlock();
			
			level.sendBlockUpdated(this.getBlockPos(), state, state, 3);
			
			if (update) {
				if (!level.isClientSide) {
					level.setBlockAndUpdate(this.getBlockPos(), state.updateShape(Direction.DOWN, state, level, worldPosition, worldPosition));
				}
			}
		}
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);

		ContainerHelper.saveAllItems(compound, this.inventoryItems);

		compound.putInt("customColour", this.customColour.getIndex());
		compound.putString("display_name", display_name);
		
		compound.putInt("ui_mode", this.uiMode.getIndex());
		compound.putInt("ui_help", this.uiHelp.getIndex());
		compound.putInt("ui_lock", this.uiLock.getIndex());
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);

		this.inventoryItems = NonNullList.<ItemStack>withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(compound, this.inventoryItems);

		this.customColour = ComponentColour.fromIndex(compound.getInt("customColour"));
		this.display_name = compound.getString("display_name");
		
		this.uiMode = EnumUIMode.getStateFromIndex(compound.getInt("ui_mode"));
		this.uiHelp = EnumUIHelp.getStateFromIndex(compound.getInt("ui_help"));
		this.uiLock = EnumUILock.getStateFromIndex(compound.getInt("ui_lock"));
	}

	//Set the data once it has been received. [NBT > TE]
	@Override
	public void handleUpdateTag(CompoundTag tag) {
		this.load(tag);
		
		//this.sendUpdates(true);
	}
	
	//Retrieve the data to be stored. [TE > NBT]
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		this.saveAdditional(tag);
		return tag;
	}
	
	//Actually sends the data to the server. [NBT > SER]
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	//Method is called once packet has been received by the client. [SER > CLT]
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		CompoundTag tag_ = pkt.getTag();
		
		this.handleUpdateTag(tag_);
	}

	@Override
	public void onLoad() { }

	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, BlockEntityContainerWorkbench entityIn) { }

	@Override
	public void setChanged() {
		super.setChanged();
		
		if (!this.getItem(1).isEmpty()) {
			if (this.getItem(1).getItem() instanceof ItemPortalContainer) {
				ItemPortalContainer item = (ItemPortalContainer) this.getItem(1).getItem();
				
				this.display_name = item.getContainerDisplayName(this.getItem(1));
			}
		} else {
			this.display_name = "";
		}
	}
	
	@Override
	public void attack(BlockState state, Level levelIn, BlockPos pos, Player player) { }

	@Override
	public InteractionResult use(BlockState state, Level levelIn, BlockPos posIn, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		ItemStack stackIn = playerIn.getItemInHand(handIn);
		if (levelIn.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			if (playerIn instanceof ServerPlayer) {
				NetworkHooks.openScreen((ServerPlayer)playerIn, this, posIn);
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.FAIL;
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) { }
	
	@Override
	public void playerWillDestroy(Level levelIn, BlockPos posIn, BlockState stateIn, Player playerIn) {
		if (!levelIn.isClientSide) {
			if (!playerIn.getInventory().add(this.getItem(0))) {
				ItemEntity entity = new ItemEntity(levelIn, posIn.getX(), posIn.getY(), posIn.getZ(), this.getItem(0));
				entity.setPickUpDelay(50);
				
				levelIn.addFreshEntity(entity);
			}
			if (!playerIn.getInventory().add(this.getItem(1))) {
				ItemEntity entity = new ItemEntity(levelIn, posIn.getX(), posIn.getY(), posIn.getZ(), this.getItem(1));
				entity.setPickUpDelay(50);
				
				levelIn.addFreshEntity(entity);
			}
		}
	}

	@Override
	public void setPlacedBy(Level levelIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) { }

	@Override
	public void onPlace(BlockState state, Level levelIn, BlockPos pos, BlockState oldState, boolean isMoving) { }
	
	@Override
	public int getContainerSize() {
		return this.inventoryItems.size();
	}

	@Override
	public ItemStack getItem(int index) {
		return this.inventoryItems.get(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		this.sendUpdates(false);
		return ContainerHelper.removeItem(this.inventoryItems, index, count);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		this.sendUpdates(false);
		return ContainerHelper.takeItem(this.inventoryItems, index);
	}
	
	@Override
	public int getMaxStackSize() {
		return 16;
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		this.inventoryItems.set(index, stack);
		
		if (stack.getCount() > this.getMaxStackSize()) {
			stack.setCount(this.getMaxStackSize());
		}
		this.sendUpdates(false);
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return true;
	}
	
	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.inventoryItems) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void clearContent() { }
	
	@Override
	public Component getDisplayName() {
		return ComponentHelper.title("cosmosportals.gui.workbench");
	}
	
	@Override
	public AbstractContainerMenu createMenu(int idIn, Inventory playerInventoryIn, Player playerIn) {
		return new ContainerContainerWorkbench(idIn, playerInventoryIn, this, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()), this.getBlockPos());
	}
	
	@Override
	public EnumUIMode getUIMode() {
		return this.uiMode;
	}

	@Override
	public void setUIMode(EnumUIMode modeIn) {
		this.uiMode = modeIn;
	}

	@Override
	public void cycleUIMode() {
		this.uiMode = EnumUIMode.getNextStateFromState(this.uiMode);
	}

	@Override
	public EnumUIHelp getUIHelp() {
		return this.uiHelp;
	}

	@Override
	public void setUIHelp(EnumUIHelp modeIn) {
		this.uiHelp = modeIn;
	}

	@Override
	public void cycleUIHelp() {
		this.uiHelp = EnumUIHelp.getNextStateFromState(this.uiHelp);
	}

	@Override
	public EnumUILock getUILock() {
		return this.uiLock;
	}

	@Override
	public void setUILock(EnumUILock modeIn) { }

	@Override
	public void cycleUILock() { }

	@Override
	public void setOwner(Player playerIn) { }

	@Override
	public boolean checkIfOwner(Player playerIn) {
		return true;
	}

	@Override
	public boolean canPlayerAccess(Player playerIn) {
		return this.checkIfOwner(playerIn);
	}

	public String getContainerDisplayName() {
		if (!this.getItem(1).isEmpty()) {
			ItemStack stack = this.getItem(1);
			
			if (stack.getItem() instanceof ItemPortalContainer) {
				ItemPortalContainer item = (ItemPortalContainer) stack.getItem();
				
				return item.getContainerDisplayName(stack);
			}
		}
		
		return "";
	}

	public void setContainerDisplayName(String nameIn) {
		if (!this.getItem(1).isEmpty()) {
			ItemStack stack = this.getItem(1);
			
			if (stack.getItem() instanceof ItemPortalContainer) {
				ItemPortalContainer item = (ItemPortalContainer) stack.getItem();
				
				item.setContainerDisplayData(stack, nameIn, -1);
			}
		}
	}

	public int getContainerDisplayColour() {
		if (!this.getItem(1).isEmpty()) {
			ItemStack stack = this.getItem(1);
			
			if (stack.getItem() instanceof ItemPortalContainer) {
				ItemPortalContainer item = (ItemPortalContainer) stack.getItem();
				
				return item.getContainerDisplayColour(stack);
			}
		}
		
		return -1;
	}
	
	public void setContainerDisplayColour(int colourIn) {
		if (!this.getItem(1).isEmpty()) {
			ItemStack stack = this.getItem(1);
			
			if (stack.getItem() instanceof ItemPortalContainer) {
				ItemPortalContainer item = (ItemPortalContainer) stack.getItem();
				
				item.setContainerDisplayData(stack, null, colourIn);
			}
		}
	}

	public void updateColour(ComponentColour colourIn) {
		if (colourIn.isEmpty()) {
			this.customColour = ComponentColour.EMPTY;
		} else {
			this.customColour = colourIn;
		}
		
		this.setContainerDisplayColour(colourIn.dec());
	}

	public ComponentColour getCustomColour() {
		return this.customColour;
	}
	
	public void setCustomColour(ComponentColour colourIn) {
		this.customColour = colourIn;
	}
	
}