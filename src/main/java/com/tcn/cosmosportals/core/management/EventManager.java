package com.tcn.cosmosportals.core.management;

import com.tcn.cosmosportals.CosmosPortals;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortalDock;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortalDockUpgraded;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Mod.EventBusSubscriber(modid = CosmosPortals.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
public class EventManager {
	
	@SubscribeEvent
	public static void onBlockBreakEvent(BlockEvent.BreakEvent event) {
		Player player = event.getPlayer();
		
		if (player != null) {
			Level level = player.level();
			BlockPos pos = event.getPos();
			
			BlockEntity blockEntity = level.getBlockEntity(pos);
			
			if (blockEntity instanceof BlockEntityPortalDock) {
				BlockEntityPortalDock dockEntity = (BlockEntityPortalDock) blockEntity;
				
				if (!dockEntity.checkIfOwner(player)) {
					event.setCanceled(true);
					
					level.setBlockAndUpdate(pos, level.getBlockState(pos));
				}
			}

			if (blockEntity instanceof BlockEntityPortalDockUpgraded) {
				BlockEntityPortalDockUpgraded dockEntity = (BlockEntityPortalDockUpgraded) blockEntity;
				
				if (!dockEntity.checkIfOwner(player)) {
					event.setCanceled(true);
					
					level.setBlockAndUpdate(pos, level.getBlockState(pos));
				}
			}
		}
	}
}