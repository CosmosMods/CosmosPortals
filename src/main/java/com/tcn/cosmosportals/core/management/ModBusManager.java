package com.tcn.cosmosportals.core.management;

import com.google.common.base.Preconditions;
import com.tcn.cosmoslibrary.common.block.CosmosBlock;
import com.tcn.cosmoslibrary.common.interfaces.IBlankCreativeTab;
import com.tcn.cosmoslibrary.common.item.CosmosItem;
import com.tcn.cosmoslibrary.common.runtime.CosmosRuntimeHelper;
import com.tcn.cosmoslibrary.common.tab.CosmosCreativeModeTab;
import com.tcn.cosmosportals.CosmosPortals;
import com.tcn.cosmosportals.client.colour.BlockColour;
import com.tcn.cosmosportals.client.colour.ItemColour;
import com.tcn.cosmosportals.client.container.ContainerPortalDock;
import com.tcn.cosmosportals.client.renderer.RendererPortalDock;
import com.tcn.cosmosportals.client.screen.ScreenConfiguration;
import com.tcn.cosmosportals.client.screen.ScreenPortalDock;
import com.tcn.cosmosportals.core.block.BlockPortal;
import com.tcn.cosmosportals.core.block.BlockPortalDock;
import com.tcn.cosmosportals.core.block.BlockPortalFrame;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortal;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortalDock;
import com.tcn.cosmosportals.core.item.BlockItemPortalDock;
import com.tcn.cosmosportals.core.item.BlockItemPortalFrame;
import com.tcn.cosmosportals.core.item.ItemPortalContainer;
import com.tcn.cosmosportals.core.item.ItemPortalGuide;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigGuiHandler.ConfigGuiFactory;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = CosmosPortals.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModBusManager {
	
	public static final CosmosCreativeModeTab COSMOS_PORTALS_ITEM_GROUP = new CosmosCreativeModeTab(CosmosPortals.MOD_ID, () -> new ItemStack(ModBusManager.DIMENSION_CONTAINER));
	
	public static final Item COSMIC_GUIDE = new ItemPortalGuide(new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP).stacksTo(1));
	
	public static final Item DIMENSION_CONTAINER = new ItemPortalContainer(new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP).stacksTo(1).fireResistant());
	public static final Item COSMIC_MATERIAL = new CosmosItem(new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP));
	public static final Item COSMIC_INGOT = new CosmosItem(new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP));
	public static final Item COSMIC_PEARL = new CosmosItem(new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP));
	public static final Item COSMIC_GEM = new CosmosItem(new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP));
	
	public static final Block COSMIC_ORE = new CosmosBlock(Block.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(2.0F, 3.0F));
	public static final Block DEEPSLATE_COSMIC_ORE = new CosmosBlock(Block.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(4.0F, 6.0F).sound(SoundType.DEEPSLATE));
	public static final Block COSMIC_BLOCK = new CosmosBlock(Block.Properties.of(Material.HEAVY_METAL).requiresCorrectToolForDrops().strength(5.0F, 7.0F));
	
	public static final Block PORTAL_FRAME = new BlockPortalFrame(Block.Properties.of(Material.HEAVY_METAL).requiresCorrectToolForDrops().strength(6.0F, 8.0F));
	public static final BlockItem ITEM_PORTAL_FRAME = new BlockItemPortalFrame(PORTAL_FRAME, new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP));
	
	public static final Block PORTAL = new BlockPortal(Block.Properties.of(Material.PORTAL).strength(-1, 3600000.0F).noOcclusion().randomTicks().noCollission().lightLevel((state) -> { return 10; } ));
	public static BlockEntityType<BlockEntityPortal> PORTAL_BLOCK_ENTITY_TYPE;
	
	public static final Block PORTAL_DOCK = new BlockPortalDock(BlockBehaviour.Properties.of(Material.HEAVY_METAL).requiresCorrectToolForDrops().strength(6.0F, 8.0F).lightLevel((state) -> { return 7; } ));
	public static final BlockItem ITEM_PORTAL_DOCK = new BlockItemPortalDock(PORTAL_DOCK, new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP));
	public static BlockEntityType<BlockEntityPortalDock> DOCK_BLOCK_ENTITY_TYPE;
	public static MenuType<ContainerPortalDock> DOCK_CONTAINER_TYPE;
	
	@SubscribeEvent
	public static void onItemRegistry(final RegistryEvent.Register<Item> event) {
		final IForgeRegistry<Item> registry = event.getRegistry();
		
		event.getRegistry().registerAll(
			CosmosRuntimeHelper.setupString(CosmosPortals.MOD_ID, "item_cosmic_guide", COSMIC_GUIDE),
			CosmosRuntimeHelper.setupString(CosmosPortals.MOD_ID, "item_cosmic_material", COSMIC_MATERIAL),
			CosmosRuntimeHelper.setupString(CosmosPortals.MOD_ID, "item_cosmic_ingot", COSMIC_INGOT),
			CosmosRuntimeHelper.setupString(CosmosPortals.MOD_ID, "item_cosmic_pearl", COSMIC_PEARL),
			CosmosRuntimeHelper.setupString(CosmosPortals.MOD_ID, "item_cosmic_gem", COSMIC_GEM),
			CosmosRuntimeHelper.setupString(CosmosPortals.MOD_ID, "item_dimension_container", DIMENSION_CONTAINER)
		);
		
		//Register BlockItems
		for (final Block block : ForgeRegistries.BLOCKS.getValues()) {
			final ResourceLocation blockRegistryName = block.getRegistryName();
			Preconditions.checkNotNull(blockRegistryName, "Registry Name of Block \"" + block + "\" of class \"" + block.getClass().getName() + "\"is null! This is not allowed!");

			if (!blockRegistryName.getNamespace().equals(CosmosPortals.MOD_ID)) {
				continue;
			}

			else if (block instanceof IBlankCreativeTab) {
				final Item.Properties properties = new Item.Properties();
				final BlockItem blockItem = new BlockItem(block, properties);
				registry.register(CosmosRuntimeHelper.setupResource(blockRegistryName, blockItem));
			} else if (block instanceof BlockPortalFrame) {
				registry.register(CosmosRuntimeHelper.setupResource(blockRegistryName, ITEM_PORTAL_FRAME));
			} else if (block instanceof BlockPortalDock) {
				registry.register(CosmosRuntimeHelper.setupResource(blockRegistryName, ITEM_PORTAL_DOCK));
			}
			
			else {
				final Item.Properties properties = new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP);
				final BlockItem blockItem = new BlockItem(block, properties);
				registry.register(CosmosRuntimeHelper.setupResource(blockRegistryName, blockItem));
			}
		}
		
		CosmosPortals.CONSOLE.startup("Item Registration complete.");
	}
	
	@SubscribeEvent
	public static void onBlockRegistry(final RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(
			CosmosRuntimeHelper.setupString(CosmosPortals.MOD_ID, "block_cosmic_ore", COSMIC_ORE),
			CosmosRuntimeHelper.setupString(CosmosPortals.MOD_ID, "block_deepslate_cosmic_ore", DEEPSLATE_COSMIC_ORE),
			CosmosRuntimeHelper.setupString(CosmosPortals.MOD_ID, "block_cosmic", COSMIC_BLOCK),
			CosmosRuntimeHelper.setupString(CosmosPortals.MOD_ID, "block_portal_frame", PORTAL_FRAME), 
			CosmosRuntimeHelper.setupString(CosmosPortals.MOD_ID, "block_portal_dock", PORTAL_DOCK), 
			CosmosRuntimeHelper.setupString(CosmosPortals.MOD_ID, "block_portal", PORTAL)
		);
		
		CosmosPortals.CONSOLE.startup("Block Registration complete.");
	}
	
	@SubscribeEvent
	public static void onMenuTypeRegistry(final RegistryEvent.Register<MenuType<?>> event) {
		DOCK_CONTAINER_TYPE = IForgeMenuType.create(ContainerPortalDock::new);
		DOCK_CONTAINER_TYPE.setRegistryName(new ResourceLocation(CosmosPortals.MOD_ID, "container_portal_dock"));

		event.getRegistry().registerAll(DOCK_CONTAINER_TYPE);
		
		CosmosPortals.CONSOLE.startup("MenuType<> Registration complete.");
	}
	
	@SubscribeEvent
	public static void onBlockEntityTypeRegistry(final RegistryEvent.Register<BlockEntityType<?>> event) {
		DOCK_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.<BlockEntityPortalDock>of(BlockEntityPortalDock::new, PORTAL_DOCK).build(null);
		DOCK_BLOCK_ENTITY_TYPE.setRegistryName(new ResourceLocation(CosmosPortals.MOD_ID, "tile_portal_dock"));
		
		PORTAL_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.<BlockEntityPortal>of(BlockEntityPortal::new, PORTAL).build(null);
		PORTAL_BLOCK_ENTITY_TYPE.setRegistryName(new ResourceLocation(CosmosPortals.MOD_ID, "tile_portal"));
		
		event.getRegistry().registerAll(DOCK_BLOCK_ENTITY_TYPE, PORTAL_BLOCK_ENTITY_TYPE);
		CosmosPortals.CONSOLE.startup("BlockEntityType<> Registration complete.");
	}

	@SubscribeEvent
	public static void onBlockEntityRendererRegistry(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(DOCK_BLOCK_ENTITY_TYPE, RendererPortalDock::new);
		CosmosPortals.CONSOLE.startup("BlockEntityRenderer Registration complete.");
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void registerClient(ModLoadingContext context) {
		
		context.registerExtensionPoint(ConfigGuiFactory.class, () -> ScreenConfiguration.getInstance());
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void onFMLClientSetup(FMLClientSetupEvent event) {
		RenderType cutoutMipped = RenderType.cutoutMipped();
		RenderType translucent = RenderType.translucent();
		
		MenuScreens.register(DOCK_CONTAINER_TYPE, ScreenPortalDock::new);
		
		CosmosRuntimeHelper.setRenderLayers(cutoutMipped, PORTAL_DOCK);
		CosmosRuntimeHelper.setRenderLayers(translucent, PORTAL);
		
		CosmosRuntimeHelper.registerBlockColours(new BlockColour(), PORTAL_FRAME, PORTAL_DOCK, PORTAL);
		CosmosRuntimeHelper.registerItemColours(new ItemColour(), DIMENSION_CONTAINER, ITEM_PORTAL_FRAME, ITEM_PORTAL_DOCK);
	}
}