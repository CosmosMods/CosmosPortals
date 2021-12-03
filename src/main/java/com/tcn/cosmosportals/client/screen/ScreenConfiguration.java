package com.tcn.cosmosportals.client.screen;

import java.util.Arrays;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmoslibrary.client.screen.option.CosmosOptionBoolean;
import com.tcn.cosmoslibrary.client.screen.option.CosmosOptionBoolean.TYPE;
import com.tcn.cosmoslibrary.client.screen.option.CosmosOptionTitle;
import com.tcn.cosmoslibrary.client.screen.option.CosmosOptionsList;
import com.tcn.cosmoslibrary.client.screen.option.CosmosOptionsList.Entry;
import com.tcn.cosmoslibrary.common.comp.CosmosColour;
import com.tcn.cosmoslibrary.common.comp.CosmosCompHelper;
import com.tcn.cosmosportals.core.management.ConfigurationManager;

import net.minecraft.client.ProgressOption;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.client.ConfigGuiHandler.ConfigGuiFactory;

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

	private CosmosOptionsList optionsRowList;
	
	private static ConfigGuiHandler.ConfigGuiFactory INSTANCE = new ConfigGuiHandler.ConfigGuiFactory((mc, screen) -> new ScreenConfiguration(screen));
	
	public static ConfigGuiFactory getInstance() {
		return INSTANCE;
	}
	
	public ScreenConfiguration(Screen parent) {
		super(CosmosCompHelper.locComp(CosmosColour.LIGHT_GRAY, true, "cosmosportals.gui.config.name"));

		this.parent = parent;
	}

	@Override
	protected void init() {
		this.optionsRowList = new CosmosOptionsList( this.minecraft, this.width, this.height,
			OPTIONS_LIST_TOP_HEIGHT, this.height - OPTIONS_LIST_BOTTOM_OFFSET, OPTIONS_LIST_ITEM_HEIGHT
		);

		this.optionsRowList.addBig(
			new CosmosOptionTitle(CosmosColour.GRAY, true, "cosmosportals.gui.config.general_title")
		);
		
		this.optionsRowList.addBig(
			new ProgressOption(
				"cosmosportals.gui.config.size", 2, 9, 1.0F,
				(options) -> (double) ConfigurationManager.getInstance().getPortalMaximumSize(),
				(options, newValue) -> ConfigurationManager.getInstance().setPortalMaximumSize(newValue.intValue()),
				(options, option) -> CosmosCompHelper.locComp(CosmosColour.WHITE, false, "cosmosportals.gui.config.size_slide").append(CosmosCompHelper.locComp(CosmosColour.GREEN, true, " [ " + option.get(options) + " ]"))
			)
		);
		
		this.optionsRowList.addSmall(
			new CosmosOptionBoolean(
				CosmosColour.ORANGE, false, "cosmosportals.gui.config.sound.travel", TYPE.YES_NO,
				(options) ->ConfigurationManager.getInstance().getPlayPortalTravelSounds(),
				(options, newValue) -> ConfigurationManager.getInstance().setPlayPortalTravelSounds(newValue)),
			new CosmosOptionBoolean(
				CosmosColour.ORANGE, false, "cosmosportals.gui.config.sound.ambient", TYPE.YES_NO,
				(options) -> ConfigurationManager.getInstance().getPlayPortalAmbientSounds(),
				(options, newValue) -> ConfigurationManager.getInstance().setPlayPortalAmbientSounds(newValue)
			)
		);
		
		this.optionsRowList.addBig(
			new CosmosOptionTitle(CosmosColour.GRAY, true, "cosmosportals.gui.config.messages_title")
		);
		
		this.optionsRowList.addSmall(
			new CosmosOptionBoolean(
				CosmosColour.CYAN, false, "cosmosportals.gui.config.message.info", TYPE.ON_OFF,
				(options) -> ConfigurationManager.getInstance().getInfoMessage(),
				(options, newValue) -> ConfigurationManager.getInstance().setInfoMessage(newValue)
			),
			new CosmosOptionBoolean(
				CosmosColour.CYAN, false, "cosmosportals.gui.config.message.debug", TYPE.ON_OFF,
				(options) -> ConfigurationManager.getInstance().getDebugMessage(),
				(options, newValue) -> ConfigurationManager.getInstance().setDebugMessage(newValue)
			)
		);

		this.optionsRowList.addBig(
			new CosmosOptionTitle(CosmosColour.GRAY, true, "cosmosportals.gui.config.visual_title")
		);
		
		this.optionsRowList.addSmall(
			new CosmosOptionBoolean(
				CosmosColour.LIGHT_BLUE, false, "cosmosportals.gui.config.frame_textures", TYPE.ON_OFF,
				(options) -> ConfigurationManager.getInstance().getFrameConnectedTextures(),
				(options, newValue) -> ConfigurationManager.getInstance().setFrameConnectedTextures(newValue)
			),
			new CosmosOptionBoolean(
				CosmosColour.LIGHT_BLUE, false, "cosmosportals.gui.config.portal_textures", TYPE.ON_OFF,
				(options) -> ConfigurationManager.getInstance().getPortalConnectedTextures(),
				(options, newValue) -> ConfigurationManager.getInstance().setPortalConnectedTextures(newValue)
			)
		);

		this.optionsRowList.addSmall(
			new CosmosOptionBoolean(
				CosmosColour.LIGHT_BLUE, false, "cosmosportals.gui.config.labels", TYPE.ON_OFF,
				(options) -> ConfigurationManager.getInstance().getRenderPortalLabels(),
				(options, newValue) -> ConfigurationManager.getInstance().setRenderPortalLabels(newValue)
			),
			new CosmosOptionBoolean(
				CosmosColour.LIGHT_BLUE, false, "cosmosportals.gui.config.particles", TYPE.ON_OFF,
				(options) -> ConfigurationManager.getInstance().getRenderPortalParticleEffects(),
				(options, newValue) -> ConfigurationManager.getInstance().setRenderPortalParticleEffects(newValue)
			)
		);
		
		this.addWidget(this.optionsRowList);
		
		this.addRenderableWidget(new Button(
			(this.width - BUTTON_WIDTH) /2, this.height - DONE_BUTTON_TOP_OFFSET, BUTTON_WIDTH, BUTTON_HEIGHT,
			CosmosCompHelper.locComp(CosmosColour.GREEN, true, "cosmosportals.gui.done"), (button) -> { this.onClose(); }
		));
	}
	
	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float ticks) {
		this.renderBackground(matrixStack);
		
		this.optionsRowList.render(matrixStack, mouseX, mouseY, ticks);
		
		drawCenteredString(matrixStack, this.font, this.title, width / 2, TITLE_HEIGHT, 0xFFFFFF);

		this.renderComponentHoverEffect(matrixStack, Style.EMPTY, mouseX, mouseY);
		super.render(matrixStack, mouseX, mouseY, ticks);
	}
	
	@Override
	public void renderComponentHoverEffect(PoseStack matrixStack, Style style, int mouseX, int mouseY) {
		if (mouseY > this.OPTIONS_LIST_TOP_HEIGHT && mouseY < this.height - this.OPTIONS_LIST_BOTTOM_OFFSET) {
			if (this.optionsRowList.children().get(0) != null) {
				for (int i = 0; i < this.optionsRowList.children().size(); i++) {
					if (!(i == 0) && !(i == 3) && !(i == 5)) {
						
						Entry testRow = this.optionsRowList.children().get(i);
						
						if (testRow.children().size() > 1) {
							GuiEventListener left = testRow.children().get(0);
							GuiEventListener right = testRow.children().get(1);
							
							if (left.isMouseOver(mouseX, mouseY)) {
								this.renderComponentTooltip(matrixStack, Arrays.asList(this.getTooltipForChild((double) i + 0.3D)), mouseX, mouseY + 30);
							} else if (right.isMouseOver(mouseX, mouseY)) {
								this.renderComponentTooltip(matrixStack, Arrays.asList(this.getTooltipForChild((double) i + 0.6D)), mouseX, mouseY + 30);
							}
							
						} else {
							if (testRow.getChildAt(mouseX, mouseY).isPresent()) {
								GuiEventListener list = testRow.getChildAt(mouseX, mouseY).get();
								
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
	
	public BaseComponent[] getTooltipForChild(double index) {
		CosmosColour desc = CosmosColour.LIGHT_GRAY;
		
		if (index == 1.0D) {
			return new BaseComponent[] { CosmosCompHelper.locComp(desc, false, "cosmosportals.gui.config.size_info"),
					CosmosCompHelper.locComp(CosmosColour.RED, false, "cosmosportals.gui.config.size_info_two"),
					CosmosCompHelper.locComp(CosmosColour.LIGHT_RED, false, "cosmosportals.gui.config.size_info_three") };
		} else if (index == 2.3D) {
			return new BaseComponent[] { CosmosCompHelper.locComp(desc, false, "cosmosportals.gui.config.sound.travel_info") };
		} else if (index == 2.6D) {
			return new BaseComponent[] { CosmosCompHelper.locComp(desc, false, "cosmosportals.gui.config.sound.ambient_info") };
		} else if (index == 4.3D) {
			return new BaseComponent[] { CosmosCompHelper.locComp(desc, false, "cosmosportals.gui.config.message.info_desc"), CosmosCompHelper.locComp(CosmosColour.RED, true, "cosmosportals.gui.config.message.restart") };
		} else if (index == 4.6D) {
			return new BaseComponent[] { CosmosCompHelper.locComp(desc, false, "cosmosportals.gui.config.message.debug_desc"), CosmosCompHelper.locComp(CosmosColour.RED, true, "cosmosportals.gui.config.message.restart") };
		} else if (index == 6.3D) {
			return new BaseComponent[] { CosmosCompHelper.locComp(desc, false, "cosmosportals.gui.config.frame_textures_info") };
		} else if (index == 6.6D) {
			return new BaseComponent[] { CosmosCompHelper.locComp(desc, false, "cosmosportals.gui.config.portal_textures_info") };
		} else if (index == 7.3D) {
			return new BaseComponent[] { CosmosCompHelper.locComp(desc, false, "cosmosportals.gui.config.labels_info") };
		} else if (index == 7.6D) {
			return new BaseComponent[] { CosmosCompHelper.locComp(desc, false, "cosmosportals.gui.config.particles_info") };
		} else {
			return new BaseComponent[] { CosmosCompHelper.locComp(desc, false, "cosmosportals.gui.config.missing") };
		}
	}
	
	@Override
	public boolean handleComponentClicked(@Nullable Style styleIn) {
		return super.handleComponentClicked(styleIn);
	}
	
	@Override
	public boolean keyPressed(int mouseX, int mouseY, int ticks) {
		return super.keyPressed(mouseX, mouseY, ticks);
	}
	
    @Override
    public void onClose() {
    	this.minecraft.setScreen(parent);
    	
        ConfigurationManager.save();
    }
}