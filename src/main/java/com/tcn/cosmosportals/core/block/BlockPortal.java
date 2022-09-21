package com.tcn.cosmosportals.core.block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.common.block.CosmosBlockUnbreakable;
import com.tcn.cosmoslibrary.common.interfaces.IBlankCreativeTab;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortal;
import com.tcn.cosmosportals.core.management.ConfigurationManagerCommon;
import com.tcn.cosmosportals.core.management.ModObjectHolder;
import com.tcn.cosmosportals.core.portal.CustomPortalShape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("unchecked")
public class BlockPortal extends CosmosBlockUnbreakable implements EntityBlock, IBlankCreativeTab {
	
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
	public BlockEntity newBlockEntity(BlockPos posIn, BlockState stateIn) {
		return new BlockEntityPortal(posIn, stateIn);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level levelIn, BlockState stateIn, BlockEntityType<T> entityTypeIn) {
		return createTicker(levelIn, entityTypeIn, ModObjectHolder.tile_portal);
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level levelIn, BlockEntityType<T> entityTypeIn, BlockEntityType<? extends BlockEntityPortal> entityIn) {
		return createTickerHelper(entityTypeIn, entityIn, BlockEntityPortal::tick);
	}

	@Override
	public void randomTick(BlockState stateIn, ServerLevel worldIn, BlockPos posIn, RandomSource random) {
		if (worldIn.dimensionType().natural() && worldIn.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && random.nextInt(2000) < worldIn.getDifficulty().getId()) {
			while (worldIn.getBlockState(posIn).is(this)) {
				posIn = posIn.below();
			}
		}
	}

	@Override
	public VoxelShape getShape(BlockState stateIn, BlockGetter worldIn, BlockPos posIn, CollisionContext context) {
		switch ((Direction.Axis) stateIn.getValue(AXIS)) {
		case Z:
			return Z_AXIS_AABB;
		case X:
		default:
			return X_AXIS_AABB;
		}
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction directionIn, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
		Direction.Axis direction$axis = directionIn.getAxis();
		Direction.Axis direction$axis1 = stateIn.getValue(AXIS);
		boolean flag = direction$axis1 != direction$axis && direction$axis.isHorizontal();
		
		return !flag && !facingState.is(this) && !(new CustomPortalShape(worldIn, currentPos, direction$axis1)).isComplete() ? Blocks.AIR.defaultBlockState() : 
			stateIn.setValue(UP, this.canSideConnect(worldIn, currentPos, Direction.UP, null))
				.setValue(DOWN, this.canSideConnect(worldIn, currentPos, Direction.DOWN, null))
				.setValue(LEFT, this.canSideConnect(worldIn, currentPos, null, "left"))
				.setValue(RIGHT, this.canSideConnect(worldIn, currentPos, null, "right"));
	}

	public BlockState updateState(BlockState stateIn, BlockPos currentPos, Level worldIn) {
		return stateIn.setValue(UP, this.canSideConnect(worldIn, currentPos, Direction.UP, null))
				.setValue(DOWN, this.canSideConnect(worldIn, currentPos, Direction.DOWN, null))
				.setValue(LEFT, this.canSideConnect(worldIn, currentPos, null, "left"))
				.setValue(RIGHT, this.canSideConnect(worldIn, currentPos, null, "right"));
	}
	
	@Override
	public void entityInside(BlockState stateIn, Level worldIn, BlockPos posIn, Entity entityIn) {
		if (!worldIn.isClientSide) {
			BlockEntity tile = worldIn.getBlockEntity(posIn);
			
			if (tile != null && tile instanceof BlockEntityPortal) {
				((BlockEntityPortal) tile).entityInside(stateIn, worldIn, posIn, entityIn);
			}
		}
	}
	
	@Override
	public boolean canHarvestBlock(BlockState state, BlockGetter world, BlockPos pos, Player player) {
        return false;
    }

	@Override
	public ItemStack getCloneItemStack(BlockGetter blockReader, BlockPos posIn, BlockState stateIn) {
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
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AXIS, DOWN, UP, LEFT, RIGHT);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos posIn, RandomSource randIn) {
		BlockEntity tile = worldIn.getBlockEntity(posIn);
		
		if (tile instanceof BlockEntityPortal) {
			((BlockEntityPortal) tile).animateTick(stateIn, worldIn, posIn, randIn);
		}
	}

	private boolean canSideConnect(LevelAccessor world, BlockPos pos, @Nullable Direction facing, @Nullable String leftRight) {
		final BlockState blockState = world.getBlockState(pos);
		
		if (ConfigurationManagerCommon.getInstance().getPortalConnectedTextures()) {
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
	
	private boolean canConnect(@Nonnull BlockState orig, @Nonnull BlockState conn) {
		return orig.getBlock() == conn.getBlock();
	}

	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> p_152133_, BlockEntityType<E> p_152134_, BlockEntityTicker<? super E> p_152135_) {
		return p_152134_ == p_152133_ ? (BlockEntityTicker<A>) p_152135_ : null;
	}
}