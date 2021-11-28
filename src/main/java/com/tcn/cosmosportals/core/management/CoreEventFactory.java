package com.tcn.cosmosportals.core.management;

import com.tcn.cosmoslibrary.common.event.PortalEvent;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;

public class CoreEventFactory {
	
	public static boolean onPortalTravel(Entity entityIn, BlockPos entityPosIn, BlockPos destPosIn, ResourceLocation destDimensionIn) {
		return !(MinecraftForge.EVENT_BUS.post(new PortalEvent.PortalTravel(entityIn, entityPosIn, destPosIn, destDimensionIn)));
	}
	
	public static boolean onContainerLink(PlayerEntity entityIn, BlockPos entityPosIn, BlockPos destPosIn, ResourceLocation destDimensionIn) {
		return !(MinecraftForge.EVENT_BUS.post(new PortalEvent.LinkContainer(entityIn, entityPosIn, destPosIn, destDimensionIn)));
	}
}