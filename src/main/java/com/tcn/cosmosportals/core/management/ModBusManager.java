package com.tcn.cosmosportals.core.management;

import java.util.List;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.tcn.cosmoslibrary.common.block.CosmosBlock;
import com.tcn.cosmoslibrary.common.item.CosmosItem;
import com.tcn.cosmoslibrary.common.runtime.CosmosRuntimeHelper;
import com.tcn.cosmoslibrary.common.tab.CosmosCreativeModeTab;
import com.tcn.cosmoslibrary.data.worldgen.CosmosWorldGenHelper;
import com.tcn.cosmosportals.CosmosPortals;
import com.tcn.cosmosportals.client.colour.BlockColour;
import com.tcn.cosmosportals.client.colour.ItemColour;
import com.tcn.cosmosportals.client.container.ContainerContainerWorkbench;
import com.tcn.cosmosportals.client.container.ContainerPortalDock;
import com.tcn.cosmosportals.client.renderer.RendererContainerWorkbench;
import com.tcn.cosmosportals.client.renderer.RendererPortalDock;
import com.tcn.cosmosportals.client.screen.ScreenConfigurationCommon;
import com.tcn.cosmosportals.client.screen.ScreenContainerWorkbench;
import com.tcn.cosmosportals.client.screen.ScreenPortalDock;
import com.tcn.cosmosportals.core.block.BlockContainerWorkbench;
import com.tcn.cosmosportals.core.block.BlockPortal;
import com.tcn.cosmosportals.core.block.BlockPortalDock;
import com.tcn.cosmosportals.core.block.BlockPortalFrame;
import com.tcn.cosmosportals.core.blockentity.BlockEntityContainerWorkbench;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortal;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortalDock;
import com.tcn.cosmosportals.core.item.BlockItemContainerWorkbench;
import com.tcn.cosmosportals.core.item.BlockItemPortalDock;
import com.tcn.cosmosportals.core.item.BlockItemPortalFrame;
import com.tcn.cosmosportals.core.item.ItemPortalContainer;
import com.tcn.cosmosportals.core.item.ItemPortalGuide;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = CosmosPortals.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModBusManager {
	
	/** - DEFERRED REGISTERS - */
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CosmosPortals.MOD_ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CosmosPortals.MOD_ID);
	
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CosmosPortals.MOD_ID);
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, CosmosPortals.MOD_ID);
	
	public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, CosmosPortals.MOD_ID);
	public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, CosmosPortals.MOD_ID);
	
	
	public static final CosmosCreativeModeTab COSMOS_PORTALS_ITEM_GROUP = new CosmosCreativeModeTab(CosmosPortals.MOD_ID, () -> new ItemStack(ModObjectHolder.item_dimension_container_unlinked));
	
	public static final RegistryObject<Item> COSMIC_GUIDE = ITEMS.register("item_cosmic_guide", () ->  new ItemPortalGuide(new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP).stacksTo(1)));
	
	public static final RegistryObject<Item> DIMENSION_CONTAINER_LINKED = ITEMS.register("item_dimension_container", () -> new ItemPortalContainer(new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP).stacksTo(1).fireResistant(), true));
	public static final RegistryObject<Item> DIMENSION_CONTAINER = ITEMS.register("item_dimension_container_unlinked", () -> new ItemPortalContainer(new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP).stacksTo(16).fireResistant(), false));

	public static final RegistryObject<Item> COSMIC_MATERIAL = ITEMS.register("item_cosmic_material", () -> new CosmosItem(new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP)));
	public static final RegistryObject<Item> COSMIC_INGOT = ITEMS.register("item_cosmic_ingot", () -> new CosmosItem(new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP)));
	public static final RegistryObject<Item> COSMIC_PEARL = ITEMS.register("item_cosmic_pearl", () -> new CosmosItem(new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP)));
	public static final RegistryObject<Item> COSMIC_GEM = ITEMS.register("item_cosmic_gem", () -> new CosmosItem(new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP)));
		
	public static final RegistryObject<Block> COSMIC_ORE = BLOCKS.register("block_cosmic_ore", () -> new CosmosBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(2.0F, 3.0F)));
	public static final RegistryObject<Item> ITEM_COSMIC_ORE = ITEMS.register("block_cosmic_ore", () -> new BlockItem(ModObjectHolder.block_cosmic_ore, new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP)));
	
	public static final RegistryObject<Block> DEEPSLATE_COSMIC_ORE = BLOCKS.register("block_deepslate_cosmic_ore", () -> new CosmosBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(4.0F, 6.0F).sound(SoundType.DEEPSLATE)));
	public static final RegistryObject<Item> ITEM_DEEPSLATE_COSMIC_ORE = ITEMS.register("block_deepslate_cosmic_ore", () -> new BlockItem(ModObjectHolder.block_deepslate_cosmic_ore, new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP)));
	
	public static final RegistryObject<Block> COSMIC_BLOCK = BLOCKS.register("block_cosmic", () -> new CosmosBlock(BlockBehaviour.Properties.of(Material.HEAVY_METAL).requiresCorrectToolForDrops().strength(5.0F, 7.0F)));
	public static final RegistryObject<Item> ITEM_COSMIC_BLOCK = ITEMS.register("block_cosmic", () -> new BlockItem(ModObjectHolder.block_cosmic, new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP)));
	
	public static final RegistryObject<Block> PORTAL_FRAME = BLOCKS.register("block_portal_frame", () -> new BlockPortalFrame(BlockBehaviour.Properties.of(Material.HEAVY_METAL).requiresCorrectToolForDrops().strength(6.0F, 8.0F)));
	public static final RegistryObject<Item> ITEM_PORTAL_FRAME = ITEMS.register("block_portal_frame", () -> new BlockItemPortalFrame(ModObjectHolder.block_portal_frame, new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP)));
	
	public static final RegistryObject<Block> BLOCK_PORTAL = BLOCKS.register("block_portal", () -> new BlockPortal(Block.Properties.of(Material.PORTAL).strength(-1, 3600000.0F).noOcclusion().randomTicks().noCollission().lightLevel((state) -> { return 10; } )));
	public static final RegistryObject<Item> ITEM_PORTAL = ITEMS.register("block_portal", () -> new BlockItemPortalFrame(ModObjectHolder.block_portal, new Item.Properties()));
	public static final RegistryObject<BlockEntityType<?>> BLOCK_ENTITY_TYPE_PORTAL = BLOCK_ENTITY_TYPES.register("tile_portal", () -> BlockEntityType.Builder.<BlockEntityPortal>of(BlockEntityPortal::new, ModObjectHolder.block_portal).build(null));
	
	public static final RegistryObject<Block> PORTAL_DOCK = BLOCKS.register("block_portal_dock", () -> new BlockPortalDock(BlockBehaviour.Properties.of(Material.HEAVY_METAL).requiresCorrectToolForDrops().strength(6.0F, 8.0F).lightLevel((state) -> { return 7; } )));
	public static final RegistryObject<Item> ITEM_PORTAL_DOCK = ITEMS.register("block_portal_dock", () -> new BlockItemPortalDock(ModObjectHolder.block_portal_dock, new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP)));
	public static final RegistryObject<BlockEntityType<?>> BLOCK_ENTITY_TYPE_PORTAL_DOCK = BLOCK_ENTITY_TYPES.register("tile_portal_dock", () -> BlockEntityType.Builder.<BlockEntityPortalDock>of(BlockEntityPortalDock::new, ModObjectHolder.block_portal_dock).build(null));
	public static final RegistryObject<MenuType<?>> MENU_TYPE_PORTAL_DOCK = MENU_TYPES.register("container_portal_dock", () -> IForgeMenuType.create(ContainerPortalDock::new));
	
	public static final RegistryObject<Block> CONTAINER_WORKBENCH = BLOCKS.register("block_container_workbench", () -> new BlockContainerWorkbench(BlockBehaviour.Properties.of(Material.HEAVY_METAL).requiresCorrectToolForDrops().strength(4.0F,6.0F).noOcclusion()));
	public static final RegistryObject<Item> ITEM_CONTAINER_WORKBENCH = ITEMS.register("block_container_workbench", () -> new BlockItemContainerWorkbench(ModObjectHolder.block_container_workbench, new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP)));
	public static final RegistryObject<BlockEntityType<?>> BLOCK_ENTITY_TYPE_CONTAINER_WORKBENCH = BLOCK_ENTITY_TYPES.register("tile_container_workbench", () -> BlockEntityType.Builder.<BlockEntityContainerWorkbench>of(BlockEntityContainerWorkbench::new, ModObjectHolder.block_container_workbench).build(null));
	public static final RegistryObject<MenuType<?>> MENU_TYPE_CONTAINER_WORKBENCH = MENU_TYPES.register("container_container_workbench", () -> IForgeMenuType.create(ContainerContainerWorkbench::new));
	
	
	/** - ORE GENERATION - */
	public static final Supplier<List<OreConfiguration.TargetBlockState>> CONFIGURED_COSMIC_ORES = Suppliers.memoize(() -> List.of(
		OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, ModObjectHolder.block_cosmic_ore.defaultBlockState()),
		OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, ModObjectHolder.block_deepslate_cosmic_ore.defaultBlockState())
	));
	
	public static final RegistryObject<ConfiguredFeature<?, ?>> CONFIGURED_COSMIC_ORE = CONFIGURED_FEATURES.register("configured_cosmic_ore", () -> 
		new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(CONFIGURED_COSMIC_ORES.get(), 6))
	);
	public static final RegistryObject<PlacedFeature> PLACED_COSMIC_ORE = PLACED_FEATURES.register("placed_cosmic_ore", () -> 
		new PlacedFeature(CONFIGURED_COSMIC_ORE.getHolder().get(), CosmosWorldGenHelper.commonOrePlacement(6, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(120))))
	);
	
	
	public static void register(IEventBus bus) {
		BLOCKS.register(bus);
		ITEMS.register(bus);
		
		BLOCK_ENTITY_TYPES.register(bus);
		MENU_TYPES.register(bus);
		CONFIGURED_FEATURES.register(bus);
		PLACED_FEATURES.register(bus);
	}
	
	@SubscribeEvent
	public static void onBlockEntityRendererRegistry(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(ModObjectHolder.tile_portal_dock, RendererPortalDock::new);
		event.registerBlockEntityRenderer(ModObjectHolder.tile_container_workbench, RendererContainerWorkbench::new);
		CosmosPortals.CONSOLE.startup("BlockEntityRenderer Registration complete.");
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void registerClient(ModLoadingContext context) {
		context.registerExtensionPoint(ConfigScreenFactory.class, () -> ScreenConfigurationCommon.getInstance());
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void onFMLClientSetup(FMLClientSetupEvent event) {
		RenderType cutoutMipped = RenderType.cutoutMipped();
		RenderType translucent = RenderType.translucent();
		
		MenuScreens.register(ModObjectHolder.container_portal_dock, ScreenPortalDock::new);
		MenuScreens.register(ModObjectHolder.container_container_workbench, ScreenContainerWorkbench::new);
		
		CosmosRuntimeHelper.setRenderLayers(cutoutMipped, ModObjectHolder.block_portal_dock);
		CosmosRuntimeHelper.setRenderLayers(translucent, ModObjectHolder.block_portal);
		
		CosmosRuntimeHelper.registerBlockColours(new BlockColour(), ModObjectHolder.block_portal_frame, ModObjectHolder.block_portal_dock, ModObjectHolder.block_portal);
		CosmosRuntimeHelper.registerItemColours(new ItemColour(), ModObjectHolder.item_dimension_container, ModObjectHolder.block_portal_frame, ModObjectHolder.block_portal_dock);
	}
}