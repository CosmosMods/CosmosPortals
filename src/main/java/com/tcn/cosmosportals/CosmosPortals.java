package com.tcn.cosmosportals;

import com.tcn.cosmoslibrary.common.runtime.CosmosConsoleManager;
import com.tcn.cosmosportals.core.management.ConfigurationManagerCommon;
import com.tcn.cosmosportals.core.management.CoreSoundManager;
import com.tcn.cosmosportals.core.management.ModBusManager;
import com.tcn.cosmosportals.core.management.NetworkManager;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CosmosPortals.MOD_ID)
public class CosmosPortals {
	
	//This must NEVER EVER CHANGE!
	public static final String MOD_ID = "cosmosportals";

	public static CosmosConsoleManager CONSOLE = new CosmosConsoleManager(CosmosPortals.MOD_ID, true, true);

	public CosmosPortals() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigurationManagerCommon.spec, "cosmos-portals-common.toml");
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		
		ModBusManager.register(bus);
		CoreSoundManager.register(bus);
		
		bus.addListener(this::onFMLCommonSetup);
		bus.addListener(this::onFMLClientSetup);

		MinecraftForge.EVENT_BUS.register(this);
	}

	public void onFMLCommonSetup(final FMLCommonSetupEvent event) {
		CONSOLE = new CosmosConsoleManager(CosmosPortals.MOD_ID, ConfigurationManagerCommon.getInstance().getDebugMessage(), ConfigurationManagerCommon.getInstance().getInfoMessage());
		
		event.enqueueWork(() -> {			
			NetworkManager.register();
		});
		
		CONSOLE.startup("CosmosPortals Common Setup complete.");
	}

	public void onFMLClientSetup(final FMLClientSetupEvent event) {
		final ModLoadingContext context = ModLoadingContext.get();
		
		ModBusManager.registerClient(context);
		ModBusManager.onFMLClientSetup(event);
		
		CONSOLE.startup("CosmosPortals Client Setup complete.");
	}
}