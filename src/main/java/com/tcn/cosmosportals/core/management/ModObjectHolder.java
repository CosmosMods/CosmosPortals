package com.tcn.cosmosportals.core.management;

import com.tcn.cosmosportals.client.container.ContainerContainerWorkbench;
import com.tcn.cosmosportals.client.container.ContainerPortalDock;
import com.tcn.cosmosportals.client.container.ContainerPortalDockUpgraded;
import com.tcn.cosmosportals.core.blockentity.BlockEntityContainerWorkbench;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortal;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortalDock;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortalDockUpgraded;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class ModObjectHolder {
	
	@ObjectHolder(registryName = "minecraft:item", value = "cosmosportals:item_cosmic_guide")
	public static final Item item_cosmic_guide = null;

	@ObjectHolder(registryName = "minecraft:item", value = "cosmosportals:item_dimension_container")
	public static final Item item_dimension_container = null;

	@ObjectHolder(registryName = "minecraft:item", value = "cosmosportals:item_dimension_container_unlinked")
	public static final Item item_dimension_container_unlinked = null;


	@ObjectHolder(registryName = "minecraft:item", value = "cosmosportals:item_cosmic_material")
	public static final Item item_cosmic_material = null;
	@ObjectHolder(registryName = "minecraft:item", value = "cosmosportals:item_cosmic_ingot")
	public static final Item item_cosmic_ingot = null;
	@ObjectHolder(registryName = "minecraft:item", value = "cosmosportals:item_cosmic_pearl")
	public static final Item item_cosmic_pearl = null;
	@ObjectHolder(registryName = "minecraft:item", value = "cosmosportals:item_cosmic_gem")
	public static final Item item_cosmic_gem = null;

	@ObjectHolder(registryName = "minecraft:block", value = "cosmosportals:block_cosmic_ore")
	public static final Block block_cosmic_ore = null;
	@ObjectHolder(registryName = "minecraft:block", value = "cosmosportals:block_deepslate_cosmic_ore")
	public static final Block block_deepslate_cosmic_ore = null;
	@ObjectHolder(registryName = "minecraft:block", value = "cosmosportals:block_cosmic")
	public static final Block block_cosmic = null;
	
	@ObjectHolder(registryName = "minecraft:block", value = "cosmosportals:block_portal_frame")
	public static final Block block_portal_frame = null;

	@ObjectHolder(registryName = "minecraft:block", value = "cosmosportals:block_portal")
	public static final Block block_portal = null;
	@ObjectHolder(registryName = "minecraft:block_entity_type", value = "cosmosportals:tile_portal")
	public static final BlockEntityType<BlockEntityPortal> tile_portal = null;

	@ObjectHolder(registryName = "minecraft:block", value = "cosmosportals:block_portal_dock")
	public static final Block block_portal_dock = null;
	@ObjectHolder(registryName = "minecraft:block_entity_type", value = "cosmosportals:tile_portal_dock")
	public static final BlockEntityType<BlockEntityPortalDock> tile_portal_dock = null;
	@ObjectHolder(registryName = "minecraft:menu", value = "cosmosportals:container_portal_dock")
	public static final MenuType<ContainerPortalDock> container_portal_dock = null;

	@ObjectHolder(registryName = "minecraft:block", value = "cosmosportals:block_portal_dock_upgraded")
	public static final Block block_portal_dock_upgraded = null;
	@ObjectHolder(registryName = "minecraft:block_entity_type", value = "cosmosportals:tile_portal_dock_upgraded")
	public static final BlockEntityType<BlockEntityPortalDockUpgraded> tile_portal_dock_upgraded = null;
	@ObjectHolder(registryName = "minecraft:menu", value = "cosmosportals:container_portal_dock_upgraded")
	public static final MenuType<ContainerPortalDockUpgraded> container_portal_dock_upgraded = null;

	@ObjectHolder(registryName = "minecraft:block", value = "cosmosportals:block_container_workbench")
	public static final Block block_container_workbench = null;
	@ObjectHolder(registryName = "minecraft:block_entity_type", value = "cosmosportals:tile_container_workbench")
	public static final BlockEntityType<BlockEntityContainerWorkbench> tile_container_workbench = null;
	@ObjectHolder(registryName = "minecraft:menu", value = "cosmosportals:container_container_workbench")
	public static final MenuType<ContainerContainerWorkbench> container_container_workbench = null;
	
	@ObjectHolder(registryName = "minecraft:sound_event", value = "cosmosportals:portal_travel")
	public static final SoundEvent portal_travel = null;
	@ObjectHolder(registryName = "minecraft:sound_event", value = "cosmosportals:portal_create")
	public static final SoundEvent portal_create = null;
	@ObjectHolder(registryName = "minecraft:sound_event", value = "cosmosportals:portal_destroy")
	public static final SoundEvent portal_destroy = null;
}
