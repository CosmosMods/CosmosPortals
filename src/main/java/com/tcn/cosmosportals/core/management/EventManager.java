package com.tcn.cosmosportals.core.management;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.tcn.cosmosportals.CosmosPortals;

import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = CosmosPortals.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
public class EventManager {

	private static final List<PlacedFeature> overworldOres = new ArrayList<>();

    @SubscribeEvent(priority = EventPriority.HIGH)
	public static void onBiomeLoadingEvent(final BiomeLoadingEvent event) {
    	BiomeGenerationSettingsBuilder generation = event.getGeneration();
		final List<Supplier<PlacedFeature>> features = generation.getFeatures(Decoration.UNDERGROUND_ORES);
		
		switch (event.getCategory()) {
			case THEEND:
				break;
			case NETHER:
				break;
			default:
				overworldOres.forEach((ore) -> features.add(() -> ore));
		}
	}

	public static void registerOresForGeneration() {
		final ConfiguredFeature<?, ?> cosmicOre = FeatureUtils.register("cosmosportals:block_cosmic_ore", 
			Feature.ORE.configured(new OreConfiguration(List.of( 
				OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, ModBusManager.COSMIC_ORE.defaultBlockState()),
				OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, ModBusManager.DEEPSLATE_COSMIC_ORE.defaultBlockState())
			),
		12)));
		
		final PlacedFeature placedCosmicOre = PlacementUtils.register("cosmosportals:block_cosmic_ore", 
			cosmicOre.placed(HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(120)), InSquarePlacement.spread(), CountPlacement.of(20))
		);
		
		overworldOres.add(placedCosmicOre);
		
		CosmosPortals.CONSOLE.info("Ore Registration complete.");
	}

	private static List<PlacementModifier> orePlacement(PlacementModifier p_195347_, PlacementModifier p_195348_) {
		return List.of(p_195347_, InSquarePlacement.spread(), p_195348_, BiomeFilter.biome());
	}

	private static List<PlacementModifier> commonOrePlacement(int p_195344_, PlacementModifier p_195345_) {
		return orePlacement(CountPlacement.of(p_195344_), p_195345_);
	}
}