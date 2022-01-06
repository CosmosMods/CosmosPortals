package com.tcn.cosmosportals.core.item;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.cosmosportals.client.screen.ScreenItemGuide;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemPortalGuide extends Item {

	public ItemPortalGuide(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		if (worldIn.isClientSide) {
			if (playerIn.getDisplayName().getString().equals("TheCosmicNebula_")) {
				this.openGUI(playerIn);
			} else {
				playerIn.sendMessage(ComponentHelper.locComp(ComponentColour.RED, false, "This feature is a WIP, sorry for the inconvenience."), UUID.randomUUID());
			}
		}
		
		playerIn.swing(handIn);
		return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
	}

	@OnlyIn(Dist.CLIENT)
	public void openGUI(Player playerIn) {
		Minecraft.getInstance().setScreen(new ScreenItemGuide(true, playerIn.getUUID(), CosmosUtil.getStack(playerIn)));
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		
		if (!ComponentHelper.isShiftKeyDown(Minecraft.getInstance())) {
			tooltip.add(ComponentHelper.getTooltipInfo("cosmosportals.item_info.guide"));
			
			if (ComponentHelper.displayShiftForDetail) {
				tooltip.add(ComponentHelper.shiftForMoreDetails());
			}
		} else {
			tooltip.add(ComponentHelper.getTooltipOne("cosmosportals.item_info.guide_one"));
			tooltip.add(ComponentHelper.getTooltipTwo("cosmosportals.item_info.guide_two"));
			
			tooltip.add(ComponentHelper.shiftForLessDetails());
		}
	}
	
	public static void setPage(ItemStack stackIn, int page) {
		if (stackIn.hasTag()) {
			CompoundTag compound = stackIn.getTag();
			
			compound.putInt("page", page);
		} else {
			CompoundTag compound = new CompoundTag();
			
			compound.putInt("page", page);
			
			stackIn.setTag(compound);
		}
	}
	
	public static int getPage(ItemStack stackIn) {
		if (stackIn.hasTag()) {
			CompoundTag compound = stackIn.getTag();
			
			return compound.getInt("page");
		}
		
		return 0;
	}
	

	public static void setUIMode(ItemStack stackIn, EnumUIMode mode) {
		if (stackIn.hasTag()) {
			CompoundTag compound = stackIn.getTag();
			
			compound.putInt("mode", mode.getIndex());
		} else {
			CompoundTag compound = new CompoundTag();
			
			compound.putInt("mode", mode.getIndex());
			
			stackIn.setTag(compound);
		}
	}
	
	public static EnumUIMode getUIMode(ItemStack stackIn) {
		if (stackIn.hasTag()) {
			CompoundTag compound = stackIn.getTag();
			
			return EnumUIMode.getStateFromIndex(compound.getInt("mode"));
		}
		
		return EnumUIMode.DARK;
	}
}
