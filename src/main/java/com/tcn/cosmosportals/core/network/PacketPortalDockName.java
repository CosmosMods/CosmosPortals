package com.tcn.cosmosportals.core.network;

import java.util.function.Supplier;

import com.tcn.cosmosportals.CosmosPortals;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortalDock;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class PacketPortalDockName  {
	
	private BlockPos pos;
	private String displayName;
	
	public PacketPortalDockName(FriendlyByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.displayName = buf.readUtf();
	}
	
	public PacketPortalDockName(BlockPos pos, String displayNameIn) {
		this.pos = pos;
		this.displayName = displayNameIn;
	}
	
	public static void encode(PacketPortalDockName packet, FriendlyByteBuf buf) {
		buf.writeBlockPos(packet.pos);
		buf.writeUtf(packet.displayName);
	}
	
	public static void handle(final PacketPortalDockName packet, Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		
		ctx.enqueueWork(() -> {
			ServerLevel world = ctx.getSender().getLevel();
			BlockEntity tile = world.getBlockEntity(packet.pos);
			
			if (tile instanceof BlockEntityPortalDock) {
				BlockEntityPortalDock tileDock = (BlockEntityPortalDock) tile;
				
				tileDock.setPortalDisplayName(packet.displayName);
			} else {
				CosmosPortals.CONSOLE.debugWarn("[Packet Delivery Failure] <portaldock> Block Entity not equal to expected.");
			}
			
		});
		
		ctx.setPacketHandled(true);
	}
}
