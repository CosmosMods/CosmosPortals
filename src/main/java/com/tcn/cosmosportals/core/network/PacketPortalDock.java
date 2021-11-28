package com.tcn.cosmosportals.core.network;

import java.util.function.Supplier;

import com.tcn.cosmosportals.core.management.CoreConsole;
import com.tcn.cosmosportals.core.tileentity.TileEntityPortalDock;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketPortalDock  {
	
	private BlockPos pos;
	private Integer id;
	
	public PacketPortalDock(PacketBuffer buf) {
		this.pos = buf.readBlockPos();
		this.id = buf.readInt();
	}
	
	public PacketPortalDock(BlockPos pos, int id) {
		this.pos = pos;
		this.id = id;
	}
	
	public static void encode(PacketPortalDock packet, PacketBuffer buf) {
		buf.writeBlockPos(packet.pos);
		buf.writeInt(packet.id);
	}
	
	public static void handle(final PacketPortalDock packet, Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		
		ctx.enqueueWork(() -> {
			ServerWorld world = ctx.getSender().getLevel();
			TileEntity tile = world.getBlockEntity(packet.pos);
			
			if (tile instanceof TileEntityPortalDock) {
				TileEntityPortalDock tileDock = (TileEntityPortalDock) tile;
			
				if (packet.id == 0) {
					tileDock.toggleRenderLabel();
				} else if (packet.id == 1) {
					tileDock.togglePlaySound();
				} else if (packet.id == 2) {
					tileDock.toggleEntities();
				} else if (packet.id == 3) {
					tileDock.toggleParticles();
				} else {
					CoreConsole.warning("[FAIL] Id not recognised.");
				}
			} else {
				CoreConsole.warning("[FAIL] TileEntity not instanceof!");
			}
			
		});
		
		ctx.setPacketHandled(true);
	}
}
