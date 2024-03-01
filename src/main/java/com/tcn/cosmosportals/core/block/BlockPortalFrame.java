package com.tcn.cosmosportals.core.block;

import javax.annotation.Nonnull;

import com.tcn.cosmoslibrary.common.block.CosmosBlockConnected;
import com.tcn.cosmosportals.core.management.ConfigurationManagerCommon;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockPortalFrame extends CosmosBlockConnected {

	public BlockPortalFrame(Properties properties) {
		super(properties);
	}

	@Override
	protected boolean canConnect(@Nonnull BlockState orig, @Nonnull BlockState conn) {
		if (ConfigurationManagerCommon.getInstance().getFrameConnectedTextures()) {
			if (conn.getBlock().equals(Blocks.AIR)) {
				return false;
			} else if (orig.getBlock().equals(conn.getBlock())) {
				return true;
			} else if (conn.getBlock() instanceof BlockPortalDock) {
				return true;
			} else if (conn.getBlock() instanceof BlockPortalDockUpgraded) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

}
