package com.tcn.cosmosportals.core.network;

import java.util.function.Supplier;

import com.tcn.cosmosportals.CosmosPortals;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortalDock;

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
			ServerLevel world = ctx.getSender().getLevel();
			BlockEntity tile = world.getBlockEntity(packet.pos);
			
			if (tile instanceof BlockEntityPortalDock) {
				BlockEntityPortalDock tileDock = (BlockEntityPortalDock) tile;
			
				if (packet.id == 0) {
					tileDock.toggleRenderLabel();
				} else if (packet.id == 1) {
					tileDock.togglePlaySound();
				} else if (packet.id == 2) {
					tileDock.toggleEntities();
				} else if (packet.id == 3) {
					tileDock.toggleParticles();
				} else {
					CosmosPortals.CONSOLE.warning("[FAIL] Id not recognised.");
				}
			} else {
				CosmosPortals.CONSOLE.warning("[FAIL] TileEntity not instanceof!");
			}
			
		});
		
		ctx.setPacketHandled(true);
	}
}
