package com.tcn.cosmosportals.client.screen;

import java.io.File;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptionBoolean;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptionBoolean.TYPE;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptionInstance;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptionTitle;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptions;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptionsList;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmosportals.core.management.ConfigurationManagerCommon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;

@OnlyIn(Dist.CLIENT)
public final class ScreenConfigurationCommon extends Screen {

	private final Screen parent;

	private final int TITLE_HEIGHT = 8;

	private final int OPTIONS_LIST_TOP_HEIGHT = 24;
	private final int OPTIONS_LIST_BOTTOM_OFFSET = 32;
	private final int OPTIONS_LIST_ITEM_HEIGHT = 25;
	private final int OPTIONS_LIST_BUTTON_HEIGHT = 20;
	private final int OPTIONS_LIST_WIDTH = 335;

	private final int BIG_WIDTH = 310;
	//private final int SMALL_WIDTH = 150;
	private final int DONE_BUTTON_TOP_OFFSET = 26;
	
	private final ComponentColour DESC = ComponentColour.LIGHT_GRAY;

	private CosmosOptionsList OPTIONS_ROW_LIST;

	private static ConfigScreenHandler.ConfigScreenFactory INSTANCE = new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new ScreenConfigurationCommon(screen));
	
	public static ConfigScreenFactory getInstance() {
		return INSTANCE;
	}

	public ScreenConfigurationCommon(Screen parent) {
		super(ComponentHelper.style(ComponentColour.WHITE, "boldunderline", "cosmosportals.gui.config.name"));

		this.parent = parent;
	}

	@Override
	protected void init() {
		this.OPTIONS_ROW_LIST = new CosmosOptionsList( 
			this.minecraft, this.width, this.height, OPTIONS_LIST_TOP_HEIGHT, this.height - OPTIONS_LIST_BOTTOM_OFFSET, 
			OPTIONS_LIST_ITEM_HEIGHT, OPTIONS_LIST_BUTTON_HEIGHT, OPTIONS_LIST_WIDTH, new CosmosOptions(Minecraft.getInstance(), new File("."))
		);
		
		this.OPTIONS_ROW_LIST.addBig(
			new CosmosOptionTitle(ComponentHelper.style(ComponentColour.LIGHT_GRAY, "boldunderline", "cosmosportals.gui.config.general_title"))
		);
		
		this.OPTIONS_ROW_LIST.addBig(
			CosmosOptionInstance.createIntSlider(ComponentHelper.style(ComponentColour.ORANGE, "cosmosportals.gui.config.size"),
				CosmosOptionInstance.getTooltipSplitComponent(ComponentHelper.style(DESC, "cosmosportals.gui.config.size_info"), ComponentHelper.style(ComponentColour.RED, "cosmosportals.gui.config.size_info_two"), ComponentHelper.style(ComponentColour.LIGHT_RED, "cosmosportals.gui.config.size_info_three")), 
				ConfigurationManagerCommon.getInstance().getPortalMaximumSize(), 2, 9, 5,
				ComponentColour.WHITE, ComponentHelper.style(ComponentColour.GREEN, "Min"), ComponentHelper.style(ComponentColour.DARK_YELLOW, "Blocks"), ComponentHelper.style(ComponentColour.RED, "Max"), (intValue) -> {
				ConfigurationManagerCommon.getInstance().setPortalMaximumSize(intValue);
			})
		);
		
		this.OPTIONS_ROW_LIST.addSmall(			
			new CosmosOptionBoolean(
				ComponentColour.ORANGE, "", "cosmosportals.gui.config.sound.travel", TYPE.YES_NO,
				CosmosOptionInstance.getTooltipSplitComponent(ComponentHelper.style(DESC, "cosmosportals.gui.config.sound.travel_info")),
				ConfigurationManagerCommon.getInstance().getPlayPortalTravelSounds(),
				(newValue) -> ConfigurationManagerCommon.getInstance().setPlayPortalTravelSounds(newValue), ":"
			),
			new CosmosOptionBoolean(
				ComponentColour.ORANGE, "", "cosmosportals.gui.config.sound.ambient", TYPE.YES_NO,
				CosmosOptionInstance.getTooltipSplitComponent(ComponentHelper.style(DESC, "cosmosportals.gui.config.sound.ambient_info")),
				ConfigurationManagerCommon.getInstance().getPlayPortalAmbientSounds(),
				( newValue) -> ConfigurationManagerCommon.getInstance().setPlayPortalAmbientSounds(newValue), ":"
			)
		);
		
		
		this.OPTIONS_ROW_LIST.addBig(
			new CosmosOptionTitle(ComponentHelper.style(ComponentColour.LIGHT_GRAY, "boldunderline", "cosmosportals.gui.config.messages_title"))
		);
		
		
		this.OPTIONS_ROW_LIST.addSmall(
			new CosmosOptionBoolean(
				ComponentColour.CYAN, "", "cosmosportals.gui.config.message.info", TYPE.ON_OFF,
				CosmosOptionInstance.getTooltipSplitComponent(ComponentHelper.style(DESC, "cosmosportals.gui.config.message.info_desc"), ComponentHelper.style(ComponentColour.RED, "bold", "cosmosportals.gui.config.message.restart")),
				ConfigurationManagerCommon.getInstance().getInfoMessage(),
				(newValue) -> ConfigurationManagerCommon.getInstance().setInfoMessage(newValue), ":"
			),
			new CosmosOptionBoolean(
				ComponentColour.CYAN, "", "cosmosportals.gui.config.message.debug", TYPE.ON_OFF,
				CosmosOptionInstance.getTooltipSplitComponent(ComponentHelper.style(DESC, "cosmosportals.gui.config.message.debug_desc"), ComponentHelper.style(ComponentColour.RED, "bold", "cosmosportals.gui.config.message.restart")),
				ConfigurationManagerCommon.getInstance().getDebugMessage(),
				(newValue) -> ConfigurationManagerCommon.getInstance().setDebugMessage(newValue), ":"
			)
		);
		
		
		
		this.OPTIONS_ROW_LIST.addBig(
			new CosmosOptionTitle(ComponentHelper.style(ComponentColour.LIGHT_GRAY, "boldunderline", "cosmosportals.gui.config.visual_title"))
		);
		
		
		this.OPTIONS_ROW_LIST.addSmall(
			new CosmosOptionBoolean(
				ComponentColour.TURQUOISE, "", "cosmosportals.gui.config.frame_textures", TYPE.ON_OFF,
				CosmosOptionInstance.getTooltipSplitComponent(ComponentHelper.style(DESC, "cosmosportals.gui.config.frame_textures_info")),
				ConfigurationManagerCommon.getInstance().getFrameConnectedTextures(),
				(newValue) -> ConfigurationManagerCommon.getInstance().setFrameConnectedTextures(newValue), ":"
			),
			new CosmosOptionBoolean(
				ComponentColour.TURQUOISE, "", "cosmosportals.gui.config.portal_textures", TYPE.ON_OFF, 
				CosmosOptionInstance.getTooltipSplitComponent(ComponentHelper.style(DESC, "cosmosportals.gui.config.portal_textures_info")),
				ConfigurationManagerCommon.getInstance().getPortalConnectedTextures(),
				(newValue) -> ConfigurationManagerCommon.getInstance().setPortalConnectedTextures(newValue), ":"
			)
		);

		this.OPTIONS_ROW_LIST.addSmall(
			new CosmosOptionBoolean(
				ComponentColour.TURQUOISE, "", "cosmosportals.gui.config.labels", TYPE.ON_OFF,
				CosmosOptionInstance.getTooltipSplitComponent(ComponentHelper.style(DESC, "cosmosportals.gui.config.labels_info")),
				ConfigurationManagerCommon.getInstance().getRenderPortalLabels(),
				(newValue) -> ConfigurationManagerCommon.getInstance().setRenderPortalLabels(newValue), ":"
			),
			new CosmosOptionBoolean(
				ComponentColour.TURQUOISE, "", "cosmosportals.gui.config.particles", TYPE.ON_OFF,
				CosmosOptionInstance.getTooltipSplitComponent(ComponentHelper.style(DESC, "cosmosportals.gui.config.particles_info")),
				ConfigurationManagerCommon.getInstance().getRenderPortalParticleEffects(),
				(newValue) -> ConfigurationManagerCommon.getInstance().setRenderPortalParticleEffects(newValue), ":"
			)
		);
		
		this.OPTIONS_ROW_LIST.addBig(
			CosmosOptionInstance.createIntSlider(
				ComponentHelper.style(ComponentColour.TURQUOISE, "cosmosportals.gui.config.label_distance"), 
				CosmosOptionInstance.getTooltipSplitComponent(ComponentHelper.style(DESC, "cosmosportals.gui.config.label_distance_info")), 
				ConfigurationManagerCommon.getInstance().getLabelMaximumDistance(), 8, 64, 32,	
				ComponentColour.WHITE, ComponentHelper.style(ComponentColour.GREEN, "Min"), ComponentHelper.style(ComponentColour.LIGHT_BLUE, "Blocks"), ComponentHelper.style(ComponentColour.RED, "Max"), (intValue) -> {
				ConfigurationManagerCommon.getInstance().setLabelMaximumDistance(intValue);
			})
		);
		
		this.addWidget(this.OPTIONS_ROW_LIST);
		
		this.addRenderableWidget(
			Button.builder(ComponentHelper.style(ComponentColour.GREEN, "bold", "cosmosportals.gui.done"), (button) -> { this.onClose(); })
			.pos((this.width - BIG_WIDTH) /2, this.height - DONE_BUTTON_TOP_OFFSET)
			.size(BIG_WIDTH, OPTIONS_LIST_BUTTON_HEIGHT)
			.build()
		);
		/*
		this.addRenderableWidget(new Button(
			(this.width - BIG_WIDTH) /2, this.height - DONE_BUTTON_TOP_OFFSET, BIG_WIDTH, OPTIONS_LIST_BUTTON_HEIGHT,
			ComponentHelper.style(ComponentColour.GREEN, "bold", "cosmosportals.gui.done"), (button) -> { this.onClose(); }
		));*/
	}

	public void updateWidgets() {
		double scroll = this.OPTIONS_ROW_LIST.getScrollAmount();
		
		this.clearWidgets();
		
		this.init();
		
		this.OPTIONS_ROW_LIST.setScrollAmount(scroll);
	}
	
	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float ticks) {
		this.renderBackground(graphics);
		this.OPTIONS_ROW_LIST.render(graphics, mouseX, mouseY, ticks);
		graphics.drawCenteredString(this.font, this.title, width / 2, TITLE_HEIGHT, 0xFFFFFF);

		super.render(graphics, mouseX, mouseY, ticks);
		//List<FormattedCharSequence> list = tooltipAt(this.OPTIONS_ROW_LIST, mouseX, mouseY);
		//graphics.renderTooltip(this.font, list, mouseX, mouseY);
	}
	
	@SuppressWarnings("unchecked")
	public static List<FormattedCharSequence> tooltipAt(CosmosOptionsList listIn, int mouseX, int mouseY) {
		Optional<AbstractWidget> optional = listIn.getMouseOver((double)  mouseX, (double) mouseY);
		return (List<FormattedCharSequence>) (optional.isPresent() && optional.get() instanceof AbstractWidget ? ((AbstractWidget) optional.get()).getTooltip() : ImmutableList.of());
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
	public boolean mouseDragged(double mouseX, double mouseY, int p_94701_, double p_94702_, double p_94703_) {
		boolean dragged = super.mouseDragged(mouseX, mouseY, p_94701_, p_94702_, p_94703_);
		
		if (this.getChildAt(mouseX, mouseY).isPresent()) {
			for (GuiEventListener listener : this.OPTIONS_ROW_LIST.children()) {
				if (listener.isMouseOver(mouseX, mouseY)) {
					this.updateWidgets();
				}
			}
		}
		
		return dragged;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int ticks) {
		boolean clicked = super.mouseClicked(mouseX, mouseY, ticks);
		
		if (this.getChildAt(mouseX, mouseY).isPresent()) {
			for (GuiEventListener listener : this.OPTIONS_ROW_LIST.children()) {
				if (listener.isMouseOver(mouseX, mouseY)) {
					this.updateWidgets();
				}
			}
		}
		
		return clicked;
	}
	
	@Override
	public void onClose() {
		this.minecraft.setScreen(parent);
		super.onClose();
		ConfigurationManagerCommon.save();
	}
}