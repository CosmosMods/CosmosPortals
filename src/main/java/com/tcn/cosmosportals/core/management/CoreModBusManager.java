package com.tcn.cosmosportals.core.management;

import com.google.common.base.Preconditions;
import com.tcn.cosmoslibrary.common.block.CosmosBlock;
import com.tcn.cosmoslibrary.common.interfaces.IItemGroupNone;
import com.tcn.cosmoslibrary.common.item.CosmosItem;
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
import com.tcn.cosmosportals.core.item.BlockItemPortalDock;
import com.tcn.cosmosportals.core.item.BlockItemPortalFrame;
import com.tcn.cosmosportals.core.item.ItemPortalContainer;
import com.tcn.cosmosportals.core.tileentity.TileEntityPortal;
import com.tcn.cosmosportals.core.tileentity.TileEntityPortalDock;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

@Mod.EventBusSubscriber(modid = CosmosPortals.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class CoreModBusManager {
	
	public static final Item DIMENSION_CONTAINER = new ItemPortalContainer(new Item.Properties().tab(CoreGroupManager.COSMOS_PORTALS_ITEM_GROUP).stacksTo(1).fireResistant());
	public static final Item COSMIC_MATERIAL = new CosmosItem(new Item.Properties().tab(CoreGroupManager.COSMOS_PORTALS_ITEM_GROUP));
	public static final Item COSMIC_INGOT = new CosmosItem(new Item.Properties().tab(CoreGroupManager.COSMOS_PORTALS_ITEM_GROUP));
	public static final Item COSMIC_PEARL = new CosmosItem(new Item.Properties().tab(CoreGroupManager.COSMOS_PORTALS_ITEM_GROUP));
	public static final Item COSMIC_GEM = new CosmosItem(new Item.Properties().tab(CoreGroupManager.COSMOS_PORTALS_ITEM_GROUP));
	
	public static final Block COSMIC_ORE = new CosmosBlock(Block.Properties.of(Material.STONE).harvestLevel(2).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops().strength(4.0F, 6.0F));
	public static final Block COSMIC_BLOCK = new CosmosBlock(Block.Properties.of(Material.HEAVY_METAL).harvestLevel(2).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops().strength(5.0F, 7.0F));
	
	public static final Block PORTAL_FRAME = new BlockPortalFrame(Block.Properties.of(Material.HEAVY_METAL).harvestLevel(2).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops().strength(6.0F, 8.0F));
	public static final BlockItem ITEM_PORTAL_FRAME = new BlockItemPortalFrame(PORTAL_FRAME, new Item.Properties().tab(CoreGroupManager.COSMOS_PORTALS_ITEM_GROUP));
	
	public static final Block PORTAL = new BlockPortal(Block.Properties.of(Material.PORTAL).sound(SoundType.GLASS).harvestLevel(2).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops().strength(-1, 3600000.0F).noOcclusion().randomTicks().noCollission().lightLevel((state) -> { return 10; } ));
	public static TileEntityType<TileEntityPortal> PORTAL_TILE_TYPE;
	
	public static final Block PORTAL_DOCK = new BlockPortalDock(Block.Properties.of(Material.HEAVY_METAL).harvestLevel(2).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops().strength(6.0F, 8.0F).lightLevel((state) -> { return 7; } ));
	public static final BlockItem ITEM_PORTAL_DOCK = new BlockItemPortalDock(PORTAL_DOCK, new Item.Properties().tab(CoreGroupManager.COSMOS_PORTALS_ITEM_GROUP));
	public static TileEntityType<TileEntityPortalDock> DOCK_TILE_TYPE;
	public static ContainerType<ContainerPortalDock> DOCK_CONTAINER_TYPE;
	
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

			else if (block instanceof IItemGroupNone) {
				final Item.Properties properties = new Item.Properties();
				final BlockItem blockItem = new BlockItem(block, properties);
				registry.register(setupResource(blockRegistryName, blockItem));
			} else if (block instanceof BlockPortalFrame) {
				registry.register(setupResource(blockRegistryName, ITEM_PORTAL_FRAME));
			} else if (block instanceof BlockPortalDock) {
				registry.register(setupResource(blockRegistryName, ITEM_PORTAL_DOCK));
			}
			
			else {
				final Item.Properties properties = new Item.Properties().tab(CoreGroupManager.COSMOS_PORTALS_ITEM_GROUP);
				final BlockItem blockItem = new BlockItem(block, properties);
				registry.register(setupResource(blockRegistryName, blockItem));
			}
		}
		
		CoreConsole.startup("BlockItems & Items Registered...");
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
		
		CoreConsole.startup("Blocks Registered.");
	}
	
	@SubscribeEvent
	public static void onContainerTypeRegistry(final RegistryEvent.Register<ContainerType<?>> event) {
		DOCK_CONTAINER_TYPE = IForgeContainerType.create(ContainerPortalDock::new);
		DOCK_CONTAINER_TYPE.setRegistryName(new ResourceLocation(CosmosPortals.MOD_ID, "container_portal_dock"));

		event.getRegistry().registerAll(DOCK_CONTAINER_TYPE);
		
		CoreConsole.startup("ContainerTypes Registered.");
	}
	
	@SubscribeEvent
	public static void onTileEntityTypeRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
		DOCK_TILE_TYPE = TileEntityType.Builder.<TileEntityPortalDock>of(TileEntityPortalDock::new, PORTAL_DOCK).build(null);
		DOCK_TILE_TYPE.setRegistryName(new ResourceLocation(CosmosPortals.MOD_ID, "tile_portal_dock"));
		
		PORTAL_TILE_TYPE = TileEntityType.Builder.<TileEntityPortal>of(TileEntityPortal::new, PORTAL).build(null);
		PORTAL_TILE_TYPE.setRegistryName(new ResourceLocation(CosmosPortals.MOD_ID, "tile_portal"));
		
		event.getRegistry().registerAll(DOCK_TILE_TYPE, PORTAL_TILE_TYPE);
		CoreConsole.startup("TileEntityTypes Registered.");
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void registerClient(ModLoadingContext context) {
		context.registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, screen) -> new ScreenConfiguration(screen));
		
		CoreConsole.startup("ClientRegistry complete.");
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void onFMLClientSetup(FMLClientSetupEvent event) {
		RenderType cutout_mipped = RenderType.cutoutMipped();
		RenderType translucent = RenderType.translucent();
		
		ScreenManager.register(DOCK_CONTAINER_TYPE, ScreenPortalDock::new);
		
		ClientRegistry.bindTileEntityRenderer(DOCK_TILE_TYPE, RendererPortalDock::new);
		
		setRenderLayers(cutout_mipped, PORTAL_DOCK);
		setRenderLayers(translucent, PORTAL);
		
		registerBlockColours(new BlockColour(), PORTAL_FRAME, PORTAL_DOCK, PORTAL);
		
		registerItemColours(new ItemColour(), DIMENSION_CONTAINER, PORTAL_FRAME, PORTAL_DOCK);
		
		CoreConsole.startup("FMLClientSetup complete.");
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void setRenderLayers(RenderType renderType, Block... blocks) {
		for (Block block : blocks) {
			RenderTypeLookup.setRenderLayer(block, renderType);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void registerItemColours(IItemColor colour, IItemProvider... items) {
		ItemColors itemColours = Minecraft.getInstance().getItemColors();
		
		for (IItemProvider item : items) {
			itemColours.register(colour, item);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void registerBlockColours(IBlockColor colour, Block... blocks) {
		BlockColors blockColours = Minecraft.getInstance().getBlockColors();
		
		for (Block block : blocks) {
			blockColours.register(colour, block);
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