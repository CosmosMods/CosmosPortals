package com.tcn.cosmosportals.core.blockentity;

import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Random;

import javax.annotation.Nullable;

import org.apache.commons.lang3.text.WordUtils;

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
import com.tcn.cosmosportals.client.container.ContainerPortalDock;
import com.tcn.cosmosportals.core.block.BlockPortalDock;
import com.tcn.cosmosportals.core.management.CoreSoundManager;
import com.tcn.cosmosportals.core.management.ModBusManager;
import com.tcn.cosmosportals.core.management.ModObjectHolder;
import com.tcn.cosmosportals.core.portal.CustomPortalShape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

@SuppressWarnings({ "unused", "deprecation" })
public class BlockEntityPortalDock extends BlockEntity implements IBlockNotifier, IBlockInteract, Container, MenuProvider, Nameable, IBlockEntityUIMode {

	NonNullList<ItemStack> inventoryItems = NonNullList.<ItemStack>withSize(1, ItemStack.EMPTY);

	private ObjectPlayerInformation owner;
	public ObjectDestinationInfo destInfo = new ObjectDestinationInfo(BlockPos.ZERO, 0, 0);
	public ResourceLocation destDimension = new ResourceLocation("");
	public String display_name = "";
	public ComponentColour customColour = ComponentColour.EMPTY;
	public int display_colour = ComponentColour.WHITE.dec();
	
	public boolean isPortalFormed = false;
	public boolean renderLabel = true;
	public boolean playSound = true;
	public boolean showParticles = true;
	public boolean playPowerDownSound = false;
	public EnumAllowedEntities allowedEntities = EnumAllowedEntities.ALL;

	private EnumUIMode uiMode = EnumUIMode.DARK;
	private EnumUIHelp uiHelp = EnumUIHelp.HIDDEN;
	private EnumUILock uiLock = EnumUILock.PRIVATE;

	public BlockEntityPortalDock(BlockPos posIn, BlockState stateIn) {
		super(ModObjectHolder.tile_portal_dock, posIn, stateIn);
	}

	public void sendUpdates(boolean update) {
		if (level != null) {
			this.setChanged();
			BlockState state = this.getBlockState();
			BlockPortalDock block = (BlockPortalDock) state.getBlock();
			
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
		return ResourceKey.create(Registry.DIMENSION_REGISTRY, this.destDimension);
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
		if (this.customColour.isEmpty()) {
			return this.display_colour;
		} else {
			return this.customColour.dec();
		}
	}
	
	public void setDisplayColour(int colourIn) {
		if (!(this.display_colour == colourIn)) {
			this.display_colour = colourIn;
		}
	}
	
	public void setDisplayColour(ComponentColour colour) {
		this.display_colour = colour.dec();
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);

		ContainerHelper.saveAllItems(compound, this.inventoryItems);
		compound.putString("namespace", this.destDimension.getNamespace());
		compound.putString("path", this.destDimension.getPath());
		this.destInfo.writeToNBT(compound);
		compound.putInt("colour", this.display_colour);
		
		compound.putBoolean("portalFormed", this.isPortalFormed);
		compound.putBoolean("renderLabel", this.renderLabel);
		compound.putBoolean("playSound", this.playSound);
		compound.putInt("allowedEntities", this.allowedEntities.getIndex());
		compound.putBoolean("showParticles", this.showParticles);
		compound.putBoolean("playPowerDownSound", this.playPowerDownSound);
		
		compound.putInt("ui_mode", this.uiMode.getIndex());
		compound.putInt("ui_help", this.uiHelp.getIndex());
		compound.putInt("ui_lock", this.uiLock.getIndex());
		
		compound.putString("name", this.display_name);
		compound.putInt("customColour", this.customColour.getIndex());
		
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
		this.display_colour = compound.getInt("colour");
		
		this.isPortalFormed = compound.getBoolean("portalFormed");
		this.renderLabel = compound.getBoolean("renderLabel");
		this.playSound = compound.getBoolean("playSound");
		this.allowedEntities = EnumAllowedEntities.getStateFromIndex(compound.getInt("allowedEntities"));
		this.showParticles = compound.getBoolean("showParticles");
		this.playPowerDownSound = compound.getBoolean("playPowerDownSound");

		this.uiMode = EnumUIMode.getStateFromIndex(compound.getInt("ui_mode"));
		this.uiHelp = EnumUIHelp.getStateFromIndex(compound.getInt("ui_help"));
		this.uiLock = EnumUILock.getStateFromIndex(compound.getInt("ui_lock"));
		
		this.display_name = compound.getString("name");
		this.customColour = ComponentColour.fromIndex(compound.getInt("customColour"));
		
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

	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, BlockEntityPortalDock entityIn) {
		if (entityIn.playPowerDownSound && entityIn.playSound) {
			entityIn.playPowerDownSound = false;
			levelIn.playLocalSound(posIn.getX(), posIn.getY(), posIn.getZ(), CoreSoundManager.PORTAL_DESTROY.get(), SoundSource.BLOCKS, 0.4F, 1.0F, false);
		} else {
			entityIn.playPowerDownSound = false;
		}
	}

	@Override
	public void attack(BlockState state, Level levelIn, BlockPos pos, Player player) { }

	@Override
	public InteractionResult use(BlockState state, Level levelIn, BlockPos posIn, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		ItemStack stackIn = playerIn.getItemInHand(handIn);
		
		if(playerIn.isShiftKeyDown()) {
			if (stackIn.getItem().equals(ModObjectHolder.item_dimension_container) && this.getItem(0).isEmpty() && !(stackIn.isEmpty())) {
				if (stackIn.hasTag()) {
					CompoundTag stack_tag = stackIn.getTag();
					
					if (stack_tag.contains("nbt_data")) {
						CompoundTag nbt_data = stack_tag.getCompound("nbt_data");
						
						if (nbt_data.contains("position_data") && nbt_data.contains("dimension_data")) {
							CompoundTag position_data = nbt_data.getCompound("position_data");
							CompoundTag dimension_data = nbt_data.getCompound("dimension_data");
	
							String namespace = dimension_data.getString("namespace");
							String path = dimension_data.getString("path");
							ResourceLocation dimension = new ResourceLocation(namespace, path);
							int colour = dimension_data.getInt("colour");
							
							int[] position = new int [] { position_data.getInt("pos_x"), position_data.getInt("pos_y"), position_data.getInt("pos_z") };
							float[] rotation = new float[] { position_data.getFloat("pos_yaw"), position_data.getFloat("pos_pitch") };
	
							if (this.createPortal(levelIn, posIn, dimension, new BlockPos(position[0], position[1], position[2]), rotation[0], rotation[1], colour)) {
								
								if (this.playSound) {
									levelIn.playLocalSound(posIn.getX() + 0.5D, posIn.getY() + 0.5D, posIn.getZ() + 0.5D, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.4F, 1.0F, false);
									levelIn.playLocalSound(posIn.getX() + 0.5D, posIn.getY() + 0.5D, posIn.getZ() + 0.5D, CoreSoundManager.PORTAL_CREATE.get(), SoundSource.BLOCKS, 0.4F, 1.0F, false);
								}
								
								this.setItem(0, stackIn.copy());
								
								this.inventoryItems.set(0, stackIn.copy());
		
								this.setDestDimension(dimension);
								this.setDestInfo(new BlockPos(position[0], position[1], position[2]), rotation[0], rotation[1]);
								this.setDisplayColour(colour);
								
								this.isPortalFormed = true;
								this.sendUpdates(true);
								
								if (!playerIn.isCreative()) {
									stackIn.shrink(1);
								}
								
								return InteractionResult.SUCCESS;
							}
						}
					}
				}
			} else if (!(this.getItem(0).isEmpty())) {
				this.destroyPortal(levelIn, posIn);

				if (this.isPortalFormed && this.playSound) {
					levelIn.playLocalSound(posIn.getX() + 0.5D, posIn.getY() + 0.5D, posIn.getZ() + 0.5D, CoreSoundManager.PORTAL_DESTROY.get(), SoundSource.BLOCKS, 0.4F, 1.0F, false);
				}
				
				if (!playerIn.isCreative() || playerIn.getItemInHand(handIn).isEmpty()) {
					playerIn.addItem(this.getItem(0).copy());
					
					if (this.playSound) {
						levelIn.playLocalSound(posIn.getX() + 0.5D, posIn.getY() + 0.5D, posIn.getZ() + 0.5D, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.4F, 1.0F, false);
					}
				}
				
				this.setItem(0, ItemStack.EMPTY);
	
				this.destDimension = new ResourceLocation("");
				this.destInfo = new ObjectDestinationInfo(BlockPos.ZERO, 0, 0);
				this.setDisplayColour(ComponentColour.WHITE);
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
			if (!playerIn.getInventory().add(this.getItem(0))) {
				ItemEntity entity = new ItemEntity(levelIn, posIn.getX(), posIn.getY(), posIn.getZ(), this.getItem(0));
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
		if (!this.updatePortalBlocks(-1, false, null, -1) && this.isPortalFormed) {
			this.isPortalFormed = false;
			this.playPowerDownSound = true;
			
			this.sendUpdates(true);
		}
	}
	
	public boolean createPortal(Level levelIn, BlockPos posIn, ResourceLocation dimensionIn, BlockPos teleportPos, float yawIn, float pitchIn, int colourIn) {
		for (Direction c : Direction.values()) {
			Optional<CustomPortalShape> optional = CustomPortalShape.findEmptyPortalShape(levelIn, posIn.offset(c.getNormal()), Direction.Axis.Z);
			
			if (optional.isPresent()) {
				if (!dimensionIn.getNamespace().isEmpty()) {
					optional.get().createPortalBlocks(levelIn, dimensionIn, teleportPos, yawIn, pitchIn, this.customColour.isEmpty() ? colourIn : this.getDisplayColour(), this.playSound, this.allowedEntities, this.showParticles);
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

	public boolean updatePortalBlocks(int id, boolean value, @Nullable EnumAllowedEntities entities, int colourIn) {
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
							if (id == 1) {
								tileEntity.setPlaySound(value);
							} else if (id == 2 && entities != null) {
								tileEntity.setAllowedEntities(entities);
							} else if (id == 3) {
								tileEntity.setShowParticles(value);
							} else if (id == 4) {
								if (colourIn >= 0) {
									tileEntity.setDisplayColour(colourIn);
								}
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
		
		this.updatePortalBlocks(1, this.playSound, null, -1);
	}

	public void toggleEntities() {
		this.allowedEntities = EnumAllowedEntities.getNextState(this.allowedEntities);
		
		this.updatePortalBlocks(2, false, this.allowedEntities, -1);
	}
	
	public void toggleParticles() {
		this.showParticles = !this.showParticles;
		
		this.updatePortalBlocks(3, this.showParticles, null, -1);
	}
	
	public void updateColour(ComponentColour colourIn) {
		if (colourIn.isEmpty()) {
			this.customColour = ComponentColour.EMPTY;
			
			this.updatePortalBlocks(4, false, null, this.display_colour);
		} else {
			this.customColour = colourIn;
			
			this.updatePortalBlocks(4, false, null, colourIn.dec());
		}
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
	
	public boolean hasPortalDisplayName() {
		return !(this.display_name.isEmpty());
	}
	
	public String getPortalDisplayName() {
		if (this.hasPortalDisplayName()) {
			return this.display_name;
		}
		return WordUtils.capitalizeFully(this.destDimension.getPath().replace("_", " "));
	}
	
	public void setPortalDisplayName(String nameIn) {
		this.display_name = nameIn;
	}

	public ComponentColour getCustomColour() {
		return this.customColour;
	}
	
	public void setCustomColour(ComponentColour colourIn) {
		this.customColour = colourIn;
	}
}