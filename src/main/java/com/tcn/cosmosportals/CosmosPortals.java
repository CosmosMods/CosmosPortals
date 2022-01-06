package com.tcn.cosmosportals;

import com.tcn.cosmoslibrary.common.runtime.CosmosConsoleManager;
import com.tcn.cosmosportals.core.management.ConfigurationManager;
import com.tcn.cosmosportals.core.management.EventManager;
import com.tcn.cosmosportals.core.management.ModBusManager;
import com.tcn.cosmosportals.core.management.NetworkManager;

import net.minecraftforge.common.MinecraftForge;
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

	public static final CosmosConsoleManager CONSOLE = new CosmosConsoleManager(CosmosPortals.MOD_ID, ConfigurationManager.getInstance().getDebugMessage(), ConfigurationManager.getInstance().getInfoMessage());

	public CosmosPortals() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFMLCommonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFMLClientSetup);
		MinecraftForge.EVENT_BUS.register(this);
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigurationManager.spec, "cosmos-portals-common.toml");
	}

	public void onFMLCommonSetup(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			EventManager.registerOresForGeneration();
			
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