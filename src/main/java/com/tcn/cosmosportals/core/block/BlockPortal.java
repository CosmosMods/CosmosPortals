package com.tcn.cosmosportals.core.block;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.common.block.CosmosBlockUnbreakable;
import com.tcn.cosmoslibrary.common.interfaces.IItemGroupNone;
import com.tcn.cosmosportals.core.management.CoreConfigurationManager;
import com.tcn.cosmosportals.core.management.CoreModBusManager;
import com.tcn.cosmosportals.core.portal.CustomPortalSize;
import com.tcn.cosmosportals.core.tileentity.TileEntityPortal;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockPortal extends CosmosBlockUnbreakable implements IItemGroupNone {
	
	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
	protected static final VoxelShape X_AXIS_AABB = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
	protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);
	
	public static final BooleanProperty DOWN = BooleanProperty.create("down");
	public static final BooleanProperty UP = BooleanProperty.create("up");
	public static final BooleanProperty LEFT = BooleanProperty.create("left");
	public static final BooleanProperty RIGHT = BooleanProperty.create("right");

	public BlockPortal(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(AXIS, Direction.Axis.X).setValue(DOWN, false).setValue(UP, false).setValue(LEFT, false).setValue(RIGHT, false));
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader worldIn) {
		return CoreModBusManager.PORTAL_TILE_TYPE.create();
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public void randomTick(BlockState stateIn, ServerWorld worldIn, BlockPos posIn, Random random) {
		if (worldIn.dimensionType().natural() && worldIn.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && random.nextInt(2000) < worldIn.getDifficulty().getId()) {
			while (worldIn.getBlockState(posIn).is(this)) {
				posIn = posIn.below();
			}

			if (worldIn.getBlockState(posIn).isValidSpawn(worldIn, posIn, EntityType.ZOMBIFIED_PIGLIN)) {
				Entity entity = EntityType.ZOMBIFIED_PIGLIN.spawn(worldIn, (CompoundNBT) null, (ITextComponent) null, (PlayerEntity) null, posIn.above(), SpawnReason.STRUCTURE, false, false);
				if (entity != null) {
					entity.setPortalCooldown();
				}
			}
		}
	}

	@Override
	public VoxelShape getShape(BlockState stateIn, IBlockReader worldIn, BlockPos posIn, ISelectionContext context) {
		switch ((Direction.Axis) stateIn.getValue(AXIS)) {
		case Z:
			return Z_AXIS_AABB;
		case X:
		default:
			return X_AXIS_AABB;
		}
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction directionIn, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		Direction.Axis direction$axis = directionIn.getAxis();
		Direction.Axis direction$axis1 = stateIn.getValue(AXIS);
		boolean flag = direction$axis1 != direction$axis && direction$axis.isHorizontal();
		
		return !flag && !facingState.is(this) && !(new CustomPortalSize(worldIn, currentPos, direction$axis1)).isComplete() ? Blocks.AIR.defaultBlockState() : 
			stateIn.setValue(UP, this.canSideConnect(worldIn, currentPos, Direction.UP, null))
				.setValue(DOWN, this.canSideConnect(worldIn, currentPos, Direction.DOWN, null))
				.setValue(LEFT, this.canSideConnect(worldIn, currentPos, null, "left"))
				.setValue(RIGHT, this.canSideConnect(worldIn, currentPos, null, "right"));
	}

	public BlockState updateState(BlockState stateIn, BlockPos currentPos, World worldIn) {
		return stateIn.setValue(UP, this.canSideConnect(worldIn, currentPos, Direction.UP, null))
				.setValue(DOWN, this.canSideConnect(worldIn, currentPos, Direction.DOWN, null))
				.setValue(LEFT, this.canSideConnect(worldIn, currentPos, null, "left"))
				.setValue(RIGHT, this.canSideConnect(worldIn, currentPos, null, "right"));
	}
	
	@Override
	public void entityInside(BlockState stateIn, World worldIn, BlockPos posIn, Entity entityIn) {
		if (!worldIn.isClientSide) {
			TileEntity tile = worldIn.getBlockEntity(posIn);
			
			if (tile != null && tile instanceof TileEntityPortal) {
				((TileEntityPortal) tile).entityInside(stateIn, worldIn, posIn, entityIn);
			}
		}
	}

	@Override
	public boolean canBeReplacedByLeaves(BlockState state, IWorldReader world, BlockPos pos) {
        return false;
    }
	
	@Override
	public boolean canHarvestBlock(BlockState state, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return false;
    }

	@Override
	public ItemStack getCloneItemStack(IBlockReader blockReader, BlockPos posIn, BlockState stateIn) {
		return ItemStack.EMPTY;
	}

	@Override
	public BlockState rotate(BlockState stateIn, Rotation rotationIn) {
		switch (rotationIn) {
		case COUNTERCLOCKWISE_90:
		case CLOCKWISE_90:
			switch ((Direction.Axis) stateIn.getValue(AXIS)) {
			case Z:
				return stateIn.setValue(AXIS, Direction.Axis.X);
			case X:
				return stateIn.setValue(AXIS, Direction.Axis.Z);
			default:
				return stateIn;
			}
		default:
			return stateIn;
		}
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(AXIS, DOWN, UP, LEFT, RIGHT);
	}

	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos posIn, Random randIn) {
		TileEntity tile = worldIn.getBlockEntity(posIn);
		
		if (tile instanceof TileEntityPortal) {
			((TileEntityPortal) tile).animateTick(stateIn, worldIn, posIn, randIn);
		}
	}

	private boolean canSideConnect(IWorld world, BlockPos pos, @Nullable Direction facing, @Nullable String leftRight) {
		final BlockState blockState = world.getBlockState(pos);
		
		if (CoreConfigurationManager.getInstance().getPortalConnectedTextures()) {
			if (facing != null) {
				final BlockState otherState = world.getBlockState(pos.offset(facing.getNormal()));
				
				return blockState != null && otherState != null && this.canConnect(blockState, otherState);
			} else {
				Axis axis = blockState.getValue(AXIS);
				
				if (axis.equals(Axis.Z)) {
					if (leftRight.equals("left")) {
						final BlockState dirState = world.getBlockState(pos.offset(Direction.SOUTH.getNormal()));
						return blockState != null && dirState != null && this.canConnect(blockState, dirState);
						
					} else if (leftRight.equals("right")) {
						final BlockState dirState = world.getBlockState(pos.offset(Direction.NORTH.getNormal()));
						return blockState != null && dirState != null && this.canConnect(blockState, dirState);
					}
					
				} else if (axis.equals(Axis.X)) {
					if (leftRight.equals("left")) {
						final BlockState dirState = world.getBlockState(pos.offset(Direction.EAST.getNormal()));
						return blockState != null && dirState != null && this.canConnect(blockState, dirState);
						
					} else if (leftRight.equals("right")) {
						final BlockState dirState = world.getBlockState(pos.offset(Direction.WEST.getNormal()));
						return blockState != null && dirState != null && this.canConnect(blockState, dirState);
					}
				}
			}
		}
		return false;
	}
	
	protected boolean canConnect(@Nonnull BlockState orig, @Nonnull BlockState conn) {
		return orig.getBlock() == conn.getBlock();
	}
	
}