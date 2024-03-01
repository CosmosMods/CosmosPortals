package com.tcn.cosmosportals.core.blockentity;

import java.util.LinkedHashMap;
import java.util.Optional;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.common.enums.EnumAllowedEntities;
import com.tcn.cosmoslibrary.common.enums.EnumUIHelp;
import com.tcn.cosmoslibrary.common.enums.EnumUILock;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockNotifier;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBlockEntityUIMode;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.registry.gson.object.ObjectDestinationInfo;
import com.tcn.cosmoslibrary.registry.gson.object.ObjectPlayerInformation;
import com.tcn.cosmosportals.client.container.ContainerPortalDockUpgraded;
import com.tcn.cosmosportals.core.block.AbstractBlockPortalDock;
import com.tcn.cosmosportals.core.item.ItemPortalContainer;
import com.tcn.cosmosportals.core.management.CoreSoundManager;
import com.tcn.cosmosportals.core.management.ModObjectHolder;
import com.tcn.cosmosportals.core.portal.CustomPortalShape;
import com.tcn.cosmosportals.core.portal.EnumPortalSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

@SuppressWarnings({ "unused" })
public abstract class AbstractBlockEntityPortalDock extends BlockEntity implements IBlockNotifier, IBlockInteract, Container, MenuProvider, Nameable, IBlockEntityUIMode {

	NonNullList<ItemStack> inventoryItems;

	private ObjectPlayerInformation owner;
	public ObjectDestinationInfo destInfo = new ObjectDestinationInfo(BlockPos.ZERO, 0, 0);
	public ResourceLocation destDimension = new ResourceLocation("");
	
	public ComponentColour[] customColours = new ComponentColour[] { ComponentColour.EMPTY, ComponentColour.EMPTY, ComponentColour.EMPTY, ComponentColour.EMPTY };
	
	public boolean isPortalFormed = false;
	public boolean renderLabel = true;
	public boolean playSound = true;
	public boolean showParticles = true;
	public boolean playPowerDownSound = false;
	
	public int currentSlotIndex = 0;
	public int maxSlotIndex = 0;
	private boolean currentlyChanging = false;
	
	public EnumAllowedEntities allowedEntities = EnumAllowedEntities.ALL;

	private EnumUIMode uiMode = EnumUIMode.DARK;
	private EnumUIHelp uiHelp = EnumUIHelp.HIDDEN;
	private EnumUILock uiLock = EnumUILock.PRIVATE;

	public AbstractBlockEntityPortalDock(BlockEntityType<?> typeIn, BlockPos posIn, BlockState stateIn, int maxSlotIndex) {
		super(typeIn, posIn, stateIn);
		
		this.maxSlotIndex = maxSlotIndex;
		
		this.inventoryItems = NonNullList.<ItemStack>withSize(maxSlotIndex + 1, ItemStack.EMPTY);
	}

	public void sendUpdates(boolean update) {
		if (level != null) {
			this.setChanged();
			BlockState state = this.getBlockState();
			AbstractBlockPortalDock block = (AbstractBlockPortalDock) state.getBlock();
			
			level.sendBlockUpdated(this.getBlockPos(), state, state, 3);
			
			if (update) {
				if (!level.isClientSide) {
					level.setBlockAndUpdate(this.getBlockPos(), state.updateShape(Direction.DOWN, state, level, worldPosition, worldPosition));
				}
			}
		}
	}

	public void setDestDimension(ResourceLocation type) {
		this.destDimension = type;
	}
	
	public ResourceKey<Level> getDestDimension() {
		return ResourceKey.create(Registries.DIMENSION, this.destDimension);
	}

	public BlockPos getDestPos() {
		return destInfo.getPos();
	}

	public void updateDestPos(BlockPos pos) {
		this.destInfo.setPos(pos);
	}

	public void setDestInfo(BlockPos posIn, float yawIn, float pitchIn) {
		this.destInfo = new ObjectDestinationInfo(posIn, yawIn, pitchIn);
	}
	
	public int getDisplayColour() {
		if (this.customColours[this.currentSlotIndex].isEmpty()) {
			return this.getContainerDisplayColour();
		} else {
			return this.customColours[this.currentSlotIndex].dec();
		}
	}
	
	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);

		ContainerHelper.saveAllItems(compound, this.inventoryItems);
		compound.putString("namespace", this.destDimension.getNamespace());
		compound.putString("path", this.destDimension.getPath());
		this.destInfo.writeToNBT(compound);
		
		compound.putBoolean("portalFormed", this.isPortalFormed);
		compound.putBoolean("renderLabel", this.renderLabel);
		compound.putBoolean("playSound", this.playSound);
		compound.putInt("allowedEntities", this.allowedEntities.getIndex());
		compound.putBoolean("showParticles", this.showParticles);
		compound.putBoolean("playPowerDownSound", this.playPowerDownSound);
		compound.putInt("currentSlot", this.currentSlotIndex);
		
		compound.putInt("ui_mode", this.uiMode.getIndex());
		compound.putInt("ui_help", this.uiHelp.getIndex());
		compound.putInt("ui_lock", this.uiLock.getIndex());
		
		for (int i = 0; i <= this.getMaxSlotIndex(); i++) {
			compound.putInt("customColour" + i, this.customColours[i].getIndex());
		}
		
		if (this.owner != null) {
			this.owner.writeToNBT(compound, "owner");
		}
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);

		this.inventoryItems = NonNullList.<ItemStack>withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(compound, this.inventoryItems);

		String namespace = compound.getString("namespace");
		String path = compound.getString("path");
		
		this.destDimension = new ResourceLocation(namespace, path);
		this.destInfo = ObjectDestinationInfo.readFromNBT(compound);
		
		this.isPortalFormed = compound.getBoolean("portalFormed");
		this.renderLabel = compound.getBoolean("renderLabel");
		this.playSound = compound.getBoolean("playSound");
		this.allowedEntities = EnumAllowedEntities.getStateFromIndex(compound.getInt("allowedEntities"));
		this.showParticles = compound.getBoolean("showParticles");
		this.playPowerDownSound = compound.getBoolean("playPowerDownSound");
		
		this.currentSlotIndex = compound.getInt("currentSlot");
		
		this.uiMode = EnumUIMode.getStateFromIndex(compound.getInt("ui_mode"));
		this.uiHelp = EnumUIHelp.getStateFromIndex(compound.getInt("ui_help"));
		this.uiLock = EnumUILock.getStateFromIndex(compound.getInt("ui_lock"));
		
		for (int i = 0; i <= this.getMaxSlotIndex(); i++) {
			this.customColours[i] = ComponentColour.fromIndex(compound.getInt("customColour" + i));
		}
		
		if (compound.contains("owner")) {
			this.owner = ObjectPlayerInformation.readFromNBT(compound, "owner");
		}
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

	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, AbstractBlockEntityPortalDock entityIn) {
		if (entityIn.playPowerDownSound && entityIn.playSound) {
			entityIn.playPowerDownSound = false;
			levelIn.playLocalSound(posIn.getX(), posIn.getY(), posIn.getZ(), CoreSoundManager.PORTAL_DESTROY.get(), SoundSource.BLOCKS, 0.4F, 1.0F, false);
		} else {
			entityIn.playPowerDownSound = false;
		}
		
		if (entityIn.currentlyChanging) {
			entityIn.selectNextSlot(true);
			entityIn.currentlyChanging = false;
		}
	}

	@Override
	public void attack(BlockState state, Level levelIn, BlockPos pos, Player player) { }

	@Override
	public InteractionResult use(BlockState state, Level levelIn, BlockPos posIn, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		ItemStack stackIn = playerIn.getItemInHand(handIn);
		
		int slotFull = this.getNextSlotItem(false);
		int slotEmpty = this.getNextSlotItem(true);

		System.out.println("SlotEmpty [" + slotEmpty + "] || SlotFull [" + slotFull + "]");
		System.out.println("EmptyHand: " + stackIn.isEmpty());
		
		if(playerIn.isShiftKeyDown()) {
			if (stackIn.getItem().equals(ModObjectHolder.item_dimension_container) && this.getItem(slotEmpty).isEmpty() && !(stackIn.isEmpty())) {
				ItemPortalContainer item = (ItemPortalContainer) stackIn.getItem();
				
				this.setItem(slotEmpty, stackIn.copy());
				
				if (!playerIn.isCreative()) {
					stackIn.shrink(1);
				}
				
				this.sendUpdates(true);
				
				return InteractionResult.SUCCESS;
			} else if (!(this.getItem(slotFull).isEmpty())) {
				System.out.println("NOT HERE");
				this.destroyPortalClean();

				if (this.isPortalFormed && this.playSound) {
					levelIn.playLocalSound(posIn.getX() + 0.5D, posIn.getY() + 0.5D, posIn.getZ() + 0.5D, CoreSoundManager.PORTAL_DESTROY.get(), SoundSource.BLOCKS, 0.4F, 1.0F, false);
				}
				
				if (!playerIn.isCreative() || playerIn.getItemInHand(handIn).isEmpty()) {
					playerIn.addItem(this.getItem(slotFull).copy());
					
					if (this.playSound) {
						levelIn.playLocalSound(posIn.getX() + 0.5D, posIn.getY() + 0.5D, posIn.getZ() + 0.5D, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.4F, 1.0F, false);
					}
				}
				
				this.setItem(slotFull, ItemStack.EMPTY);
	
				this.destDimension = new ResourceLocation("");
				this.destInfo = new ObjectDestinationInfo(BlockPos.ZERO, 0, 0);
				this.isPortalFormed = false;
				this.sendUpdates(true);

				return InteractionResult.SUCCESS;
			}
		} else {
			if (levelIn.isClientSide) {
				return InteractionResult.SUCCESS;
			} else {
				if (playerIn instanceof ServerPlayer) {
					if (this.canPlayerAccess(playerIn)) {
						NetworkHooks.openScreen((ServerPlayer)playerIn, this, posIn);
						return InteractionResult.SUCCESS;
					}
				}
			}
		}
		return InteractionResult.FAIL;
	}

	@Override
	public void playerWillDestroy(Level levelIn, BlockPos posIn, BlockState stateIn, Player playerIn) {
		if (!levelIn.isClientSide) {
			
			for (int i = 0; i < this.getMaxSlotIndex() +1; i++) {
				ItemEntity entity = new ItemEntity(levelIn, posIn.getX(), posIn.getY(), posIn.getZ(), this.getItem(i));
				entity.setPickUpDelay(50);
				
				levelIn.addFreshEntity(entity);
			}
		}
	}

	@Override
	public void setPlacedBy(Level levelIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) { 
		if (!levelIn.isClientSide) {
			if (placer instanceof Player) {
				Player player = (Player) placer;
				this.setOwner(player);
			}
		}
	}

	@Override
	public void onPlace(BlockState state, Level levelIn, BlockPos pos, BlockState oldState, boolean isMoving) { }
	
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, Level levelIn, BlockPos posIn, RandomSource randIn) {
		if (this.playSound && this.isPortalFormed) {
			if (randIn.nextInt(100) == 0) {
				levelIn.playLocalSound(posIn.getX() + 0.5D, posIn.getY() + 0.5D, posIn.getZ() + 0.5D, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5F, randIn.nextFloat() * 0.4F + 0.8F, false);
			}
		}
	}
	
	@Override
	public void neighborChanged(BlockState state, Level levelIn, BlockPos posIn, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (!this.updatePortalBlocks(EnumPortalSettings.NONE, false, null, -1) && this.isPortalFormed) {
			this.isPortalFormed = false;
			this.playPowerDownSound = true;
			
			this.sendUpdates(true);
		}
		
		if (levelIn.hasNeighborSignal(posIn)) {
			this.currentlyChanging = true;
		}
	}
	
	public boolean createPortal(Level levelIn, BlockPos posIn, ResourceLocation dimensionIn, BlockPos teleportPos, float yawIn, float pitchIn, int colourIn) {
		for (Direction c : Direction.values()) {
			Optional<CustomPortalShape> optional = CustomPortalShape.findEmptyPortalShape(levelIn, posIn.offset(c.getNormal()), Direction.Axis.Z);
			
			if (optional.isPresent()) {
				if (!dimensionIn.getNamespace().isEmpty()) {
					optional.get().createPortalBlocks(levelIn, dimensionIn, teleportPos, yawIn, pitchIn, this.customColours[this.currentSlotIndex].isEmpty() ? colourIn : this.getDisplayColour(), this.playSound, this.allowedEntities, this.showParticles);
					this.isPortalFormed = true;
					this.sendUpdates(true);
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean destroyPortal(Level levelIn, BlockPos posIn) {
		for (Direction c : Direction.values()) {
			BlockEntity tile = levelIn.getBlockEntity(posIn.offset(c.getNormal()));
			
			if (tile instanceof BlockEntityPortal) {
				BlockEntityPortal portalTile = (BlockEntityPortal) tile;

				if (portalTile.getDestDimension().location() == this.getDestDimension().location()) {
					if (portalTile.getDestPos().equals(this.destInfo.getPos())) {
						levelIn.setBlockAndUpdate(tile.getBlockPos(), Blocks.AIR.defaultBlockState());
						
						this.isPortalFormed = false;
						return true;
					}
				}
			}
		}
		
		return false;
	}

	public boolean updatePortalBlocks(EnumPortalSettings settingsIn, boolean value, @Nullable EnumAllowedEntities entities, int colourIn) {
		for (Direction c : Direction.values()) {
			Optional<CustomPortalShape> optional = CustomPortalShape.findPortalShape(this.getLevel(), this.getBlockPos().offset(c.getNormal()), (portalSize) -> { return portalSize.isValid(); }, Direction.Axis.Z);
			
			if (optional.isPresent()) {
				LinkedHashMap<Integer, BlockPos> blockMap = optional.get().getPortalBlocks(this.getLevel(), destDimension);
				
				if (blockMap.size() < 2) {
					return false;
				}
				
				for (int i = 0; i < blockMap.size(); i++) {
					BlockPos testPos = blockMap.get(i);
					
					BlockEntity testTile = this.getLevel().getBlockEntity(testPos);

					if (testTile instanceof BlockEntityPortal) {
						BlockEntityPortal tileEntity = (BlockEntityPortal) testTile;
						
						if (tileEntity.destDimension.equals(this.destDimension) && tileEntity.getDestPos().equals(this.getDestPos())) {
							switch (settingsIn) {
								case PLAY_SOUNDS:
									tileEntity.setPlaySound(value);
									break;
								case ALLOWED_ENTITIES:
									tileEntity.setAllowedEntities(entities);
									break;
								case SHOW_PARTICLES:
									tileEntity.setShowParticles(value);
									break;
								case DISPLAY_COLOUR:
									if (colourIn > -1) {
										tileEntity.setDisplayColour(colourIn);
									}
									break;
								case NONE:
									break;
								default:
									break;
							}
						}
						
						tileEntity.sendUpdates(true);
					}
				}
				return true;
			}
		}
		
		return false;
	}
	
	public void toggleRenderLabel() {
		this.renderLabel = !this.renderLabel;
	}

	public void togglePlaySound() {
		this.playSound = !this.playSound;
		
		this.updatePortalBlocks(EnumPortalSettings.PLAY_SOUNDS, this.playSound, null, -1);
	}

	public void toggleEntities(boolean reverse) {
		if (!reverse) {
			this.allowedEntities = EnumAllowedEntities.getNextState(this.allowedEntities);
		} else {
			this.allowedEntities = EnumAllowedEntities.getNextStateReverse(this.allowedEntities);
		}
		
		this.updatePortalBlocks(EnumPortalSettings.ALLOWED_ENTITIES, false, this.allowedEntities, -1);
	}
	
	public void toggleParticles() {
		this.showParticles = !this.showParticles;
		
		this.updatePortalBlocks(EnumPortalSettings.SHOW_PARTICLES, this.showParticles, null, -1);
	}
	
	public void updateColour(ComponentColour colourIn) {
		if (colourIn.isEmpty()) {
			this.customColours[this.currentSlotIndex] = ComponentColour.EMPTY;
			
			this.updatePortalBlocks(EnumPortalSettings.DISPLAY_COLOUR, false, null, this.getDisplayColour());
		} else {
			this.customColours[this.currentSlotIndex] = colourIn;
			
			this.updatePortalBlocks(EnumPortalSettings.DISPLAY_COLOUR, false, null, colourIn.dec());
		}
	}
	
	@Override
	public void setChanged() {
		super.setChanged();		
	}
	
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
		return ContainerHelper.removeItem(this.inventoryItems, index, count);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return ContainerHelper.takeItem(this.inventoryItems, index);
	}
	
	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		this.inventoryItems.set(index, stack);
		
		if (stack.getCount() > this.getMaxStackSize()) {
			stack.setCount(this.getMaxStackSize());
		}
		
		if (this.getCurrentSlotIndex() == index) {
			this.updatePortalBasedOnSlot();
		}
		
		this.setChanged();
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
	public void setUILock(EnumUILock modeIn) {
		this.uiLock = modeIn;
	}

	@Override
	public void cycleUILock() {
		this.uiLock = EnumUILock.getNextStateFromState(this.uiLock);
	}

	@Override
	public void setOwner(Player playerIn) {
		this.owner = new ObjectPlayerInformation(playerIn);
	}

	@Override
	public boolean checkIfOwner(Player playerIn) {
		if (this.owner != null) {
			return this.owner.getPlayerUUID().equals(playerIn.getUUID());
		}
		return true;
	}

	@Override
	public boolean canPlayerAccess(Player playerIn) {
		if (this.getUILock().equals(EnumUILock.PUBLIC)) {
			return true;
		}
		return this.checkIfOwner(playerIn);
	}
	
	public ComponentColour getCustomColour() {
		return this.customColours[this.currentSlotIndex];
	}
	
	public void setCustomColour(ComponentColour colourIn) {
		this.customColours[this.currentSlotIndex] = colourIn;
	}
	
	public String getContainerDisplayName() {
		if (!this.getItem(this.getCurrentSlotIndex()).isEmpty()) {
			ItemStack stack = this.getItem(this.getCurrentSlotIndex());
			
			if (stack.getItem() instanceof ItemPortalContainer) {
				ItemPortalContainer item = (ItemPortalContainer) stack.getItem();
				String displayName = item.getContainerDisplayName(stack);
				
				if (displayName.length() > 12) {
					 displayName = displayName.substring(0, Math.min(displayName.length(), 12));
				}
				
				return this.maxSlotIndex > 0 ? "[" + (this.getCurrentSlotIndex() + 1) + "]: " + displayName : displayName;
			}
		}
		
		return "";
	}

	public int getContainerDisplayColour() {
		if (!this.getItem(this.getCurrentSlotIndex()).isEmpty()) {
			ItemStack stack = this.getItem(this.getCurrentSlotIndex());
			
			if (stack.getItem() instanceof ItemPortalContainer) {
				ItemPortalContainer item = (ItemPortalContainer) stack.getItem();
				
				return item.getContainerDisplayColour(stack);
			}
		}
		
		return -1;
	}
	
	public void updatePortalBasedOnSlot() {
		int slot = this.currentSlotIndex;
		
		if (!getItem(slot).isEmpty()) {
			this.destroyPortalClean();
			
			ItemStack stack = this.getItem(slot);
			ItemPortalContainer item = (ItemPortalContainer) stack.getItem();
			
			if (stack.hasTag()) {
				CompoundTag stack_tag = stack.getTag();
				
				if (stack_tag.contains("nbt_data")) {
					CompoundTag nbt_data = stack_tag.getCompound("nbt_data");
					
					if (nbt_data.contains("position_data") && nbt_data.contains("dimension_data")) {
						CompoundTag position_data = nbt_data.getCompound("position_data");
						CompoundTag dimension_data = nbt_data.getCompound("dimension_data");

						String namespace = dimension_data.getString("namespace");
						String path = dimension_data.getString("path");
						ResourceLocation dimension = new ResourceLocation(namespace, path);
						int colour = item.getContainerDisplayColour(stack);
						
						int[] position = new int [] { position_data.getInt("pos_x"), position_data.getInt("pos_y"), position_data.getInt("pos_z") };
						float[] rotation = new float[] { position_data.getFloat("pos_yaw"), position_data.getFloat("pos_pitch") };

						if (this.createPortal(this.getLevel(), this.getBlockPos(), dimension, new BlockPos(position[0], position[1], position[2]), rotation[0], rotation[1], colour)) {
							
							if (this.playSound) {
								this.getLevel().playLocalSound(this.getBlockPos().getX() + 0.5D, this.getBlockPos().getY() + 0.5D, this.getBlockPos().getZ() + 0.5D, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.4F, 1.0F, false);
								this.getLevel().playLocalSound(this.getBlockPos().getX() + 0.5D, this.getBlockPos().getY() + 0.5D, this.getBlockPos().getZ() + 0.5D, CoreSoundManager.PORTAL_CREATE.get(), SoundSource.BLOCKS, 0.4F, 1.0F, false);
							}
							
							this.setDestDimension(dimension);
							this.setDestInfo(new BlockPos(position[0], position[1], position[2]), rotation[0], rotation[1]);
							
							this.isPortalFormed = true;
							this.sendUpdates(true);
						}
					}
				}
			}
		} else {
			this.destroyPortalClean();
		}
	}
	
	public void destroyPortalClean() {
		this.destroyPortal(level, worldPosition);

		this.destDimension = new ResourceLocation("");
		this.destInfo = new ObjectDestinationInfo(BlockPos.ZERO, 0, 0);
		this.isPortalFormed = false;
		//this.setCustomColour(ComponentColour.EMPTY); Need to not do this to preserve Custom Slot Colours.
		this.sendUpdates(true);
	}
	
	public int getCurrentSlotIndex() {
		return this.currentSlotIndex;
	}
	
	public int getMaxSlotIndex() {
		return this.maxSlotIndex;
	}
	
	public void setCurrentSlot(int newSlot) {
		if (newSlot >= 0 && newSlot <= this.maxSlotIndex) {
			this.currentSlotIndex = newSlot;
			this.updatePortalBasedOnSlot();
		}
	}
	
	public void selectNextSlot(boolean forward) {
		if (this.getMaxSlotIndex() > 1) {
			if (forward) {
				if (this.currentSlotIndex < this.maxSlotIndex) {
					this.currentSlotIndex++;
					this.updatePortalBasedOnSlot();
				} else {
					this.currentSlotIndex = 0;
					this.updatePortalBasedOnSlot();
				}
			} else {
				if (this.currentSlotIndex > 0) {
					this.currentSlotIndex--;
					this.updatePortalBasedOnSlot();
				} else {
					this.currentSlotIndex = this.maxSlotIndex;
					this.updatePortalBasedOnSlot();
				}
			}
		}
	}
	
	public int getNextSlotItem(boolean isEmpty) {
		for (int i = 0; i <= this.maxSlotIndex; i++) {
			ItemStack testStack = this.getItem(i);
			
			if (isEmpty == testStack.isEmpty()) {
				return i;
			}
		}
		
		return 0;
	}

	@Override
	public Component getDisplayName() {
		return null;
	}
}