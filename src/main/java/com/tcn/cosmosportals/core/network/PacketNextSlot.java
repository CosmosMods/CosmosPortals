package com.tcn.cosmosportals.core.network;

import java.util.function.Supplier;

import com.tcn.cosmosportals.CosmosPortals;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortalDockUpgraded;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class PacketNextSlot  {
	
	private BlockPos pos;
	private boolean forward;
	
	public PacketNextSlot(FriendlyByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.forward = buf.readBoolean();
	}
	
	public PacketNextSlot(BlockPos pos, boolean forward) {
		this.pos = pos;
		this.forward = forward;
	}
	
	public static void encode(PacketNextSlot packet, FriendlyByteBuf buf) {
		buf.writeBlockPos(packet.pos);
		buf.writeBoolean(packet.forward);
	}
	
	public static void handle(final PacketNextSlot packet, Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		
		ctx.enqueueWork(() -> {
			ServerLevel world = (ServerLevel) ctx.getSender().level();
			BlockEntity tile = world.getBlockEntity(packet.pos);
			
			if (tile instanceof BlockEntityPortalDockUpgraded) {
				BlockEntityPortalDockUpgraded tileDock = (BlockEntityPortalDockUpgraded) tile;
				
				tileDock.selectNextSlot(packet.forward);
			} 
			
			else {
				CosmosPortals.CONSOLE.debugWarn("[Packet Delivery Failure] <portaldockupgraded> Block Entity not equal to expected.");
			}			
		});
		
		ctx.setPacketHandled(true);
	}
}
