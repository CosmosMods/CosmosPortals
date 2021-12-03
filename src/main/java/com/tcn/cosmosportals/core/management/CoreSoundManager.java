package com.tcn.cosmosportals.core.management;

import com.tcn.cosmosportals.CosmosPortals;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Mod.EventBusSubscriber(modid = CosmosPortals.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class CoreSoundManager {

	//public static final ForgeSoundType PORTAL_TYPE = new ForgeSoundType();
	
	public static final SoundEvent PORTAL_TRAVEL = new SoundEvent(new ResourceLocation(CosmosPortals.MOD_ID + ":" + "portal_travel")).setRegistryName("portal_travel");
	
	public static final SoundEvent PORTAL_CREATE = new SoundEvent(new ResourceLocation(CosmosPortals.MOD_ID + ":" + "portal_create")).setRegistryName("portal_create");
	public static final SoundEvent PORTAL_DESTROY = new SoundEvent(new ResourceLocation(CosmosPortals.MOD_ID + ":" + "portal_destroy")).setRegistryName("portal_destroy");
	
	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
		event.getRegistry().registerAll(PORTAL_TRAVEL, PORTAL_CREATE, PORTAL_DESTROY);
	}
}