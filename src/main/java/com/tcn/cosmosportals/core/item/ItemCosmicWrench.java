package com.tcn.cosmosportals.core.item;

import java.util.List;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmosportals.core.blockentity.AbstractBlockEntityPortalDock;
import com.tcn.cosmosportals.core.blockentity.BlockEntityDockController;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ItemCosmicWrench extends Item {

	public ItemCosmicWrench(Item.Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		
		if (!ComponentHelper.isShiftKeyDown(Minecraft.getInstance())) {
			tooltip.add(ComponentHelper.getTooltipInfo("cosmosportals.item_info.wrench"));
			
			if (ComponentHelper.displayShiftForDetail) {
				tooltip.add(ComponentHelper.shiftForMoreDetails());
			}
		} else {
			tooltip.add(ComponentHelper.getTooltipOne("cosmosportals.item_info.wrench_one"));
			tooltip.add(ComponentHelper.getTooltipTwo("cosmosportals.item_info.wrench_two"));
			tooltip.add(ComponentHelper.getTooltipThree("cosmosportals.item_info.wrench_four"));
			tooltip.add(ComponentHelper.getTooltipFour("cosmosportals.item_info.wrench_three"));
			
			tooltip.add(ComponentHelper.shiftForLessDetails());
		}
		
		if (stack.hasTag()) {
			CompoundTag stackTag = stack.getTag();
			CompoundTag dockInfo = stackTag.getCompound("dockInfo");
			
			int X = dockInfo.getInt("dockX");
			int Y = dockInfo.getInt("dockY");
			int Z = dockInfo.getInt("dockZ");
		
			if (!ComponentHelper.isControlKeyDown(Minecraft.getInstance())) {
	
				if (ComponentHelper.displayCtrlForDetail) {
					tooltip.add(ComponentHelper.ctrlForMoreDetails());
				}
				
			} else {
				tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "", "cosmosportals.item_info.wrench_info")
						.append(ComponentHelper.style(ComponentColour.GREEN, "bold", "[" + X + ", " + Y + ", " + Z + "]")));
				
				tooltip.add(ComponentHelper.ctrlForLessDetails());
			}
		}
	}

	@Override
	public boolean canAttackBlock(BlockState state, Level worldIn, BlockPos pos, Player player) {
		return false;
	}
	
	@Override
	public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
		return true;
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		Player playerIn = context.getPlayer();
		
		BlockPos pos = context.getClickedPos();
		Level world = context.getLevel();
		BlockEntity entity = world.getBlockEntity(pos);

		if (playerIn.isShiftKeyDown()) {
			if (entity != null) {
				if (entity instanceof AbstractBlockEntityPortalDock) {
					AbstractBlockEntityPortalDock dockEntity = (AbstractBlockEntityPortalDock) entity;
	
					CompoundTag tag = stack.getOrCreateTag();
					CompoundTag dockInfo = new CompoundTag();
					
					dockInfo.putInt("dockX", dockEntity.getBlockPos().getX());
					dockInfo.putInt("dockY", dockEntity.getBlockPos().getY());
					dockInfo.putInt("dockZ", dockEntity.getBlockPos().getZ());
					
					tag.put("dockInfo", dockInfo);
					
					CosmosChatUtil.sendClientPlayerMessage(playerIn, ComponentHelper.style(ComponentColour.GREEN, "bold", "cosmosportals.item.use.wrench_one"));
					return InteractionResult.SUCCESS;
				}

				if (entity instanceof BlockEntityDockController) {
					BlockEntityDockController controllerEntity = (BlockEntityDockController) entity;
					
					if (stack.hasTag()) {
						CompoundTag stackTag = stack.getTag();
						CompoundTag dockInfo = stackTag.getCompound("dockInfo");
						
						int X = dockInfo.getInt("dockX");
						int Y = dockInfo.getInt("dockY");
						int Z = dockInfo.getInt("dockZ");
						
						BlockPos setPos = new BlockPos(X, Y, Z);
						
						if (!controllerEntity.setDockPos(setPos)) {
							CosmosChatUtil.sendClientPlayerMessage(playerIn, ComponentHelper.style(ComponentColour.RED, "boldunderline", "cosmosportals.item.use.wrench_two"));
							return InteractionResult.FAIL;
						}

						CosmosChatUtil.sendClientPlayerMessage(playerIn, ComponentHelper.style(ComponentColour.GREEN, "bold", "cosmosportals.item.use.wrench_three"));
						return InteractionResult.SUCCESS;
					} else {
						CosmosChatUtil.sendClientPlayerMessage(playerIn, ComponentHelper.style(ComponentColour.RED, "bold", "cosmosportals.item.use.wrench_four"));
						return InteractionResult.FAIL;
					}
				}
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level levelIn, Player playerIn, InteractionHand handIn) {
		return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
	}
}
