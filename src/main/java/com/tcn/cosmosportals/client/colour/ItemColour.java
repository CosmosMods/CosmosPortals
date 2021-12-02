package com.tcn.cosmosportals.client.colour;

import com.tcn.cosmoslibrary.common.comp.CosmosColour;
import com.tcn.cosmosportals.core.management.ModBusManager;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemColour implements ItemColor {

	@Override
	public int getColor(ItemStack stackIn, int itemLayerIn) {
		Item item = stackIn.getItem();
		
		if (item.equals(ModBusManager.DIMENSION_CONTAINER)) {
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
				return CosmosColour.WHITE.dec();
			}
		} else if (item.equals(ModBusManager.ITEM_PORTAL_FRAME)) {
			return CosmosColour.GRAY.dec();
		} else if (item.equals(ModBusManager.ITEM_PORTAL_DOCK)) {
			if (itemLayerIn == 0) {
				return CosmosColour.GRAY.dec();
			} else if (itemLayerIn == 1) {
				return CosmosColour.WHITE.dec();
			}
		}
		
		if (itemLayerIn == 0) {
			return CosmosColour.GRAY.dec();
		} else {
			return CosmosColour.WHITE.dec();
		}
	}
}