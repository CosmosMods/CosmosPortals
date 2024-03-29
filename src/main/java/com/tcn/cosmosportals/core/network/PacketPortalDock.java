package com.tcn.cosmosportals.core.network;

import java.util.function.Supplier;

import com.tcn.cosmosportals.CosmosPortals;
import com.tcn.cosmosportals.core.blockentity.AbstractBlockEntityPortalDock;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class PacketPortalDock  {
	
	private BlockPos pos;
	private Integer id;
	
	public PacketPortalDock(FriendlyByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.id = buf.readInt();
	}
	
	public PacketPortalDock(BlockPos pos, int id) {
		this.pos = pos;
		this.id = id;
	}
	
	public static void encode(PacketPortalDock packet, FriendlyByteBuf buf) {
		buf.writeBlockPos(packet.pos);
		buf.writeInt(packet.id);
	}
	
	public static void handle(final PacketPortalDock packet, Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		
		ctx.enqueueWork(() -> {
			ServerLevel world = (ServerLevel) ctx.getSender().level();
			BlockEntity tile = world.getBlockEntity(packet.pos);
			
			if (tile instanceof AbstractBlockEntityPortalDock) {
				AbstractBlockEntityPortalDock tileDock = (AbstractBlockEntityPortalDock) tile;
			
				if (packet.id == 0) {
					tileDock.toggleRenderLabel();
				} else if (packet.id == 1) {
					tileDock.togglePlaySound();
				} else if (packet.id == 2) {
					tileDock.toggleEntities(false);
				} else if (packet.id == 3) {
					tileDock.toggleParticles();
				} else if (packet.id == 4) {
					tileDock.toggleEntities(true);
				} else {
					CosmosPortals.CONSOLE.debugWarn("[Packet Delivery Failure] <portaldock> Setting Id: { " + packet.id + " } not recognised.");
				}
			} else {
				CosmosPortals.CONSOLE.debugWarn("[Packet Delivery Failure] <portaldock> Block Entity not equal to expected.");
			}			
		});
		
		ctx.setPacketHandled(true);
	}
}
