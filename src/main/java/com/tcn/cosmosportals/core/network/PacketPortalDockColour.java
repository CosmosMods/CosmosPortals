package com.tcn.cosmosportals.core.network;

import java.util.function.Supplier;

import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmosportals.CosmosPortals;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortalDock;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class PacketPortalDockColour  {
	
	private BlockPos pos;
	private int colour;
	
	public PacketPortalDockColour(FriendlyByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.colour = buf.readInt();
	}
	
	public PacketPortalDockColour(BlockPos pos, ComponentColour colourIn) {
		this.pos = pos;
		this.colour = colourIn.getIndex();
	}
	
	public static void encode(PacketPortalDockColour packet, FriendlyByteBuf buf) {
		buf.writeBlockPos(packet.pos);
		buf.writeInt(packet.colour);
	}
	
	public static void handle(final PacketPortalDockColour packet, Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		
		ctx.enqueueWork(() -> {
			ServerLevel world = ctx.getSender().getLevel();
			BlockEntity tile = world.getBlockEntity(packet.pos);
			
			if (tile instanceof BlockEntityPortalDock) {
				BlockEntityPortalDock tileDock = (BlockEntityPortalDock) tile;
				
				tileDock.updateColour(ComponentColour.fromIndex(packet.colour));
				tileDock.sendUpdates(true);
			} else {
				CosmosPortals.CONSOLE.debugWarn("[Packet Delivery Failure] <portaldock> Block Entity not equal to expected.");
			}			
		});
		
		ctx.setPacketHandled(true);
	}
}
