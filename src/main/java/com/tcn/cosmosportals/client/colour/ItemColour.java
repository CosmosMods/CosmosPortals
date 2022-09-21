package com.tcn.cosmosportals.client.colour;

import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmosportals.core.management.ModObjectHolder;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemColour implements ItemColor {

	@Override
	public int getColor(ItemStack stackIn, int itemLayerIn) {
		Item item = stackIn.getItem();
		
		if (item.equals(ModObjectHolder.item_dimension_container)) {
			if (stackIn.hasTag()) {
				CompoundTag stack_tag = stackIn.getTag();
				
				if (stack_tag.contains("nbt_data")) {
					CompoundTag nbt_data = stack_tag.getCompound("nbt_data");
					
					if (nbt_data.contains("dimension_data")) {
						CompoundTag dimension_data = nbt_data.getCompound("dimension_data");
						
						if (dimension_data.contains("colour")) {
							int colour = dimension_data.getInt("colour");
							
							if (itemLayerIn == 0) {
								return colour;
							}
						}
					}
				}
			} else {
				return ComponentColour.WHITE.dec();
			}
		} else if (item.equals(ModObjectHolder.block_portal_frame.asItem())) {
			return ComponentColour.GRAY.dec();
		} else if (item.equals(ModObjectHolder.block_portal_dock.asItem())) {
			if (itemLayerIn == 0) {
				return ComponentColour.GRAY.dec();
			} else if (itemLayerIn == 1) {
				return ComponentColour.WHITE.dec();
			}
		}
		
		if (itemLayerIn == 0) {
			return ComponentColour.GRAY.dec();
		} else {
			return ComponentColour.WHITE.dec();
		}
	}
}