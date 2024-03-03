package com.tcn.cosmosportals.client.renderer;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tcn.cosmoslibrary.client.renderer.lib.CosmosRendererHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.MathHelper;
import com.tcn.cosmosportals.CosmosPortals;
import com.tcn.cosmosportals.client.renderer.model.DockButtonModel;
import com.tcn.cosmosportals.core.blockentity.AbstractBlockEntityPortalDock;
import com.tcn.cosmosportals.core.blockentity.BlockEntityDockController;
import com.tcn.cosmosportals.core.item.ItemCosmicWrench;
import com.tcn.cosmosportals.core.management.ConfigurationManagerCommon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RendererDockController implements BlockEntityRenderer<BlockEntityDockController> {
	
	public static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation(CosmosPortals.MOD_ID, "textures/model/dock_controller_button.png");
	
	private BlockEntityRendererProvider.Context context;

	private Model buttonModel = new DockButtonModel();
	
	public RendererDockController(BlockEntityRendererProvider.Context contextIn) {
		this.context = contextIn;
	}	

	@Override
	public void render(BlockEntityDockController entityIn, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		Minecraft minecraft = Minecraft.getInstance();
		Font fontRenderer = this.context.getFont();
		
		LocalPlayer player = minecraft.player;
		Level level = entityIn.getLevel();
		
		BlockPos pos = player.blockPosition();
		BlockPos entityPos = entityIn.getBlockPos();
		double distanceToPlayer = pos.distManhattan(entityPos);
		
		BlockEntity testEntity = level.getBlockEntity(entityIn.getDockPos());
		
		
		BlockState north = level.getBlockState(entityIn.getBlockPos().north(1));
		BlockState east = level.getBlockState(entityIn.getBlockPos().east(1));
		BlockState south = level.getBlockState(entityIn.getBlockPos().south(1));
		BlockState west = level.getBlockState(entityIn.getBlockPos().west(1));
		
		float g = ComponentColour.GRAY.dec() / 255F;
		float lg = 120 / 255F;
		
		float r1 = g; float g1 = g; float b1 = g;
		float r2 = g; float g2 = g; float b2 = g;
		float r3 = g; float g3 = g; float b3 = g;
		float r4 = g; float g4 = g; float b4 = g;

		if (entityIn.isLinked()) {
			AbstractBlockEntityPortalDock dockEntity = (AbstractBlockEntityPortalDock) testEntity;
			
			if (dockEntity != null) {
				ComponentColour[] colours = dockEntity.getCustomColours(true);
				//int[] coloursItem = new int[] { dockEntity.getItem(0), dockEntity.getItem(0), dockEntity.getItem(0), dockEntity.getItem(0) };
				
				ComponentColour colour0 = colours[0];
				ComponentColour colour1 = colours[1];
				ComponentColour colour2 = colours[2];
				ComponentColour colour3 = colours[3];
				
				r1 = dockEntity.getMaxSlotIndex() >= 0 ? (colour0 == ComponentColour.EMPTY ? lg : colour0.getFloatRGB()[0]) : g;
				g1 = dockEntity.getMaxSlotIndex() >= 0 ? (colour0 == ComponentColour.EMPTY ? lg : colour0.getFloatRGB()[1]) : g;
				b1 = dockEntity.getMaxSlotIndex() >= 0 ? (colour0 == ComponentColour.EMPTY ? lg : colour0.getFloatRGB()[2]) : g;
				
				r2 = dockEntity.getMaxSlotIndex() >= 1 ? (colour1 == ComponentColour.EMPTY ? lg : colour1.getFloatRGB()[0]) : g;
				g2 = dockEntity.getMaxSlotIndex() >= 1 ? (colour1 == ComponentColour.EMPTY ? lg : colour1.getFloatRGB()[1]) : g;
				b2 = dockEntity.getMaxSlotIndex() >= 1 ? (colour1 == ComponentColour.EMPTY ? lg : colour1.getFloatRGB()[2]) : g;
				
				r3 = dockEntity.getMaxSlotIndex() >= 2 ? (colour2 == ComponentColour.EMPTY ? lg : colour2.getFloatRGB()[0]) : g;
				g3 = dockEntity.getMaxSlotIndex() >= 2 ? (colour2 == ComponentColour.EMPTY ? lg : colour2.getFloatRGB()[1]) : g;
				b3 = dockEntity.getMaxSlotIndex() >= 2 ? (colour2 == ComponentColour.EMPTY ? lg : colour2.getFloatRGB()[2]) : g;
				
				r4 = dockEntity.getMaxSlotIndex() >= 3 ? (colour3 == ComponentColour.EMPTY ? lg : colour3.getFloatRGB()[0]) : g;
				g4 = dockEntity.getMaxSlotIndex() >= 3 ? (colour3 == ComponentColour.EMPTY ? lg : colour3.getFloatRGB()[1]) : g;
				b4 = dockEntity.getMaxSlotIndex() >= 3 ? (colour3 == ComponentColour.EMPTY ? lg : colour3.getFloatRGB()[2]) : g;
												
				if (player != null) {
					if (distanceToPlayer <= ConfigurationManagerCommon.getInstance().getLabelMaximumDistance()) {
						if (MathHelper.isPlayerLookingAt(player, entityPos, false)) {
							ItemStack playerStack = player.getItemInHand(InteractionHand.MAIN_HAND);
							
							if (playerStack.getItem() instanceof ItemCosmicWrench) {
	
								poseStack.pushPose();
								
								RenderSystem.disableBlend();
								RenderSystem.disableDepthTest();
								RenderSystem.depthMask(false);
								
					            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					            GL11.glEnable(GL11.GL_LINE_SMOOTH);
					            
					            int transX = MathHelper.offsetBlockPos(entityPos, dockEntity.getBlockPos()).getX();
					            int transY = MathHelper.offsetBlockPos(entityPos, dockEntity.getBlockPos()).getY();
					            int transZ = MathHelper.offsetBlockPos(entityPos, dockEntity.getBlockPos()).getZ();
					            
								poseStack.translate(-transX, -transY, -transZ);
								
								poseStack.pushPose();
								VertexConsumer consumer = bufferIn.getBuffer(RenderType.LINES);
								LevelRenderer.renderLineBox(poseStack, consumer, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1);
	
								RenderSystem.enableDepthTest();
								RenderSystem.depthMask(true);
								RenderSystem.enableBlend();
								
								poseStack.popPose();
	
					            GL11.glDisable(GL11.GL_LINE_SMOOTH);
	
								poseStack.popPose();
							}
						}
					}
				}
			}
		}

		VertexConsumer buttonBuilder = bufferIn.getBuffer(this.buttonModel.renderType(BUTTON_TEXTURE));
		if (distanceToPlayer <= ConfigurationManagerCommon.getInstance().getLabelMaximumDistance()) {
			poseStack.pushPose();
			
			poseStack.pushPose();
			if (north.isAir()) {
				poseStack.pushPose();
				
				poseStack.translate(1 / 16D, 0.0D, 9 / 16D);
				poseStack.mulPose(Axis.YP.rotationDegrees(90));
				
				poseStack.pushPose();
				
				poseStack.translate(9 / 16D, 9 / 16D, 8 / 16D);
				buttonModel.renderToBuffer(poseStack, buttonBuilder, combinedLightIn, combinedOverlayIn, r1, g1, b1, 1.0F);
				poseStack.popPose();
	
				poseStack.pushPose();
				poseStack.translate(9 / 16D, 9 / 16D, 0.0D);
				buttonModel.renderToBuffer(poseStack, buttonBuilder, combinedLightIn, combinedOverlayIn, r2, g2, b2, 1.0F);
				poseStack.popPose();
	
				poseStack.pushPose();
				poseStack.translate(9 / 16D, 1 / 16D, 8 / 16D);
				buttonModel.renderToBuffer(poseStack, buttonBuilder, combinedLightIn, combinedOverlayIn, r3, g3, b3, 1.0F);
				poseStack.popPose();
	
				poseStack.pushPose();
				poseStack.translate(9 / 16D, 1 / 16D, 0.0D);
				buttonModel.renderToBuffer(poseStack, buttonBuilder, combinedLightIn, combinedOverlayIn, r4, g4, b4, 1.0F);
				poseStack.popPose();
				
				poseStack.popPose();
			}
			poseStack.popPose();
			
			poseStack.pushPose();
			if (south.isAir()) {
				poseStack.mulPose(Axis.YP.rotationDegrees(-180));
				
				poseStack.pushPose();
				
				poseStack.mulPose(Axis.YP.rotationDegrees(90));
				//poseStack.translate(1 / 16D, 0.0D, 9 / 16D);
				poseStack.translate(7 / 16.0D, 0.0D, -15 / 16D);
	
				poseStack.pushPose();
				poseStack.translate(9 / 16D, 9 / 16D, 8 / 16D);
				buttonModel.renderToBuffer(poseStack, buttonBuilder, combinedLightIn, combinedOverlayIn, r1, g1, b1, 1.0F);
				poseStack.popPose();
	
				poseStack.pushPose();
				poseStack.translate(9 / 16D, 9 / 16D, 0.0D);
				buttonModel.renderToBuffer(poseStack, buttonBuilder, combinedLightIn, combinedOverlayIn, r2, g2, b2, 1.0F);
				poseStack.popPose();
	
				poseStack.pushPose();
				poseStack.translate(9 / 16D, 1 / 16D, 8 / 16D);
				buttonModel.renderToBuffer(poseStack, buttonBuilder, combinedLightIn, combinedOverlayIn, r3, g3, b3, 1.0F);
				poseStack.popPose();
	
				poseStack.pushPose();
				poseStack.translate(9 / 16D, 1 / 16D, 0.0D);
				buttonModel.renderToBuffer(poseStack, buttonBuilder, combinedLightIn, combinedOverlayIn, r4, g4, b4, 1.0F);
				poseStack.popPose();
				
				poseStack.popPose();
			}
			poseStack.popPose();
	
			poseStack.pushPose();
			if (east.isAir()) {
				poseStack.mulPose(Axis.YP.rotationDegrees(90));
				
				poseStack.pushPose();
				
				poseStack.mulPose(Axis.YP.rotationDegrees(-90));
				poseStack.translate(7 / 16.0D, 0.0D, 1 / 16D);
	
				poseStack.pushPose();
				poseStack.translate(9 / 16D, 9 / 16D, 8 / 16D);
				buttonModel.renderToBuffer(poseStack, buttonBuilder, combinedLightIn, combinedOverlayIn, r1, g1, b1, 1.0F);
				poseStack.popPose();
	
				poseStack.pushPose();
				poseStack.translate(9 / 16D, 9 / 16D, 0.0D);
				buttonModel.renderToBuffer(poseStack, buttonBuilder, combinedLightIn, combinedOverlayIn, r2, g2, b2, 1.0F);
				poseStack.popPose();
	
				poseStack.pushPose();
				poseStack.translate(9 / 16D, 1 / 16D, 8 / 16D);
				buttonModel.renderToBuffer(poseStack, buttonBuilder, combinedLightIn, combinedOverlayIn, r3, g3, b3, 1.0F);
				poseStack.popPose();
	
				poseStack.pushPose();
				poseStack.translate(9 / 16D, 1 / 16D, 0.0D);
				buttonModel.renderToBuffer(poseStack, buttonBuilder, combinedLightIn, combinedOverlayIn, r4, g4, b4, 1.0F);
				poseStack.popPose();
				
				poseStack.popPose();
			}
			poseStack.popPose();
			
			poseStack.pushPose();
			if (west.isAir()) {
				poseStack.mulPose(Axis.YP.rotationDegrees(-90));
				
				poseStack.pushPose();
				
				poseStack.mulPose(Axis.YP.rotationDegrees(-90));
				poseStack.translate(-9 / 16.0D, 0.0D, -15 / 16D);
	
				poseStack.pushPose();
				poseStack.translate(9 / 16D, 9 / 16D, 8 / 16D);
				buttonModel.renderToBuffer(poseStack, buttonBuilder, combinedLightIn, combinedOverlayIn, r1, g1, b1, 1.0F);
				poseStack.popPose();
	
				poseStack.pushPose();
				poseStack.translate(9 / 16D, 9 / 16D, 0.0D);
				buttonModel.renderToBuffer(poseStack, buttonBuilder, combinedLightIn, combinedOverlayIn, r2, g2, b2, 1.0F);
				poseStack.popPose();
	
				poseStack.pushPose();
				poseStack.translate(9 / 16D, 1 / 16D, 8 / 16D);
				buttonModel.renderToBuffer(poseStack, buttonBuilder, combinedLightIn, combinedOverlayIn, r3, g3, b3, 1.0F);
				poseStack.popPose();
	
				poseStack.pushPose();
				poseStack.translate(9 / 16D, 1 / 16D, 0.0D);
				buttonModel.renderToBuffer(poseStack, buttonBuilder, combinedLightIn, combinedOverlayIn, r4, g4, b4, 1.0F);
				poseStack.popPose();
				
				poseStack.popPose();
			}
			poseStack.popPose();
	
			poseStack.popPose();
			
			if (ConfigurationManagerCommon.getInstance().getRenderPortalLabels()) {
				if (distanceToPlayer <= ConfigurationManagerCommon.getInstance().getLabelMaximumDistance()) {
					ComponentColour textColour = entityIn.isLinked() ? ComponentColour.GREEN : ComponentColour.RED;
	
					int currentIndex = -1;
					int maxIndex = -1;
					String modifier = "";
					
					if (entityIn.isLinked()) {
						AbstractBlockEntityPortalDock portalEntity = (AbstractBlockEntityPortalDock) testEntity;
						
						if (portalEntity != null) {
							currentIndex = portalEntity.getCurrentSlotIndex();
							maxIndex = portalEntity.getMaxSlotIndex();
						}
					}
					
					MutableComponent one = ComponentHelper.style(maxIndex >= 0 ? textColour : ComponentColour.RED, currentIndex == 0 ? modifier : "", "1");
					MutableComponent two = ComponentHelper.style(maxIndex >= 1 ? textColour : ComponentColour.RED, currentIndex == 1 ? modifier : "", "2");
					MutableComponent three = ComponentHelper.style(maxIndex >= 2 ? textColour : ComponentColour.RED, currentIndex == 2 ? modifier : "", "3");
					MutableComponent four = ComponentHelper.style(maxIndex >= 3 ? textColour : ComponentColour.RED, currentIndex == 3 ? modifier : "", "4");
	
					poseStack.pushPose();
					if (north.isAir()) {
						poseStack.pushPose();
						
						poseStack.translate(8 / 16F, 8 / 16F, 8 / 16F);
					
						poseStack.pushPose();
						poseStack.translate(3.8 / 16F, 5.4 / 16F, -9 / 16F);
						CosmosRendererHelper.renderLabelInWorld(fontRenderer, poseStack, one, bufferIn, combinedLightIn, false, currentIndex == 0);
						poseStack.popPose();
	
						poseStack.pushPose();
						poseStack.translate(-4.2 / 16F, 5.4 / 16F, -9 / 16F);
						CosmosRendererHelper.renderLabelInWorld(fontRenderer, poseStack, two, bufferIn, combinedLightIn, false, currentIndex == 1);
						poseStack.popPose();
	
						poseStack.pushPose();
						poseStack.translate(3.8 / 16F, -2.6 / 16F, -9 / 16F);
						CosmosRendererHelper.renderLabelInWorld(fontRenderer, poseStack, three, bufferIn, combinedLightIn, false, currentIndex == 2);
						poseStack.popPose();
	
						poseStack.pushPose();
						poseStack.translate(-4.2 / 16F, -2.6 / 16F, -9 / 16F);
						CosmosRendererHelper.renderLabelInWorld(fontRenderer, poseStack, four, bufferIn, combinedLightIn, false, currentIndex == 3);
						poseStack.popPose();
						
						poseStack.popPose();
					}
					poseStack.popPose();
					
					poseStack.pushPose();
					if (south.isAir()) {
						poseStack.mulPose(Axis.YP.rotationDegrees(180));
						
						poseStack.pushPose();
						
						poseStack.translate(-8 / 16F, 8 / 16F, -8 / 16F);
	
						poseStack.pushPose();
						poseStack.translate(3.8 / 16F, 5.4 / 16F, -9 / 16F);
						CosmosRendererHelper.renderLabelInWorld(fontRenderer, poseStack, one, bufferIn, combinedLightIn, false, currentIndex == 0);
						poseStack.popPose();
	
						poseStack.pushPose();
						poseStack.translate(-4.2 / 16F, 5.4 / 16F, -9 / 16F);
						CosmosRendererHelper.renderLabelInWorld(fontRenderer, poseStack, two, bufferIn, combinedLightIn, false, currentIndex == 1);
						poseStack.popPose();
	
						poseStack.pushPose();
						poseStack.translate(3.8 / 16F, -2.6 / 16F, -9 / 16F);
						CosmosRendererHelper.renderLabelInWorld(fontRenderer, poseStack, three, bufferIn, combinedLightIn, false, currentIndex == 2);
						poseStack.popPose();
	
						poseStack.pushPose();
						poseStack.translate(-4.2 / 16F, -2.6 / 16F, -9 / 16F);
						CosmosRendererHelper.renderLabelInWorld(fontRenderer, poseStack, four, bufferIn, combinedLightIn, false, currentIndex == 3);
						poseStack.popPose();
						
						poseStack.popPose();
					}
					poseStack.popPose();
	
					poseStack.pushPose();
					if (east.isAir()) {
						poseStack.mulPose(Axis.YP.rotationDegrees(-90));
						
						poseStack.pushPose();
						
						poseStack.translate(8 / 16F, 8 / 16F, -8 / 16F);
	
						poseStack.pushPose();
						poseStack.translate(3.8 / 16F, 5.4 / 16F, -9 / 16F);
						CosmosRendererHelper.renderLabelInWorld(fontRenderer, poseStack, one, bufferIn, combinedLightIn, false, currentIndex == 0);
						poseStack.popPose();
	
						poseStack.pushPose();
						poseStack.translate(-4.2 / 16F, 5.4 / 16F, -9 / 16F);
						CosmosRendererHelper.renderLabelInWorld(fontRenderer, poseStack, two, bufferIn, combinedLightIn, false, currentIndex == 1);
						poseStack.popPose();
	
						poseStack.pushPose();
						poseStack.translate(3.8 / 16F, -2.6 / 16F, -9 / 16F);
						CosmosRendererHelper.renderLabelInWorld(fontRenderer, poseStack, three, bufferIn, combinedLightIn, false, currentIndex == 2);
						poseStack.popPose();
	
						poseStack.pushPose();
						poseStack.translate(-4.2 / 16F, -2.6 / 16F, -9 / 16F);
						CosmosRendererHelper.renderLabelInWorld(fontRenderer, poseStack, four, bufferIn, combinedLightIn, false, currentIndex == 3);
						poseStack.popPose();
						
						poseStack.popPose();
					}
					poseStack.popPose();
	
					poseStack.pushPose();
					if (west.isAir()) {
						poseStack.mulPose(Axis.YP.rotationDegrees(90));
						
						poseStack.pushPose();
						
						poseStack.translate(-8 / 16F, 8 / 16F, 8 / 16F);
	
						poseStack.pushPose();
						poseStack.translate(3.8 / 16F, 5.4 / 16F, -9 / 16F);
						CosmosRendererHelper.renderLabelInWorld(fontRenderer, poseStack, one, bufferIn, combinedLightIn, false, currentIndex == 0);
						poseStack.popPose();
	
						poseStack.pushPose();
						poseStack.translate(-4.2 / 16F, 5.4 / 16F, -9 / 16F);
						CosmosRendererHelper.renderLabelInWorld(fontRenderer, poseStack, two, bufferIn, combinedLightIn, false, currentIndex == 1);
						poseStack.popPose();
	
						poseStack.pushPose();
						poseStack.translate(3.8 / 16F, -2.6 / 16F, -9 / 16F);
						CosmosRendererHelper.renderLabelInWorld(fontRenderer, poseStack, three, bufferIn, combinedLightIn, false, currentIndex == 2);
						poseStack.popPose();
	
						poseStack.pushPose();
						poseStack.translate(-4.2 / 16F, -2.6 / 16F, -9 / 16F);
						CosmosRendererHelper.renderLabelInWorld(fontRenderer, poseStack, four, bufferIn, combinedLightIn, false, currentIndex == 3);
						poseStack.popPose();
						
						poseStack.popPose();
					}
					poseStack.popPose();
				}
			}
		}
	}
	
}