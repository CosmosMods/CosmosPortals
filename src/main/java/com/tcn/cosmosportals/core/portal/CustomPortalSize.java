package com.tcn.cosmosportals.core.portal;

import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.tcn.cosmosportals.core.block.BlockPortal;
import com.tcn.cosmosportals.core.block.BlockPortalDock;
import com.tcn.cosmosportals.core.block.BlockPortalFrame;
import com.tcn.cosmosportals.core.management.CoreConfigurationManager;
import com.tcn.cosmosportals.core.management.CoreModBusManager;
import com.tcn.cosmosportals.core.tileentity.TileEntityPortal;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.PortalInfo;
import net.minecraft.entity.EntitySize;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.TeleportationRepositioner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class CustomPortalSize {
	private static final AbstractBlock.IPositionPredicate FRAME = (stateIn, worldIn, posIn) -> {
		return stateIn.getBlock() instanceof BlockPortalFrame || stateIn.getBlock() instanceof BlockPortalDock;
	};

	private final IWorld level;
	private final Direction.Axis axis;
	private final Direction rightDir;
	private int numPortalBlocks;
	@Nullable
	private BlockPos bottomLeft;
	private int height;
	private int width;

	public static Optional<CustomPortalSize> findEmptyPortalShape(IWorld worldIn, BlockPos posIn, Direction.Axis axisIn) {
		return findPortalShape(worldIn, posIn, (portalSize) -> {
			return portalSize.isValid() && portalSize.numPortalBlocks == 0;
		}, axisIn);
	}

	public static Optional<CustomPortalSize> findPortalShape(IWorld worldIn, BlockPos posIn, Predicate<CustomPortalSize> predicate, Direction.Axis axisIn) {
		Optional<CustomPortalSize> optional = Optional.of(new CustomPortalSize(worldIn, posIn, axisIn)).filter(predicate);
		if (optional.isPresent()) {
			return optional;
		} else {
			Direction.Axis direction$axis = axisIn == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
			return Optional.of(new CustomPortalSize(worldIn, posIn, direction$axis)).filter(predicate);
		}
	}

	public CustomPortalSize(IWorld worldIn, BlockPos posIn, Direction.Axis axisIn) {
		this.level = worldIn;
		this.axis = axisIn;
		this.rightDir = axisIn == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
		this.bottomLeft = this.calculateBottomLeft(posIn);
		
		if (this.bottomLeft == null) {
			this.bottomLeft = posIn;
			this.width = 1;
			this.height = 1;
		} else {
			this.width = this.calculateWidth();
			if (this.width > 0) {
				this.height = this.calculateHeight();
			}
		}
	}

	@Nullable
	private BlockPos calculateBottomLeft(BlockPos posIn) {
		for (int i = Math.max(0, posIn.getY() - (CoreConfigurationManager.getInstance().getPortalMaximumSize())); posIn.getY() > i && isEmpty(this.level.getBlockState(posIn.below())); posIn = posIn.below()) { }

		Direction direction = this.rightDir.getOpposite();
		int j = this.getDistanceUntilEdgeAboveFrame(posIn, direction) - 1;
		return j < 0 ? null : posIn.relative(direction, j);
	}

	private int calculateWidth() {
		int i = this.getDistanceUntilEdgeAboveFrame(this.bottomLeft, this.rightDir);
		return i >= 1 && i <= (CoreConfigurationManager.getInstance().getPortalMaximumSize()) ? i : 0;
	}

	private int getDistanceUntilEdgeAboveFrame(BlockPos posIn, Direction directionIn) {
		BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

		for (int i = 0; i <= (CoreConfigurationManager.getInstance().getPortalMaximumSize()); ++i) {
			blockpos$mutable.set(posIn).move(directionIn, i);
			BlockState blockstate = this.level.getBlockState(blockpos$mutable);
			if (!isEmpty(blockstate)) {
				if (FRAME.test(blockstate, this.level, blockpos$mutable)) {
					return i;
				}
				break;
			}

			BlockState blockstate1 = this.level.getBlockState(blockpos$mutable.move(Direction.DOWN));
			if (!FRAME.test(blockstate1, this.level, blockpos$mutable)) {
				break;
			}
		}

		return 0;
	}

	private int calculateHeight() {
		BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
		int i = this.getDistanceUntilTop(blockpos$mutable);
		return i >= 2 && i <= (CoreConfigurationManager.getInstance().getPortalMaximumSize()) && this.hasTopFrame(blockpos$mutable, i) ? i : 0;
	}

	private boolean hasTopFrame(BlockPos.Mutable mutableIn, int distance) {
		for (int i = 0; i < this.width; ++i) {
			BlockPos.Mutable blockpos$mutable = mutableIn.set(this.bottomLeft).move(Direction.UP, distance).move(this.rightDir, i);
			if (!FRAME.test(this.level.getBlockState(blockpos$mutable), this.level, blockpos$mutable)) {
				return false;
			}
		}

		return true;
	}

	private int getDistanceUntilTop(BlockPos.Mutable mutableIn) {
		for (int i = 0; i < (CoreConfigurationManager.getInstance().getPortalMaximumSize() + 2); ++i) {
			mutableIn.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, -1);
			if (!FRAME.test(this.level.getBlockState(mutableIn), this.level, mutableIn)) {
				return i;
			}

			mutableIn.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, this.width);
			if (!FRAME.test(this.level.getBlockState(mutableIn), this.level, mutableIn)) {
				return i;
			}

			for (int j = 0; j < this.width; ++j) {
				mutableIn.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, j);
				BlockState blockstate = this.level.getBlockState(mutableIn);
				if (!isEmpty(blockstate)) {
					return i;
				}

				if (blockstate.is(CoreModBusManager.PORTAL)) {
					++this.numPortalBlocks;
				}
			}
		}

		return (CoreConfigurationManager.getInstance().getPortalMaximumSize() + 2);
	}

	@SuppressWarnings("deprecation")
	private static boolean isEmpty(BlockState stateIn) {
		return stateIn.isAir() || stateIn.is(CoreModBusManager.PORTAL);
	}

	public boolean isValid() {
		return this.bottomLeft != null && this.width >= 1 && this.width <= (CoreConfigurationManager.getInstance().getPortalMaximumSize()) && this.height >= 2 && this.height <= (CoreConfigurationManager.getInstance().getPortalMaximumSize());
	}

	public void createPortalBlocks(World worldIn, ResourceLocation dimensionIn, BlockPos teleportPos, float pitchIn, float yawIn, int colourIn) {
		BlockState blockstate = CoreModBusManager.PORTAL.defaultBlockState().setValue(BlockPortal.AXIS, this.axis);
		
		BlockPos.betweenClosed(this.bottomLeft,	this.bottomLeft.relative(Direction.UP, this.height - 1).relative(this.rightDir, this.width - 1)).forEach((pos) -> {
			if (!(this.level.getBlockState(pos).getBlock() instanceof BlockPortal)) {
				this.level.setBlock(pos, blockstate, 18);
				
				if (this.level.getBlockEntity(pos) != null) {
					TileEntity tile = this.level.getBlockEntity(pos);
					
					if (tile instanceof TileEntityPortal) {
						TileEntityPortal portal_tile = (TileEntityPortal) tile;
						
						portal_tile.setDestDimension(dimensionIn);
						portal_tile.setDestInfo(teleportPos, yawIn, pitchIn);
						portal_tile.setDisplayColour(colourIn);
						portal_tile.sendUpdates(true);
					}
				}
			}
		});
	}
	
	public LinkedHashMap<Integer, BlockPos> getPortalBlocks(World worldIn, ResourceLocation dimensionIn) {
		LinkedHashMap<Integer, BlockPos> blockMap = new LinkedHashMap<Integer, BlockPos>();
		
		BlockPos.betweenClosed(this.bottomLeft,	this.bottomLeft.relative(Direction.UP, this.height - 1).relative(this.rightDir, this.width - 1)).forEach((pos) -> {
			if ((worldIn.getBlockState(pos).getBlock() instanceof BlockPortal)) {
				if (worldIn.getBlockEntity(pos) != null) {
					TileEntity tile = worldIn.getBlockEntity(pos);
					
					if (tile instanceof TileEntityPortal) {
						TileEntityPortal portal_tile = (TileEntityPortal) tile;

						if (portal_tile.destDimension.equals(dimensionIn)) {
							blockMap.put(blockMap.size(), new BlockPos(pos));
						}
					}
				}
			}
		});

		return blockMap;
	}

	public boolean isComplete() {
		return this.isValid() && this.numPortalBlocks == this.width * this.height;
	}

	public static Vector3d getRelativePosition(TeleportationRepositioner.Result resultIn, Direction.Axis axisIn, Vector3d vecIn, EntitySize entitySizeIn) {
		double d0 = (double) resultIn.axis1Size - (double) entitySizeIn.width;
		double d1 = (double) resultIn.axis2Size - (double) entitySizeIn.height;
		BlockPos blockpos = resultIn.minCorner;
		
		double d2;
		if (d0 > 0.0D) {
			float f = (float) blockpos.get(axisIn) + entitySizeIn.width / 2.0F;
			d2 = MathHelper.clamp(MathHelper.inverseLerp(vecIn.get(axisIn) - (double) f, 0.0D, d0), 0.0D, 1.0D);
		} else {
			d2 = 0.5D;
		}

		double d4;
		if (d1 > 0.0D) {
			Direction.Axis direction$axis = Direction.Axis.Y;
			d4 = MathHelper.clamp(MathHelper.inverseLerp(vecIn.get(direction$axis) - (double) blockpos.get(direction$axis), 0.0D, d1), 0.0D, 1.0D);
		} else {
			d4 = 0.0D;
		}

		Direction.Axis direction$axis1 = axisIn == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
		double d3 = vecIn.get(direction$axis1) - ((double) blockpos.get(direction$axis1) + 0.5D);
		return new Vector3d(d2, d4, d3);
	}

	public static PortalInfo createPortalInfo(ServerWorld serverWorldIn, TeleportationRepositioner.Result resultIn, Direction.Axis axisIn, Vector3d vecIn, EntitySize entitySizeIn, Vector3d speedIn, float yRot, float xRot) {
		BlockPos blockpos = resultIn.minCorner;
		BlockState blockstate = serverWorldIn.getBlockState(blockpos);
		Direction.Axis direction$axis = blockstate.getValue(BlockStateProperties.HORIZONTAL_AXIS);
		
		double d0 = (double) resultIn.axis1Size;
		double d1 = (double) resultIn.axis2Size;
		int i = axisIn == direction$axis ? 0 : 90;

		Vector3d vector3d = axisIn == direction$axis ? speedIn : new Vector3d(speedIn.z, speedIn.y, -speedIn.x);

		double d2 = (double) entitySizeIn.width / 2.0D + (d0 - (double) entitySizeIn.width) * vecIn.x();
		double d3 = (d1 - (double) entitySizeIn.height) * vecIn.y();
		double d4 = 0.5D + vecIn.z();
		boolean flag = direction$axis == Direction.Axis.X;

		Vector3d vector3d1 = new Vector3d((double) blockpos.getX() + (flag ? d2 : d4), (double) blockpos.getY() + d3, (double) blockpos.getZ() + (flag ? d4 : d2));

		return new PortalInfo(vector3d1, vector3d, yRot + (float) i, xRot);
	}
}
