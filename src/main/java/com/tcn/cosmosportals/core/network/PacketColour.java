package com.tcn.cosmosportals.core.network;

import java.util.function.Supplier;

import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmosportals.CosmosPortals;
import com.tcn.cosmosportals.core.blockentity.AbstractBlockEntityPortalDock;
import com.tcn.cosmosportals.core.blockentity.BlockEntityContainerWorkbench;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class PacketColour  {
	
	private BlockPos pos;
	private int colour;
	private int slotIndex;
	
	public PacketColour(FriendlyByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.colour = buf.readInt();
		this.slotIndex = buf.readInt();
	}
	
	public PacketColour(BlockPos pos, ComponentColour colourIn, int slotIndex) {
		this.pos = pos;
		this.colour = colourIn.getIndex();
		this.slotIndex = slotIndex;
	}
	
	public static void encode(PacketColour packet, FriendlyByteBuf buf) {
		buf.writeBlockPos(packet.pos);
		buf.writeInt(packet.colour);
		buf.writeInt(packet.slotIndex);
	}
	
	public static void handle(final PacketColour packet, Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		
		ctx.enqueueWork(() -> {
			ServerLevel world = (ServerLevel) ctx.getSender().level();
			BlockEntity tile = world.getBlockEntity(packet.pos);
			
			if (tile instanceof AbstractBlockEntityPortalDock) {
				AbstractBlockEntityPortalDock tileDock = (AbstractBlockEntityPortalDock) tile;
				
				tileDock.updateColour(ComponentColour.fromIndex(packet.colour), packet.slotIndex);
				tileDock.sendUpdates(true);
			} 
			
			else if (tile instanceof BlockEntityContainerWorkbench) {
				BlockEntityContainerWorkbench tileDock = (BlockEntityContainerWorkbench) tile;

				tileDock.updateColour(ComponentColour.fromIndex(packet.colour));
				tileDock.sendUpdates(true);
			}
			
			else {
				CosmosPortals.CONSOLE.debugWarn("[Packet Delivery Failure] <portaldock> Block Entity not equal to expected.");
			}			
		});
		
		ctx.setPacketHandled(true);
	}
}
