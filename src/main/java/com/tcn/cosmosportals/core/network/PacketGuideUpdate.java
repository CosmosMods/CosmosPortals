package com.tcn.cosmosportals.core.network;

import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.cosmosportals.core.item.ItemPortalGuide;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

public class PacketGuideUpdate  {
	
	private UUID playerUUID;
	private int pageNum;
	private EnumUIMode mode;
	
	public PacketGuideUpdate(FriendlyByteBuf buf) {
		this.playerUUID = buf.readUUID();
		this.pageNum = buf.readInt();
		
		int index = buf.readInt();
		
		if (index > -1) {
			this.mode = EnumUIMode.getStateFromIndex(index);
		}
	}
	
	public PacketGuideUpdate(UUID id, int pageNumIn, @Nullable EnumUIMode modeIn) {
		this.playerUUID = id;
		this.pageNum = pageNumIn;
		this.mode = modeIn;
	}
	
	public static void encode(PacketGuideUpdate packet, FriendlyByteBuf buf) {
		buf.writeUUID(packet.playerUUID);
		buf.writeInt(packet.pageNum);
		
		if (packet.mode != null) {
			buf.writeInt(packet.mode.getIndex());
		} else {
			buf.writeInt(-1);
		}
	}
	
	public static void handle(final PacketGuideUpdate packet, Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		
		ctx.enqueueWork(() -> {
			ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(packet.playerUUID);
			
			ItemStack stack = CosmosUtil.getStack(player);
			
			if (stack != null) {
				if (packet.pageNum > -1) {
					ItemPortalGuide.setPage(stack, packet.pageNum);
				}
				
				if (packet.mode != null) {
					ItemPortalGuide.setUIMode(stack, packet.mode);
				}
			}
		});
		
		ctx.setPacketHandled(true);
	}
}
