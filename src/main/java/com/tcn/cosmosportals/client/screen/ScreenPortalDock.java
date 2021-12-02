package com.tcn.cosmosportals.client.screen;

import java.util.Arrays;

import org.apache.commons.lang3.text.WordUtils;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmoslibrary.client.screen.widget.CosmosButtonWithType;
import com.tcn.cosmoslibrary.client.screen.widget.CosmosButtonWithType.TYPE;
import com.tcn.cosmoslibrary.client.util.CosmosScreenHelper;
import com.tcn.cosmoslibrary.client.util.CosmosScreenHelper.DRAW;
import com.tcn.cosmoslibrary.common.comp.CosmosColour;
import com.tcn.cosmoslibrary.common.comp.CosmosCompHelper;
import com.tcn.cosmosportals.CosmosPortalsReference;
import com.tcn.cosmosportals.client.container.ContainerPortalDock;
import com.tcn.cosmosportals.core.blockentity.BlockEntityPortalDock;
import com.tcn.cosmosportals.core.management.NetworkManager;
import com.tcn.cosmosportals.core.network.PacketPortalDock;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

@SuppressWarnings({ "unused", "deprecation" })
public class ScreenPortalDock extends AbstractContainerScreen<ContainerPortalDock> {
	
	private CosmosButtonWithType toggleLabelButton;     private int[] indexL = new int[] { 16,   139, 20 };
	private CosmosButtonWithType toggleSoundButton;     private int[] indexS = new int[] { 46,   139, 20 };
	private CosmosButtonWithType toggleEntityButton; 	private int[] indexE = new int[] { 114,  139, 20 };
	private CosmosButtonWithType toggleParticlesButton; private int[] indexP = new int[] { 144,  139, 20 };

	public ScreenPortalDock(ContainerPortalDock containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);

		this.imageWidth = 180;
		this.imageHeight = 256;
	}

	@Override
	protected void init() {
		int[] screen_coords = CosmosScreenHelper.INIT.getScreenCoords(this, this.imageWidth, this.imageHeight);
		
		this.drawButtons(screen_coords);
		
		super.init();
		this.titleLabelX = 50;
	}

	@Override
	public void render(PoseStack matrixStackIn, int mouseX, int mouseY, float partialTicks) {
		int[] screen_coords = CosmosScreenHelper.INIT.getScreenCoords(this, this.imageWidth, this.imageHeight);
		
		this.renderBackground(matrixStackIn);
		super.render(matrixStackIn, mouseX, mouseY, partialTicks);
		this.drawButtons(screen_coords);
		
		ContainerPortalDock container = this.menu;
		Level world = container.getWorld();
		BlockPos pos = container.getBlockPos();
		BlockEntity tile = world.getBlockEntity(pos);
		
		if (tile instanceof BlockEntityPortalDock) {
			BlockEntityPortalDock tileEntity = (BlockEntityPortalDock) tile;
			
			if (tileEntity.isPortalFormed && tileEntity.renderLabel) {
				String human_name = WordUtils.capitalizeFully(tileEntity.destDimension.getPath().replace("_", " "));
				int portalColour = tileEntity.getDisplayColour();
				
				drawCenteredString(matrixStackIn, font, human_name, screen_coords[0] + this.imageWidth / 2, screen_coords[1] + 119, portalColour);
			}
		}
		
		this.renderComponentHoverEffect(matrixStackIn, Style.EMPTY, mouseX, mouseY);
		this.renderTooltip(matrixStackIn, mouseX, mouseY);
	}

	@Override
	protected void renderBg(PoseStack matrixStackIn, float partialTicks, int mouseX, int mouseY) {
		int[] screen_coords = CosmosScreenHelper.INIT.getScreenCoords(this, this.imageWidth, this.imageHeight);

		ContainerPortalDock container = this.menu;
		Level world = container.getWorld();
		BlockPos pos = container.getBlockPos();
		BlockEntity tile = world.getBlockEntity(pos);
		
		DRAW.drawStaticElement(this, matrixStackIn, screen_coords, 0, 0, 0, 0, this.imageWidth, this.imageHeight, CosmosPortalsReference.DOCK_BACKGROUND);
		
		if (tile instanceof BlockEntityPortalDock) {
			BlockEntityPortalDock tileEntity = (BlockEntityPortalDock) tile;

			int portalColour = tileEntity.getDisplayColour();
			float frame[] = CosmosColour.rgbFloatArray(CosmosColour.GRAY);
			float[] colour = new float[] {((portalColour >> 16) & 255) / 255.0F, ((portalColour >> 8) & 255) / 255.0F, (portalColour & 255) / 255.0F, 1F};
			
			DRAW.drawStaticElement(this, matrixStackIn, screen_coords, 0, 0, 0, 0, this.imageWidth, this.imageHeight, new float[] { frame[0], frame[1], frame[2], 1.0F }, CosmosPortalsReference.DOCK_FRAME);
			
			if (tileEntity.isPortalFormed) {
				GlStateManager._enableBlend();
				DRAW.drawStaticElement(this, matrixStackIn, screen_coords, 0, 0, 0, 0, this.imageWidth, this.imageHeight, colour, CosmosPortalsReference.DOCK_PORTAL);
				GlStateManager._disableBlend();
				
			}
			
			DRAW.drawStaticElement(this, matrixStackIn, screen_coords, 0, 0, 0, 0, this.imageWidth, this.imageHeight, colour, CosmosPortalsReference.DOCK_CONTAINER);
			DRAW.drawStaticElement(this, matrixStackIn, screen_coords, 0, 0, 0, 0, this.imageWidth, this.imageHeight, new float[] { 1.0F, 1.0F, 1.0F, 1.0F }, CosmosPortalsReference.DOCK_SLOTS);
			
			if (tileEntity.isPortalFormed && tileEntity.renderLabel) {
				String human_name = WordUtils.capitalizeFully(tileEntity.destDimension.getPath().replace("_", " "));
				int width = this.font.width(human_name) + 2;
				
				DRAW.drawStaticElement(this, matrixStackIn, screen_coords, this.imageWidth / 2 - width / 2, 117, 0, 0, width, 12, new float[] { 1.0F, 1.0F, 1.0F, 0.6F }, CosmosPortalsReference.DOCK_LABEL);
			}
		}
	}
	
	@Override
	public void renderComponentHoverEffect(PoseStack matrixStack, Style style, int mouseX, int mouseY) {
		int[] screen_coords = CosmosScreenHelper.INIT.getScreenCoords(this, this.imageWidth, this.imageHeight);

		ContainerPortalDock container = this.menu;
		Level world = container.getWorld();
		BlockPos pos = container.getBlockPos();
		BlockEntity tile = world.getBlockEntity(pos);
		
		if (tile instanceof BlockEntityPortalDock) {
			BlockEntityPortalDock tileEntity = (BlockEntityPortalDock) tile;
		
			if (this.toggleLabelButton.isMouseOver(mouseX, mouseY)) {
				BaseComponent[] comp = new BaseComponent[] { CosmosCompHelper.locComp(CosmosColour.WHITE, false, "cosmosportals.gui.dock.label_info"), 
					(BaseComponent) CosmosCompHelper.locComp(CosmosColour.GRAY, false, "cosmosportals.gui.dock.label_value", " ")
					.append(tileEntity.renderLabel ? CosmosCompHelper.locComp(CosmosColour.GREEN, true, "cosmosportals.gui.dock.label_shown") : CosmosCompHelper.locComp(CosmosColour.RED, true, "cosmosportals.gui.dock.label_hidden"))
				};
				
				this.renderComponentTooltip(matrixStack, Arrays.asList(comp), mouseX, mouseY);
			}
			
			if (this.toggleSoundButton.isMouseOver(mouseX, mouseY)) {
				BaseComponent[] comp = new BaseComponent[] { CosmosCompHelper.locComp(CosmosColour.WHITE, false, "cosmosportals.gui.dock.sounds_info"), 
					(BaseComponent) CosmosCompHelper.locComp(CosmosColour.GRAY, false, "cosmosportals.gui.dock.sounds_value", " ")
					.append(tileEntity.playSound ? CosmosCompHelper.locComp(CosmosColour.GREEN, true, "cosmosportals.gui.dock.sounds_played") : CosmosCompHelper.locComp(CosmosColour.RED, true, "cosmosportals.gui.dock.sounds_muffled"))
				};
				
				this.renderComponentTooltip(matrixStack, Arrays.asList(comp), mouseX, mouseY);
			}
			
			if (this.toggleEntityButton.isMouseOver(mouseX, mouseY)) {
				BaseComponent[] comp = new BaseComponent[] { CosmosCompHelper.locComp(CosmosColour.WHITE, false, "cosmosportals.gui.dock.entity_info"), 
					(BaseComponent) CosmosCompHelper.locComp(CosmosColour.GRAY, false, "cosmosportals.gui.dock.entity_value", " ")
					.append(tileEntity.allowedEntities.getColouredComp())
				};
				
				this.renderComponentTooltip(matrixStack, Arrays.asList(comp), mouseX, mouseY);
			}

			if (this.toggleParticlesButton.isMouseOver(mouseX, mouseY)) {
				BaseComponent[] comp = new BaseComponent[] { CosmosCompHelper.locComp(CosmosColour.WHITE, false, "cosmosportals.gui.dock.particle_info"), 
					(BaseComponent) CosmosCompHelper.locComp(CosmosColour.GRAY, false, "cosmosportals.gui.dock.particle_value", " ")
					.append(tileEntity.showParticles ? CosmosCompHelper.locComp(CosmosColour.GREEN, true, "cosmosportals.gui.dock.particle_shown") : CosmosCompHelper.locComp(CosmosColour.RED, true, "cosmosportals.gui.dock.particle_hidden"))
				};
				
				this.renderComponentTooltip(matrixStack, Arrays.asList(comp), mouseX, mouseY);
			}
		}
	}
		
	@Override
	protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
		drawCenteredString(matrixStack, font, this.title, this.imageWidth / 2, 17, CosmosScreenHelper.DEFAULT_COLOUR_FONT_LIST);
		
		this.font.draw(matrixStack, this.playerInventoryTitle, (float) this.inventoryLabelX + 1, (float) this.inventoryLabelY + 94, CosmosScreenHelper.DEFAULT_COLOUR_BACKGROUND);
	}
	
	private void drawButtons(int[] screen_coords) {
		this.children().clear();
		
		ContainerPortalDock container = this.menu;
		Level world = container.getWorld();
		BlockPos pos = container.getBlockPos();
		BlockEntity tile = world.getBlockEntity(pos);
		
		if (tile instanceof BlockEntityPortalDock) {
			BlockEntityPortalDock tileEntity = (BlockEntityPortalDock) tile;
			
			int i = 0;
			int j = tileEntity.allowedEntities.getIndex();
			
			if (j == 0) {
				i = 2;
			} else if (j == 1) {
				i = 15;
			} else if (j == 2) {
				i = 19;
			} else if (j == 3) {
				i = 1;
			}

			this.toggleLabelButton  	= this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, screen_coords[0] + indexL[0], screen_coords[1] + indexL[1], indexL[2], true, true, tileEntity.renderLabel   ? 1 : 2, CosmosCompHelper.locComp(""), (button) -> { this.pushButton(this.toggleLabelButton);	    }));
			this.toggleSoundButton   	= this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, screen_coords[0] + indexS[0], screen_coords[1] + indexS[1], indexS[2], true, true, tileEntity.playSound     ? 1 : 2, CosmosCompHelper.locComp(""), (button) -> { this.pushButton(this.toggleSoundButton);     }));
			this.toggleEntityButton  	= this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, screen_coords[0] + indexE[0], screen_coords[1] + indexE[1], indexE[2], true, true, i,                                CosmosCompHelper.locComp(""), (button) -> { this.pushButton(this.toggleEntityButton);	}));
			this.toggleParticlesButton  = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, screen_coords[0] + indexP[0], screen_coords[1] + indexP[1], indexP[2], true, true, tileEntity.showParticles ? 1 : 2, CosmosCompHelper.locComp(""), (button) -> { this.pushButton(this.toggleParticlesButton); }));
			
		}
	}
	
	public void pushButton(Button button) {
		ContainerPortalDock container = this.menu;
		Level world = container.getWorld();
		BlockPos pos = container.getBlockPos();
		BlockEntity tile = world.getBlockEntity(pos);
		
		if (tile instanceof BlockEntityPortalDock) {
			BlockEntityPortalDock tileEntity = (BlockEntityPortalDock) tile;
			
			if (button.equals(this.toggleLabelButton)) {
				NetworkManager.sendToServer(new PacketPortalDock(pos, 0));
				tileEntity.toggleRenderLabel();
			}
			
			if (button.equals(this.toggleSoundButton)) {
				NetworkManager.sendToServer(new PacketPortalDock(pos, 1));
				tileEntity.togglePlaySound();
			}
			
			if (button.equals(this.toggleEntityButton)) {
				NetworkManager.sendToServer(new PacketPortalDock(pos, 2));
				tileEntity.toggleEntities();
			}

			if (button.equals(this.toggleParticlesButton)) {
				NetworkManager.sendToServer(new PacketPortalDock(pos, 3));
				tileEntity.toggleParticles();
			}
		}
	}
}