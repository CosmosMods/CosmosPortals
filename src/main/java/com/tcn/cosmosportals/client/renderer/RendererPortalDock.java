package com.tcn.cosmosportals.client.renderer;

import org.apache.commons.lang3.text.WordUtils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tcn.cosmoslibrary.client.util.TERUtil;
import com.tcn.cosmoslibrary.common.comp.CosmosCompHelper;
import com.tcn.cosmosportals.core.block.BlockPortal;
import com.tcn.cosmosportals.core.management.CoreConfigurationManager;
import com.tcn.cosmosportals.core.tileentity.TileEntityPortalDock;

import net.minecraft.block.BlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
public class RendererPortalDock extends TileEntityRenderer<TileEntityPortalDock> {

	public RendererPortalDock(TileEntityRendererDispatcher disp) {
		super(disp);
	}

	@Override
	public void render(TileEntityPortalDock tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		FontRenderer fontRenderer = this.renderer.getFont();
		World world = tileEntityIn.getLevel();
		int colour = tileEntityIn.getDisplayColour();
		
		String human_name = WordUtils.capitalizeFully(tileEntityIn.destDimension.getPath().replace("_", " "));

		BlockState above = world.getBlockState(tileEntityIn.getBlockPos().above());
		BlockState below = world.getBlockState(tileEntityIn.getBlockPos().below());

		matrixStackIn.pushPose();
		if (tileEntityIn.renderLabel && CoreConfigurationManager.getInstance().getRenderPortalLabels()) {
			if (above.getBlock() instanceof BlockPortal) {
				Axis axis = above.getValue(BlockPortal.AXIS);
				matrixStackIn.translate(0.5F, 1.5F, 0.5F);
				
				if (axis.equals(Axis.Z)) {
					//Facing East
					matrixStackIn.pushPose();
					matrixStackIn.translate(0.25F, 0.0F, 0.0F);
					matrixStackIn.mulPose(new Quaternion(Vector3f.YN, 90, true));
					TERUtil.renderLabelInWorld(fontRenderer, matrixStackIn, CosmosCompHelper.locComp(colour, false, human_name), bufferIn, combinedLightIn);
					matrixStackIn.popPose();
					
					matrixStackIn.pushPose();
					matrixStackIn.translate(-0.25F, 0.0F, 0.0F);
					matrixStackIn.mulPose(new Quaternion(Vector3f.YN, -90, true));
					TERUtil.renderLabelInWorld(fontRenderer, matrixStackIn, CosmosCompHelper.locComp(colour, false, human_name), bufferIn, combinedLightIn);
					matrixStackIn.popPose();
				} else {
					//Facing East
					matrixStackIn.pushPose();
					matrixStackIn.translate(0.0F, 0.0F, -0.25F);
					TERUtil.renderLabelInWorld(fontRenderer, matrixStackIn, CosmosCompHelper.locComp(colour, false, human_name), bufferIn, combinedLightIn);
					matrixStackIn.popPose();
					
					matrixStackIn.pushPose();
					matrixStackIn.translate(0.0F, 0.0F, 0.25F);
					matrixStackIn.mulPose(new Quaternion(Vector3f.YN, 180, true));
					TERUtil.renderLabelInWorld(fontRenderer, matrixStackIn, CosmosCompHelper.locComp(colour, false, human_name), bufferIn, combinedLightIn);
					matrixStackIn.popPose();
				}
			} else if (below.getBlock() instanceof BlockPortal) {
				Axis axis = below.getValue(BlockPortal.AXIS);
	
				matrixStackIn.translate(0.5F, -0.3F, 0.5F);
				if (axis.equals(Axis.Z)) {
					//Facing East
					matrixStackIn.pushPose();
					matrixStackIn.translate(0.25F, 0.0F, 0.0F);
					matrixStackIn.mulPose(new Quaternion(Vector3f.YN, 90, true));
					TERUtil.renderLabelInWorld(fontRenderer, matrixStackIn, CosmosCompHelper.locComp(colour, false, human_name), bufferIn, combinedLightIn);
					matrixStackIn.popPose();
					
					matrixStackIn.pushPose();
					matrixStackIn.translate(-0.25F, 0.0F, 0.0F);
					matrixStackIn.mulPose(new Quaternion(Vector3f.YN, -90, true));
					TERUtil.renderLabelInWorld(fontRenderer, matrixStackIn, CosmosCompHelper.locComp(colour, false, human_name), bufferIn, combinedLightIn);
					matrixStackIn.popPose();
				} else {
					//Facing East
					matrixStackIn.pushPose();
					matrixStackIn.translate(0.0F, 0.0F, -0.25F);
					TERUtil.renderLabelInWorld(fontRenderer, matrixStackIn, CosmosCompHelper.locComp(colour, false, human_name), bufferIn, combinedLightIn);
					matrixStackIn.popPose();
					
					matrixStackIn.pushPose();
					matrixStackIn.translate(0.0F, 0.0F, 0.25F);
					matrixStackIn.mulPose(new Quaternion(Vector3f.YN, 180, true));
					TERUtil.renderLabelInWorld(fontRenderer, matrixStackIn, CosmosCompHelper.locComp(colour, false, human_name), bufferIn, combinedLightIn);
					matrixStackIn.popPose();
				}
			}
		}
		
		matrixStackIn.popPose();
	}
	
}