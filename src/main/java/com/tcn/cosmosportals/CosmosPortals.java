package com.tcn.cosmosportals;

import com.tcn.cosmosportals.core.management.CoreConfigurationManager;
import com.tcn.cosmosportals.core.management.CoreEventManager;
import com.tcn.cosmosportals.core.management.CoreModBusManager;
import com.tcn.cosmosportals.core.management.CoreNetworkManager;

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

	public CosmosPortals() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFMLCommonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFMLClientSetup);
		MinecraftForge.EVENT_BUS.register(this);
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CoreConfigurationManager.spec, "cosmos-portals-common.toml");
	}

	public void onFMLCommonSetup(final FMLCommonSetupEvent event) {
		CoreEventManager.registerOresForGeneration();
		
		CoreNetworkManager.register();
		
		//CoreConsoleManager.message(LEVEL.STARTUP, "FMLCommonSetup complete.");
	}

	public void onFMLClientSetup(final FMLClientSetupEvent event) {
		final ModLoadingContext context = ModLoadingContext.get();
		
		CoreModBusManager.registerClient(context);
		CoreModBusManager.onFMLClientSetup(event);
	}
}
