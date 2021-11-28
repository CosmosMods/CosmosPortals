package com.tcn.cosmosportals.client.screen;

import java.util.Arrays;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tcn.cosmoslibrary.client.screen.option.BlankTitleOption;
import com.tcn.cosmoslibrary.client.screen.option.CustomBooleanOption;
import com.tcn.cosmoslibrary.client.screen.option.CustomBooleanOption.TYPE;
import com.tcn.cosmoslibrary.common.comp.CosmosColour;
import com.tcn.cosmoslibrary.common.comp.CosmosCompHelper;
import com.tcn.cosmosportals.core.management.CoreConfigurationManager;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.client.gui.widget.list.OptionsRowList.Row;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ScreenConfiguration extends Screen {

	private final Screen parent;

	private final int TITLE_HEIGHT = 8;

	private final int OPTIONS_LIST_TOP_HEIGHT = 24;
	private final int OPTIONS_LIST_BOTTOM_OFFSET = 32;
	private final int OPTIONS_LIST_ITEM_HEIGHT = 25;

	private final int BUTTON_WIDTH = 200;
	private final int BUTTON_HEIGHT = 20;
	private final int DONE_BUTTON_TOP_OFFSET = 26;

	private OptionsRowList optionsRowList;

	public ScreenConfiguration(Screen parent) {
		super(CosmosCompHelper.locComp(CosmosColour.LIGHT_GRAY, true, "cosmosportals.gui.config.name"));

		this.parent = parent;
	}

	@Override
	protected void init() {
		this.optionsRowList = new OptionsRowList(
			this.minecraft, this.width, this.height,
			OPTIONS_LIST_TOP_HEIGHT,
			this.height - OPTIONS_LIST_BOTTOM_OFFSET,
			OPTIONS_LIST_ITEM_HEIGHT);

		this.optionsRowList.addBig(
			new BlankTitleOption(CosmosColour.GRAY, true, "cosmosportals.gui.config.general_title")
		);
		
		this.optionsRowList.addBig(
			new SliderPercentageOption(
				"cosmosportals.gui.config.size", 2, 9, 1.0F,
				(options) -> (double) CoreConfigurationManager.getInstance().getPortalMaximumSize(),
				(options, newValue) -> CoreConfigurationManager.getInstance().setPortalMaximumSize(newValue.intValue()),
				(options, option) -> CosmosCompHelper.locComp(CosmosColour.WHITE, false, "cosmosportals.gui.config.size_slide").append(CosmosCompHelper.locComp(CosmosColour.GREEN, true, " [ " + option.get(options) + " ]"))
			)
		);
		
		this.optionsRowList.addSmall(
			new CustomBooleanOption(
				CosmosColour.ORANGE, false, "cosmosportals.gui.config.sound.travel", TYPE.YES_NO,
				(options) ->CoreConfigurationManager.getInstance().getPlayPortalTravelSounds(),
				(options, newValue) -> CoreConfigurationManager.getInstance().setPlayPortalTravelSounds(newValue)),
			new CustomBooleanOption(
				CosmosColour.ORANGE, false, "cosmosportals.gui.config.sound.ambient", TYPE.YES_NO,
				(options) -> CoreConfigurationManager.getInstance().getPlayPortalAmbientSounds(),
				(options, newValue) -> CoreConfigurationManager.getInstance().setPlayPortalAmbientSounds(newValue)
			)
		);
		
		this.optionsRowList.addBig(
			new BlankTitleOption(CosmosColour.GRAY, true, "cosmosportals.gui.config.messages_title")
		);
		
		this.optionsRowList.addSmall(
			new CustomBooleanOption(
				CosmosColour.CYAN, false, "cosmosportals.gui.config.message.info", TYPE.ON_OFF,
				(options) -> CoreConfigurationManager.getInstance().getInfoMessage(),
				(options, newValue) -> CoreConfigurationManager.getInstance().setInfoMessage(newValue)
			),
			new CustomBooleanOption(
				CosmosColour.CYAN, false, "cosmosportals.gui.config.message.debug", TYPE.ON_OFF,
				(options) -> CoreConfigurationManager.getInstance().getDebugMessage(),
				(options, newValue) -> CoreConfigurationManager.getInstance().setDebugMessage(newValue)
			)
		);

		this.optionsRowList.addBig(
			new BlankTitleOption(CosmosColour.GRAY, true, "cosmosportals.gui.config.visual_title")
		);
		
		this.optionsRowList.addSmall(
			new CustomBooleanOption(
				CosmosColour.LIGHT_BLUE, false, "cosmosportals.gui.config.frame_textures", TYPE.ON_OFF,
				(options) -> CoreConfigurationManager.getInstance().getFrameConnectedTextures(),
				(options, newValue) -> CoreConfigurationManager.getInstance().setFrameConnectedTextures(newValue)
			),
			new CustomBooleanOption(
				CosmosColour.LIGHT_BLUE, false, "cosmosportals.gui.config.portal_textures", TYPE.ON_OFF,
				(options) -> CoreConfigurationManager.getInstance().getPortalConnectedTextures(),
				(options, newValue) -> CoreConfigurationManager.getInstance().setPortalConnectedTextures(newValue)
			)
		);

		this.optionsRowList.addSmall(
			new CustomBooleanOption(
				CosmosColour.LIGHT_BLUE, false, "cosmosportals.gui.config.labels", TYPE.ON_OFF,
				(options) -> CoreConfigurationManager.getInstance().getRenderPortalLabels(),
				(options, newValue) -> CoreConfigurationManager.getInstance().setRenderPortalLabels(newValue)
			),
			new CustomBooleanOption(
				CosmosColour.LIGHT_BLUE, false, "cosmosportals.gui.config.particles", TYPE.ON_OFF,
				(options) -> CoreConfigurationManager.getInstance().getRenderPortalParticleEffects(),
				(options, newValue) -> CoreConfigurationManager.getInstance().setRenderPortalParticleEffects(newValue)
			)
		);
		
		this.children.add(this.optionsRowList);
		
		this.addButton(
			new Button(
				(this.width - BUTTON_WIDTH) /2, this.height - DONE_BUTTON_TOP_OFFSET, BUTTON_WIDTH, BUTTON_HEIGHT,
				CosmosCompHelper.locComp(CosmosColour.GREEN, true, "cosmosportals.gui.done"), (button) -> { this.onClose(); }
			)
		);
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float ticks) {
		this.renderBackground(matrixStack);
		
		this.optionsRowList.render(matrixStack, mouseX, mouseY, ticks);
		
		drawCenteredString(matrixStack, this.font, this.title, width / 2, TITLE_HEIGHT, 0xFFFFFF);

		this.renderComponentHoverEffect(matrixStack, Style.EMPTY, mouseX, mouseY);
		super.render(matrixStack, mouseX, mouseY, ticks);
	}
	
	@Override
	public void renderComponentHoverEffect(MatrixStack matrixStack, Style style, int mouseX, int mouseY) {
		if (mouseY > this.OPTIONS_LIST_TOP_HEIGHT && mouseY < this.height - this.OPTIONS_LIST_BOTTOM_OFFSET) {
			if (this.optionsRowList.children().get(0) != null) {
				for (int i = 0; i < this.optionsRowList.children().size(); i++) {
						if (!(i == 0) && !(i == 3) && !(i == 5)) {
						Row testRow = this.optionsRowList.children().get(i);
						
						if (testRow.children().size() > 1) {
							IGuiEventListener left = testRow.children().get(0);
							IGuiEventListener right = testRow.children().get(1);
							
							if (left.isMouseOver(mouseX, mouseY)) {
								this.renderComponentTooltip(matrixStack, Arrays.asList(this.getTooltipForChild((double) i + 0.3D)), mouseX, mouseY + 30);
							} else if (right.isMouseOver(mouseX, mouseY)) {
								this.renderComponentTooltip(matrixStack, Arrays.asList(this.getTooltipForChild((double) i + 0.6D)), mouseX, mouseY + 30);
							}
							
						} else {
							if (testRow.getChildAt(mouseX, mouseY).isPresent()) {
								IGuiEventListener list = testRow.getChildAt(mouseX, mouseY).get();
								
								if (list.isMouseOver(mouseX, mouseY)) {
									this.renderComponentTooltip(matrixStack, Arrays.asList(this.getTooltipForChild(i)), mouseX, mouseY + 30);
								}
							}
						}
					}
				}
			}
		}
		super.renderComponentHoverEffect(matrixStack, style, mouseX, mouseY);
	}
	
	public IFormattableTextComponent[] getTooltipForChild(double index) {
		CosmosColour desc = CosmosColour.LIGHT_GRAY;
		
		if (index == 1.0D) {
			return new IFormattableTextComponent[] {CosmosCompHelper.locComp(desc, false, "cosmosportals.gui.config.size_info"),
					CosmosCompHelper.locComp(CosmosColour.RED, false, "cosmosportals.gui.config.size_info_two"),
					CosmosCompHelper.locComp(CosmosColour.LIGHT_RED, false, "cosmosportals.gui.config.size_info_three")};
		} else if (index == 2.3D) {
			return new IFormattableTextComponent[] {CosmosCompHelper.locComp(desc, false, "cosmosportals.gui.config.sound.travel_info")};
		} else if (index == 2.6D) {
			return new IFormattableTextComponent[] {CosmosCompHelper.locComp(desc, false, "cosmosportals.gui.config.sound.ambient_info")};
		} else if (index == 4.3D) {
			return new IFormattableTextComponent[] {CosmosCompHelper.locComp(desc, false, "cosmosportals.gui.config.message.info_desc")};
		} else if (index == 4.6D) {
			return new IFormattableTextComponent[] {CosmosCompHelper.locComp(desc, false, "cosmosportals.gui.config.message.debug_desc")};
		} else if (index == 6.3D) {
			return new IFormattableTextComponent[] {CosmosCompHelper.locComp(desc, false, "cosmosportals.gui.config.frame_textures_info")};
		} else if (index == 6.6D) {
			return new IFormattableTextComponent[] {CosmosCompHelper.locComp(desc, false, "cosmosportals.gui.config.portal_textures_info")};
		} else if (index == 7.3D) {
			return new IFormattableTextComponent[] {CosmosCompHelper.locComp(desc, false, "cosmosportals.gui.config.labels_info")};
		} else if (index == 7.6D) {
			return new IFormattableTextComponent[] {CosmosCompHelper.locComp(desc, false, "cosmosportals.gui.config.particles_info")};
		} else {
			return new IFormattableTextComponent[] {CosmosCompHelper.locComp(desc, false, "cosmosportals.gui.config.missing")};
		}
	}
	
    @Override
    public void onClose() {
    	this.minecraft.setScreen(parent);
    	
        CoreConfigurationManager.save();
    }
}