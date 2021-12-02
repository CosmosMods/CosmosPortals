package com.tcn.cosmosportals.core.blockentity;

import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Random;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.common.comp.CosmosColour;
import com.tcn.cosmoslibrary.common.comp.CosmosCompHelper;
import com.tcn.cosmoslibrary.common.enums.EnumAllowedEntities;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.registry.gson.object.ObjectDestinationInfo;
import com.tcn.cosmosportals.CosmosPortals;
import com.tcn.cosmosportals.client.container.ContainerPortalDock;
import com.tcn.cosmosportals.core.block.BlockPortalDock;
import com.tcn.cosmosportals.core.management.CoreSoundManager;
import com.tcn.cosmosportals.core.management.ModBusManager;
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
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
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
import net.minecraftforge.fmllegacy.network.NetworkHooks;

@SuppressWarnings("unused")
public class BlockEntityPortalDock extends BlockEntity implements IBlockInteract, Container, MenuProvider, Nameable {

	NonNullList<ItemStack> inventoryItems = NonNullList.<ItemStack>withSize(1, ItemStack.EMPTY);

	public ObjectDestinationInfo destInfo = new ObjectDestinationInfo(BlockPos.ZERO, 0, 0);
	public ResourceLocation destDimension = new ResourceLocation("");
	public int display_colour = CosmosColour.WHITE.dec();
	
	public boolean isPortalFormed = false;
	
	public boolean renderLabel = true;
	public boolean playSound = true;
	//public boolean allowEntities = true;
	public boolean showParticles = true;
	
	public boolean playPowerDownSound = false;
	
	public EnumAllowedEntities allowedEntities = EnumAllowedEntities.ALL;
	
	public BlockEntityPortalDock(BlockPos posIn, BlockState stateIn) {
		super(ModBusManager.BLOCK_ENTITY_TYPE_DOCK, posIn, stateIn);
	}

	public void sendUpdates(boolean update) {
		if (level != null) {
			this.setChanged();
			BlockState state = this.getBlockState();
			BlockPortalDock block = (BlockPortalDock) state.getBlock();
			
			level.sendBlockUpdated(this.getBlockPos(), state, state, 3);
			
			if (update) {
				if (!level.isClientSide) {
					//level.setBlockAndUpdate(this.getBlockPos(), block.updateState(state, this.getBlockPos(), level));
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
		return this.display_colour;
	}
	
	public void setDisplayColour(int colourIn) {
		if (!(this.display_colour == colourIn)) {
			this.display_colour = colourIn;
		}
	}
	
	public void setDisplayColour(CosmosColour colour) {
		this.display_colour = colour.dec();
	}

	@Override
	public CompoundTag save(CompoundTag compound) {
		super.save(compound);

		ContainerHelper.saveAllItems(compound, this.inventoryItems);
		compound.putString("namespace", this.destDimension.getNamespace());
		compound.putString("path", this.destDimension.getPath());
		this.destInfo.writeToNBT(compound);
		compound.putInt("colour", this.getDisplayColour());
		
		compound.putBoolean("portalFormed", this.isPortalFormed);
		compound.putBoolean("renderLabel", this.renderLabel);
		compound.putBoolean("playSound", this.playSound);
		compound.putInt("allowedEntities", this.allowedEntities.getIndex());
		compound.putBoolean("showParticles", this.showParticles);
		compound.putBoolean("playPowerDownSound", this.playPowerDownSound);
		
		return compound;
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
	}

	//Set the data once it has been received. [NBT > TE]
	@Override
	public void handleUpdateTag(CompoundTag tag) {
		this.load(tag);
	}
	
	//Retrieve the data to be stored. [TE > NBT]
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		this.save(tag);
		return tag;
	}
	
	//Actually sends the data to the server. [NBT > SER]
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return new ClientboundBlockEntityDataPacket(this.getBlockPos(), 0, this.getUpdateTag());
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
			levelIn.playLocalSound(posIn.getX(), posIn.getY(), posIn.getZ(), CoreSoundManager.PORTAL_DESTROY, SoundSource.BLOCKS, 0.4F, 1.0F, false);
		} else {
			entityIn.playPowerDownSound = false;
		}
	}

	@Override
	public void attack(BlockState state, Level worldIn, BlockPos pos, Player player) { }

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos posIn, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		ItemStack stackIn = playerIn.getItemInHand(handIn);
		
		if(playerIn.isShiftKeyDown()) {
			if (stackIn.getItem().equals(ModBusManager.DIMENSION_CONTAINER) && this.getItem(0).isEmpty() && !(stackIn.isEmpty())) {
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
	
							if (this.createPortal(worldIn, posIn, dimension, new BlockPos(position[0], position[1], position[2]), rotation[0], rotation[1], colour)) {
								
								if (this.playSound) {
									worldIn.playLocalSound(posIn.getX() + 0.5D, posIn.getY() + 0.5D, posIn.getZ() + 0.5D, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.4F, 1.0F, false);
									worldIn.playLocalSound(posIn.getX() + 0.5D, posIn.getY() + 0.5D, posIn.getZ() + 0.5D, CoreSoundManager.PORTAL_CREATE, SoundSource.BLOCKS, 0.4F, 1.0F, false);
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
				this.destroyPortal(worldIn, posIn);

				if (this.isPortalFormed && this.playSound) {
					worldIn.playLocalSound(posIn.getX() + 0.5D, posIn.getY() + 0.5D, posIn.getZ() + 0.5D, CoreSoundManager.PORTAL_DESTROY, SoundSource.BLOCKS, 0.4F, 1.0F, false);
				}
				
				if (!playerIn.isCreative() || playerIn.getItemInHand(handIn).isEmpty()) {
					playerIn.addItem(this.getItem(0).copy());
					
					if (this.playSound) {
						worldIn.playLocalSound(posIn.getX() + 0.5D, posIn.getY() + 0.5D, posIn.getZ() + 0.5D, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.4F, 1.0F, false);
					}
				}
				
				this.setItem(0, ItemStack.EMPTY);
	
				this.destDimension = new ResourceLocation("");
				this.destInfo = new ObjectDestinationInfo(BlockPos.ZERO, 0, 0);
				this.setDisplayColour(CosmosColour.WHITE);
				this.isPortalFormed = false;
				this.sendUpdates(true);

				return InteractionResult.SUCCESS;
			}
		} else {
			if (worldIn.isClientSide) {
				return InteractionResult.SUCCESS;
			} else {
				if (playerIn instanceof ServerPlayer) {
					NetworkHooks.openGui((ServerPlayer)playerIn, this, posIn);
					return InteractionResult.SUCCESS;
				}
			}
		}
		return InteractionResult.FAIL;
	}

	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos posIn, Random randIn) {
		if (this.playSound && this.isPortalFormed) {
			if (randIn.nextInt(100) == 0) {
				worldIn.playLocalSound(posIn.getX() + 0.5D, posIn.getY() + 0.5D, posIn.getZ() + 0.5D, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5F, randIn.nextFloat() * 0.4F + 0.8F, false);
			}
		}
	}
	
	public void neighborChanged(BlockState state, Level worldIn, BlockPos posIn, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (!this.updatePortalBlocks(-1, false, null) && this.isPortalFormed) {
			this.isPortalFormed = false;
			this.playPowerDownSound = true;
			
			this.sendUpdates(true);
		}
	}
	
	public boolean createPortal(Level worldIn, BlockPos posIn, ResourceLocation dimensionIn, BlockPos teleportPos, float yawIn, float pitchIn, int colourIn) {
		for (Direction c : Direction.values()) {
			Optional<CustomPortalShape> optional = CustomPortalShape.findEmptyPortalShape(worldIn, posIn.offset(c.getNormal()), Direction.Axis.Z);
			
			if (optional.isPresent()) {
				if (!dimensionIn.getNamespace().isEmpty()) {
					optional.get().createPortalBlocks(worldIn, dimensionIn, teleportPos, yawIn, pitchIn, colourIn, this.playSound, this.allowedEntities, this.showParticles);
					this.isPortalFormed = true;
					this.sendUpdates(true);
					return true;
				}
			}
			
		}
		return false;
	}
	
	public boolean destroyPortal(Level worldIn, BlockPos posIn) {
		for (Direction c : Direction.values()) {
			BlockEntity tile = worldIn.getBlockEntity(posIn.offset(c.getNormal()));
			
			if (tile instanceof BlockEntityPortal) {
				BlockEntityPortal portalTile = (BlockEntityPortal) tile;

				if (portalTile.getDestDimension().location() == this.getDestDimension().location()) {
					if (portalTile.getDestPos().equals(this.destInfo.getPos())) {
						worldIn.setBlockAndUpdate(tile.getBlockPos(), Blocks.AIR.defaultBlockState());
						
						this.isPortalFormed = false;
						return true;
					}
				}
			}
		}
		
		return false;
	}

	public boolean updatePortalBlocks(int id, boolean value, @Nullable EnumAllowedEntities entities) {
		for (Direction c : Direction.values()) {
			Optional<CustomPortalShape> optional = CustomPortalShape.findPortalShape(this.getLevel(), this.getBlockPos().offset(c.getNormal()), (portalSize) -> { return portalSize.isValid(); }, Direction.Axis.Z);
			
			if (optional.isPresent()) {
				LinkedHashMap<Integer, BlockPos> blockMap = optional.get().getPortalBlocks(this.getLevel(), destDimension);
				
				CosmosPortals.CONSOLE.info(blockMap);
				
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
		
		this.updatePortalBlocks(1, this.playSound, null);
	}

	public void toggleEntities() {
		this.allowedEntities = EnumAllowedEntities.getNextState(this.allowedEntities);
		
		this.updatePortalBlocks(2, false, this.allowedEntities);
	}
	
	public void toggleParticles() {
		this.showParticles = !this.showParticles;
		
		this.updatePortalBlocks(3, this.showParticles, null);
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
	public void setItem(int index, ItemStack stack) {
		this.inventoryItems.set(index, stack);
		
		if (stack.getCount() > this.getContainerSize()) {
			stack.setCount(this.getContainerSize());
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
		return CosmosCompHelper.locComp("cosmosportals.gui.dock");
	}
	
	@Override
	public AbstractContainerMenu createMenu(int idIn, Inventory playerInventoryIn, Player playerIn) {
		return new ContainerPortalDock(idIn, playerInventoryIn, this, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()), this.getBlockPos());
	}

	@Override
	public Component getName() {
		return CosmosCompHelper.locComp("cosmosportals.gui.dock");
	}

}