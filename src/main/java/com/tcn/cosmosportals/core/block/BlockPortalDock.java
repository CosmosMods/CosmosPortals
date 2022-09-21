package com.tcn.cosmosportals.core.block;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.common.block.CosmosEntityBlock;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortalDock;
import com.tcn.cosmosportals.core.management.ModObjectHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockPortalDock extends CosmosEntityBlock {

	public BlockPortalDock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos posIn, BlockState stateIn) {
		return new BlockEntityPortalDock(posIn, stateIn);
	}
	
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level levelIn, BlockState stateIn, BlockEntityType<T> entityTypeIn) {
		return createTicker(levelIn, entityTypeIn, ModObjectHolder.tile_portal_dock);
	}
	
	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level levelIn, BlockEntityType<T> entityTypeIn, BlockEntityType<? extends BlockEntityPortalDock> entityIn) {
		return createTickerHelper(entityTypeIn, entityIn, BlockEntityPortalDock::tick);
	}

	@Override
	public void attack(BlockState state, Level world, BlockPos pos, Player player) {
		BlockEntity tileEntity = world.getBlockEntity(pos);
		
		if (tileEntity instanceof BlockEntityPortalDock) {
			((BlockEntityPortalDock) tileEntity).attack(state, world, pos, player);
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level levelIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		BlockEntity tileEntity = levelIn.getBlockEntity(pos);
		
		if (tileEntity instanceof BlockEntityPortalDock) {
			return ((BlockEntityPortalDock) tileEntity).use(state, levelIn, pos, playerIn, handIn, hit);
		}
		
		return InteractionResult.PASS;
	}

	@Override
	public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		
		if (tileEntity instanceof BlockEntityPortalDock) {
			((BlockEntityPortalDock) tileEntity).onPlace(state, worldIn, pos, oldState, isMoving);
		}
	}

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		
		if (tileEntity instanceof BlockEntityPortalDock) {
			((BlockEntityPortalDock) tileEntity).setPlacedBy(worldIn, pos, state, placer, stack);
		}
	}

	@Override
	public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		
		if (tileEntity instanceof BlockEntityPortalDock) {
			((BlockEntityPortalDock) tileEntity).playerWillDestroy(worldIn, pos, state, player);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState stateIn, Level levelIn, BlockPos posIn, RandomSource randIn) {
		BlockEntity tileEntity = levelIn.getBlockEntity(posIn);
		
		if (tileEntity instanceof BlockEntityPortalDock) {
			((BlockEntityPortalDock) tileEntity).animateTick(stateIn, levelIn, posIn, randIn);
		}
	}
	
	@Override
	public void neighborChanged(BlockState state, Level levelIn, BlockPos posIn, Block blockIn, BlockPos fromPos, boolean isMoving) {
		BlockEntity tileEntity = levelIn.getBlockEntity(posIn);
		
		if (tileEntity instanceof BlockEntityPortalDock) {
			((BlockEntityPortalDock) tileEntity).neighborChanged(state, levelIn, posIn, blockIn, fromPos, isMoving);
		}
	}

	@Override
	public RenderShape getRenderShape(BlockState stateIn) {
		return RenderShape.MODEL;
	}
}