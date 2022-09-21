package com.tcn.cosmosportals.core.management;

import com.tcn.cosmosportals.CosmosPortals;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = CosmosPortals.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class CoreSoundManager {
	
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CosmosPortals.MOD_ID);

	//public static final ForgeSoundType PORTAL_TYPE = new ForgeSoundType();
	
	public static final RegistryObject<SoundEvent> PORTAL_TRAVEL = SOUNDS.register("portal_travel", () -> new SoundEvent(new ResourceLocation(CosmosPortals.MOD_ID + ":" + "portal_travel")));
	
	public static final RegistryObject<SoundEvent> PORTAL_CREATE = SOUNDS.register("portal_create", () -> new SoundEvent(new ResourceLocation(CosmosPortals.MOD_ID + ":" + "portal_create")));
	public static final RegistryObject<SoundEvent> PORTAL_DESTROY = SOUNDS.register("portal_destroy", () -> new SoundEvent(new ResourceLocation(CosmosPortals.MOD_ID + ":" + "portal_destroy")));
	
	public static void register(IEventBus bus) {
		SOUNDS.register(bus);
	}
}