package com.tcn.cosmosportals.core.management;

import javax.annotation.Nonnull;

import com.google.common.base.Supplier;
import com.tcn.cosmosportals.CosmosPortals;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class CoreGroupManager {

	public static final ItemGroup COSMOS_PORTALS_ITEM_GROUP = new ModItemGroup(CosmosPortals.MOD_ID, () -> new ItemStack(CoreModBusManager.DIMENSION_CONTAINER));
	
	public static final class ModItemGroup extends ItemGroup {

		@Nonnull
		private final Supplier<ItemStack> iconSupplier;

		public ModItemGroup(@Nonnull final String name, @Nonnull final Supplier<ItemStack> iconSupplier) {
			super(name);
			this.iconSupplier = iconSupplier;
		}
		
		@Override
		public ItemStack makeIcon() {
			return iconSupplier.get();
		}
	}
}
