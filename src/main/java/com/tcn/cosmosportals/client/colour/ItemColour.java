package com.tcn.cosmosportals.client.colour;

import com.tcn.cosmoslibrary.common.comp.CosmosColour;
import com.tcn.cosmosportals.core.management.CoreModBusManager;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class ItemColour implements IItemColor {

	@Override
	public int getColor(ItemStack stackIn, int itemLayerIn) {
		Item item = stackIn.getItem();
		
		if (item.equals(CoreModBusManager.DIMENSION_CONTAINER)) {
			if (stackIn.hasTag()) {
				CompoundNBT stack_tag = stackIn.getTag();
				
				if (stack_tag.contains("nbt_data")) {
					CompoundNBT nbt_data = stack_tag.getCompound("nbt_data");
					
					if (nbt_data.contains("dimension_data")) {
						CompoundNBT dimension_data = nbt_data.getCompound("dimension_data");
						
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
		} else if (item.equals(CoreModBusManager.ITEM_PORTAL_FRAME)) {
			return CosmosColour.GRAY.dec();
		} else if (item.equals(CoreModBusManager.ITEM_PORTAL_DOCK)) {
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