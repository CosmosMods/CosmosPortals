package com.tcn.cosmosportals.core.item;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.text.WordUtils;

import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.item.CosmosItem;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper.Value;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortalDock;
import com.tcn.cosmosportals.core.management.EventFactory;
import com.tcn.cosmosportals.core.management.ModObjectHolder;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("deprecation")
public class ItemPortalContainer extends CosmosItem {
	
	public boolean linked;

	public ItemPortalContainer(Properties properties, boolean linkedIn) {
		super(properties);
		
		this.linked = linkedIn;
	}

	@Override
	public void appendHoverText(ItemStack stackIn, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		if (!ComponentHelper.isShiftKeyDown(Minecraft.getInstance())) {
			if (this.linked) {
				tooltip.add(ComponentHelper.getTooltipInfo("cosmosportals.item_info.container_linked"));
			} else {
				tooltip.add(ComponentHelper.getTooltipInfo("cosmosportals.item_info.container"));
			}
			
			if (ComponentHelper.displayShiftForDetail) {
				tooltip.add(ComponentHelper.shiftForMoreDetails());
			}
		} else {
			if (this.linked) {
				tooltip.add(ComponentHelper.getTooltipOne("cosmosportals.item_info.container_linked_one"));
				tooltip.add(ComponentHelper.getTooltipTwo("cosmosportals.item_info.container_linked_two"));
				
				if (stackIn.hasTag()) {
					tooltip.add(ComponentHelper.getTooltipThree("cosmosportals.item_info.container_linked_three"));
					CompoundTag stack_tag = stackIn.getTag();
					
					if (stack_tag.contains("nbt_data")) {
						CompoundTag nbt_data = stack_tag.getCompound("nbt_data");
						
						if (nbt_data.contains("position_data")) {
							CompoundTag position_data = nbt_data.getCompound("position_data");
							
							int[] position = new int [] { position_data.getInt("pos_x"), position_data.getInt("pos_y"), position_data.getInt("pos_z") };
							
							tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "cosmosportals.item_info.container_position")
									.append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ " + Value.CYAN + position[0] + Value.LIGHT_GRAY + ", " + Value.CYAN + position[1] + Value.LIGHT_GRAY + ", " + Value.CYAN + position[2] + Value.LIGHT_GRAY + " ]")));
						}
	
						if (nbt_data.contains("dimension_data")) {
							CompoundTag dimension_data = nbt_data.getCompound("dimension_data");
							
							String namespace = dimension_data.getString("namespace");
							String path = dimension_data.getString("path");
							
							tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "cosmosportals.item_info.container_dimension")
									.append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ " + Value.GREEN + namespace + Value.LIGHT_GRAY + ": " + Value.GREEN + path + Value.LIGHT_GRAY + " ]")));
						}
					}
					
					tooltip.add(ComponentHelper.shiftForLessDetails());
				} else {
					tooltip.add(ComponentHelper.getErrorText("cosmosportals.item_info.container_linked_error"));
				}
			} else {
				tooltip.add(ComponentHelper.getTooltipOne("cosmosportals.item_info.container_one"));
				tooltip.add(ComponentHelper.getTooltipTwo("cosmosportals.item_info.container_two"));
				
				tooltip.add(ComponentHelper.shiftForLessDetails());
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
	public InteractionResult onItemUseFirst(ItemStack stackIn, UseOnContext context) {
		Player playerIn = context.getPlayer();
		BlockPos pos = context.getClickedPos();
		Level level = context.getLevel();
		BlockEntity entity = level.getBlockEntity(pos);
		
		if (entity != null) {
			if (entity instanceof BlockEntityPortalDock) {
				playerIn.swing(context.getHand());
				return InteractionResult.PASS;
			}
		}
		
		return InteractionResult.PASS;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		if (!this.linked) {
			BlockPos player_pos = playerIn.blockPosition();
			ResourceKey<Level> dim = worldIn.dimension();
			ResourceLocation destDimension = dim.location();
			Holder<Biome> biome = worldIn.getBiome(player_pos);
			
			ItemStack linkedStack = new ItemStack(ModObjectHolder.item_dimension_container, 1);

			if (playerIn.isShiftKeyDown()) {
				if (EventFactory.onContainerLink(playerIn, player_pos, player_pos, destDimension)) {
					CompoundTag stack_tag = new CompoundTag();
					CompoundTag nbt_data = new CompoundTag();
					CompoundTag dimension_data = new CompoundTag();
					CompoundTag position_data = new CompoundTag();

					dimension_data.putString("namespace", dim.location().getNamespace());
					dimension_data.putString("path", dim.location().getPath());

					String dim_path = dim.location().getPath();

					int colour = 0;

					if (dim_path.equals("the_nether")) {
						colour = ComponentColour.RED.dec();
					} else if (dim_path.equals("the_end")) {
						colour = 2458740;
					} else if (dim_path.equals("overworld")) {
						colour = ComponentColour.GREEN.dec();
					} else {
						colour = biome.value().getGrassColor(player_pos.getX(), player_pos.getZ());
					}
					
					dimension_data.putInt("colour", colour);
					
					position_data.putInt("pos_x", player_pos.getX());
					position_data.putInt("pos_y", player_pos.getY());
					position_data.putInt("pos_z", player_pos.getZ());
					position_data.putFloat("pos_yaw", playerIn.getRotationVector().x);
					position_data.putFloat("pos_pitch", playerIn.getRotationVector().y);
					
					nbt_data.put("dimension_data", dimension_data);
					nbt_data.put("position_data", position_data);
					
					stack_tag.put("nbt_data", nbt_data);
					linkedStack.setTag(stack_tag);
		
					playerIn.swing(handIn);
					
					MutableComponent preName = Component.literal(linkedStack.getHoverName().getString() + ":");
					String human_name = WordUtils.capitalizeFully(destDimension.getPath().replace("_", " "));
					MutableComponent newName = ComponentHelper.style(colour, " [" + human_name + "]");
					
					linkedStack.setHoverName(preName.append(newName));
					
					if (playerIn.getInventory().add(linkedStack)) {
						//playerIn.getInventory().add(linkedStack);
						CosmosUtil.getStack(playerIn, handIn).shrink(1);
					}
					
					CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.style(ComponentColour.GREEN, "cosmosportals.item_message.container_recorded"));
				}
			}
		}
		return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
	}
}
