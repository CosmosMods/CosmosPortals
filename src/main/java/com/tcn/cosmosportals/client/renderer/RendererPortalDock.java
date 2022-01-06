package com.tcn.cosmosportals.client.renderer;

import org.apache.commons.lang3.text.WordUtils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.tcn.cosmoslibrary.client.renderer.lib.CosmosBERHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmosportals.core.block.BlockPortal;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortalDock;
import com.tcn.cosmosportals.core.management.ConfigurationManager;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public class RendererPortalDock implements BlockEntityRenderer<BlockEntityPortalDock> {
	
	private BlockEntityRendererProvider.Context context;

	public RendererPortalDock(BlockEntityRendererProvider.Context contextIn) {
		this.context = contextIn;
	}	

	@Override
	public void render(BlockEntityPortalDock tileEntityIn, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		Font fontRenderer = this.context.getFont();
		Level world = tileEntityIn.getLevel();
		int colour = tileEntityIn.getDisplayColour();
		
		String human_name = WordUtils.capitalizeFully(tileEntityIn.destDimension.getPath().replace("_", " "));

		BlockState above = world.getBlockState(tileEntityIn.getBlockPos().above());
		BlockState below = world.getBlockState(tileEntityIn.getBlockPos().below());

		poseStack.pushPose();
		if (tileEntityIn.renderLabel && ConfigurationManager.getInstance().getRenderPortalLabels()) {
			if (above.getBlock() instanceof BlockPortal) {
				Axis axis = above.getValue(BlockPortal.AXIS);
				poseStack.translate(0.5F, 1.5F, 0.5F);
				
				if (axis.equals(Axis.Z)) {
					//Facing East
					poseStack.pushPose();
					poseStack.translate(0.25F, 0.0F, 0.0F);
					poseStack.mulPose(new Quaternion(Vector3f.YN, 90, true));
					CosmosBERHelper.renderLabelInWorld(fontRenderer, poseStack, ComponentHelper.locComp(colour, false, human_name), bufferIn, combinedLightIn);
					poseStack.popPose();
					
					poseStack.pushPose();
					poseStack.translate(-0.25F, 0.0F, 0.0F);
					poseStack.mulPose(new Quaternion(Vector3f.YN, -90, true));
					CosmosBERHelper.renderLabelInWorld(fontRenderer, poseStack, ComponentHelper.locComp(colour, false, human_name), bufferIn, combinedLightIn);
					poseStack.popPose();
				} else {
					//Facing East
					poseStack.pushPose();
					poseStack.translate(0.0F, 0.0F, -0.25F);
					CosmosBERHelper.renderLabelInWorld(fontRenderer, poseStack, ComponentHelper.locComp(colour, false, human_name), bufferIn, combinedLightIn);
					poseStack.popPose();
					
					poseStack.pushPose();
					poseStack.translate(0.0F, 0.0F, 0.25F);
					poseStack.mulPose(new Quaternion(Vector3f.YN, 180, true));
					CosmosBERHelper.renderLabelInWorld(fontRenderer, poseStack, ComponentHelper.locComp(colour, false, human_name), bufferIn, combinedLightIn);
					poseStack.popPose();
				}
			} else if (below.getBlock() instanceof BlockPortal) {
				Axis axis = below.getValue(BlockPortal.AXIS);
	
				poseStack.translate(0.5F, -0.3F, 0.5F);
				if (axis.equals(Axis.Z)) {
					//Facing East
					poseStack.pushPose();
					poseStack.translate(0.25F, 0.0F, 0.0F);
					poseStack.mulPose(new Quaternion(Vector3f.YN, 90, true));
					CosmosBERHelper.renderLabelInWorld(fontRenderer, poseStack, ComponentHelper.locComp(colour, false, human_name), bufferIn, combinedLightIn);
					poseStack.popPose();
					
					poseStack.pushPose();
					poseStack.translate(-0.25F, 0.0F, 0.0F);
					poseStack.mulPose(new Quaternion(Vector3f.YN, -90, true));
					CosmosBERHelper.renderLabelInWorld(fontRenderer, poseStack, ComponentHelper.locComp(colour, false, human_name), bufferIn, combinedLightIn);
					poseStack.popPose();
				} else {
					//Facing East
					poseStack.pushPose();
					poseStack.translate(0.0F, 0.0F, -0.25F);
					CosmosBERHelper.renderLabelInWorld(fontRenderer, poseStack, ComponentHelper.locComp(colour, false, human_name), bufferIn, combinedLightIn);
					poseStack.popPose();
					
					poseStack.pushPose();
					poseStack.translate(0.0F, 0.0F, 0.25F);
					poseStack.mulPose(new Quaternion(Vector3f.YN, 180, true));
					CosmosBERHelper.renderLabelInWorld(fontRenderer, poseStack, ComponentHelper.locComp(colour, false, human_name), bufferIn, combinedLightIn);
					poseStack.popPose();
				}
			}
		}
		
		poseStack.popPose();
	}
	
}