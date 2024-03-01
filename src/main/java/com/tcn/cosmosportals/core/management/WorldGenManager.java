package com.tcn.cosmosportals.core.management;

import com.tcn.cosmosportals.CosmosPortals;
import com.tcn.cosmosportals.core.worldgen.CosmicOreFeature;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WorldGenManager {
	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, CosmosPortals.MOD_ID);
	
	public static final RegistryObject<Feature<?>> COSMIC_ORE = FEATURES.register("ore_cosmic", CosmicOreFeature::new);
	
	public static void register(IEventBus bus) {
		FEATURES.register(bus);
	}
}