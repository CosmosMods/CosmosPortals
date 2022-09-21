package com.tcn.cosmosportals;

import net.minecraft.resources.ResourceLocation;

public class CosmosPortalsReference {
	
	public static final String PRE = CosmosPortals.MOD_ID + ":";

	public static final String RESOURCE = PRE + "textures/";
	public static final String GUI = RESOURCE + "gui/";

	public static final String BLOCKS = PRE + "blocks/";
	public static final String ITEMS = RESOURCE + "items/";
	
	public static final String MODELS = RESOURCE + "models/";
		
	public static final ResourceLocation[] DOCK = new ResourceLocation[] { new ResourceLocation(GUI + "dock/background.png"), new ResourceLocation(GUI + "dock/background_dark.png") };
	public static final ResourceLocation[] DOCK_SLOTS = new ResourceLocation[] { new ResourceLocation(GUI + "dock/slots.png"), new ResourceLocation(GUI + "dock/slots_dark.png") };
	public static final ResourceLocation[] DOCK_SETTINGS = new ResourceLocation[] { new ResourceLocation(GUI + "dock/background_settings.png"), new ResourceLocation(GUI + "dock/background_settings_dark.png") };
	public static final ResourceLocation[] DOCK_OVERLAY = new ResourceLocation[] { new ResourceLocation(GUI + "dock/overlay_settings.png"), new ResourceLocation(GUI + "dock/overlay_settings_dark.png") };
	public static final ResourceLocation DOCK_FRAME = new ResourceLocation(GUI + "dock/frame.png");
	public static final ResourceLocation DOCK_PORTAL = new ResourceLocation(GUI + "dock/portal.png");
	public static final ResourceLocation DOCK_BACKING = new ResourceLocation(GUI + "dock/backing.png");
	public static final ResourceLocation DOCK_CONTAINER = new ResourceLocation(GUI + "dock/container.png");
	public static final ResourceLocation DOCK_LABEL = new ResourceLocation(GUI + "dock/label.png");
	
	public static final ResourceLocation[] WORKBENCH = new ResourceLocation[] { new ResourceLocation(GUI + "workbench/background.png"), new ResourceLocation(GUI + "workbench/background_dark.png") };
	public static final ResourceLocation[] WORKBENCH_SLOTS = new ResourceLocation[] { new ResourceLocation(GUI + "workbench/slots.png"), new ResourceLocation(GUI + "workbench/slots_dark.png") };
	
	public static final ResourceLocation[] GUIDE = new ResourceLocation[] { new ResourceLocation(CosmosPortals.MOD_ID, "textures/gui/guide/guide.png"), new ResourceLocation(CosmosPortals.MOD_ID, "textures/gui/guide/guide_dark.png") };
	public static final ResourceLocation GUIDE_FLAT_TEXTURES = new ResourceLocation(CosmosPortals.MOD_ID, "textures/gui/guide/textures_flat.png");
	public static final ResourceLocation GUIDE_BLOCK_TEXTURES = new ResourceLocation(CosmosPortals.MOD_ID, "textures/gui/guide/blocks.png");
}