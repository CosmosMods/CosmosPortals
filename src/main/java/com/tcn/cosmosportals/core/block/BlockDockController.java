package com.tcn.cosmosportals.core.block;

import java.util.ArrayList;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.common.block.CosmosEntityBlock;
import com.tcn.cosmosportals.core.blockentity.BlockEntityDockController;
import com.tcn.cosmosportals.core.management.ModObjectHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockDockController extends CosmosEntityBlock {

	//									minX  minY  minZ  maxX   maxY   maxZ
	private VoxelShape BASE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	private VoxelShape BASE_ONE = Block.box(-1.0D, -1.0D, -1.0D, 17.0D, 17.0D, 17.0D);
	
	public BlockDockController(BlockBehaviour.Properties properties) {
		super(properties);
		
		this.hasDynamicShape();
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos posIn, BlockState stateIn) {
		return new BlockEntityDockController(posIn, stateIn);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level levelIn, BlockState stateIn, BlockEntityType<T> entityTypeIn) {
		return createTicker(levelIn, entityTypeIn, ModObjectHolder.tile_dock_controller);
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level levelIn, BlockEntityType<T> entityTypeIn, BlockEntityType<? extends BlockEntityDockController> entityIn) {
		return createTickerHelper(entityTypeIn, entityIn, BlockEntityDockController::tick);
	}

	@Override
	public RenderShape getRenderShape(BlockState stateIn) {
		return RenderShape.MODEL;
	}
	
	@Override
	public VoxelShape getShape(BlockState stateIn, BlockGetter blockGetterIn, BlockPos posIn, CollisionContext contextIn) {
		ArrayList<VoxelShape> shapes = new ArrayList<VoxelShape>();
		
		if (blockGetterIn.getBlockState(posIn.north()).isAir()) {
			shapes.add(Block.box(1.0D, 9.0D, -1.0D, 7.0D , 15.0D, 0.0D)); //TOP LEFT
			shapes.add(Block.box(9.0D, 9.0D, -1.0D, 15.0D, 15.0D, 0.0D)); //TOP RIGHT
			shapes.add(Block.box(1.0D, 1.0D, -1.0D, 7.0D , 7.0D , 0.0D)); //BOTTOM LEFT
			shapes.add(Block.box(9.0D, 1.0D, -1.0D, 15.0D, 7.0D , 0.0D)); //BOTTOM RIGHT
		}
		
		if (blockGetterIn.getBlockState(posIn.east()).isAir()) {
			shapes.add(Block.box(16.0D, 9.0D, 1.0D, 17.0D, 15.0D, 7.0D )); //TOP LEFT
			shapes.add(Block.box(16.0D, 9.0D, 9.0D, 17.0D, 15.0D, 15.0D)); //TOP RIGHT
			shapes.add(Block.box(16.0D, 1.0D, 1.0D, 17.0D, 7.0D , 7.0D )); //BOTTOM LEFT
			shapes.add(Block.box(16.0D, 1.0D, 9.0D, 17.0D, 7.0D,  15.0D)); //BOTTOM RIGHT
		}
		
		if (blockGetterIn.getBlockState(posIn.south(1)).isAir()) {
			shapes.add(Block.box(1.0D, 9.0D, 16.0D, 7.0D , 15.0D, 17.0D)); //TOP LEFT
			shapes.add(Block.box(9.0D, 9.0D, 16.0D, 15.0D, 15.0D, 17.0D)); //TOP RIGHT
			shapes.add(Block.box(1.0D, 1.0D, 16.0D, 7.0D , 7.0D , 17.0D)); //BOTTOM LEFT
			shapes.add(Block.box(9.0D, 1.0D, 16.0D, 15.0D, 7.0D,  17.0D)); //BOTTOM RIGHT
		}
		
		if (blockGetterIn.getBlockState(posIn.west(1)).isAir()) {
			shapes.add(Block.box(-1.0D, 9.0D, 1.0D, 0.0D, 15.0D, 7.0D )); //TOP LEFT
			shapes.add(Block.box(-1.0D, 9.0D, 9.0D, 0.0D, 15.0D, 15.0D)); //TOP RIGHT
			shapes.add(Block.box(-1.0D, 1.0D, 1.0D, 0.0D, 7.0D , 7.0D )); //BOTTOM LEFT
			shapes.add(Block.box(-1.0D, 1.0D, 9.0D, 0.0D, 7.0D,  15.0D)); //BOTTOM RIGHT
		}
		
		return or(BASE, shapes);
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState stateIn, BlockGetter blockGetterIn, BlockPos posIn, CollisionContext contextIn) {
		return BASE;
	}

	@Override
	public VoxelShape getInteractionShape(BlockState stateIn, BlockGetter blockGetterIn, BlockPos posIn) {
		return BASE_ONE;
	}

	@Override
	public VoxelShape getVisualShape(BlockState p_60479_, BlockGetter p_60480_, BlockPos p_60481_, CollisionContext p_60482_) {
		return BASE_ONE;
	}
	@Override
	public InteractionResult use(BlockState state, Level levelIn, BlockPos posIn, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		BlockEntity tileEntity = levelIn.getBlockEntity(posIn);
		
		if (tileEntity instanceof BlockEntityDockController) {
			return ((BlockEntityDockController) tileEntity).use(state, levelIn, posIn, playerIn, handIn, hit);
		}
		
		return InteractionResult.PASS;
	}

	public static VoxelShape or(VoxelShape baseShape, ArrayList<VoxelShape> shapeArray) {
		return shapeArray.stream().reduce(baseShape, Shapes::or);
	}

}