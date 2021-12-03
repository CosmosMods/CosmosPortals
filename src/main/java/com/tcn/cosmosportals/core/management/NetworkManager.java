package com.tcn.cosmosportals.core.management;

import com.tcn.cosmosportals.CosmosPortals;
import com.tcn.cosmosportals.core.network.PacketPortalDock;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkManager {

	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
		new ResourceLocation(CosmosPortals.MOD_ID, "portal_net"), 
		() -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals
	);
	
	public static void register() {
		INSTANCE.registerMessage(0, PacketPortalDock.class, PacketPortalDock::encode, PacketPortalDock::new, PacketPortalDock::handle);

		CosmosPortals.CONSOLE.info("Packets Registered");
	}

	public static void sendToServer(Object message) {
        INSTANCE.sendToServer(message);
    }
}