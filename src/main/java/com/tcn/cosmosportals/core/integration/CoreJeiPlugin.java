package com.tcn.cosmosportals.core.integration;

import com.tcn.cosmosportals.CosmosPortals;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class CoreJeiPlugin implements IModPlugin {

	public CoreJeiPlugin() { }

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(CosmosPortals.MOD_ID, "integration_jei");
	}

}
