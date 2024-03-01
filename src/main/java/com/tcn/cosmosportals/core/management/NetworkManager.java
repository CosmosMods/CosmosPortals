package com.tcn.cosmosportals.core.management;

import com.tcn.cosmosportals.CosmosPortals;
import com.tcn.cosmosportals.core.network.PacketColour;
import com.tcn.cosmosportals.core.network.PacketGuideUpdate;
import com.tcn.cosmosportals.core.network.PacketNextSlot;
import com.tcn.cosmosportals.core.network.PacketPortalDock;
import com.tcn.cosmosportals.core.network.PacketWorkbenchName;

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
		INSTANCE.registerMessage(2, PacketWorkbenchName.class, PacketWorkbenchName::encode, PacketWorkbenchName::new, PacketWorkbenchName::handle);
		INSTANCE.registerMessage(3, PacketColour.class, PacketColour::encode, PacketColour::new, PacketColour::handle);
		INSTANCE.registerMessage(4, PacketNextSlot.class, PacketNextSlot::encode, PacketNextSlot::new, PacketNextSlot::handle);
		
		CosmosPortals.CONSOLE.info("CosmosPortals Network Setup complete.");
	}

	public static void sendToServer(Object message) {
        INSTANCE.sendToServer(message);
    }
}