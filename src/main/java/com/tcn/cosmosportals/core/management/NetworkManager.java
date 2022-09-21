package com.tcn.cosmosportals.core.management;

import com.tcn.cosmosportals.CosmosPortals;
import com.tcn.cosmosportals.core.network.PacketGuideUpdate;
import com.tcn.cosmosportals.core.network.PacketPortalDock;
import com.tcn.cosmosportals.core.network.PacketPortalDockColour;
import com.tcn.cosmosportals.core.network.PacketPortalDockName;

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
		INSTANCE.registerMessage(1, PacketGuideUpdate.class, PacketGuideUpdate::encode, PacketGuideUpdate::new, PacketGuideUpdate::handle);
		INSTANCE.registerMessage(2, PacketPortalDockName.class, PacketPortalDockName::encode, PacketPortalDockName::new, PacketPortalDockName::handle);
		INSTANCE.registerMessage(3, PacketPortalDockColour.class, PacketPortalDockColour::encode, PacketPortalDockColour::new, PacketPortalDockColour::handle);
		
		CosmosPortals.CONSOLE.info("CosmosPortals Network Setup complete.");
	}

	public static void sendToServer(Object message) {
        INSTANCE.sendToServer(message);
    }
}