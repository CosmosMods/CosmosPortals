package com.tcn.cosmosportals.client.screen;

import java.util.Arrays;

import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenUIModeBE;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType.TYPE;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosColourButton;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmosportals.CosmosPortalsReference;
import com.tcn.cosmosportals.client.container.ContainerContainerWorkbench;
import com.tcn.cosmosportals.core.blockentity.BlockEntityContainerWorkbench;
import com.tcn.cosmosportals.core.item.ItemPortalContainer;
import com.tcn.cosmosportals.core.management.NetworkManager;
import com.tcn.cosmosportals.core.network.PacketColour;
import com.tcn.cosmosportals.core.network.PacketWorkbenchName;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ScreenContainerWorkbench extends CosmosScreenUIModeBE<ContainerContainerWorkbench> {
	
	private CosmosButtonWithType clearButton; private int[] indexCl = new int[] { 5, 17, 18 };
	private CosmosButtonWithType applyButton; private int[] indexA = new int[] { 5, 45, 18 };
	private CosmosColourButton colourButton; private int[] indexC = new int[] { 149, 45, 18 };
		
	private EditBox textField; private int[] textFieldI = new int[] { 38, 22, 104, 16 };
	
	public ScreenContainerWorkbench(ContainerContainerWorkbench containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);

		this.setImageDims(172, 157);

		this.setLight(CosmosPortalsReference.WORKBENCH[0]);
		this.setDark(CosmosPortalsReference.WORKBENCH[1]);

		this.setUIModeButtonIndex(155, 5);
		this.setUIHelpButtonIndex(155, 19);
		this.setUIHelpElementDeadzone(144, 109, 163, 128);
		this.setUIHelpTitleOffset(4);
		
		this.setUIHelpElementDeadzone(29, 14, 143, 32);
		
		this.setInventoryLabelDims(5, 66);
		this.setTitleLabelDims(5, 5);
		this.setHasEditBox();
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	protected void containerTick() {
		this.textField.tick();
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.render(graphics, mouseX, mouseY, partialTicks);
		
		Slot slot = this.menu.getSlot(1);
		
		if (!slot.getItem().isEmpty()) {
			ItemStack stack = slot.getItem();
			
			if (stack.getItem() instanceof ItemPortalContainer) {
				if (stack.hasTag()) {
					CompoundTag stack_tag = stack.getTag();
					
					if (stack_tag.contains("nbt_data")) {
						CompoundTag nbt_data = stack_tag.getCompound("nbt_data");
						
						if (nbt_data.contains("display_data")) {
							CompoundTag display_data = nbt_data.getCompound("display_data");
							
							String display_name = display_data.getString("name");
							
							if (this.textField.getValue().isBlank() && !this.textField.isFocused()) {
								this.textField.setValue(display_name);
								this.textField.setHighlightPos(0);
								this.textField.setFocused(true);
							}
						}
					}
				}
			}
		} else if (!this.textField.getValue().isBlank()) {
			this.textField.setValue("");
		}
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(graphics, partialTicks, mouseX, mouseY);
		
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityContainerWorkbench) {
			BlockEntityContainerWorkbench blockEntity = (BlockEntityContainerWorkbench) entity;

			CosmosUISystem.renderStaticElementWithUIMode(this, graphics, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, new float[] { 1.0F, 1.0F, 1.0F, 1.0F }, blockEntity, CosmosPortalsReference.WORKBENCH_SLOTS);
			CosmosUISystem.renderStaticElementWithUIMode(this, graphics, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, new float[] { 1.0F, 1.0F, 1.0F, 1.0F }, blockEntity, CosmosPortalsReference.WORKBENCH_OVERLAY);
		}

		this.textField.render(graphics, mouseX, mouseY, CosmosUISystem.DEFAULT_COLOUR_FONT_LIST);
	}
	
	@Override
	protected void addUIHelpElements() {
		super.addUIHelpElements();
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 32, 44, 20, 20, ComponentHelper.style(ComponentColour.WHITE, "bold", "cosmosportals.ui.help.workbench.container"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.workbench.container_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.workbench.container_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.workbench.container_three")
		);
		
		this.addRenderableUIHelpElement(this.getScreenCoords(), 76, 44, 20, 20, ComponentHelper.style(ComponentColour.GREEN, "bold", "cosmosportals.ui.help.workbench.container_linked"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.workbench.container_linked_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.workbench.container_linked_two")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 120, 44, 20, 20, ComponentHelper.style(ComponentColour.GREEN, "bold", "cosmosportals.ui.help.workbench.container_copied"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.workbench.container_copied_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.workbench.container_copied_two")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 4, 44, 20, 20, ComponentHelper.style(ComponentColour.GREEN, "bold", "cosmosportals.ui.help.workbench.button_apply"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.workbench.button_apply_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.workbench.button_apply_two")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 4, 16, 20, 20, ComponentHelper.style(ComponentColour.GREEN, "bold", "cosmosportals.ui.help.workbench.button_clear"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.workbench.button_clear_one")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 32, 16, 108, 20, ComponentHelper.style(ComponentColour.GREEN, "bold", "cosmosportals.ui.help.workbench.text"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.workbench.text_one"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.workbench.text_two")
		);

		this.addRenderableUIHelpElement(this.getScreenCoords(), 148, 44, 20, 20, ComponentHelper.style(ComponentColour.DARK_GREEN, "bold", "cosmosportals.ui.help.workbench.colour"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.workbench.colour_one"), 
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.workbench.colour_two"),
			ComponentHelper.style(ComponentColour.LIGHT_GRAY, "cosmosportals.ui.help.workbench.colour_three")
		);
	}
	
	@Override
	public void renderStandardHoverEffect(GuiGraphics graphics, Style style, int mouseX, int mouseY) {
		BlockEntity entity = this.getBlockEntity();

		if (entity instanceof BlockEntityContainerWorkbench) {
			BlockEntityContainerWorkbench blockEntity = (BlockEntityContainerWorkbench) entity;

			if (this.clearButton.isMouseOver(mouseX, mouseY)) {
				graphics.renderTooltip(this.font, ComponentHelper.style(ComponentColour.RED, "cosmosportals.gui.button.text.clear"), mouseX, mouseY);
			}
			
			if (this.colourButton.isMouseOver(mouseX, mouseY)) {
				MutableComponent[] comp = new MutableComponent[] { ComponentHelper.style(ComponentColour.WHITE, "cosmoslibrary.gui.colour.info"), 
					(MutableComponent) ComponentHelper.style(ComponentColour.GRAY, "cosmoslibrary.gui.colour.value").append(blockEntity.getCustomColour().getColouredName())
				};
				
				graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			}

			if (this.applyButton.isMouseOver(mouseX, mouseY)) {
				if (!this.textField.getValue().isEmpty()) {
					graphics.renderTooltip(this.font, ComponentHelper.style(ComponentColour.GREEN, "cosmosportals.gui.button.text.set"), mouseX, mouseY);
				} else {
					graphics.renderTooltip(this.font, ComponentHelper.style(ComponentColour.GREEN, "cosmosportals.gui.button.text.reset"), mouseX, mouseY);
				}
			}
		}
		super.renderStandardHoverEffect(graphics, style, mouseX, mouseY);
	}
	
	@Override
	protected void addButtons() {
		super.addButtons();
		BlockEntity entity = this.getBlockEntity();

		if (entity instanceof BlockEntityContainerWorkbench) {
			BlockEntityContainerWorkbench blockEntity = (BlockEntityContainerWorkbench) entity;
			
			this.clearButton = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + indexCl[0], this.getScreenCoords()[1] + indexCl[1], indexCl[2], !(this.textField.getValue().isEmpty()), true, 14, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.clearButton, isLeftClick);}));
			this.applyButton = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + indexA[0], this.getScreenCoords()[1] + indexA[1], indexA[2], true, true, 1, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.applyButton, isLeftClick); }));
			this.colourButton = this.addRenderableWidget(new CosmosColourButton(blockEntity.getCustomColour(), this.getScreenCoords()[0] + indexC[0], this.getScreenCoords()[1] + indexC[1], indexC[2], true, true, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.colourButton, isLeftClick); }));
		}
	}
	
	@Override
	public void clickButton(Button button, boolean isLeftClick) {
		super.clickButton(button, isLeftClick);
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityContainerWorkbench) {
			BlockEntityContainerWorkbench blockEntity = (BlockEntityContainerWorkbench) entity;
			
			if (isLeftClick) {
				if (button.equals(this.applyButton)) {
					NetworkManager.sendToServer(new PacketWorkbenchName(this.menu.getBlockPos(), this.textField.getValue()));
					blockEntity.setContainerDisplayName(this.textField.getValue());
				}
	
				if (button.equals(this.colourButton)) {
					ComponentColour colour = hasShiftDown() ? ComponentColour.EMPTY : blockEntity.getCustomColour().getNextVanillaColour(true);
					NetworkManager.sendToServer(new PacketColour(this.menu.getBlockPos(), colour));
				}
				
				if (button.equals(this.clearButton)) {
					this.textField.setValue("");
				}
			}
			
			//Do right click
			else {
				if (button.equals(this.colourButton)) {
					ComponentColour colour = hasShiftDown() ? ComponentColour.EMPTY : blockEntity.getCustomColour().getNextVanillaColourReverse(true);
					NetworkManager.sendToServer(new PacketColour(this.menu.getBlockPos(), colour));
				}
			}
		}
	}

	@Override
	public void initEditBox() {
		super.initEditBox();
		//this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.textField = new EditBox(this.font, this.getScreenCoords()[0] + this.textFieldI[0], this.getScreenCoords()[1] + this.textFieldI[1], this.textFieldI[2], this.textFieldI[3], ComponentHelper.comp("Portal Name Entry"));
		this.textField.setMaxLength(12);
		this.textField.setTextColor(CosmosUISystem.DEFAULT_COLOUR_FONT_LIST);
		this.textField.setVisible(true);
		this.textField.setBordered(false);
		this.textField.setCanLoseFocus(true);
		this.textField.setEditable(true);
		
		this.addWidget(this.textField);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (this.textField.mouseClicked(mouseX, mouseY, mouseButton)) {
			this.textField.setFocused(true);
		} else {
			this.textField.setFocused(false);
		}
		
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
				this.textField.setFocused(false);
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