package com.tcn.cosmosportals.core.management;

import java.util.ArrayList;

import com.tcn.cosmosportals.CosmosPortals;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Mod.EventBusSubscriber(modid = CosmosPortals.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
public class CoreEventManager {

	private static final ArrayList<ConfiguredFeature<?, ?>> overworldOres = new ArrayList<ConfiguredFeature<?, ?>>();
	
	@SubscribeEvent
	public static void onBiomeLoadingEvent(final BiomeLoadingEvent event) {
		BiomeGenerationSettingsBuilder generation = event.getGeneration();
		
		for (ConfiguredFeature<?, ?> ore : overworldOres) {
			if (ore != null)
				generation.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, ore);
		}
	}

	public static void registerOresForGeneration() {
		overworldOres.add(register("dimensional_ore", Feature.ORE.configured(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, CoreModBusManager.COSMIC_ORE.defaultBlockState(), 4))
			.range(48).squared().count(24)));
	}

	private static <FC extends IFeatureConfig> ConfiguredFeature<FC, ?> register(String name, ConfiguredFeature<FC, ?> configuredFeature) {
		return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, CosmosPortals.MOD_ID + ":" + name, configuredFeature);
	}
}
