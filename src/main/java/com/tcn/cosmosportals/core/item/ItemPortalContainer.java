package com.tcn.cosmosportals.core.item;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.text.WordUtils;

import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.comp.CosmosColour;
import com.tcn.cosmoslibrary.common.comp.CosmosCompHelper;
import com.tcn.cosmoslibrary.common.comp.CosmosCompHelper.Value;
import com.tcn.cosmoslibrary.common.item.CosmosItem;
import com.tcn.cosmosportals.core.management.CoreEventFactory;
import com.tcn.cosmosportals.core.tileentity.TileEntityPortalDock;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

@SuppressWarnings("deprecation")
public class ItemPortalContainer extends CosmosItem {

	public ItemPortalContainer(Properties properties) {
		super(properties);
		
	}

	@Override
	public void appendHoverText(ItemStack stackIn, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if (!CosmosCompHelper.isShiftKeyDown(Minecraft.getInstance())) {
			tooltip.add(CosmosCompHelper.getTooltipInfo("cosmosportals.item_info.container"));
			
			if (CosmosCompHelper.displayShiftForDetail) {
				tooltip.add(CosmosCompHelper.shiftForMoreDetails());
			}
		} else {
			tooltip.add(CosmosCompHelper.getTooltipOne("cosmosportals.item_info.container_one"));
			tooltip.add(CosmosCompHelper.getTooltipTwo("cosmosportals.item_info.container_two"));
			tooltip.add(CosmosCompHelper.getTooltipThree("cosmosportals.item_info.container_three"));

			tooltip.add(CosmosCompHelper.shiftForLessDetails());
			
			if (stackIn.hasTag()) {
				CompoundNBT stack_tag = stackIn.getTag();
				
				if (stack_tag.contains("nbt_data")) {
					CompoundNBT nbt_data = stack_tag.getCompound("nbt_data");
					
					if (nbt_data.contains("position_data")) {
						CompoundNBT position_data = nbt_data.getCompound("position_data");
						
						int[] position = new int [] { position_data.getInt("pos_x"), position_data.getInt("pos_y"), position_data.getInt("pos_z") };
						
						tooltip.add(CosmosCompHelper.locComp(CosmosColour.GRAY, false, "cosmosportals.item_info.container_position")
								.append(CosmosCompHelper.locComp(Value.LIGHT_GRAY + "[ " + Value.CYAN + position[0] + Value.LIGHT_GRAY + ", " + Value.CYAN + position[1] + Value.LIGHT_GRAY + ", " + Value.CYAN + position[2] + Value.LIGHT_GRAY + " ]")));
					}

					if (nbt_data.contains("dimension_data")) {
						CompoundNBT dimension_data = nbt_data.getCompound("dimension_data");
						
						String namespace = dimension_data.getString("namespace");
						String path = dimension_data.getString("path");
						
						tooltip.add(CosmosCompHelper.locComp(CosmosColour.GRAY, false, "cosmosportals.item_info.container_dimension")
								.append(CosmosCompHelper.locComp(Value.LIGHT_GRAY + "[ " + Value.GREEN + namespace + Value.LIGHT_GRAY + ": " + Value.GREEN + path + Value.LIGHT_GRAY + " ]")));
					}
				}
			}
			
		}
	}
	
	@Override
	public boolean canAttackBlock(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
		return false;
	}
	
	@Override
	public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
		return true;
	}
	
	@Override
	public ActionResultType onItemUseFirst(ItemStack stackIn, ItemUseContext context) {
		PlayerEntity playerIn = context.getPlayer();
		BlockPos pos = context.getClickedPos();
		World world = context.getLevel();
		TileEntity entity = world.getBlockEntity(pos);
		
		if (entity != null) {
			if (entity instanceof TileEntityPortalDock) {
				playerIn.swing(context.getHand());
				return ActionResultType.PASS;
			}
		}
		
		return ActionResultType.PASS;
	}
	
	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack stack = playerIn.getItemInHand(handIn);
		BlockPos player_pos = playerIn.blockPosition();
		RegistryKey<World> dim = worldIn.dimension();
		ResourceLocation destDimension = dim.location();
		Biome biome = worldIn.getBiome(player_pos);
		
		if (playerIn.isShiftKeyDown()) {
			if (!stack.hasTag()) {
				if (CoreEventFactory.onContainerLink(playerIn, player_pos, player_pos, destDimension)) {
					CompoundNBT stack_tag = new CompoundNBT();
					CompoundNBT nbt_data = new CompoundNBT();
					CompoundNBT dimension_data = new CompoundNBT();
					CompoundNBT position_data = new CompoundNBT();
					
					dimension_data.putString("namespace", dim.location().getNamespace());
					dimension_data.putString("path", dim.location().getPath());
					
					String dim_path = dim.location().getPath();
					
					int colour = 0;
					
					if (dim_path.equals("the_nether")) {
						colour = CosmosColour.RED.dec();
					} else if (dim_path.equals("the_end")) {
						colour = 2458740;
					} else if (dim_path.equals("overworld")) {
						colour = CosmosColour.GREEN.dec();
					} else {
						colour = biome.getGrassColor(player_pos.getX(), player_pos.getZ());
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
					stack.setTag(stack_tag);
		
					playerIn.swing(handIn);
					
					IFormattableTextComponent preName = new StringTextComponent(stack.getHoverName().getString());
					String human_name = WordUtils.capitalizeFully(destDimension.getPath().replace("_", " "));
					IFormattableTextComponent newName = CosmosCompHelper.locComp(colour, false, " [" + human_name + "]");
					
					stack.setHoverName(preName.append(newName));
					
					CosmosChatUtil.sendServerPlayerMessage(playerIn, CosmosCompHelper.locComp(CosmosColour.GREEN, false, "cosmosportals.item_message.container_recorded"));
				}
			}
		}
		return ActionResult.pass(playerIn.getItemInHand(handIn));
	}
}
