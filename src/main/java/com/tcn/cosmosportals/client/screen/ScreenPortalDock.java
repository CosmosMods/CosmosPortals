package com.tcn.cosmosportals.client.screen;

import java.util.Arrays;

import org.apache.commons.lang3.text.WordUtils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenUIMode;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType.TYPE;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmosportals.CosmosPortalsReference;
import com.tcn.cosmosportals.client.container.ContainerPortalDock;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortalDock;
import com.tcn.cosmosportals.core.management.NetworkManager;
import com.tcn.cosmosportals.core.network.PacketPortalDock;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;

@SuppressWarnings({ "deprecation" })
public class ScreenPortalDock extends CosmosScreenUIMode<ContainerPortalDock> {
	
	private CosmosButtonWithType toggleLabelButton;     private int[] indexL = new int[] { 16,   139, 20 };
	private CosmosButtonWithType toggleSoundButton;     private int[] indexS = new int[] { 46,   139, 20 };
	private CosmosButtonWithType toggleEntityButton; 	private int[] indexE = new int[] { 114,  139, 20 };
	private CosmosButtonWithType toggleParticlesButton; private int[] indexP = new int[] { 144,  139, 20 };
	
	//private CosmosButtonWithType reformPortalButton;    private int[] indexR = new int[] { 16,  109, 20 };

	public ScreenPortalDock(ContainerPortalDock containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);

		this.setImageDims(191, 256);

		this.setLight(CosmosPortalsReference.DOCK[0]);
		this.setDark(CosmosPortalsReference.DOCK[1]);

		this.setUIModeButtonIndex(175, 4);
		this.setUIHelpButtonIndex(175, 17);
		this.setUIHelpElementDeadzone(144, 109, 163, 128);
		this.setUIHelpTitleOffset(4);
		
		this.setInventoryLabelDims(9, 166);
		this.setNoTitleLabel();
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		super.render(poseStack, mouseX, mouseY, partialTicks);
		
		this.renderPortalLabel(poseStack);
	}

	@Override
	protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(poseStack, partialTicks, mouseX, mouseY);

		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityPortalDock) {
			BlockEntityPortalDock blockEntity = (BlockEntityPortalDock) entity;

			int portalColour = blockEntity.getDisplayColour();
			float frame[] = ComponentColour.rgbFloatArray(ComponentColour.GRAY);
			float[] colour = new float[] {((portalColour >> 16) & 255) / 255.0F, ((portalColour >> 8) & 255) / 255.0F, (portalColour & 255) / 255.0F, 1F};
			
			CosmosUISystem.renderStaticElement(this, poseStack, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, new float[] { frame[0], frame[1], frame[2], 1.0F }, CosmosPortalsReference.DOCK_FRAME);
			CosmosUISystem.renderStaticElement(this, poseStack, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, CosmosPortalsReference.DOCK_BACKING);
			
			if (blockEntity.isPortalFormed) {
				RenderSystem.enableBlend();
				CosmosUISystem.renderStaticElement(this, poseStack, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, colour, CosmosPortalsReference.DOCK_PORTAL);
				RenderSystem.disableBlend();
			}
			
			CosmosUISystem.renderStaticElement(this, poseStack, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, colour, CosmosPortalsReference.DOCK_CONTAINER);
			CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, new float[] { 1.0F, 1.0F, 1.0F, 1.0F }, blockEntity, CosmosPortalsReference.DOCK_SLOTS);
			
			if (blockEntity.isPortalFormed && blockEntity.renderLabel) {
				String human_name = WordUtils.capitalizeFully(blockEntity.destDimension.getPath().replace("_", " "));
				int width = this.font.width(human_name) + 2;
				
				CosmosUISystem.renderStaticElement(this, poseStack, this.getScreenCoords(), (this.imageWidth - 11) / 2 - width / 2, 117, 0, 0, width, 12, new float[] { 1.0F, 1.0F, 1.0F, 0.6F }, CosmosPortalsReference.DOCK_LABEL);
			}
		}
	}
	
	@Override
	public void renderStandardHoverEffect(PoseStack poseStack, Style style, int mouseX, int mouseY) {
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityPortalDock) {
			BlockEntityPortalDock blockEntity = (BlockEntityPortalDock) entity;

		
			if (this.toggleLabelButton.isMouseOver(mouseX, mouseY)) {
				BaseComponent[] comp = new BaseComponent[] { ComponentHelper.locComp(ComponentColour.WHITE, false, "cosmosportals.gui.dock.label_info"), 
					(BaseComponent) ComponentHelper.locComp(ComponentColour.GRAY, false, "cosmosportals.gui.dock.label_value", " ")
					.append(blockEntity.renderLabel ? ComponentHelper.locComp(ComponentColour.GREEN, true, "cosmosportals.gui.dock.label_shown") : ComponentHelper.locComp(ComponentColour.RED, true, "cosmosportals.gui.dock.label_hidden"))
				};
				
				this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
			}
			
			if (this.toggleSoundButton.isMouseOver(mouseX, mouseY)) {
				BaseComponent[] comp = new BaseComponent[] { ComponentHelper.locComp(ComponentColour.WHITE, false, "cosmosportals.gui.dock.sounds_info"), 
					(BaseComponent) ComponentHelper.locComp(ComponentColour.GRAY, false, "cosmosportals.gui.dock.sounds_value", " ")
					.append(blockEntity.playSound ? ComponentHelper.locComp(ComponentColour.GREEN, true, "cosmosportals.gui.dock.sounds_played") : ComponentHelper.locComp(ComponentColour.RED, true, "cosmosportals.gui.dock.sounds_muffled"))
				};
				
				this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
			}
			
			if (this.toggleEntityButton.isMouseOver(mouseX, mouseY)) {
				BaseComponent[] comp = new BaseComponent[] { ComponentHelper.locComp(ComponentColour.WHITE, false, "cosmosportals.gui.dock.entity_info"), 
					(BaseComponent) ComponentHelper.locComp(ComponentColour.GRAY, false, "cosmosportals.gui.dock.entity_value", " ")
					.append(blockEntity.allowedEntities.getColouredComp())
				};
				
				this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
			}

			if (this.toggleParticlesButton.isMouseOver(mouseX, mouseY)) {
				BaseComponent[] comp = new BaseComponent[] { ComponentHelper.locComp(ComponentColour.WHITE, false, "cosmosportals.gui.dock.particle_info"), 
					(BaseComponent) ComponentHelper.locComp(ComponentColour.GRAY, false, "cosmosportals.gui.dock.particle_value", " ")
					.append(blockEntity.showParticles ? ComponentHelper.locComp(ComponentColour.GREEN, true, "cosmosportals.gui.dock.particle_shown") : ComponentHelper.locComp(ComponentColour.RED, true, "cosmosportals.gui.dock.particle_hidden"))
				};
				
				this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
			}
		}
		super.renderStandardHoverEffect(poseStack, style, mouseX, mouseY);
	}
		
	@Override
	protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
		drawCenteredString(poseStack, font, this.title, this.imageWidth / 2, 17, CosmosUISystem.DEFAULT_COLOUR_FONT_LIST);
		
		super.renderLabels(poseStack, mouseX, mouseY);
	}
	
	@Override
	protected void addButtons() {
		super.addButtons();
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityPortalDock) {
			BlockEntityPortalDock blockEntity = (BlockEntityPortalDock) entity;
			
			int i = 0;
			int j = blockEntity.allowedEntities.getIndex();
			
			if (j == 0) {
				i = 2;
			} else if (j == 1) {
				i = 15;
			} else if (j == 2) {
				i = 19;
			} else if (j == 3) {
				i = 1;
			}

			this.toggleLabelButton  	= this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + indexL[0], this.getScreenCoords()[1] + indexL[1], indexL[2], true, true, blockEntity.renderLabel   ? 1 : 2, ComponentHelper.locComp(""), (button) -> { this.pushButton(this.toggleLabelButton);	 }));
			this.toggleSoundButton   	= this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + indexS[0], this.getScreenCoords()[1] + indexS[1], indexS[2], true, true, blockEntity.playSound     ? 1 : 2, ComponentHelper.locComp(""), (button) -> { this.pushButton(this.toggleSoundButton);     }));
			this.toggleEntityButton  	= this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + indexE[0], this.getScreenCoords()[1] + indexE[1], indexE[2], true, true, i,                                 ComponentHelper.locComp(""), (button) -> { this.pushButton(this.toggleEntityButton);	 }));
			this.toggleParticlesButton  = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + indexP[0], this.getScreenCoords()[1] + indexP[1], indexP[2], true, true, blockEntity.showParticles ? 1 : 2, ComponentHelper.locComp(""), (button) -> { this.pushButton(this.toggleParticlesButton); }));
			
			//this.reformPortalButton     = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + indexR[0], this.getScreenCoords()[1] + indexR[1], indexR[2], !blockEntity.isPortalFormed, true,          1, ComponentHelper.locComp(""), (button) -> { this.pushButton(this.reformPortalButton);    }));
		}
	}
	
	@Override
	public void pushButton(Button button) {
		super.pushButton(button);
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityPortalDock) {
			BlockEntityPortalDock blockEntity = (BlockEntityPortalDock) entity;
			
			if (button.equals(this.toggleLabelButton)) {
				NetworkManager.sendToServer(new PacketPortalDock(this.menu.getBlockPos(), 0));
				blockEntity.toggleRenderLabel();
			}
			
			if (button.equals(this.toggleSoundButton)) {
				NetworkManager.sendToServer(new PacketPortalDock(this.menu.getBlockPos(), 1));
				blockEntity.togglePlaySound();
			}
			
			if (button.equals(this.toggleEntityButton)) {
				NetworkManager.sendToServer(new PacketPortalDock(this.menu.getBlockPos(), 2));
				blockEntity.toggleEntities();
			}

			if (button.equals(this.toggleParticlesButton)) {
				NetworkManager.sendToServer(new PacketPortalDock(this.menu.getBlockPos(), 3));
				blockEntity.toggleParticles();
			}
			
		}
	}
	
	@Override
	protected void addUIHelpElements() {
		super.addUIHelpElements();

		this.addRenderableUIHelpElement(this.getScreenCoords(), 73, 132, 34, 34, ComponentHelper.locComp(ComponentColour.WHITE, true, "cosmosportals.ui.help.container"), 
			ComponentHelper.locComp(ComponentColour.LIGHT_GRAY, false, "cosmosportals.ui.help.container_one"), 
			ComponentHelper.locComp(ComponentColour.LIGHT_GRAY, false, "cosmosportals.ui.help.container_two")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 144, 109, 20, 20, ComponentHelper.locComp(ComponentColour.LIGHT_GRAY, true, "cosmosportals.ui.help.container_item"), 
			ComponentHelper.locComp(ComponentColour.LIGHT_GRAY, false, "cosmosportals.ui.help.container_item_one"), 
			ComponentHelper.locComp(ComponentColour.LIGHT_GRAY, false, "cosmosportals.ui.help.container_item_two")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 15, 138, 22, 22, ComponentHelper.locComp(ComponentColour.GREEN, true, "cosmosportals.ui.help.button_label"), 
			ComponentHelper.locComp(ComponentColour.LIGHT_GRAY, false, "cosmosportals.ui.help.button_label_one"), 
			ComponentHelper.locComp(ComponentColour.LIGHT_GRAY, false, "cosmosportals.ui.help.button_label_two")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 45, 138, 22, 22, ComponentHelper.locComp(ComponentColour.GREEN, true, "cosmosportals.ui.help.button_sounds"), 
			ComponentHelper.locComp(ComponentColour.LIGHT_GRAY, false, "cosmosportals.ui.help.button_sounds_one"), 
			ComponentHelper.locComp(ComponentColour.LIGHT_GRAY, false, "cosmosportals.ui.help.button_sounds_two")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 113, 138, 22, 22, ComponentHelper.locComp(ComponentColour.GREEN, true, "cosmosportals.ui.help.button_entity"), 
			ComponentHelper.locComp(ComponentColour.LIGHT_GRAY, false, "cosmosportals.ui.help.button_entity_one"), 
			ComponentHelper.locComp(ComponentColour.LIGHT_GRAY, false, "cosmosportals.ui.help.button_entity_two"),
			ComponentHelper.locComp(ComponentColour.LIGHT_GRAY, false, "cosmosportals.ui.help.button_entity_three")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 143, 138, 22, 22, ComponentHelper.locComp(ComponentColour.GREEN, true, "cosmosportals.ui.help.button_effects"), 
			ComponentHelper.locComp(ComponentColour.LIGHT_GRAY, false, "cosmosportals.ui.help.button_effects_one"), 
			ComponentHelper.locComp(ComponentColour.LIGHT_GRAY, false, "cosmosportals.ui.help.button_effects_two")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 43, 115, 94, 16, ComponentHelper.locComp(ComponentColour.DARK_GREEN, true, "cosmosportals.ui.help.label"), 
			ComponentHelper.locComp(ComponentColour.LIGHT_GRAY, false, "cosmosportals.ui.help.label_one"), 
			ComponentHelper.locComp(ComponentColour.LIGHT_GRAY, false, "cosmosportals.ui.help.label_two")
		);
	}
	
	public void renderPortalLabel(PoseStack poseStack) {
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityPortalDock) {
			BlockEntityPortalDock blockEntity = (BlockEntityPortalDock) entity;
			
			if (blockEntity.isPortalFormed && blockEntity.renderLabel) {
				String human_name = WordUtils.capitalizeFully(blockEntity.destDimension.getPath().replace("_", " "));
				int portalColour = blockEntity.getDisplayColour();
				
				drawCenteredString(poseStack, font, human_name, this.getScreenCoords()[0] + (this.imageWidth - 11) / 2, this.getScreenCoords()[1] + 119, portalColour);
			}
		}
	}
}