package com.tcn.cosmosportals;

import net.minecraft.resources.ResourceLocation;

public class CosmosPortalsReference {
	
	public static final String PRE = CosmosPortals.MOD_ID + ":";

	public static final String RESOURCE = PRE + "textures/";
	public static final String GUI = RESOURCE + "gui/";

	public static final String BLOCKS = PRE + "blocks/";
	public static final String ITEMS = RESOURCE + "items/";
	
	public static final String MODELS = RESOURCE + "models/";
		
	public static final ResourceLocation DOCK_BACKGROUND = new ResourceLocation(GUI + "background.png");
	public static final ResourceLocation DOCK_FRAME = new ResourceLocation(GUI + "frame.png");
	public static final ResourceLocation DOCK_PORTAL = new ResourceLocation(GUI + "portal.png");
	public static final ResourceLocation DOCK_CONTAINER = new ResourceLocation(GUI + "container.png");
	public static final ResourceLocation DOCK_SLOTS = new ResourceLocation(GUI + "slots.png");
	public static final ResourceLocation DOCK_LABEL = new ResourceLocation(GUI + "label.png");

}
