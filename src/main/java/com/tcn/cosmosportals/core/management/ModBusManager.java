package com.tcn.cosmosportals.core.management;

import java.util.ArrayList;
import java.util.function.Supplier;

import com.tcn.cosmoslibrary.common.block.CosmosBlock;
import com.tcn.cosmoslibrary.common.item.CosmosItem;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.runtime.CosmosRuntimeHelper;
import com.tcn.cosmosportals.CosmosPortals;
import com.tcn.cosmosportals.client.colour.BlockColour;
import com.tcn.cosmosportals.client.colour.ItemColour;
import com.tcn.cosmosportals.client.container.ContainerContainerWorkbench;
import com.tcn.cosmosportals.client.container.ContainerPortalDock;
import com.tcn.cosmosportals.client.container.ContainerPortalDockUpgraded;
import com.tcn.cosmosportals.client.renderer.RendererContainerWorkbench;
import com.tcn.cosmosportals.client.renderer.RendererPortalDock;
import com.tcn.cosmosportals.client.screen.ScreenConfigurationCommon;
import com.tcn.cosmosportals.client.screen.ScreenContainerWorkbench;
import com.tcn.cosmosportals.client.screen.ScreenPortalDock;
import com.tcn.cosmosportals.client.screen.ScreenPortalDockUpgraded;
import com.tcn.cosmosportals.core.block.BlockContainerWorkbench;
import com.tcn.cosmosportals.core.block.BlockPortal;
import com.tcn.cosmosportals.core.block.BlockPortalDock;
import com.tcn.cosmosportals.core.block.BlockPortalDockUpgraded;
import com.tcn.cosmosportals.core.block.BlockPortalFrame;
import com.tcn.cosmosportals.core.blockentity.BlockEntityContainerWorkbench;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortal;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortalDock;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortalDockUpgraded;
import com.tcn.cosmosportals.core.item.BlockItemContainerWorkbench;
import com.tcn.cosmosportals.core.item.BlockItemPortalDock;
import com.tcn.cosmosportals.core.item.BlockItemPortalDockUpgraded;
import com.tcn.cosmosportals.core.item.BlockItemPortalFrame;
import com.tcn.cosmosportals.core.item.ItemPortalContainer;
import com.tcn.cosmosportals.core.item.ItemPortalGuide;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
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
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CosmosPortals.MOD_ID);
	
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CosmosPortals.MOD_ID);
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, CosmosPortals.MOD_ID);
	
	public static final ArrayList<Supplier<? extends ItemLike>> TAB_ITEMS = new ArrayList<>();
	
	public static final RegistryObject<CreativeModeTab> COSMOS_PORTALS_ITEM_GROUP = TABS.register("cosmos_portals", 
		() -> CreativeModeTab.builder()
		.title(ComponentHelper.style(ComponentColour.POCKET_PURPLE, "Cosmos Portals")).icon(() -> { return new ItemStack(ModObjectHolder.item_dimension_container_unlinked); })
		.displayItems((params, output) -> TAB_ITEMS.forEach(itemLike -> output.accept(itemLike.get())))
		.withSearchBar()
		.build()
	);
	
	public static final RegistryObject<Item> COSMIC_GUIDE = addToTab(ITEMS.register("item_cosmic_guide", () ->  new ItemPortalGuide(new Item.Properties().stacksTo(1))));
	
	public static final RegistryObject<Item> DIMENSION_CONTAINER_LINKED = addToTab(ITEMS.register("item_dimension_container", () -> new ItemPortalContainer(new Item.Properties().stacksTo(1).fireResistant(), true)));
	public static final RegistryObject<Item> DIMENSION_CONTAINER = addToTab(ITEMS.register("item_dimension_container_unlinked", () -> new ItemPortalContainer(new Item.Properties().stacksTo(16).fireResistant(), false)));

	public static final RegistryObject<Item> COSMIC_MATERIAL = addToTab(ITEMS.register("item_cosmic_material", () -> new CosmosItem(new Item.Properties())));
	public static final RegistryObject<Item> COSMIC_INGOT = addToTab(ITEMS.register("item_cosmic_ingot", () -> new CosmosItem(new Item.Properties())));
	public static final RegistryObject<Item> COSMIC_PEARL = addToTab(ITEMS.register("item_cosmic_pearl", () -> new CosmosItem(new Item.Properties())));
	public static final RegistryObject<Item> COSMIC_GEM = addToTab(ITEMS.register("item_cosmic_gem", () -> new CosmosItem(new Item.Properties())));
		
	public static final RegistryObject<Block> COSMIC_ORE = BLOCKS.register("block_cosmic_ore", () -> new CosmosBlock(BlockBehaviour.Properties.of().sound(SoundType.STONE).requiresCorrectToolForDrops().strength(2.0F, 3.0F)));
	public static final RegistryObject<Item> ITEM_COSMIC_ORE = addToTab(ITEMS.register("block_cosmic_ore", () -> new BlockItem(ModObjectHolder.block_cosmic_ore, new Item.Properties())));
	
	public static final RegistryObject<Block> DEEPSLATE_COSMIC_ORE = BLOCKS.register("block_deepslate_cosmic_ore", () -> new CosmosBlock(BlockBehaviour.Properties.of().sound(SoundType.STONE).requiresCorrectToolForDrops().strength(4.0F, 6.0F).sound(SoundType.DEEPSLATE)));
	public static final RegistryObject<Item> ITEM_DEEPSLATE_COSMIC_ORE = addToTab(ITEMS.register("block_deepslate_cosmic_ore", () -> new BlockItem(ModObjectHolder.block_deepslate_cosmic_ore, new Item.Properties())));
	
	public static final RegistryObject<Block> COSMIC_BLOCK = BLOCKS.register("block_cosmic", () -> new CosmosBlock(BlockBehaviour.Properties.of().sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5.0F, 7.0F)));
	public static final RegistryObject<Item> ITEM_COSMIC_BLOCK = addToTab(ITEMS.register("block_cosmic", () -> new BlockItem(ModObjectHolder.block_cosmic, new Item.Properties())));
	
	public static final RegistryObject<Block> PORTAL_FRAME = BLOCKS.register("block_portal_frame", () -> new BlockPortalFrame(BlockBehaviour.Properties.of().sound(SoundType.METAL).requiresCorrectToolForDrops().strength(6.0F, 8.0F).lightLevel((state) -> { return 10; })));
	public static final RegistryObject<Item> ITEM_PORTAL_FRAME = addToTab(ITEMS.register("block_portal_frame", () -> new BlockItemPortalFrame(ModObjectHolder.block_portal_frame, new Item.Properties())));
	
	public static final RegistryObject<Block> BLOCK_PORTAL = BLOCKS.register("block_portal", () -> new BlockPortal(Block.Properties.of().sound(SoundType.METAL).strength(-1, 3600000.0F).noOcclusion().randomTicks().noCollission().lightLevel((state) -> { return 10; } )));
	public static final RegistryObject<Item> ITEM_PORTAL = ITEMS.register("block_portal", () -> new BlockItemPortalFrame(ModObjectHolder.block_portal, new Item.Properties()));
	public static final RegistryObject<BlockEntityType<?>> BLOCK_ENTITY_TYPE_PORTAL = BLOCK_ENTITY_TYPES.register("tile_portal", () -> BlockEntityType.Builder.<BlockEntityPortal>of(BlockEntityPortal::new, ModObjectHolder.block_portal).build(null));
	
	public static final RegistryObject<Block> PORTAL_DOCK = BLOCKS.register("block_portal_dock", () -> new BlockPortalDock(BlockBehaviour.Properties.of().sound(SoundType.METAL).requiresCorrectToolForDrops().strength(6.0F, 8.0F).lightLevel((state) -> { return 7; } )));
	public static final RegistryObject<Item> ITEM_PORTAL_DOCK = addToTab(ITEMS.register("block_portal_dock", () -> new BlockItemPortalDock(ModObjectHolder.block_portal_dock, new Item.Properties())));
	public static final RegistryObject<BlockEntityType<?>> BLOCK_ENTITY_TYPE_PORTAL_DOCK = BLOCK_ENTITY_TYPES.register("tile_portal_dock", () -> BlockEntityType.Builder.<BlockEntityPortalDock>of(BlockEntityPortalDock::new, ModObjectHolder.block_portal_dock).build(null));
	public static final RegistryObject<MenuType<?>> MENU_TYPE_PORTAL_DOCK = MENU_TYPES.register("container_portal_dock", () -> IForgeMenuType.create(ContainerPortalDock::new));

	public static final RegistryObject<Block> PORTAL_DOCK_UPGRADED = BLOCKS.register("block_portal_dock_upgraded", () -> new BlockPortalDockUpgraded(BlockBehaviour.Properties.of().sound(SoundType.METAL).requiresCorrectToolForDrops().strength(6.0F, 8.0F).lightLevel((state) -> { return 7; } )));
	public static final RegistryObject<Item> ITEM_PORTAL_DOCK_UPGRADED = addToTab(ITEMS.register("block_portal_dock_upgraded", () -> new BlockItemPortalDockUpgraded(ModObjectHolder.block_portal_dock_upgraded, new Item.Properties())));
	public static final RegistryObject<BlockEntityType<?>> BLOCK_ENTITY_TYPE_PORTAL_DOCK_UPGRADED = BLOCK_ENTITY_TYPES.register("tile_portal_dock_upgraded", () -> BlockEntityType.Builder.<BlockEntityPortalDockUpgraded>of(BlockEntityPortalDockUpgraded::new, ModObjectHolder.block_portal_dock_upgraded).build(null));
	public static final RegistryObject<MenuType<?>> MENU_TYPE_PORTAL_DOCK_UPGRADED = MENU_TYPES.register("container_portal_dock_upgraded", () -> IForgeMenuType.create(ContainerPortalDockUpgraded::new));
	
	public static final RegistryObject<Block> CONTAINER_WORKBENCH = BLOCKS.register("block_container_workbench", () -> new BlockContainerWorkbench(BlockBehaviour.Properties.of().sound(SoundType.METAL).requiresCorrectToolForDrops().strength(4.0F,6.0F).noOcclusion()));
	public static final RegistryObject<Item> ITEM_CONTAINER_WORKBENCH = addToTab(ITEMS.register("block_container_workbench", () -> new BlockItemContainerWorkbench(ModObjectHolder.block_container_workbench, new Item.Properties())));
	public static final RegistryObject<BlockEntityType<?>> BLOCK_ENTITY_TYPE_CONTAINER_WORKBENCH = BLOCK_ENTITY_TYPES.register("tile_container_workbench", () -> BlockEntityType.Builder.<BlockEntityContainerWorkbench>of(BlockEntityContainerWorkbench::new, ModObjectHolder.block_container_workbench).build(null));
	public static final RegistryObject<MenuType<?>> MENU_TYPE_CONTAINER_WORKBENCH = MENU_TYPES.register("container_container_workbench", () -> IForgeMenuType.create(ContainerContainerWorkbench::new));
	
	public static void register(IEventBus bus) {
		BLOCKS.register(bus);
		ITEMS.register(bus);
		
		BLOCK_ENTITY_TYPES.register(bus);
		MENU_TYPES.register(bus);
		
		TABS.register(bus);
	}

    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) { }
    
	@SubscribeEvent
	public static void onBlockEntityRendererRegistry(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(ModObjectHolder.tile_portal_dock, RendererPortalDock::new);
		event.registerBlockEntityRenderer(ModObjectHolder.tile_portal_dock_upgraded, RendererPortalDock::new);
		event.registerBlockEntityRenderer(ModObjectHolder.tile_container_workbench, RendererContainerWorkbench::new);
		CosmosPortals.CONSOLE.startup("BlockEntityRenderer Registration complete.");
	}
	
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onRegisterColourHandlersEventBlock(RegisterColorHandlersEvent.Block event) {
		CosmosRuntimeHelper.registerBlockColours(event, new BlockColour(), ModObjectHolder.block_portal_frame, ModObjectHolder.block_portal_dock, ModObjectHolder.block_portal_dock_upgraded, ModObjectHolder.block_portal);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onRegisterColourHandlersEventItem(RegisterColorHandlersEvent.Item event) {
		CosmosRuntimeHelper.registerItemColours(event, new ItemColour(), ModObjectHolder.item_dimension_container, ModObjectHolder.block_portal_frame, ModObjectHolder.block_portal_dock, ModObjectHolder.block_portal_dock_upgraded);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void registerClient(ModLoadingContext context) {
		context.registerExtensionPoint(ConfigScreenFactory.class, () -> ScreenConfigurationCommon.getInstance());
	}
	
	@SuppressWarnings("deprecation")
	@OnlyIn(Dist.CLIENT)
	public static void onFMLClientSetup(FMLClientSetupEvent event) {
		RenderType cutoutMipped = RenderType.cutoutMipped();
		RenderType translucent = RenderType.translucent();
		
		MenuScreens.register(ModObjectHolder.container_portal_dock, ScreenPortalDock::new);
		MenuScreens.register(ModObjectHolder.container_portal_dock_upgraded, ScreenPortalDockUpgraded::new);
		MenuScreens.register(ModObjectHolder.container_container_workbench, ScreenContainerWorkbench::new);
		
		CosmosRuntimeHelper.setRenderLayers(cutoutMipped, ModObjectHolder.block_portal_dock);
		CosmosRuntimeHelper.setRenderLayers(cutoutMipped, ModObjectHolder.block_portal_dock_upgraded);
		CosmosRuntimeHelper.setRenderLayers(translucent, ModObjectHolder.block_portal);
	}

    public static <T extends Item> RegistryObject<T> addToTab(RegistryObject<T> itemLike) {
        TAB_ITEMS.add(itemLike);
        return itemLike;
    }

    public static <A extends Block> RegistryObject<A> addToTabA(RegistryObject<A> itemLike) {
        TAB_ITEMS.add(itemLike);
        return itemLike;
    }
    
}