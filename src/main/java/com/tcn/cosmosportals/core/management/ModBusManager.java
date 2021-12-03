package com.tcn.cosmosportals.core.management;

import com.google.common.base.Preconditions;
import com.tcn.cosmoslibrary.common.block.CosmosBlock;
import com.tcn.cosmoslibrary.common.interfaces.IBlankCreativeTab;
import com.tcn.cosmoslibrary.common.item.CosmosItem;
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

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
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
import net.minecraftforge.registries.IForgeRegistryEntry;

@Mod.EventBusSubscriber(modid = CosmosPortals.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModBusManager {
	
	public static final CosmosCreativeModeTab COSMOS_PORTALS_ITEM_GROUP = new CosmosCreativeModeTab(CosmosPortals.MOD_ID, () -> new ItemStack(ModBusManager.DIMENSION_CONTAINER));
	
	public static final Item DIMENSION_CONTAINER = new ItemPortalContainer(new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP).stacksTo(1).fireResistant());
	public static final Item COSMIC_MATERIAL = new CosmosItem(new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP));
	public static final Item COSMIC_INGOT = new CosmosItem(new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP));
	public static final Item COSMIC_PEARL = new CosmosItem(new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP));
	public static final Item COSMIC_GEM = new CosmosItem(new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP));
	
	public static final Block COSMIC_ORE = new CosmosBlock(Block.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(4.0F, 6.0F));
	public static final Block COSMIC_BLOCK = new CosmosBlock(Block.Properties.of(Material.HEAVY_METAL).requiresCorrectToolForDrops().strength(5.0F, 7.0F));
	
	public static final Block PORTAL_FRAME = new BlockPortalFrame(Block.Properties.of(Material.HEAVY_METAL).requiresCorrectToolForDrops().strength(6.0F, 8.0F));
	public static final BlockItem ITEM_PORTAL_FRAME = new BlockItemPortalFrame(PORTAL_FRAME, new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP));
	
	public static final Block PORTAL = new BlockPortal(Block.Properties.of(Material.PORTAL).strength(-1, 3600000.0F).noOcclusion().randomTicks().noCollission().lightLevel((state) -> { return 10; } ));
	public static BlockEntityType<BlockEntityPortal> BLOCK_ENTITY_TYPE_PORTAL;
	
	public static final Block PORTAL_DOCK = new BlockPortalDock(BlockBehaviour.Properties.of(Material.HEAVY_METAL).requiresCorrectToolForDrops().strength(6.0F, 8.0F).lightLevel((state) -> { return 7; } ));
	public static final BlockItem ITEM_PORTAL_DOCK = new BlockItemPortalDock(PORTAL_DOCK, new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP));
	public static BlockEntityType<BlockEntityPortalDock> BLOCK_ENTITY_TYPE_DOCK;
	public static MenuType<ContainerPortalDock> CONTAINER_TYPE_DOCK;
	
	@SubscribeEvent
	public static void onItemRegistry(final RegistryEvent.Register<Item> event) {
		final IForgeRegistry<Item> registry = event.getRegistry();
		
		event.getRegistry().registerAll(
			setupString("item_cosmic_material", COSMIC_MATERIAL),
			setupString("item_cosmic_ingot", COSMIC_INGOT),
			setupString("item_cosmic_pearl", COSMIC_PEARL),
			setupString("item_cosmic_gem", COSMIC_GEM),
			setupString("item_dimension_container", DIMENSION_CONTAINER)
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
				registry.register(setupResource(blockRegistryName, blockItem));
			} else if (block instanceof BlockPortalFrame) {
				registry.register(setupResource(blockRegistryName, ITEM_PORTAL_FRAME));
			} else if (block instanceof BlockPortalDock) {
				registry.register(setupResource(blockRegistryName, ITEM_PORTAL_DOCK));
			}
			
			else {
				final Item.Properties properties = new Item.Properties().tab(COSMOS_PORTALS_ITEM_GROUP);
				final BlockItem blockItem = new BlockItem(block, properties);
				registry.register(setupResource(blockRegistryName, blockItem));
			}
		}
		
		CosmosPortals.CONSOLE.startup("BlockItems & Items Registered...");
	}
	
	@SubscribeEvent
	public static void onBlockRegistry(final RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(
			setupString("block_cosmic_ore", COSMIC_ORE),
			setupString("block_cosmic", COSMIC_BLOCK),
			setupString("block_portal_frame", PORTAL_FRAME), 
			setupString("block_portal_dock", PORTAL_DOCK), 
			setupString("block_portal", PORTAL)
		);
		
		CosmosPortals.CONSOLE.startup("Blocks Registered.");
	}
	
	@SubscribeEvent
	public static void onContainerTypeRegistry(final RegistryEvent.Register<MenuType<?>> event) {
		CONTAINER_TYPE_DOCK = IForgeMenuType.create(ContainerPortalDock::new);
		CONTAINER_TYPE_DOCK.setRegistryName(new ResourceLocation(CosmosPortals.MOD_ID, "container_portal_dock"));

		event.getRegistry().registerAll(CONTAINER_TYPE_DOCK);
		
		CosmosPortals.CONSOLE.startup("ContainerTypes Registered.");
	}
	
	@SubscribeEvent
	public static void onBlockEntityTypeRegistry(final RegistryEvent.Register<BlockEntityType<?>> event) {
		BLOCK_ENTITY_TYPE_DOCK = BlockEntityType.Builder.<BlockEntityPortalDock>of(BlockEntityPortalDock::new, PORTAL_DOCK).build(null);
		BLOCK_ENTITY_TYPE_DOCK.setRegistryName(new ResourceLocation(CosmosPortals.MOD_ID, "tile_portal_dock"));
		
		BLOCK_ENTITY_TYPE_PORTAL = BlockEntityType.Builder.<BlockEntityPortal>of(BlockEntityPortal::new, PORTAL).build(null);
		BLOCK_ENTITY_TYPE_PORTAL.setRegistryName(new ResourceLocation(CosmosPortals.MOD_ID, "tile_portal"));
		
		event.getRegistry().registerAll(BLOCK_ENTITY_TYPE_DOCK, BLOCK_ENTITY_TYPE_PORTAL);
		CosmosPortals.CONSOLE.startup("BlockEntityTypes Registered.");
	}

	@SubscribeEvent
	public static void onBlockEntityRendererRegistry(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_DOCK, RendererPortalDock::new);
		CosmosPortals.CONSOLE.startup("BlockEntityRenderers Registered.");
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void registerClient(ModLoadingContext context) {
		
		context.registerExtensionPoint(ConfigGuiFactory.class, () -> ScreenConfiguration.getInstance());
		
		CosmosPortals.CONSOLE.startup("ClientRegistry complete.");
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void onFMLClientSetup(FMLClientSetupEvent event) {
		RenderType cutoutMipped = RenderType.cutoutMipped();
		RenderType translucent = RenderType.translucent();
		
		MenuScreens.register(CONTAINER_TYPE_DOCK, ScreenPortalDock::new);
		
		setRenderLayers(cutoutMipped, PORTAL_DOCK);
		setRenderLayers(translucent, PORTAL);
		
		registerBlockColours(new BlockColour(), PORTAL_FRAME, PORTAL_DOCK, PORTAL);
		registerItemColours(new ItemColour(), DIMENSION_CONTAINER, (Item)ITEM_PORTAL_FRAME, (Item)ITEM_PORTAL_DOCK);
		
		CosmosPortals.CONSOLE.startup("FMLClientSetup complete.");
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void setRenderLayers(RenderType renderType, Block... blocks) {
		for (Block block : blocks) {
			ItemBlockRenderTypes.setRenderLayer(block, renderType);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void registerBlockColours(BlockColor colour, Block... blocks) {
		BlockColors blockColours = Minecraft.getInstance().getBlockColors();
		
		for (Block block : blocks) {
			blockColours.register(colour, block);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void registerItemColours(ItemColor colour, Item... items) {
		ItemColors itemColours = Minecraft.getInstance().getItemColors();
		
		for (Item item : items) {
			itemColours.register(colour, item);
		}
	}

	public static <T extends IForgeRegistryEntry<T>> T setupString(final String name, final T entry) {
		return setupLow(entry, new ResourceLocation(CosmosPortals.MOD_ID, name));
	}
	
	public static <T extends IForgeRegistryEntry<T>> T setupResource(final ResourceLocation name, final T entry) {
		return setupLow(entry, name);
	}

	public static <T extends IForgeRegistryEntry<T>> T setupLow(final T entry, final ResourceLocation registryName) {
		entry.setRegistryName(registryName);
		return entry;
	}
}