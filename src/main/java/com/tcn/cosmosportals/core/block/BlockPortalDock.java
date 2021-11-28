package com.tcn.cosmosportals.core.block;

import java.util.Random;

import com.tcn.cosmoslibrary.common.block.CosmosBlock;
import com.tcn.cosmosportals.core.management.CoreModBusManager;
import com.tcn.cosmosportals.core.tileentity.TileEntityPortalDock;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockPortalDock extends CosmosBlock {

	public BlockPortalDock(Properties properties) {
		super(properties);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader worldIn) {
		return CoreModBusManager.DOCK_TILE_TYPE.create();
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public boolean canBeReplacedByLeaves(BlockState state, IWorldReader world, BlockPos pos) {
        return false;
    }

	@Override
	public void attack(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		TileEntity tileEntity = world.getBlockEntity(pos);
		
		if (tileEntity instanceof TileEntityPortalDock) {
			((TileEntityPortalDock) tileEntity).attack(state, world, pos, player);
		}
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand handIn, BlockRayTraceResult hit) {
		TileEntity tileEntity = worldIn.getBlockEntity(pos);
		
		if (tileEntity instanceof TileEntityPortalDock) {
			return ((TileEntityPortalDock) tileEntity).use(state, worldIn, pos, playerIn, handIn, hit);
		}
		
		return ActionResultType.PASS;
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos posIn, Random randIn) {
		TileEntity tileEntity = worldIn.getBlockEntity(posIn);
		
		if (tileEntity instanceof TileEntityPortalDock) {
			((TileEntityPortalDock) tileEntity).animateTick(stateIn, worldIn, posIn, randIn);
		}
	}
}