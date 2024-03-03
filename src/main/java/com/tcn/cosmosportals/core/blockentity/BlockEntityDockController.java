package com.tcn.cosmosportals.core.blockentity;

import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockNotifier;
import com.tcn.cosmoslibrary.common.lib.MathHelper;
import com.tcn.cosmosportals.core.block.BlockDockController;
import com.tcn.cosmosportals.core.management.ModObjectHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings({ "unused" })
public class BlockEntityDockController extends BlockEntity implements IBlockNotifier, IBlockInteract {
	
	public int buttonTimer = 20;
	public int maxButtonTimer = 20;
	public boolean buttonPressed = false;

	private boolean linked = false;
	private BlockPos dockPos = BlockPos.ZERO;
	
	
	public BlockEntityDockController(BlockPos posIn, BlockState stateIn) {
		super(ModObjectHolder.tile_dock_controller, posIn, stateIn);
	}

	public void sendUpdates(boolean update) {
		if (level != null) {
			this.setChanged();
			BlockState state = this.getBlockState();
			BlockDockController block = (BlockDockController) state.getBlock();
			
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
		
		compound.putInt("dockX", this.dockPos.getX());
		compound.putInt("dockY", this.dockPos.getY());
		compound.putInt("dockZ", this.dockPos.getZ());
		
		compound.putBoolean("linked", linked);
		
		compound.putBoolean("pressed", this.buttonPressed);
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		
		this.dockPos = new BlockPos(compound.getInt("dockX"), compound.getInt("dockY"), compound.getInt("dockZ"));
		
		this.linked = compound.getBoolean("linked");
		
		this.buttonPressed = compound.getBoolean("pressed");
	}
	
	public boolean setDockPos(BlockPos posIn) {
		if (posIn.distManhattan(this.getBlockPos()) < 16) {
			this.dockPos = posIn;
			this.setLinked(true);
			
			return true;
		} else {
			return false;
		}
	}
	
	public BlockPos getDockPos() {
		return this.dockPos;
	}

	public boolean isLinked() {
		return this.linked;
	}
	
	public void setLinked(boolean linked) {
		this.linked = linked;
		this.sendUpdates(true);
	}
	
	public boolean performLinkCheck() {
		BlockEntity testEntity = this.getLevel().getBlockEntity(this.dockPos);
		
		if (testEntity != null) {
			if (testEntity instanceof AbstractBlockEntityPortalDock) {
				return true;
			} else {
				this.setLinked(false);
				return false;
			}
		} else {
			this.setLinked(false);
			return false;
		}
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

	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, BlockEntityDockController entityIn) {
		if (entityIn.buttonPressed) {
			if (entityIn.buttonTimer > 0) {
				entityIn.buttonTimer--;
			} else {
				entityIn.buttonPressed = false;
				entityIn.buttonTimer = entityIn.maxButtonTimer;
			}
		}
	}

	@Override
	public void attack(BlockState state, Level levelIn, BlockPos pos, Player player) { }

	@Override
	public InteractionResult use(BlockState state, Level levelIn, BlockPos posIn, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		boolean success = false;
		
		if (!this.buttonPressed) {
			if (this.performLinkCheck()) {
				Vec3 hitLocation = hit.getLocation();
				
				double hitX = Math.round(((hitLocation.x() - posIn.getX()) * 16) * 10D) / 10D;
				double hitY = Math.round(((hitLocation.y() - posIn.getY()) * 16) * 10D) / 10D;
				double hitZ = Math.round(((hitLocation.z() - posIn.getZ()) * 16) * 10D) / 10D;
				
				//MATHS
				
				{//NORTH
					if ((hitX >= 9 && hitX <= 15) && (hitY >= 9 & hitY <= 15) && ((hitZ >= -1 && hitZ < 0))) {
						success = true;
						this.pressButton(levelIn, playerIn, 0);
					} if ((hitX >= 1 && hitX < 8) && (hitY >= 9 & hitY <= 15) && ((hitZ >= -1 && hitZ < 0))) {
						success = true;
						this.pressButton(levelIn, playerIn, 1);
					} if ((hitX >= 9 && hitX < 16) && (hitY >= 1 & hitY < 8) && ((hitZ >= -1 && hitZ < 0))) {
						success = true;
						this.pressButton(levelIn, playerIn, 2);
					} if ((hitX >= 1 && hitX <= 9) && (hitY >= 1 & hitY < 8) && ((hitZ >= -1 && hitZ < 0))) {
						success = true;
						this.pressButton(levelIn, playerIn, 3);
					}
				}
				{//SOUTH
					if ((hitX >= 1 && hitX <= 7) && (hitY >= 9 & hitY <= 15) && (hitZ >= 16 && hitZ <= 17)) {
						success = true;
						this.pressButton(levelIn, playerIn, 0);
					} if ((hitX >= 9 && hitX <= 15) && (hitY >= 9 & hitY <= 15) && (hitZ >= 16 && hitZ <= 17)) {
						success = true;
						this.pressButton(levelIn, playerIn, 1);
					} if ((hitX >= 1 && hitX <= 7) && (hitY >= 1 & hitY <= 7) && (hitZ >= 16 && hitZ <= 17)) {
						success = true;
						this.pressButton(levelIn, playerIn, 2);
					} if ((hitX >= 9 && hitX <= 15) && (hitY >= 1 & hitY <= 7) && (hitZ >= 16 && hitZ <= 17)) {
						success = true;
						this.pressButton(levelIn, playerIn, 3);
					}
				} 
				{//WEST
					if ((hitX >= -1 && hitX < 0) && (hitY >= 9 & hitY <= 15) && (hitZ >= 1 && hitZ <= 7)) {
						success = true;
						this.pressButton(levelIn, playerIn, 0);
					} if ((hitX >= -1 && hitX < 0) && (hitY >= 9 & hitY <= 15) && (hitZ >= 9 && hitZ <= 15)) {
						success = true;
						this.pressButton(levelIn, playerIn, 1);
					} if ((hitX >= -1 && hitX < 0) && (hitY >= 1 & hitY <= 7) && (hitZ >= 1 && hitZ <= 7)) {
						success = true;
						this.pressButton(levelIn, playerIn, 2);
					} if ((hitX >= -1 && hitX < 0) && (hitY >= 1 & hitY <= 7) && (hitZ >= 9 && hitZ <= 15)) {
						success = true;
						this.pressButton(levelIn, playerIn, 3);
					}
				} 
				{//WEST
					if ((hitX >= 16 && hitX <= 17) && (hitY >= 9 & hitY <= 15) && (hitZ >= 9 && hitZ <= 15)) {
						success = true;
						this.pressButton(levelIn, playerIn, 0);
					}
					if ((hitX >= 16 && hitX <= 17) && (hitY >= 9 & hitY <= 15) && (hitZ >= 1 && hitZ <= 7)) {
						success = true;
						this.pressButton(levelIn, playerIn, 1);
					} if ((hitX >= 16 && hitX <= 17) && (hitY >= 1 & hitY <= 7) && (hitZ >= 9 && hitZ <= 15)) {
						success = true;
						this.pressButton(levelIn, playerIn, 2);
					} if ((hitX >= 16 && hitX <= 17) && (hitY >= 1 & hitY <= 7) && (hitZ >= 1 && hitZ <= 7)) {
						success = true;
						this.pressButton(levelIn, playerIn, 3);
					}
				}
			}
		}
		
		return success ? InteractionResult.SUCCESS : InteractionResult.FAIL;
	}
	
	public void pressButton(Level levelIn, Player playerIn, int buttonID) {
		if (this.isLinked()) {
			if (this.performLinkCheck()) {
				BlockEntity entity = levelIn.getBlockEntity(this.getDockPos());
				
				if (entity != null) {
					AbstractBlockEntityPortalDock dockEntity = (AbstractBlockEntityPortalDock) entity;

					if (!(levelIn.isClientSide)) {
						dockEntity.setCurrentSlot(buttonID);
						this.buttonPressed = true;
						dockEntity.sendUpdates(true);
					}
					
				    this.getLevel().playSound(playerIn, this.getBlockPos(), BlockSetType.STONE.buttonClickOn(), SoundSource.BLOCKS);
				} else {
					this.performLinkCheck();
				}
			}
		}
	}

	@Override
	public void playerWillDestroy(Level levelIn, BlockPos posIn, BlockState stateIn, Player playerIn) { }

	@Override
	public void setPlacedBy(Level levelIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) { }

	@Override
	public void onPlace(BlockState state, Level levelIn, BlockPos pos, BlockState oldState, boolean isMoving) { }
	
	@Override
	public void neighborChanged(BlockState state, Level levelIn, BlockPos posIn, Block blockIn, BlockPos fromPos, boolean isMoving) { }
	
	@Override
	public void setChanged() {
		super.setChanged();		
	}
}