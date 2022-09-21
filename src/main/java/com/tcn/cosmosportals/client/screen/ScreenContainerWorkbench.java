package com.tcn.cosmosportals.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenUIModeBE;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmosportals.CosmosPortalsReference;
import com.tcn.cosmosportals.client.container.ContainerContainerWorkbench;
import com.tcn.cosmosportals.core.blockentity.BlockEntityContainerWorkbench;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ScreenContainerWorkbench extends CosmosScreenUIModeBE<ContainerContainerWorkbench> {
	
	public ScreenContainerWorkbench(ContainerContainerWorkbench containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);

		this.setImageDims(172, 126);

		this.setLight(CosmosPortalsReference.WORKBENCH[0]);
		this.setDark(CosmosPortalsReference.WORKBENCH[1]);

		this.setUIModeButtonIndex(155, 5);
		this.setUIHelpButtonIndex(155, 19);
		this.setUIHelpElementDeadzone(144, 109, 163, 128);
		this.setUIHelpTitleOffset(4);
		
		this.setUIHelpElementDeadzone(29, 14, 143, 32);
		
		this.setInventoryLabelDims(5, 36);
		this.setTitleLabelDims(5, 5);
	}

	@Override
	protected void init() {
		super.init();
	}
	
	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		super.render(poseStack, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(poseStack, partialTicks, mouseX, mouseY);
		
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityContainerWorkbench) {
			BlockEntityContainerWorkbench blockEntity = (BlockEntityContainerWorkbench) entity;

			CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, new float[] { 1.0F, 1.0F, 1.0F, 1.0F }, blockEntity, CosmosPortalsReference.WORKBENCH_SLOTS);
		}
	}
	
	@Override
	protected void addUIHelpElements() {
		super.addUIHelpElements();
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 28, 13, 20, 20, ComponentHelper.style(ComponentColour.WHITE, "bold", "cosmosportals.ui.help.workbench.container"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.workbench.container_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.workbench.container_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.workbench.container_three")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 72, 13, 20, 20, ComponentHelper.style(ComponentColour.GREEN, "bold", "cosmosportals.ui.help.workbench.container_linked"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.workbench.container_linked_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.workbench.container_linked_two")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 124, 13, 20, 20, ComponentHelper.style(ComponentColour.GREEN, "bold", "cosmosportals.ui.help.workbench.container_copied"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.workbench.container_copied_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.workbench.container_copied_two")
		);
	}
}