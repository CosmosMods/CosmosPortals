package com.tcn.cosmosportals.core.management;

import com.tcn.cosmoslibrary.common.event.PortalEvent;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;

public class EventFactory {
	
	public static boolean onPortalTravel(Entity entityIn, BlockPos entityPosIn, BlockPos destPosIn, ResourceLocation destDimensionIn) {
		return !(MinecraftForge.EVENT_BUS.post(new PortalEvent.PortalTravel(entityIn, entityPosIn, destPosIn, destDimensionIn)));
	}
	
	public static boolean onContainerLink(Player entityIn, BlockPos entityPosIn, BlockPos destPosIn, ResourceLocation destDimensionIn) {
		return !(MinecraftForge.EVENT_BUS.post(new PortalEvent.LinkContainer(entityIn, entityPosIn, destPosIn, destDimensionIn)));
	}
}