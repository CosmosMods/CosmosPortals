package com.tcn.cosmosportals.core.block;

import javax.annotation.Nonnull;

import com.tcn.cosmoslibrary.common.block.CosmosBlockConnected;
import com.tcn.cosmosportals.core.management.CoreConfigurationManager;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class BlockPortalFrame extends CosmosBlockConnected {

	public BlockPortalFrame(Properties properties) {
		super(properties);
	}

	@Override
	protected boolean canConnect(@Nonnull BlockState orig, @Nonnull BlockState conn) {
		if (CoreConfigurationManager.getInstance().getFrameConnectedTextures()) {
			if (conn.getBlock().equals(Blocks.AIR)) {
				return false;
			} else if (orig.getBlock().equals(conn.getBlock())) {
				return true;
			} else if (conn.getBlock() instanceof BlockPortalDock) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean canBeReplacedByLeaves(BlockState state, IWorldReader world, BlockPos pos) {
        return false;
    }
	
}
