package com.tcn.cosmosportals.client.screen;

import java.util.Arrays;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenUIModeBE;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType.TYPE;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosColourButton;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmosportals.CosmosPortalsReference;
import com.tcn.cosmosportals.client.container.ContainerPortalDock;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortalDock;
import com.tcn.cosmosportals.core.management.NetworkManager;
import com.tcn.cosmosportals.core.network.PacketPortalDock;
import com.tcn.cosmosportals.core.network.PacketPortalDockColour;
import com.tcn.cosmosportals.core.network.PacketPortalDockName;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
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
	
	private CosmosButtonWithType settingsButton; private int[] indexSe = new int[] { 16, 109, 20 }; //21
	
	private CosmosButtonWithType clearButton; private int[] indexCl = new int[] { 110, 66, 18 };
	private CosmosButtonWithType applyButton; private int[] indexA = new int[] { 130, 66, 18 };
	private CosmosColourButton colourButton; private int[] indexC = new int[] { 150, 66, 18 };

	private EditBox textField; private int[] textFieldI = new int[] { 17, 72, 93, 16 };
	
	private boolean displaySettings = false;
	
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
		this.setScreenCoords(CosmosUISystem.getScreenCoords(this, this.imageWidth, this.imageHeight));
		this.initTextField();
		super.init();
	}
	
	@Override
	protected void containerTick() {
		if (this.displaySettings) {
			this.textField.tick();
		}
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
				String human_name = blockEntity.getPortalDisplayName();
				int width = this.font.width(human_name) + 2;
				
				CosmosUISystem.renderStaticElement(this, poseStack, this.getScreenCoords(), (this.imageWidth - 11) / 2 - width / 2, 117, 0, 0, width, 12, new float[] { 1.0F, 1.0F, 1.0F, 0.6F }, CosmosPortalsReference.DOCK_LABEL);
			}
			
			if (this.displaySettings) {
				CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, this.getScreenCoords(), 8, 50, 0, 0, 165, 39, new float[] { 1.0F, 1.0F, 1.0F, 1.0F }, blockEntity, CosmosPortalsReference.DOCK_SETTINGS);
				CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, this.getScreenCoords(), 8, 61, 0, 0, 165, 28, new float[] { 1.0F, 1.0F, 1.0F, 1.0F }, blockEntity, CosmosPortalsReference.DOCK_OVERLAY);
				
				this.textField.render(poseStack, mouseX, mouseY, portalColour);
			}
		}
	}
	
	@Override
	public void renderStandardHoverEffect(PoseStack poseStack, Style style, int mouseX, int mouseY) {
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityPortalDock) {
			BlockEntityPortalDock blockEntity = (BlockEntityPortalDock) entity;
			
			if (this.toggleLabelButton.isMouseOver(mouseX, mouseY)) {
				MutableComponent[] comp = new MutableComponent[] { ComponentHelper.style(ComponentColour.WHITE, "cosmosportals.gui.dock.label_info"), 
					(MutableComponent) ComponentHelper.style2(ComponentColour.GRAY, "cosmosportals.gui.dock.label_value", " ")
					.append(blockEntity.renderLabel ? ComponentHelper.style(ComponentColour.GREEN, "bold", "cosmosportals.gui.dock.label_shown") : ComponentHelper.style(ComponentColour.RED, "bold", "cosmosportals.gui.dock.label_hidden"))
				};
				
				this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
			}
			
			if (this.toggleSoundButton.isMouseOver(mouseX, mouseY)) {
				MutableComponent[] comp = new MutableComponent[] { ComponentHelper.style(ComponentColour.WHITE, "cosmosportals.gui.dock.sounds_info"), 
					(MutableComponent) ComponentHelper.style2(ComponentColour.GRAY, "cosmosportals.gui.dock.sounds_value", " ")
					.append(blockEntity.playSound ? ComponentHelper.style(ComponentColour.GREEN, "bold", "cosmosportals.gui.dock.sounds_played") : ComponentHelper.style(ComponentColour.RED, "bold", "cosmosportals.gui.dock.sounds_muffled"))
				};
				
				this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
			}
			
			if (this.toggleEntityButton.isMouseOver(mouseX, mouseY)) {
				MutableComponent[] comp = new MutableComponent[] { ComponentHelper.style(ComponentColour.WHITE, "cosmosportals.gui.dock.entity_info"), 
					(MutableComponent) ComponentHelper.style2(ComponentColour.GRAY, "cosmosportals.gui.dock.entity_value", " ")
					.append(blockEntity.allowedEntities.getColouredComp())
				};
				
				this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
			}

			if (this.toggleParticlesButton.isMouseOver(mouseX, mouseY)) {
				MutableComponent[] comp = new MutableComponent[] { ComponentHelper.style(ComponentColour.WHITE, "cosmosportals.gui.dock.particle_info"), 
					(MutableComponent) ComponentHelper.style2(ComponentColour.GRAY, "cosmosportals.gui.dock.particle_value", " ")
					.append(blockEntity.showParticles ? ComponentHelper.style(ComponentColour.GREEN, "bold", "cosmosportals.gui.dock.particle_shown") : ComponentHelper.style(ComponentColour.RED, "bold", "cosmosportals.gui.dock.particle_hidden"))
				};
				
				this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
			}
			
			if (this.settingsButton.isMouseOver(mouseX, mouseY)) {
				this.renderTooltip(poseStack, ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.gui.dock.settings"), mouseX, mouseY);
			}
			
			if (this.displaySettings) {
				if (this.clearButton.isMouseOver(mouseX, mouseY)) {
					this.renderTooltip(poseStack, ComponentHelper.style(ComponentColour.RED, "cosmosportals.gui.button.text.clear"), mouseX, mouseY);
				}
				
				if (this.applyButton.isMouseOver(mouseX, mouseY)) {
					if (!this.textField.getValue().isEmpty()) {
						this.renderTooltip(poseStack, ComponentHelper.style(ComponentColour.GREEN, "cosmosportals.gui.button.text.set"), mouseX, mouseY);
					} else {
						this.renderTooltip(poseStack, ComponentHelper.style(ComponentColour.GREEN, "cosmosportals.gui.button.text.reset"), mouseX, mouseY);
					}
				}
				
				if (this.colourButton.isMouseOver(mouseX, mouseY)) {
					MutableComponent[] comp = new MutableComponent[] { ComponentHelper.style(ComponentColour.WHITE, "cosmoslibrary.gui.colour.info"), 
						(MutableComponent) ComponentHelper.style(ComponentColour.GRAY, "cosmoslibrary.gui.colour.value").append(blockEntity.getCustomColour().getColouredName())
					};
					
					this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
				}
			}
		}
		super.renderStandardHoverEffect(poseStack, style, mouseX, mouseY);
	}
		
	@Override
	protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
		drawCenteredString(poseStack, font, this.title, this.imageWidth / 2, 17, CosmosUISystem.DEFAULT_COLOUR_FONT_LIST);
		super.renderLabels(poseStack, mouseX, mouseY);

		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityPortalDock) {
			BlockEntityPortalDock blockEntity = (BlockEntityPortalDock) entity;
			
			if (this.displaySettings) {
				this.font.draw(poseStack, ComponentHelper.title("cosmosportals.gui.dock.settings_title"), 13, 56, blockEntity.getUIMode().equals(EnumUIMode.DARK) ? CosmosUISystem.DEFAULT_COLOUR_FONT_LIST : ComponentColour.BLACK.dec());
			}
		}
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

			this.toggleLabelButton = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + indexL[0], this.getScreenCoords()[1] + indexL[1], indexL[2], true, true, blockEntity.renderLabel ? 1 : 2, ComponentHelper.empty(), (button) -> { this.pushButton(this.toggleLabelButton);	}));
			this.toggleSoundButton = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + indexS[0], this.getScreenCoords()[1] + indexS[1], indexS[2], true, true, blockEntity.playSound ? 1 : 2, ComponentHelper.empty(), (button) -> { this.pushButton(this.toggleSoundButton); }));
			this.toggleEntityButton = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + indexE[0], this.getScreenCoords()[1] + indexE[1], indexE[2], true, true, i, ComponentHelper.empty(), (button) -> { this.pushButton(this.toggleEntityButton); }));
			this.toggleParticlesButton = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + indexP[0], this.getScreenCoords()[1] + indexP[1], indexP[2], true, true, blockEntity.showParticles ? 1 : 2, ComponentHelper.empty(), (button) -> { this.pushButton(this.toggleParticlesButton); }));
			
			this.settingsButton = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + indexSe[0], this.getScreenCoords()[1] + indexSe[1], indexSe[2], true, true, 21, ComponentHelper.empty(), (button) -> { this.pushButton(this.settingsButton); }));
			
			this.clearButton = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + indexCl[0], this.getScreenCoords()[1] + indexCl[1], indexCl[2], !(this.textField.getValue().isEmpty()), this.displaySettings, 14, ComponentHelper.empty(), (button) -> { this.pushButton(this.clearButton);}));
			this.applyButton = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + indexA[0], this.getScreenCoords()[1] + indexA[1], indexA[2], true, this.displaySettings, 1, ComponentHelper.empty(), (button) -> { this.pushButton(this.applyButton); }));			
			this.colourButton = this.addRenderableWidget(new CosmosColourButton(blockEntity.getCustomColour(), this.getScreenCoords()[0] + indexC[0], this.getScreenCoords()[1] + indexC[1], indexC[2], true, this.displaySettings, ComponentHelper.empty(), (button) -> { this.pushButton(this.colourButton); }));
			
			//this.reformPortalButton = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + indexR[0], this.getScreenCoords()[1] + indexR[1], indexR[2], !blockEntity.isPortalFormed, true,          1, ComponentHelper.style(""), (button) -> { this.pushButton(this.reformPortalButton);    }));
		}
	}
	
	@Override
	public void pushButton(Button button) {
		super.pushButton(button);
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityPortalDock) {
			BlockEntityPortalDock blockEntity = (BlockEntityPortalDock) entity;
			
			if (!this.displaySettings) {
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
			
			if (button.equals(this.settingsButton)) {
				this.displaySettings = !this.displaySettings;
				this.textField.setVisible(this.displaySettings);
				this.textField.setFocus(this.displaySettings);
			}
			
			if (this.displaySettings) {
				if (button.equals(this.applyButton)) {
					NetworkManager.sendToServer(new PacketPortalDockName(this.menu.getBlockPos(), this.textField.getValue()));
					blockEntity.setPortalDisplayName(this.textField.getValue());
				}
				
				if (button.equals(this.colourButton)) {
					ComponentColour colour = blockEntity.getCustomColour().getNextVanillaColour(true);
					NetworkManager.sendToServer(new PacketPortalDockColour(this.menu.getBlockPos(), colour));
				}
				
				if (button.equals(this.clearButton)) {
					this.textField.setValue("");
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
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 144, 109, 20, 20, ComponentHelper.style(ComponentColour.LIGHT_GRAY, "bold", "cosmosportals.ui.help.container_item"), 
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

		this.addRenderableUIHelpElement(this.getScreenCoords(), 15, 108, 22, 22, ComponentHelper.style(ComponentColour.GREEN, "bold", "cosmosportals.ui.help.button_settings"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.button_settings_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.button_settings_two")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 143, 138, 22, 22, ComponentHelper.style(ComponentColour.GREEN, "bold", "cosmosportals.ui.help.button_effects"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.button_effects_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.button_effects_two")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 43, 115, 94, 16, ComponentHelper.style(ComponentColour.DARK_GREEN, "bold", "cosmosportals.ui.help.label"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.label_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.label_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.label_three")
		);
		
		if (this.displaySettings) {
			this.addRenderableUIHelpElement(this.getScreenCoords(), 13, 66, 95, 18, ComponentHelper.style(ComponentColour.DARK_GREEN, "bold", "cosmosportals.ui.help.name"), 
				ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.name_one"), 
				ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.name_two"),
				ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.name_three")
			);
			
			this.addRenderableUIHelpElement(this.getScreenCoords(), 110, 66, 18, 18, ComponentHelper.style(ComponentColour.DARK_GREEN, "bold", "cosmosportals.ui.help.clear"), 
				ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.clear_one"), 
				ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.clear_two")
			);
			
			this.addRenderableUIHelpElement(this.getScreenCoords(), 130, 66, 18, 18, ComponentHelper.style(ComponentColour.DARK_GREEN, "bold", "cosmosportals.ui.help.apply"), 
				ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.apply_one"), 
				ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.apply_two"),
				ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.apply_three")
			);

			this.addRenderableUIHelpElement(this.getScreenCoords(), 150, 66, 18, 18, ComponentHelper.style(ComponentColour.DARK_GREEN, "bold", "cosmosportals.ui.help.colour"), 
				ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.colour_one"), 
				ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.colour_two"),
				ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.colour_three")
			);
		}
	}
	
	public void renderPortalLabel(PoseStack poseStack) {
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityPortalDock) {
			BlockEntityPortalDock blockEntity = (BlockEntityPortalDock) entity;
			
			if (blockEntity.isPortalFormed && blockEntity.renderLabel) {
				int portalColour = blockEntity.getDisplayColour();
				
				drawCenteredString(poseStack, font, blockEntity.getPortalDisplayName(), this.getScreenCoords()[0] + (this.imageWidth - 11) / 2, this.getScreenCoords()[1] + 119, portalColour);
			}
		}
	}

	public void initTextField() {
		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.textField = new EditBox(this.font, this.getScreenCoords()[0] + this.textFieldI[0], this.getScreenCoords()[1] + this.textFieldI[1], this.textFieldI[2], this.textFieldI[3], ComponentHelper.comp("Portal Name Entry"));
		this.textField.setMaxLength(20);
		this.textField.setTextColor(CosmosUISystem.DEFAULT_COLOUR_FONT_LIST);
		this.textField.setVisible(true);
		this.textField.setBordered(false);
		this.textField.setCanLoseFocus(true);
		this.textField.setEditable(true);
		
		this.addWidget(this.textField);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		return this.textField.mouseClicked(mouseX, mouseY, mouseButton) ? true : super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollDirection) {
		return this.textField.mouseScrolled(mouseX, mouseY, scrollDirection) ? true : super.mouseScrolled(mouseX, mouseY, scrollDirection);
	}

	@Override
	public boolean keyPressed(int keyCode, int mouseX, int mouseY) {
		if (keyCode == 256) {
			if (this.textField.isFocused()) {
				this.textField.setFocus(false);
			} else {
				this.minecraft.player.closeContainer();
			}
		}
		
		return !this.textField.keyPressed(keyCode, mouseX, mouseY) && !this.textField.canConsumeInput() ? super.keyPressed(keyCode, mouseX, mouseY) : true;
	}

	@Override
	public boolean charTyped(char charIn, int charCode) {
		return !this.textField.charTyped(charIn, charCode) && !this.textField.canConsumeInput() ? super.charTyped(charIn, charCode) : true;
	}
}