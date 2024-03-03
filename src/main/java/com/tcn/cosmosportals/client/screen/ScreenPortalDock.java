package com.tcn.cosmosportals.client.screen;

import java.util.Arrays;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenUIModeBE;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType.TYPE;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosColourButton;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmosportals.CosmosPortalsReference;
import com.tcn.cosmosportals.client.container.ContainerPortalDock;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortalDock;
import com.tcn.cosmosportals.core.management.NetworkManager;
import com.tcn.cosmosportals.core.network.PacketColour;
import com.tcn.cosmosportals.core.network.PacketPortalDock;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ScreenPortalDock extends CosmosScreenUIModeBE<ContainerPortalDock> {
	
	private CosmosButtonWithType toggleLabelButton;     private int[] indexL = new int[] { 16,   139, 20 };
	private CosmosButtonWithType toggleSoundButton;     private int[] indexS = new int[] { 46,   139, 20 };
	private CosmosButtonWithType toggleEntityButton; 	private int[] indexE = new int[] { 114,  139, 20 };
	private CosmosButtonWithType toggleParticlesButton; private int[] indexP = new int[] { 144,  139, 20 };
	
	private CosmosColourButton colourButton; private int[] indexC = new int[] { 166, 110, 20 };

	public ScreenPortalDock(ContainerPortalDock containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);

		this.setImageDims(194, 256);

		this.setLight(CosmosPortalsReference.DOCK[0]);
		this.setDark(CosmosPortalsReference.DOCK[1]);

		this.setUIModeButtonIndex(177, 5);
		this.setUIHelpButtonIndex(177, 33);
		this.setUILockButtonIndex(177, 19);
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
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.render(graphics, mouseX, mouseY, partialTicks);
		
		this.renderPortalLabel(graphics);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(graphics, partialTicks, mouseX, mouseY);
		
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityPortalDock) {
			BlockEntityPortalDock blockEntity = (BlockEntityPortalDock) entity;

			int portalColour = blockEntity.getDisplayColour();
			float frame[] = ComponentColour.rgbFloatArray(ComponentColour.GRAY);
			float[] colour = new float[] {((portalColour >> 16) & 255) / 255.0F, ((portalColour >> 8) & 255) / 255.0F, (portalColour & 255) / 255.0F, 1F};
			
			CosmosUISystem.renderStaticElement(this, graphics, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, new float[] { frame[0], frame[1], frame[2], 1.0F }, CosmosPortalsReference.DOCK_FRAME);
			CosmosUISystem.renderStaticElement(this, graphics, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, CosmosPortalsReference.DOCK_BACKING);
			
			if (blockEntity.isPortalFormed) {
				RenderSystem.enableBlend();
				CosmosUISystem.renderStaticElement(this, graphics, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, colour, CosmosPortalsReference.DOCK_PORTAL);
				RenderSystem.disableBlend();
			}
			
			CosmosUISystem.renderStaticElement(this, graphics, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, colour, CosmosPortalsReference.DOCK_CONTAINER);
			CosmosUISystem.renderStaticElementWithUIMode(this, graphics, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, new float[] { 1.0F, 1.0F, 1.0F, 1.0F }, blockEntity.getUIMode(), CosmosPortalsReference.DOCK_SLOTS);
			
			if (blockEntity.isPortalFormed && blockEntity.renderLabel) {
				String human_name = blockEntity.getContainerDisplayName();
				int width = this.font.width(human_name) + 2;
				
				CosmosUISystem.renderStaticElement(this, graphics, this.getScreenCoords(), (this.imageWidth - 11) / 2 - width / 2, 117, 0, 0, width, 12, new float[] { 1.0F, 1.0F, 1.0F, 0.6F }, CosmosPortalsReference.DOCK_LABEL);
			}

			ComponentColour customColour = blockEntity.getCustomColour();
			if (customColour != ComponentColour.EMPTY) {
				float[] floatCol = new float[] { 
					ComponentColour.rgbFloatArray(customColour.dec())[0], 
					ComponentColour.rgbFloatArray(customColour.dec())[1],
					ComponentColour.rgbFloatArray(customColour.dec())[2],
					1 
				};
				
				CosmosUISystem.renderStaticElement(this, graphics, this.getScreenCoords(), 167, 111, 0, 22, 4, 16, floatCol, CosmosPortalsReference.DOCK_OVERLAY_ONE_UPGRADED);
				CosmosUISystem.setTextureColour(ComponentColour.WHITE);
			}
			
			
			boolean hovered3 = this.colourButton.isMouseOver(mouseX, mouseY);
			CosmosUISystem.renderStaticElement(this, graphics, this.getScreenCoords(), indexC[0], indexC[1], hovered3 ? 6 : 0, 38, 6, 18, CosmosPortalsReference.DOCK_OVERLAY_ONE_UPGRADED);
		}
	}
	
	@Override
	public void renderStandardHoverEffect(GuiGraphics graphics, Style style, int mouseX, int mouseY) {
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityPortalDock) {
			BlockEntityPortalDock blockEntity = (BlockEntityPortalDock) entity;
			
			if (this.toggleLabelButton.isMouseOver(mouseX, mouseY)) {
				MutableComponent[] comp = new MutableComponent[] { ComponentHelper.style(ComponentColour.WHITE, "cosmosportals.gui.dock.label_info"), 
					(MutableComponent) ComponentHelper.style2(ComponentColour.GRAY, "cosmosportals.gui.dock.label_value", " ")
					.append(blockEntity.renderLabel ? ComponentHelper.style(ComponentColour.GREEN, "bold", "cosmosportals.gui.dock.label_shown") : ComponentHelper.style(ComponentColour.RED, "bold", "cosmosportals.gui.dock.label_hidden"))
				};
				
				graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			}
			
			if (this.toggleSoundButton.isMouseOver(mouseX, mouseY)) {
				MutableComponent[] comp = new MutableComponent[] { ComponentHelper.style(ComponentColour.WHITE, "cosmosportals.gui.dock.sounds_info"), 
					(MutableComponent) ComponentHelper.style2(ComponentColour.GRAY, "cosmosportals.gui.dock.sounds_value", " ")
					.append(blockEntity.playSound ? ComponentHelper.style(ComponentColour.GREEN, "bold", "cosmosportals.gui.dock.sounds_played") : ComponentHelper.style(ComponentColour.RED, "bold", "cosmosportals.gui.dock.sounds_muffled"))
				};
				
				graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			}
			
			if (this.toggleEntityButton.isMouseOver(mouseX, mouseY)) {
				MutableComponent[] comp = new MutableComponent[] { ComponentHelper.style(ComponentColour.WHITE, "cosmosportals.gui.dock.entity_info"), 
					(MutableComponent) ComponentHelper.style2(ComponentColour.GRAY, "cosmosportals.gui.dock.entity_value", " ")
					.append(blockEntity.allowedEntities.getColouredComp())
				};
				
				graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			}

			if (this.toggleParticlesButton.isMouseOver(mouseX, mouseY)) {
				MutableComponent[] comp = new MutableComponent[] { ComponentHelper.style(ComponentColour.WHITE, "cosmosportals.gui.dock.particle_info"), 
					(MutableComponent) ComponentHelper.style2(ComponentColour.GRAY, "cosmosportals.gui.dock.particle_value", " ")
					.append(blockEntity.showParticles ? ComponentHelper.style(ComponentColour.GREEN, "bold", "cosmosportals.gui.dock.particle_shown") : ComponentHelper.style(ComponentColour.RED, "bold", "cosmosportals.gui.dock.particle_hidden"))
				};
				
				graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			}
			
			if (this.colourButton.isMouseOver(mouseX, mouseY)) {
				MutableComponent[] comp = new MutableComponent[] { ComponentHelper.style(ComponentColour.WHITE, "cosmoslibrary.gui.colour.info"), 
					(MutableComponent) ComponentHelper.style(ComponentColour.GRAY, "cosmoslibrary.gui.colour.value").append(blockEntity.getCustomColour().getColouredName())
				};
				
				graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			}
		}
		super.renderStandardHoverEffect(graphics, style, mouseX, mouseY);
	}
		
	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		graphics.drawCenteredString( font, this.title, (this.imageWidth -11) / 2, 17, CosmosUISystem.DEFAULT_COLOUR_FONT_LIST);
		
		super.renderLabels(graphics, mouseX, mouseY);
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
				i = 22;
			} else if (j == 4) {
				i = 1;
			}

			this.toggleLabelButton = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + indexL[0], this.getScreenCoords()[1] + indexL[1], indexL[2], true, true, blockEntity.renderLabel ? 1 : 2, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.toggleLabelButton, isLeftClick);} ));
			this.toggleSoundButton = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + indexS[0], this.getScreenCoords()[1] + indexS[1], indexS[2], true, true, blockEntity.playSound ? 1 : 2, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.toggleSoundButton, isLeftClick); }));
			this.toggleEntityButton = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + indexE[0], this.getScreenCoords()[1] + indexE[1], indexE[2], true, true, i, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.toggleEntityButton, isLeftClick); }));
			this.toggleParticlesButton = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + indexP[0], this.getScreenCoords()[1] + indexP[1], indexP[2], true, true, blockEntity.showParticles ? 1 : 2, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.toggleParticlesButton, isLeftClick); }));
			
			this.colourButton = this.addRenderableWidget(new CosmosColourButton(blockEntity.getCustomColour(), this.getScreenCoords()[0] + indexC[0], this.getScreenCoords()[1] + indexC[1], 6, 18, true, true, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.colourButton, isLeftClick); }));
			
			//this.reformPortalButton = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + indexR[0], this.getScreenCoords()[1] + indexR[1], indexR[2], !blockEntity.isPortalFormed, true,          1, ComponentHelper.style(""), (button) -> { this.clickButton(this.reformPortalButton);    }));
		}
	}
	
	@Override
	public void clickButton(Button button, boolean isLeftClick) {
		super.clickButton(button, isLeftClick);
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityPortalDock) {
			BlockEntityPortalDock blockEntity = (BlockEntityPortalDock) entity;
			
			if (isLeftClick) {
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
					blockEntity.toggleEntities(false);
				}
	
				if (button.equals(this.toggleParticlesButton)) {
					NetworkManager.sendToServer(new PacketPortalDock(this.menu.getBlockPos(), 3));
					blockEntity.toggleParticles();
				}
				
				if (button.equals(this.colourButton)) {
					ComponentColour colour = hasShiftDown() ? ComponentColour.EMPTY : blockEntity.getCustomColour().getNextVanillaColour(true);
					NetworkManager.sendToServer(new PacketColour(this.menu.getBlockPos(), colour, 0));
				}
			} else {
				if (button.equals(this.toggleEntityButton)) {
					NetworkManager.sendToServer(new PacketPortalDock(this.menu.getBlockPos(), 4));
					blockEntity.toggleEntities(true);
				}
	
				if (button.equals(this.colourButton)) {
					ComponentColour colour = hasShiftDown() ? ComponentColour.EMPTY : blockEntity.getCustomColour().getNextVanillaColourReverse(true);
					NetworkManager.sendToServer(new PacketColour(this.menu.getBlockPos(), colour, 0));
				}
			}
		} 
	}
	
	@Override
	protected void addUIHelpElements() {
		super.addUIHelpElements();

		this.addRenderableUIHelpElement(this.getScreenCoords(), 73, 132, 34, 34, ComponentHelper.style(ComponentColour.WHITE, "bold", "cosmosportals.ui.help.container"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.container_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.container_two")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 144, 109, 20, 20, ComponentColour.RED, ComponentHelper.style(ComponentColour.RED, "bold", "cosmosportals.ui.help.container_item"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.container_item_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.container_item_two")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 15, 138, 22, 22, ComponentHelper.style(ComponentColour.GREEN, "bold", "cosmosportals.ui.help.button_label"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.button_label_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.button_label_two")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 45, 138, 22, 22, ComponentHelper.style(ComponentColour.GREEN, "bold", "cosmosportals.ui.help.button_sounds"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.button_sounds_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.button_sounds_two")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 113, 138, 22, 22, ComponentHelper.style(ComponentColour.GREEN, "bold", "cosmosportals.ui.help.button_entity"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.button_entity_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.button_entity_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.button_entity_three")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 143, 138, 22, 22, ComponentHelper.style(ComponentColour.GREEN, "bold", "cosmosportals.ui.help.button_effects"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.button_effects_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.button_effects_two")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 43, 115, 94, 16, ComponentColour.DARK_GREEN, ComponentHelper.style(ComponentColour.DARK_GREEN, "bold", "cosmosportals.ui.help.label"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.label_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.label_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.label_three")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 165, 109, 8, 20, ComponentColour.YELLOW, ComponentHelper.style(ComponentColour.YELLOW, "bold", "cosmosportals.ui.help.colour"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.colour_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.colour_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.colour_three")
		);
	}
	
	public void renderPortalLabel(GuiGraphics graphics) {
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityPortalDock) {
			BlockEntityPortalDock blockEntity = (BlockEntityPortalDock) entity;
			
			if (blockEntity.isPortalFormed && blockEntity.renderLabel) {
				int portalColour = blockEntity.getDisplayColour();
				
				graphics.drawCenteredString(font, blockEntity.getContainerDisplayName(), this.getScreenCoords()[0] + (this.imageWidth - 11) / 2, this.getScreenCoords()[1] + 119, portalColour);
			}
		}
	}
}