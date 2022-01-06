package com.tcn.cosmosportals;

import net.minecraft.resources.ResourceLocation;

public class CosmosPortalsReference {
	
	public static final String PRE = CosmosPortals.MOD_ID + ":";

	public static final String RESOURCE = PRE + "textures/";
	public static final String GUI = RESOURCE + "gui/";

	public static final String BLOCKS = PRE + "blocks/";
	public static final String ITEMS = RESOURCE + "items/";
	
	public static final String MODELS = RESOURCE + "models/";
		
	public static final ResourceLocation[] DOCK = new ResourceLocation[] { new ResourceLocation(GUI + "background.png"), new ResourceLocation(GUI + "background_dark.png") };
	public static final ResourceLocation[] DOCK_SLOTS = new ResourceLocation[] { new ResourceLocation(GUI + "slots.png"), new ResourceLocation(GUI + "slots_dark.png") };
	public static final ResourceLocation DOCK_FRAME = new ResourceLocation(GUI + "frame.png");
	public static final ResourceLocation DOCK_PORTAL = new ResourceLocation(GUI + "portal.png");
	public static final ResourceLocation DOCK_BACKING = new ResourceLocation(GUI + "backing.png");
	public static final ResourceLocation DOCK_CONTAINER = new ResourceLocation(GUI + "container.png");
	public static final ResourceLocation DOCK_LABEL = new ResourceLocation(GUI + "label.png");
	
	
	
	public static final ResourceLocation[] GUIDE = new ResourceLocation[] { new ResourceLocation(CosmosPortals.MOD_ID, "textures/gui/guide/guide.png"), new ResourceLocation(CosmosPortals.MOD_ID, "textures/gui/guide/guide_dark.png") };
	public static final ResourceLocation GUIDE_FLAT_TEXTURES = new ResourceLocation(CosmosPortals.MOD_ID, "textures/gui/guide/textures_flat.png");
	public static final ResourceLocation GUIDE_BLOCK_TEXTURES = new ResourceLocation(CosmosPortals.MOD_ID, "textures/gui/guide/blocks.png");
}