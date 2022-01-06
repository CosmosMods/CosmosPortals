package com.tcn.cosmosportals.client.screen;

import java.util.Arrays;
import java.util.UUID;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem.FONT;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonUIMode;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper.Value;
import com.tcn.cosmosportals.CosmosPortalsReference;
import com.tcn.cosmosportals.client.screen.button.GuideButton;
import com.tcn.cosmosportals.client.screen.button.GuideChangeButton;
import com.tcn.cosmosportals.core.item.ItemPortalGuide;
import com.tcn.cosmosportals.core.management.ModBusManager;
import com.tcn.cosmosportals.core.management.NetworkManager;
import com.tcn.cosmosportals.core.network.PacketGuideUpdate;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenItemGuide extends Screen {
	private ResourceLocation FLAT_TEXTURES = CosmosPortalsReference.GUIDE_FLAT_TEXTURES;
	private ResourceLocation BLOCK_TEXTURES = CosmosPortalsReference.GUIDE_BLOCK_TEXTURES;
	
	private ItemStack stack;
	
	private int flipTimer = 0;
	private int flipTimerMulti = 0;
	
	private int currPage;
	private int pageCount = 17;

	protected CosmosButtonUIMode uiModeButton;
	private UUID playerUUID;
	
	private GuideChangeButton buttonNextPage;
	private GuideChangeButton buttonPreviousPage;
	private GuideButton buttonExit;
	private GuideButton buttonHome;
	
	private GuideButton tabIntroduction;
	private GuideButton tabPortals;
	private GuideButton tabItems;
	private GuideButton tabConfiguration;
	private GuideButton tabRecipes;
	private GuideButton tabCredits;
	
	private GuideButton tabPlaceholder;
	private GuideButton tabPlaceholder2;
	
	private final boolean pageTurnSounds;

	public ScreenItemGuide(boolean pageTurnSoundsIn, UUID playerUUIDIn, ItemStack stackIn) {
		super(ComponentHelper.locComp("cosmosportals.guide.heading"));
		
		this.pageTurnSounds = pageTurnSoundsIn;
		this.stack = stackIn;
		this.playerUUID = playerUUIDIn;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
	
	@Override
	protected void init() {
		this.drawRenderableWidgets();
		super.init();
		
		this.currPage = ItemPortalGuide.getPage(this.stack);
	}
	
	public void renderBackground(PoseStack poseStack) {
		super.renderBackground(poseStack);
		int[] screen_coords = CosmosUISystem.getScreenCoords(this, 202, 225);
		
		if (this.stack != null) {
			CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, screen_coords, 0, 0, 0, 0, 202, 255, this.getUIMode(), CosmosPortalsReference.GUIDE);
		} else {
			CosmosUISystem.renderStaticElementWithUIMode(this, poseStack, screen_coords, 0, 0, 0, 0, 202, 255, EnumUIMode.DARK, CosmosPortalsReference.GUIDE);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTicks);
		
		int[] screen_coords = CosmosUISystem.getScreenCoords(this, 202, 225);
		
		if (this.flipTimer < 2000) {
			this.flipTimer++;
		} else {
			this.flipTimer = 0;
		}

		if (this.flipTimerMulti < 8000) {
			this.flipTimerMulti++;
		} else {
			this.flipTimerMulti = 0;
		}

		this.drawRenderableWidgets();

		FONT.drawString(poseStack, font, screen_coords, 23, 10, true, ComponentHelper.locComp(ComponentColour.BLACK, false, "cosmosportals.guide.page", Integer.toString(this.currPage)));
		FONT.drawString(poseStack, font, screen_coords, 76, 10, true, ComponentHelper.locComp(ComponentColour.BLACK, false, true, "cosmosportals.guide.heading"));
		
		if (this.currPage == 0) {
			FONT.drawWrappedStringBR(poseStack, font, screen_coords, 104, -4, 0, ComponentColour.BLACK.dec(), ComponentHelper.locString("cosmosportals.guide.one_body")
					+ ComponentHelper.locString(Value.GRAY + Value.UNDERLINE, "cosmosportals.guide.one_body_one") + ComponentHelper.locString("cosmosportals.guide.one_body_two"));
			
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 75, ComponentColour.BLACK.dec(), ComponentHelper.locString(Value.UNDERLINE, "cosmosportals.guide.one_body_heading"), false);
			
			FONT.drawString(poseStack, font, screen_coords, 30, 120, true, ComponentHelper.locComp(ComponentColour.POCKET_PURPLE_LIGHT, false, "cosmosportals.guide.two_heading"));
			FONT.drawString(poseStack, font, screen_coords, 173, 120, true, ComponentHelper.locComp(ComponentColour.POCKET_PURPLE_LIGHT, false, "1"));
			
			FONT.drawString(poseStack, font, screen_coords, 30, 130, true, ComponentHelper.locComp(ComponentColour.CYAN, false, "cosmosportals.guide.three_heading"));
			FONT.drawString(poseStack, font, screen_coords, 161, 130, true, ComponentHelper.locComp(ComponentColour.CYAN, false, "2-5"));
			
			FONT.drawString(poseStack, font, screen_coords, 30, 140, true, ComponentHelper.locComp(ComponentColour.LIGHT_BLUE, false, "cosmosportals.guide.four_heading"));
			FONT.drawString(poseStack, font, screen_coords, 161, 140, true, ComponentHelper.locComp(ComponentColour.LIGHT_BLUE, false, "6-7"));
			
			FONT.drawString(poseStack, font, screen_coords, 30, 150, true, ComponentHelper.locComp(ComponentColour.GREEN, false, "cosmosportals.guide.five_heading"));
			FONT.drawString(poseStack, font, screen_coords, 155, 150, true, ComponentHelper.locComp(ComponentColour.GREEN, false, "8-12"));
			
			FONT.drawString(poseStack, font, screen_coords, 30, 160, true, ComponentHelper.locComp(ComponentColour.DARK_GREEN, false, "cosmosportals.guide.six_heading"));
			FONT.drawString(poseStack, font, screen_coords, 149, 160, true, ComponentHelper.locComp(ComponentColour.DARK_GREEN, false, "13-15"));
			
			FONT.drawString(poseStack, font, screen_coords, 30, 170, true, ComponentHelper.locComp(ComponentColour.RED, false, "cosmosportals.guide.seven_tab"));
			FONT.drawString(poseStack, font, screen_coords, 167, 170, true, ComponentHelper.locComp(ComponentColour.RED, false, "16"));
			
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 174, ComponentColour.GRAY.dec(), ComponentHelper.locString("[ ", "cosmosportals.guide.foot_one", " ]"), false);
		} 
		
		else if (this.currPage == 1) {
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, -8, ComponentColour.POCKET_PURPLE_LIGHT.dec(), ComponentHelper.locString(Value.UNDERLINE, "cosmosportals.guide.two_heading"), false);
			
			FONT.drawWrappedStringBR(poseStack, font, screen_coords, 104, 4, 0, ComponentColour.BLACK.dec(), ComponentHelper.locString("cosmosportals.guide.two_body_one"));
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 65, ComponentColour.POCKET_PURPLE_LIGHT.dec(), ComponentHelper.locString(Value.UNDERLINE, "cosmosportals.guide.two_body_two"), false);
			
			FONT.drawString(poseStack, font, screen_coords, 25, 110, true, ComponentHelper.locComp(ComponentColour.CYAN, false, "cosmosportals.guide.two_sub_one"));
			FONT.drawString(poseStack, font, screen_coords, 25, 120, true, ComponentHelper.locComp(ComponentColour.LIGHT_BLUE, false, "cosmosportals.guide.two_sub_two"));
			FONT.drawString(poseStack, font, screen_coords, 25, 130, true, ComponentHelper.locComp(ComponentColour.GREEN, false, "cosmosportals.guide.two_sub_three"));
			FONT.drawString(poseStack, font, screen_coords, 25, 140, true, ComponentHelper.locComp(ComponentColour.DARK_GREEN, false, "cosmosportals.guide.two_sub_four"));
			
			FONT.drawWrappedStringBR(poseStack, font, screen_coords, 104, 140, 0, ComponentColour.BLACK.dec(), ComponentHelper.locString("cosmosportals.guide.two_body_three"));
			
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 174, ComponentColour.GRAY.dec(), ComponentHelper.locString("[ ", "cosmosportals.guide.foot_two", " ]"), false);
		}
		
		else if (this.currPage == 2) {
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, -8, ComponentColour.CYAN.dec(), ComponentHelper.locString(Value.UNDERLINE, "cosmosportals.guide.three_heading"), false);
			
			CosmosUISystem.setTextureWithColour(poseStack, BLOCK_TEXTURES, new float[] { 1.0F, 1.0F, 1.0F, 1.0F });
			CosmosUISystem.renderStaticElement(this, poseStack, screen_coords, 75, 40, 0, 0, 60, 60);
			
			FONT.drawWrappedStringBR(poseStack, font, screen_coords, 104, 70, 0, ComponentColour.BLACK.dec(), ComponentHelper.locString("cosmosportals.guide.three_body_one"));
			
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 174, ComponentColour.GRAY.dec(), ComponentHelper.locString("[ ", "cosmosportals.guide.foot_three", " 1 ]"), false);
		} 
		
		else if (this.currPage == 3) {
			FONT.drawWrappedStringBR(poseStack, font, screen_coords, 104, -4, 0, ComponentColour.BLACK.dec(), ComponentHelper.locString("cosmosportals.guide.three_body_one_"));
			
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 174, ComponentColour.GRAY.dec(), ComponentHelper.locString("[ ", "cosmosportals.guide.foot_three", " 2 ]"), false);
		} 
		
		else if (this.currPage == 4) {
			
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 174, ComponentColour.GRAY.dec(), ComponentHelper.locString("[ ", "cosmosportals.guide.foot_three", " 3 ]"), false);
		} 
		
		else if (this.currPage == 5) {
			
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 174, ComponentColour.GRAY.dec(), ComponentHelper.locString("[ ", "cosmosportals.guide.foot_three", " 4 ]"), false);
		} 
		
		//Modules
		else if (this.currPage == 6) {
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, -8, ComponentColour.LIGHT_BLUE.dec(), ComponentHelper.locString(Value.UNDERLINE, "cosmosportals.guide.four_heading"), false);

			FONT.drawWrappedStringBR(poseStack, font, screen_coords, 104, 4, 0, ComponentColour.BLACK.dec(), ComponentHelper.locString("cosmosportals.guide.four_body"));
			
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 174, ComponentColour.GRAY.dec(), ComponentHelper.locString("[ ", "cosmosportals.guide.foot_four", " 1 ]"), false);
		} 
		
		else if (this.currPage == 7) {
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, -8, ComponentColour.LIGHT_BLUE.dec(), ComponentHelper.locString(Value.UNDERLINE, "cosmosportals.guide.four_heading_one"), false);
			CosmosUISystem.setTextureWithColour(poseStack, FLAT_TEXTURES, new float[] { 1.0F, 1.0F, 1.0F, 1.0F });
			
			CosmosUISystem.renderStaticElement(this, poseStack, screen_coords, (202) / 2 - 16, 40, 192, 224, 32, 32);
			
			FONT.drawWrappedStringBR(poseStack, font, screen_coords, 104, 50, 0, ComponentColour.BLACK.dec(), ComponentHelper.locString("cosmosportals.guide.four_body"));
			
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 174, ComponentColour.GRAY.dec(), ComponentHelper.locString("[ ", "cosmosportals.guide.foot_four", " 1 ]"), false);
		} 

		else if (this.currPage == 8) {
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, -8, ComponentColour.GREEN.dec(), ComponentHelper.locString(Value.UNDERLINE, "cosmosportals.guide.five_heading"), false);
			
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 174, ComponentColour.GRAY.dec(), ComponentHelper.locString("[ ", "cosmosportals.guide.foot_five", " 0 ]"), false);
		}
		
		else if (this.currPage == 9) {
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, -8, ComponentColour.GREEN.dec(), ComponentHelper.locString(Value.UNDERLINE, "cosmosportals.guide.five_heading_one"), false);
			
			CosmosUISystem.setTextureWithColour(poseStack, FLAT_TEXTURES, new float[] { 1.0F, 1.0F, 1.0F, 1.0F });
			CosmosUISystem.renderStaticElement(this, poseStack, screen_coords, 30,  40, 0,   128, 20, 20);
			CosmosUISystem.renderStaticElement(this, poseStack, screen_coords, 55,  40, 20,  128, 20, 20);
			CosmosUISystem.renderStaticElement(this, poseStack, screen_coords, 80,  40, 40,  128, 20, 20);
			CosmosUISystem.renderStaticElement(this, poseStack, screen_coords, 105, 40, 60,  128, 20, 20);
			CosmosUISystem.renderStaticElement(this, poseStack, screen_coords, 130, 40, 80,  128, 20, 20);
			CosmosUISystem.renderStaticElement(this, poseStack, screen_coords, 155, 40, 100, 128, 20, 20);
			
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 174, ComponentColour.GRAY.dec(), ComponentHelper.locString("[ ", "cosmosportals.guide.foot_five", " 1 ]"), false);
		} 

		else if (this.currPage == 10) {
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, -8, ComponentColour.GREEN.dec(), ComponentHelper.locString(Value.UNDERLINE, "cosmosportals.guide.five_heading_two"), false);

			//CosmosUISystem.setTexture(poseStack, FLAT_TEXTURES, new float[] { 1.0F, 1.0F, 1.0F, 1.0F });
			
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 174, ComponentColour.GRAY.dec(), ComponentHelper.locString("[ ", "cosmosportals.guide.foot_five", " 2 ]"), false);
		} 
		
		else if (this.currPage == 11) {
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, -8, ComponentColour.GREEN.dec(), ComponentHelper.locString(Value.UNDERLINE, "cosmosportals.guide.five_heading_three"), false);

			//CosmosUISystem.setTexture(poseStack, FLAT_TEXTURES, new float[] { 1.0F, 1.0F, 1.0F, 1.0F });
			
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 174, ComponentColour.GRAY.dec(), ComponentHelper.locString("[ ", "cosmosportals.guide.foot_five", " 3 ]"), false);
		} 

		else if (this.currPage == 12) {
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, -8, ComponentColour.GREEN.dec(), ComponentHelper.locString(Value.UNDERLINE, "cosmosportals.guide.five_heading_four"), false);
			
			//CosmosUISystem.setTexture(poseStack, FLAT_TEXTURES, new float[] { 1.0F, 1.0F, 1.0F, 1.0F });
			
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 174, ComponentColour.GRAY.dec(), ComponentHelper.locString("[ ", "cosmosportals.guide.foot_five", " 4 ]"), false);
		} 
		
		else if (this.currPage == 13) {
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, -10, ComponentColour.DARK_GREEN.dec(),ComponentHelper.locString(Value.UNDERLINE, "cosmosportals.guide.six_heading"), false);
			
			CosmosUISystem.setTextureWithColour(poseStack, FLAT_TEXTURES, new float[] { 1.0F, 1.0F, 1.0F, 1.0F });
			CosmosUISystem.renderStaticElement(this, poseStack, screen_coords, (202) / 2 - 16, 40, 192, 96, 32, 32);
			
			FONT.drawWrappedStringBR(poseStack, font, screen_coords, 104, 45, 0, ComponentColour.BLACK.dec(), ComponentHelper.locString("cosmosportals.guide.six_body"));
			
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 174, ComponentColour.GRAY.dec(), ComponentHelper.locString("[ ", "cosmosportals.guide.foot_six", " Intro ]"), false);
		}
		
		else if (this.currPage >= 14 && this.currPage <= 15) {
			CosmosUISystem.setTextureWithColourAlpha(poseStack, FLAT_TEXTURES, new float[] { 1.0F, 1.0F, 1.0F, 1.0F });
			CosmosUISystem.renderStaticElement(this, poseStack, screen_coords, 202 / 2 - 30, 225 / 2 - 30, 128, 0, 64, 64);

			CosmosUISystem.setTextureWithColour(poseStack, this.getTexture(), new float[] { 1.0F, 1.0F, 1.0F, 1.0F });
			CosmosUISystem.renderStaticElement(this, poseStack, screen_coords, 30, 35, 202, 0, 54, 74);
			CosmosUISystem.renderStaticElement(this, poseStack, screen_coords, 30, 125, 202, 0, 54, 74);
			CosmosUISystem.renderStaticElement(this, poseStack, screen_coords, 123, 35, 202, 0, 54, 74);
			CosmosUISystem.renderStaticElement(this, poseStack, screen_coords, 123, 125, 202, 0, 54, 74);

			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 174, ComponentColour.GRAY.dec(), ComponentHelper.locString("[ ", "cosmosportals.guide.foot_six", " " + (this.currPage - 13) + " ]"), false);
			
			if (this.currPage == 14) {
				FONT.drawCenteredString(poseStack, font, screen_coords, 104, -10, ComponentColour.DARK_GREEN.dec(),ComponentHelper.locString(Value.UNDERLINE, "cosmosportals.guide.six_heading_one"), false);
				
				font.draw(poseStack, Value.BOLD + "8", screen_coords[0] + 67, screen_coords[1] + 102, ComponentColour.BLACK.dec());
				font.draw(poseStack, Value.BOLD + "2", screen_coords[0] + 160, screen_coords[1] + 102, ComponentColour.BLACK.dec());
				font.draw(poseStack, Value.BOLD + "2", screen_coords[0] + 67, screen_coords[1] + 192, ComponentColour.BLACK.dec());
			}
			
			if (this.currPage == 15) {
				font.draw(poseStack, Value.BOLD + "2", screen_coords[0] + 160, screen_coords[1] + 102, ComponentColour.BLACK.dec());
			}
		}
		
		else if (this.currPage == 16) {
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 20, ComponentColour.RED.dec(), ComponentHelper.locString(Value.UNDERLINE, "cosmosportals.guide.seven_heading"), false);
			
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 40, ComponentColour.POCKET_PURPLE_LIGHT.dec(), "TheCosmicNebula", false);
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 50, ComponentColour.POCKET_PURPLE_LIGHT.dec(), "(Lead Programmer)", false);
			
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 70, ComponentColour.BLUE.dec(), "XCompWiz + Team", false);
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 80, ComponentColour.BLUE.dec(), "(Original Mod, Concept)", false);
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 90, ComponentColour.BLUE.dec(), "(Mystcraft Portals)", false);
			
			/*
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 40, ComponentColour.DARK_GREEN.dec(), "Apolybrium", false);
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 50, ComponentColour.DARK_GREEN.dec(), "(Texture Artist, Creative Input)", false);
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 60, ComponentColour.DARK_GREEN.dec(), "(Sound Design)", false);
			
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 80, ComponentColour.BLUE.dec(), "Scarlet Spark", false);
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 90, ComponentColour.BLUE.dec(), "(Lead Beta Tester)", false);
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 100, ComponentColour.BLUE.dec(), "(Creative Lead, Ideas)", false);
			
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 120, ComponentColour.PURPLE.dec(), "Rechalow", false);
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 130, ComponentColour.PURPLE.dec(), "(Chinese Translation)", false);
			*/
			
			FONT.drawCenteredString(poseStack, font, screen_coords, 104, 174, ComponentColour.GRAY.dec(), ComponentHelper.locString("[ ", "cosmosportals.guide.foot_seven", " 1 ]"), false);
		}
		
		//CosmosUISystem.setTexture(poseStack, TEXTURE, new float[] { 1.0F, 1.0F, 1.0F, 1.0F });
		this.renderComponentHoverEffect(poseStack, Style.EMPTY, mouseX, mouseY);
	}
	
	@Override
	public void renderComponentHoverEffect(PoseStack poseStack, Style style, int mouseX, int mouseY) {
		int[] screen_coords = CosmosUISystem.getScreenCoords(this, 202, 225);
		
		if (this.buttonExit.isMouseOver(mouseX, mouseY)) {
			this.renderTooltip(poseStack, ComponentHelper.locComp(ComponentColour.RED, false, "cosmosportals.guide.button_one"), mouseX, mouseY);
		} else if (this.buttonHome.isMouseOver(mouseX, mouseY)) {
			this.renderTooltip(poseStack, ComponentHelper.locComp(ComponentColour.GREEN, false, "cosmosportals.guide.button_two"), mouseX, mouseY);
		} 
		
		else if (this.buttonNextPage.isMouseOver(mouseX, mouseY)) {
			this.renderTooltip(poseStack, ComponentHelper.locComp(ComponentColour.LIGHT_GRAY, false, "cosmosportals.guide.button_three"), mouseX, mouseY);
		} else if (this.buttonPreviousPage.isMouseOver(mouseX, mouseY)) {
			this.renderTooltip(poseStack, ComponentHelper.locComp(ComponentColour.LIGHT_GRAY, false, "cosmosportals.guide.button_four"), mouseX, mouseY);
		} 
		
		else if (this.tabIntroduction.isMouseOver(mouseX, mouseY)) {
			this.renderTooltip(poseStack, ComponentHelper.locComp(ComponentColour.GRAY, true, "cosmosportals.guide.two_heading"), mouseX, mouseY);
		} else if (this.tabPortals.isMouseOver(mouseX, mouseY)) {
			this.renderTooltip(poseStack, ComponentHelper.locComp(ComponentColour.CYAN, true, "cosmosportals.guide.three_heading"), mouseX, mouseY);
		} else if (this.tabItems.isMouseOver(mouseX, mouseY)) {
			this.renderTooltip(poseStack, ComponentHelper.locComp(ComponentColour.LIGHT_BLUE, true, "cosmosportals.guide.four_heading"), mouseX, mouseY);
		} else if (this.tabConfiguration.isMouseOver(mouseX, mouseY)) {
			this.renderTooltip(poseStack, ComponentHelper.locComp(ComponentColour.GREEN, true, "cosmosportals.guide.five_heading"), mouseX, mouseY);
		}  else if (this.tabRecipes.isMouseOver(mouseX, mouseY)) {
			this.renderTooltip(poseStack, ComponentHelper.locComp(ComponentColour.DARK_GREEN, true, "cosmosportals.guide.six_heading"), mouseX, mouseY);
		} else if (this.tabCredits.isMouseOver(mouseX, mouseY)) {
			this.renderTooltip(poseStack, ComponentHelper.locComp(ComponentColour.RED, true, "cosmosportals.guide.seven_tab"), mouseX, mouseY);
		}
		
		/*
		else if (this.tabPlaceholder.isMouseOver(mouseX, mouseY)) {
			this.renderTooltip(poseStack, ComponentHelper.locComp(ComponentColour.BLUE, true, "cosmosportals.guide.four_heading"), mouseX, mouseY);
		} else if (this.tabPlaceholder2.isMouseOver(mouseX, mouseY)) {
			this.renderTooltip(poseStack, ComponentHelper.locComp(ComponentColour.GRAY, true, "cosmosportals.guide.seven_heading"), mouseX, mouseY);
		}
		*/
		
		else if (this.uiModeButton.isMouseOver(mouseX, mouseY)) {
			Component[] comp = new Component[] { 
				ComponentHelper.locComp(ComponentColour.WHITE, false, "cosmoslibrary.gui.ui_mode.info"),
				ComponentHelper.locComp(ComponentColour.GRAY, false, "cosmoslibrary.gui.ui_mode.value").append(this.getUIMode().getColouredComp())
			};
				
				this.renderComponentTooltip(poseStack, Arrays.asList(comp), mouseX, mouseY);
		}
		
		if (this.currPage == 14) {
			this.renderCraftingGrid(poseStack, screen_coords, mouseX, mouseY, new int[] { 1, 3, 1, 3, 5, 3, 1, 3, 1, 6 }, 0);
			this.renderCraftingGrid(poseStack, screen_coords, mouseX, mouseY, new int[] { -1, 5, -1, 5, 4, 5, -1, 5, -1, 8 }, 1);
			
			this.renderCraftingGrid(poseStack, screen_coords, mouseX, mouseY, new int[] { -1, 5, -1, 5, 2, 5, -1, 5, -1, 7 }, 2);
			this.renderCraftingGrid(poseStack, screen_coords, mouseX, mouseY, new int[] { 6, 7, 6, 7, 8, 7, 6, 7, 6, 9 }, 3);
		}
		
		else if (this.currPage == 15) {
			this.renderCraftingGrid(poseStack, screen_coords, mouseX, mouseY, new int[] { 6, 6, 6, 6, 6, 6, 6, 6, 6, 10 }, 0);
			this.renderCraftingGrid(poseStack, screen_coords, mouseX, mouseY, new int[] { 6, 5, 6, 5, 10, 5, 6, 5, 6, 11 }, 1);

			this.renderCraftingGrid(poseStack, screen_coords, mouseX, mouseY, new int[] { 11, 7, 11, 7, 8, 7, 11, 7, 11, 12 }, 2);
			//this.renderCraftingGrid(poseStack, screen_coords, mouseX, mouseY, new int[] { 6, 7, 6, 7, 8, 7, 6, 7, 6, 9 }, 3);
		} 

		
		super.renderComponentHoverEffect(poseStack, style, mouseX, mouseY);
	}
	
	protected void drawRenderableWidgets() {
		this.clearWidgets();
		
		this.uiModeButton = this.addRenderableWidget(new CosmosButtonUIMode(this.getUIMode(), this.width / 2 + 71, this.height / 2 - 90, true, true, ComponentHelper.locComp(""), (button) -> { this.changeUIMode(); } ));
		
		this.buttonNextPage = this.addRenderableWidget(new GuideChangeButton(this.width / 2 + 58, this.height / 2 + 92, true, (p_214159_1_) -> { this.nextPage(); }, this.pageTurnSounds, this.getTexture()));
		this.buttonPreviousPage = this.addRenderableWidget(new GuideChangeButton(this.width / 2 - 79, this.height / 2 + 92, false, (p_214158_1_) -> { this.previousPage(); }, this.pageTurnSounds, this.getTexture()));
		
		this.buttonExit = this.addRenderableWidget(new GuideButton(this.width / 2 + 70, this.height / 2 - 105, 13, 0, ComponentColour.WHITE.dec(), this.getTexture(), (button) -> { this.onClose(); }));
		this.buttonHome = this.addRenderableWidget(new GuideButton(this.width / 2 + 54, this.height / 2 - 105, 13, 1, ComponentColour.WHITE.dec(), this.getTexture(), (button) -> { this.showPage(0); }));
		
		this.tabIntroduction = this.addRenderableWidget(new GuideButton(this.width / 2 + 85, this.height / 2 - 106, ComponentColour.GRAY.dec(), this.getTexture(), (button) -> { this.showPage(1); }));
		this.tabPortals = this.addRenderableWidget(new GuideButton(this.width / 2 + 85, this.height / 2 - 80, ComponentColour.CYAN.dec(), this.getTexture(), (button) -> { this.showPage(2); }));
		this.tabItems = this.addRenderableWidget(new GuideButton(this.width / 2 + 85, this.height / 2 - 54, ComponentColour.LIGHT_BLUE.dec(), this.getTexture(), (button) -> { this.showPage(6); }));
		this.tabConfiguration = this.addRenderableWidget(new GuideButton(this.width / 2 + 85, this.height / 2 - 28, ComponentColour.LIME.dec(), this.getTexture(), (button) -> { this.showPage(8); }));
		this.tabRecipes = this.addRenderableWidget(new GuideButton(this.width / 2 + 85, this.height / 2 - 2, ComponentColour.DARK_GREEN.dec(), this.getTexture(), (button) -> { this.showPage(13); }));
		this.tabCredits = this.addRenderableWidget(new GuideButton(this.width / 2 + 85, this.height / 2 + 24, ComponentColour.RED.dec(), this.getTexture(), (button) -> { this.showPage(16); }));

		
		this.tabPlaceholder = this.addRenderableWidget(new GuideButton(this.width / 2 + 85, this.height / 2 + 50, ComponentColour.BLACK.dec(), this.getTexture(), (button) -> { }));
		this.tabPlaceholder2 = this.addRenderableWidget(new GuideButton(this.width / 2 + 85, this.height / 2 + 76, ComponentColour.BLACK.dec(), this.getTexture(), (button) -> { }));
		
		this.tabPlaceholder.active = false;
		this.tabPlaceholder2.active = false;
			
		this.updateButtons();
	}

	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else {
			switch (keyCode) {
			case 266:
				this.buttonPreviousPage.onPress();
				return true;
			case 267:
				this.buttonNextPage.onPress();
				return true;
			default:
				return false;
			}
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean handleComponentClicked(Style style) {
		ClickEvent clickevent = style.getClickEvent();
		if (clickevent == null) {
			return false;
		} else if (clickevent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
			String s = clickevent.getValue();

			try {
				int i = Integer.parseInt(s) - 1;
				return this.showPage(i);
			} catch (Exception exception) {
				return false;
			}
		} else {
			boolean flag = super.handleComponentClicked(style);
			if (flag && clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
				this.minecraft.setScreen((Screen) null);
			}

			return flag;
		}
	}

	public ResourceLocation getTexture() {
		EnumUIMode mode = this.getUIMode();
		
		if (mode.equals(EnumUIMode.DARK)) {
			return CosmosPortalsReference.GUIDE[1];
		} else {
			return CosmosPortalsReference.GUIDE[0];
		}
	}
	
	public EnumUIMode getUIMode() {
		if (this.stack != null) {
			return ItemPortalGuide.getUIMode(this.stack);
		}
		
		return EnumUIMode.DARK;
	}

	private void changeUIMode() {
		NetworkManager.sendToServer(new PacketGuideUpdate(this.playerUUID, this.currPage, this.getUIMode().getNextState()));
		ItemPortalGuide.setUIMode(this.stack, this.getUIMode().getNextState());
	}

	private void updateButtons() {
		this.buttonNextPage.visible = this.currPage < this.pageCount - 1;
		this.buttonPreviousPage.visible = this.currPage > 0;
	}

	public boolean showPage(int pageNum) {
		int i = Mth.clamp(pageNum, 0, this.pageCount - 1);
		if (i != this.currPage) {
			this.currPage = i;
			this.updateButtons();
			return true;
		} else {
			return false;
		}
		
	}
	
	@Override
	public void onClose() {
		NetworkManager.sendToServer(new PacketGuideUpdate(this.playerUUID, this.currPage, null));
		super.onClose();
	}

	protected void previousPage() {
		if (this.currPage > 0) {
			--this.currPage;
		}
		this.updateButtons();
	}
	
	protected void nextPage() {
		if (this.currPage < this.pageCount - 1) {
			++this.currPage;
		}
		this.updateButtons();
	}
	
	@SuppressWarnings("unused")
	private boolean shouldDrawRecipe() {
		return getRecipeType() >= 0;
	}

	private int getRecipeType() {
		int type = -1;

		if (currPage == 0) {
			type = 0;
		} else if (currPage == 5) {
			type = 1;
		} else if (currPage == 6) {
			type = 2;
		}

		return type;
	}
	
	public void renderCraftingGrid(PoseStack poseStack, int[] screen_coords, int mouseX, int mouseY, int[] ref, int grid_ref) {
		int[] LX = new int[] { 31, 49, 67 }; //left to right [L]
		int[] RX = new int[] { 124, 142, 160 }; //left to right [R]
		int[] TY = new int[] { 36, 54, 72, 92 }; //top to bottom [T]
		int[] BY = new int[] { 126, 144, 162, 182 }; // top to bottom [B]
		
		int[] SLX = new int[] { 31, 67, 49,  124, 160, 142 };
		
		int[] STY = new int[] { 36, 56, 78, 98,  120, 140, 162, 182 };
		
		final ItemStack[] items = new ItemStack[] {
			ItemStack.EMPTY, // 0
			
			new ItemStack(Items.IRON_INGOT), //  1
			new ItemStack(Items.DIAMOND), //  2
			new ItemStack(Items.COPPER_INGOT), //  3
			new ItemStack(Items.ENDER_PEARL), //  4

			new ItemStack(ModBusManager.COSMIC_MATERIAL), // 5
			new ItemStack(ModBusManager.COSMIC_INGOT), // 6
			new ItemStack(ModBusManager.COSMIC_GEM), // 7
			new ItemStack(ModBusManager.COSMIC_PEARL), // 8
			
			new ItemStack(ModBusManager.DIMENSION_CONTAINER), // 9

			new ItemStack(ModBusManager.COSMIC_BLOCK), // 10
			new ItemStack(ModBusManager.PORTAL_FRAME), // 11
			new ItemStack(ModBusManager.PORTAL_DOCK), // 12
		};
		
		if (grid_ref == 0) {
			//Top Left
			
			if (ref[0] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[0]], screen_coords, LX[0], TY[0], mouseX, mouseY, true); }// 0
			if (ref[1] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[1]], screen_coords, LX[1], TY[0], mouseX, mouseY, true); }// 1
			if (ref[2] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[2]], screen_coords, LX[2], TY[0], mouseX, mouseY, true); }// 2
			if (ref[3] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[3]], screen_coords, LX[0], TY[1], mouseX, mouseY, true); }// 3
			if (ref[4] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[4]], screen_coords, LX[1], TY[1], mouseX, mouseY, true); }// 4
			if (ref[5] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[5]], screen_coords, LX[2], TY[1], mouseX, mouseY, true); }// 5
			if (ref[6] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[6]], screen_coords, LX[0], TY[2], mouseX, mouseY, true); }// 6
			if (ref[7] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[7]], screen_coords, LX[1], TY[2], mouseX, mouseY, true); }// 7
			if (ref[8] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[8]], screen_coords, LX[2], TY[2], mouseX, mouseY, true); }// 8
			if (ref[9] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[9]], screen_coords, LX[1], TY[3], mouseX, mouseY, true); }// Out
		} else if (grid_ref == 2) {
			
			//Bottom Left
			if (ref[0] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[0]], screen_coords, LX[0], BY[0], mouseX, mouseY, true); }// 0
			if (ref[1] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[1]], screen_coords, LX[1], BY[0], mouseX, mouseY, true); }// 1
			if (ref[2] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[2]], screen_coords, LX[2], BY[0], mouseX, mouseY, true); }// 2
			if (ref[3] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[3]], screen_coords, LX[0], BY[1], mouseX, mouseY, true); }// 3
			if (ref[4] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[4]], screen_coords, LX[1], BY[1], mouseX, mouseY, true); }// 4
			if (ref[5] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[5]], screen_coords, LX[2], BY[1], mouseX, mouseY, true); }// 5
			if (ref[6] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[6]], screen_coords, LX[0], BY[2], mouseX, mouseY, true); }// 6
			if (ref[7] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[7]], screen_coords, LX[1], BY[2], mouseX, mouseY, true); }// 7
			if (ref[8] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[8]], screen_coords, LX[2], BY[2], mouseX, mouseY, true); }// 8
			if (ref[9] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[9]], screen_coords, LX[1], BY[3], mouseX, mouseY, true); }// Out
		} else if (grid_ref == 1) {
			
			//Top Right
			if (ref[0] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[0]], screen_coords, RX[0], TY[0], mouseX, mouseY, true); }// 0
			if (ref[1] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[1]], screen_coords, RX[1], TY[0], mouseX, mouseY, true); }// 1
			if (ref[2] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[2]], screen_coords, RX[2], TY[0], mouseX, mouseY, true); }// 2
			if (ref[3] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[3]], screen_coords, RX[0], TY[1], mouseX, mouseY, true); }// 3
			if (ref[4] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[4]], screen_coords, RX[1], TY[1], mouseX, mouseY, true); }// 4
			if (ref[5] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[5]], screen_coords, RX[2], TY[1], mouseX, mouseY, true); }// 5
			if (ref[6] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[6]], screen_coords, RX[0], TY[2], mouseX, mouseY, true); }// 6
			if (ref[7] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[7]], screen_coords, RX[1], TY[2], mouseX, mouseY, true); }// 7
			if (ref[8] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[8]], screen_coords, RX[2], TY[2], mouseX, mouseY, true); }// 8
			if (ref[9] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[9]], screen_coords, RX[1], TY[3], mouseX, mouseY, true); }// Out
		} else if (grid_ref == 3) {
			
			//Bottom Right
			if (ref[0] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[0]], screen_coords, RX[0], BY[0], mouseX, mouseY, true); }// 0
			if (ref[1] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[1]], screen_coords, RX[1], BY[0], mouseX, mouseY, true); }// 1
			if (ref[2] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[2]], screen_coords, RX[2], BY[0], mouseX, mouseY, true); }// 2
			if (ref[3] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[3]], screen_coords, RX[0], BY[1], mouseX, mouseY, true); }// 3
			if (ref[4] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[4]], screen_coords, RX[1], BY[1], mouseX, mouseY, true); }// 4
			if (ref[5] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[5]], screen_coords, RX[2], BY[1], mouseX, mouseY, true); }// 5
			if (ref[6] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[6]], screen_coords, RX[0], BY[2], mouseX, mouseY, true); }// 6
			if (ref[7] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[7]], screen_coords, RX[1], BY[2], mouseX, mouseY, true); }// 7
			if (ref[8] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[8]], screen_coords, RX[2], BY[2], mouseX, mouseY, true); }// 8
			if (ref[9] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[9]], screen_coords, RX[1], BY[3], mouseX, mouseY, true); }// Out
		} else if (grid_ref == 10) {
			
			if (ref[0] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[0]], screen_coords, SLX[0], STY[0], mouseX, mouseY, true); }// 0
			if (ref[1] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[1]], screen_coords, SLX[1], STY[0], mouseX, mouseY, true); }// 1
			if (ref[2] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[2]], screen_coords, SLX[2], STY[1], mouseX, mouseY, true); }// Out
		} else if (grid_ref == 11) {
			
			if (ref[0] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[0]], screen_coords, SLX[0], STY[2], mouseX, mouseY, true); }// 0
			if (ref[1] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[1]], screen_coords, SLX[1], STY[2], mouseX, mouseY, true); }// 1
			if (ref[2] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[2]], screen_coords, SLX[2], STY[3], mouseX, mouseY, true); }// Out
		} else if (grid_ref == 12) {
			
			if (ref[0] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[0]], screen_coords, SLX[0], STY[4], mouseX, mouseY, true); }// 0
			if (ref[1] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[1]], screen_coords, SLX[1], STY[4], mouseX, mouseY, true); }// 1
			if (ref[2] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[2]], screen_coords, SLX[2], STY[5], mouseX, mouseY, true); }// Out
		} else if (grid_ref == 13) {
			
			if (ref[0] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[0]], screen_coords, SLX[0], STY[6], mouseX, mouseY, true); }// 0
			if (ref[1] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[1]], screen_coords, SLX[1], STY[6], mouseX, mouseY, true); }// 1
			if (ref[2] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[2]], screen_coords, SLX[2], STY[7], mouseX, mouseY, true); }// Out
		}else if (grid_ref == 14) {
			
			if (ref[0] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[0]], screen_coords, SLX[3], STY[0], mouseX, mouseY, true); }// 0
			if (ref[1] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[1]], screen_coords, SLX[4], STY[0], mouseX, mouseY, true); }// 1
			if (ref[2] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[2]], screen_coords, SLX[5], STY[1], mouseX, mouseY, true); }// Out
		} else if (grid_ref == 15) {
			
			if (ref[0] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[0]], screen_coords, SLX[3], STY[2], mouseX, mouseY, true); }// 0
			if (ref[1] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[1]], screen_coords, SLX[4], STY[2], mouseX, mouseY, true); }// 1
			if (ref[2] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[2]], screen_coords, SLX[5], STY[3], mouseX, mouseY, true); }// Out
		} else if (grid_ref == 16) {
			
			if (ref[0] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[0]], screen_coords, SLX[3], STY[4], mouseX, mouseY, true); }// 0
			if (ref[1] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[1]], screen_coords, SLX[4], STY[4], mouseX, mouseY, true); }// 1
			if (ref[2] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[2]], screen_coords, SLX[5], STY[5], mouseX, mouseY, true); }// Out
		} else if (grid_ref == 17) {
			
			if (ref[0] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[0]], screen_coords, SLX[3], STY[6], mouseX, mouseY, true); }// 0
			if (ref[1] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[1]], screen_coords, SLX[4], STY[6], mouseX, mouseY, true); }// 1
			if (ref[2] != -1) { CosmosUISystem.renderItemStack(this, font, poseStack, items[ref[2]], screen_coords, SLX[5], STY[7], mouseX, mouseY, true); }// Out
		}
	}

}