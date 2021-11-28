package com.tcn.cosmosportals.core.tileentity;

import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Random;

import com.tcn.cosmoslibrary.common.comp.CosmosColour;
import com.tcn.cosmoslibrary.common.comp.CosmosCompHelper;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.registry.gson.object.ObjectDestinationInfo;
import com.tcn.cosmosportals.client.container.ContainerPortalDock;
import com.tcn.cosmosportals.core.block.BlockPortalDock;
import com.tcn.cosmosportals.core.management.CoreConsole;
import com.tcn.cosmosportals.core.management.CoreModBusManager;
import com.tcn.cosmosportals.core.portal.CustomPortalSize;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

@SuppressWarnings("unused")
public class TileEntityPortalDock extends TileEntity implements IBlockInteract, ITickableTileEntity, IInventory, INamedContainerProvider {

	NonNullList<ItemStack> inventoryItems = NonNullList.<ItemStack>withSize(1, ItemStack.EMPTY);

	public ObjectDestinationInfo destInfo = new ObjectDestinationInfo(BlockPos.ZERO, 0, 0);
	public ResourceLocation destDimension = new ResourceLocation("");
	public int display_colour = CosmosColour.WHITE.dec();
	
	public boolean isPortalFormed = false;
	
	public boolean renderLabel = true;
	public boolean playSound = true;
	public boolean allowEntities = true;
	public boolean showParticles = true;
	
	public TileEntityPortalDock() {
		super(CoreModBusManager.DOCK_TILE_TYPE);
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
	
	public RegistryKey<World> getDestDimension() {
		return RegistryKey.create(Registry.DIMENSION_REGISTRY, this.destDimension);
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
	public CompoundNBT save(CompoundNBT compound) {
		super.save(compound);

		ItemStackHelper.saveAllItems(compound, this.inventoryItems);

		compound.putString("namespace", this.destDimension.getNamespace());
		compound.putString("path", this.destDimension.getPath());
		
		this.destInfo.writeToNBT(compound);
		
		compound.putInt("colour", this.getDisplayColour());
		
		compound.putBoolean("portalFormed", this.isPortalFormed);
		compound.putBoolean("renderLabel", this.renderLabel);
		compound.putBoolean("playSound", this.playSound);
		compound.putBoolean("showParticles", this.showParticles);
		
		return compound;
	}

	@Override
	public void load(BlockState state, CompoundNBT compound) {
		super.load(state, compound);

		this.inventoryItems = NonNullList.<ItemStack>withSize(this.getContainerSize(), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(compound, this.inventoryItems);

		String namespace = compound.getString("namespace");
		String path = compound.getString("path");
		
		this.destDimension = new ResourceLocation(namespace, path);
		this.destInfo = ObjectDestinationInfo.readFromNBT(compound);
		this.display_colour = compound.getInt("colour");
		
		this.isPortalFormed = compound.getBoolean("portalFormed");
		this.renderLabel = compound.getBoolean("renderLabel");
		this.playSound = compound.getBoolean("playSound");
		this.showParticles = compound.getBoolean("showParticles");
	}

	//Set the data once it has been received. [NBT > TE]
	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		this.load(state, tag);
	}
	
	//Retrieve the data to be stored. [TE > NBT]
	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT tag = new CompoundNBT();
		
		this.save(tag);
		
		return tag;
	}
	
	//Actually sends the data to the server. [NBT > SER]
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.getBlockPos(), 0, this.getUpdateTag());
	}
	
	//Method is called once packet has been received by the client. [SER > CLT]
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		super.onDataPacket(net, pkt);
		
		CompoundNBT tag_ = pkt.getTag();
		BlockState state = level.getBlockState(pkt.getPos());
		
		this.handleUpdateTag(state, tag_);
	}

	@Override
	public void onLoad() { }

	@Override
	public void tick() { }

	@Override
	public void attack(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) { }

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand handIn, BlockRayTraceResult hit) {
		ItemStack stackIn = playerIn.getItemInHand(handIn);
		
		if(playerIn.isShiftKeyDown()) {
			if (stackIn.getItem().equals(CoreModBusManager.DIMENSION_CONTAINER) && this.getItem(0).isEmpty() && !(stackIn.isEmpty())) {
				if (stackIn.hasTag()) {
					CompoundNBT stack_tag = stackIn.getTag();
					
					if (stack_tag.contains("nbt_data")) {
						CompoundNBT nbt_data = stack_tag.getCompound("nbt_data");
						
						if (nbt_data.contains("position_data") && nbt_data.contains("dimension_data")) {
							CompoundNBT position_data = nbt_data.getCompound("position_data");
							CompoundNBT dimension_data = nbt_data.getCompound("dimension_data");
	
							String namespace = dimension_data.getString("namespace");
							String path = dimension_data.getString("path");
							ResourceLocation dimension = new ResourceLocation(namespace, path);
							int colour = dimension_data.getInt("colour");
							
							int[] position = new int [] { position_data.getInt("pos_x"), position_data.getInt("pos_y"), position_data.getInt("pos_z") };
							float[] rotation = new float[] { position_data.getFloat("pos_yaw"), position_data.getFloat("pos_pitch") };
	
							if (this.createPortal(worldIn, pos, dimension, new BlockPos(position[0], position[1], position[2]), rotation[0], rotation[1], colour)) {
								
								if (this.playSound) {
									worldIn.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_PICKUP, SoundCategory.BLOCKS, 0.4F, 1.0F, false);
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
								
								return ActionResultType.SUCCESS;
							}
						}
					}
				}
			} else if (!(this.getItem(0).isEmpty())) {
				this.destroyPortal(worldIn, pos);
				
				if (this.playSound) {
					worldIn.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_PICKUP, SoundCategory.BLOCKS, 0.4F, 1.0F, false);
				}
				
				if (!playerIn.isCreative() || playerIn.getItemInHand(handIn).isEmpty()) {
					playerIn.addItem(this.getItem(0).copy());
				}
				
				this.setItem(0, ItemStack.EMPTY);
	
				this.destDimension = new ResourceLocation("");
				this.destInfo = new ObjectDestinationInfo(BlockPos.ZERO, 0, 0);
				this.setDisplayColour(CosmosColour.WHITE);
				this.isPortalFormed = false;
				this.sendUpdates(true);
				
				return ActionResultType.SUCCESS;
			}
		} else {
			if (worldIn.isClientSide) {
				return ActionResultType.SUCCESS;
			} else {
				if (playerIn instanceof ServerPlayerEntity) {
					NetworkHooks.openGui((ServerPlayerEntity)playerIn, this, (packetBuffer)->{ packetBuffer.writeBlockPos(pos); });
					return ActionResultType.SUCCESS;
				}
			}
		}
		return ActionResultType.FAIL;
	}
	
	public boolean destroyPortal(World worldIn, BlockPos posIn) {
		for (Direction c : Direction.values()) {
			TileEntity tile = worldIn.getBlockEntity(posIn.offset(c.getNormal()));
			
			if (tile instanceof TileEntityPortal) {
				TileEntityPortal portalTile = (TileEntityPortal) tile;

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
	
	public void toggleRenderLabel() {
		this.renderLabel = !this.renderLabel;
	}

	public void togglePlaySound() {
		this.playSound = !this.playSound;
		
		this.updatePortalBlocks(1, this.playSound);
	}

	public void toggleEntities() {
		this.allowEntities = !this.allowEntities;
		
		this.updatePortalBlocks(2, this.allowEntities);
	}
	
	public void toggleParticles() {
		this.showParticles = !this.showParticles;
		
		this.updatePortalBlocks(3, this.showParticles);
	}
	
	public void updatePortalBlocks(int id, boolean value) {
		for (Direction c : Direction.values()) {
			Optional<CustomPortalSize> optional = CustomPortalSize.findPortalShape(this.getLevel(), this.getBlockPos().offset(c.getNormal()), (portalSize) -> { return portalSize.isValid(); }, Direction.Axis.Z);
			
			if (optional.isPresent()) {
				LinkedHashMap<Integer, BlockPos> blockMap = optional.get().getPortalBlocks(this.getLevel(), destDimension);
				
				for (int i = 0; i < blockMap.size(); i++) {
					BlockPos testPos = blockMap.get(i);
					
					TileEntity testTile = this.getLevel().getBlockEntity(testPos);

					if (testTile instanceof TileEntityPortal) {
						TileEntityPortal tileEntity = (TileEntityPortal) testTile;
						
						if (tileEntity.destDimension.equals(this.destDimension) && tileEntity.getDestPos().equals(this.getDestPos())) {
							if (id == 1) {
								tileEntity.setPlaySound(value);
							} else if (id == 2) {
								tileEntity.setAllowEntities(value);
							} else if (id == 3) {
								tileEntity.setShowParticles(value);
							}
						}
						
						tileEntity.sendUpdates(true);
					}
				}
			}
		}
	}
	
	public boolean createPortal(World worldIn, BlockPos posIn, ResourceLocation dimensionIn, BlockPos teleportPos, float yawIn, float pitchIn, int colourIn) {
		for (Direction c : Direction.values()) {
			Optional<CustomPortalSize> optional = CustomPortalSize.findEmptyPortalShape(worldIn, posIn.offset(c.getNormal()), Direction.Axis.Z);
			
			if (optional.isPresent()) {
				if (!dimensionIn.getNamespace().isEmpty()) {
					optional.get().createPortalBlocks(worldIn, dimensionIn, teleportPos, yawIn, pitchIn, colourIn);
					this.isPortalFormed = true;
					this.sendUpdates(true);
					return true;
				}
			}
		}
		return false;
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
		return ItemStackHelper.removeItem(this.inventoryItems, index, count);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return ItemStackHelper.takeItem(this.inventoryItems, index);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		this.inventoryItems.set(index, stack);
		
		if (stack.getCount() > this.getContainerSize()) {
			stack.setCount(this.getContainerSize());
		}
	}

	@Override
	public boolean stillValid(PlayerEntity playerIn) {
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
	public ITextComponent getDisplayName() {
		return CosmosCompHelper.locComp("cosmosportals.gui.dock");
	}
	
	@Override
	public Container createMenu(int idIn, PlayerInventory playerInventoryIn, PlayerEntity playerIn) {
		return new ContainerPortalDock(idIn, playerInventoryIn, this, IWorldPosCallable.create(this.getLevel(), this.getBlockPos()), this.getBlockPos());
	}
	
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos posIn, Random randIn) {
		if (this.playSound && this.isPortalFormed) {
			if (randIn.nextInt(100) == 0) {
				worldIn.playLocalSound((double) posIn.getX() + 0.5D, (double) posIn.getY() + 0.5D, (double) posIn.getZ() + 0.5D, SoundEvents.PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5F, randIn.nextFloat() * 0.4F + 0.8F, false);
			}
		}
	}
	
}