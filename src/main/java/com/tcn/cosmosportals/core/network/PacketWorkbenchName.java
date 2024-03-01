package com.tcn.cosmosportals.core.network;

import java.util.function.Supplier;

import com.tcn.cosmosportals.CosmosPortals;
import com.tcn.cosmosportals.core.blockentity.BlockEntityContainerWorkbench;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class PacketWorkbenchName  {
	
	private BlockPos pos;
	private String displayName;
	
	public PacketWorkbenchName(FriendlyByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.displayName = buf.readUtf();
	}
	
	public PacketWorkbenchName(BlockPos pos, String displayNameIn) {
		this.pos = pos;
		this.displayName = displayNameIn;
	}
	
	public static void encode(PacketWorkbenchName packet, FriendlyByteBuf buf) {
		buf.writeBlockPos(packet.pos);
		buf.writeUtf(packet.displayName);
	}
	
	public static void handle(final PacketWorkbenchName packet, Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		
		ctx.enqueueWork(() -> {
			ServerLevel world = (ServerLevel) ctx.getSender().level();
			BlockEntity tile = world.getBlockEntity(packet.pos);
			
			if (tile instanceof BlockEntityContainerWorkbench) {
				BlockEntityContainerWorkbench tileDock = (BlockEntityContainerWorkbench) tile;
				
				tileDock.setContainerDisplayName(packet.displayName);
			} else {
				CosmosPortals.CONSOLE.debugWarn("[Packet Delivery Failure] <container_workbench> Block Entity not equal to expected.");
			}
			
		});
		
		ctx.setPacketHandled(true);
	}
}
