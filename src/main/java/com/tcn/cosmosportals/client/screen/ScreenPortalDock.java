package com.tcn.cosmosportals.client.screen;

import java.util.Arrays;

import org.apache.commons.lang3.text.WordUtils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tcn.cosmoslibrary.client.screen.button.CosmosButtonCustom;
import com.tcn.cosmoslibrary.client.screen.button.CosmosButtonCustom.TYPE;
import com.tcn.cosmoslibrary.client.util.ScreenUtil;
import com.tcn.cosmoslibrary.client.util.ScreenUtil.DRAW;
import com.tcn.cosmoslibrary.common.comp.CosmosColour;
import com.tcn.cosmoslibrary.common.comp.CosmosCompHelper;
import com.tcn.cosmosportals.CosmosPortals;
import com.tcn.cosmosportals.CosmosPortalsReference;
import com.tcn.cosmosportals.client.container.ContainerPortalDock;
import com.tcn.cosmosportals.core.management.CoreNetworkManager;
import com.tcn.cosmosportals.core.network.PacketPortalDock;
import com.tcn.cosmosportals.core.tileentity.TileEntityPortalDock;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

@SuppressWarnings({ "unused", "deprecation" })
public class ScreenPortalDock extends ContainerScreen<ContainerPortalDock> {
	
	private CosmosButtonCustom toggleLabelButton;     private int[] indexL = new int[] { 17, 140, 18 };
	private CosmosButtonCustom toggleSoundButton;     private int[] indexS = new int[] { 47, 140, 18 };
	private CosmosButtonCustom toggleEntityButton; 	  private int[] indexE = new int[] { 115,  140, 18 };
	private CosmosButtonCustom toggleParticlesButton; private int[] indexP = new int[] { 145,  140, 18 };

	public ScreenPortalDock(ContainerPortalDock containerIn, PlayerInventory playerInventoryIn, ITextComponent titleIn) {
		super(containerIn, playerInventoryIn, titleIn);

		this.imageWidth = 180;
		this.imageHeight = 256;
	}

	@Override
	protected void init() {
		int[] screen_coords = ScreenUtil.INIT.getScreenCoords(this, this.imageWidth, this.imageHeight);
		
		this.drawButtons(screen_coords);
		
		super.init();
		this.titleLabelX = 50;
	}

	@Override
	public void render(MatrixStack matrixStackIn, int mouseX, int mouseY, float partialTicks) {
		int[] screen_coords = ScreenUtil.INIT.getScreenCoords(this, this.imageWidth, this.imageHeight);
		
		this.renderBackground(matrixStackIn);
		super.render(matrixStackIn, mouseX, mouseY, partialTicks);
		this.drawButtons(screen_coords);
		
		ContainerPortalDock container = this.menu;
		World world = container.getWorld();
		BlockPos pos = container.getBlockPos();
		TileEntity tile = world.getBlockEntity(pos);
		
		if (tile instanceof TileEntityPortalDock) {
			TileEntityPortalDock tileEntity = (TileEntityPortalDock) tile;
			
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
	protected void renderBg(MatrixStack matrixStackIn, float partialTicks, int mouseX, int mouseY) {
		int[] screen_coords = ScreenUtil.INIT.getScreenCoords(this, this.imageWidth, this.imageHeight);
		
		ContainerPortalDock container = this.menu;
		World world = container.getWorld();
		BlockPos pos = container.getBlockPos();
		TileEntity tile = world.getBlockEntity(pos);
		
		DRAW.drawStaticElement(this, matrixStackIn, screen_coords, 0, 0, 0, 0, this.imageWidth, this.imageHeight, CosmosPortalsReference.DOCK_BACKGROUND);
		
		if (tile instanceof TileEntityPortalDock) {
			TileEntityPortalDock tileEntity = (TileEntityPortalDock) tile;

			int portalColour = tileEntity.getDisplayColour();
			float frame[] = CosmosColour.rgbFloatArray(CosmosColour.GRAY);
			float[] colour = new float[] {((portalColour >> 16) & 255) / 255.0F, ((portalColour >> 8) & 255) / 255.0F, (portalColour & 255) / 255.0F, 1.0F};
			
			DRAW.drawStaticElement(this, matrixStackIn, screen_coords, 0, 0, 0, 0, this.imageWidth, this.imageHeight, new float[] { frame[0], frame[1], frame[2], 1.0F }, CosmosPortalsReference.DOCK_FRAME);
			
			if (tileEntity.isPortalFormed) {
				DRAW.drawStaticElement(this, matrixStackIn, screen_coords, 0, 0, 0, 0, this.imageWidth, this.imageHeight, colour, CosmosPortalsReference.DOCK_PORTAL);
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
	public void renderComponentHoverEffect(MatrixStack matrixStack, Style style, int mouseX, int mouseY) {
		int[] screen_coords = ScreenUtil.INIT.getScreenCoords(this, this.imageWidth, this.imageHeight);

		ContainerPortalDock container = this.menu;
		World world = container.getWorld();
		BlockPos pos = container.getBlockPos();
		TileEntity tile = world.getBlockEntity(pos);
		
		if (tile instanceof TileEntityPortalDock) {
			TileEntityPortalDock tileEntity = (TileEntityPortalDock) tile;
		
			if (this.toggleLabelButton.isMouseOver(mouseX, mouseY)) {
				IFormattableTextComponent[] comp = new IFormattableTextComponent[] { CosmosCompHelper.locComp(CosmosColour.WHITE, false, "cosmosportals.gui.dock.label_info"), 
					CosmosCompHelper.locComp(CosmosColour.GRAY, false, "cosmosportals.gui.dock.label_value", " ")
					.append(tileEntity.renderLabel ? CosmosCompHelper.locComp(CosmosColour.GREEN, true, "cosmosportals.gui.dock.label_shown") : CosmosCompHelper.locComp(CosmosColour.RED, true, "cosmosportals.gui.dock.label_hidden"))
				};
				
				this.renderComponentTooltip(matrixStack, Arrays.asList(comp), mouseX, mouseY);
			}
			
			if (this.toggleSoundButton.isMouseOver(mouseX, mouseY)) {
				IFormattableTextComponent[] comp = new IFormattableTextComponent[] { CosmosCompHelper.locComp(CosmosColour.WHITE, false, "cosmosportals.gui.dock.sounds_info"), 
					CosmosCompHelper.locComp(CosmosColour.GRAY, false, "cosmosportals.gui.dock.sounds_value", " ")
					.append(tileEntity.playSound ? CosmosCompHelper.locComp(CosmosColour.GREEN, true, "cosmosportals.gui.dock.sounds_played") : CosmosCompHelper.locComp(CosmosColour.RED, true, "cosmosportals.gui.dock.sounds_muffled"))
				};
				
				this.renderComponentTooltip(matrixStack, Arrays.asList(comp), mouseX, mouseY);
			}
			
			if (this.toggleEntityButton.isMouseOver(mouseX, mouseY)) {
				IFormattableTextComponent[] comp = new IFormattableTextComponent[] { CosmosCompHelper.locComp(CosmosColour.WHITE, false, "cosmosportals.gui.dock.entity_info"), 
					CosmosCompHelper.locComp(CosmosColour.GRAY, false, "cosmosportals.gui.dock.entity_value", " ")
					.append(tileEntity.allowEntities ? CosmosCompHelper.locComp(CosmosColour.GREEN, true, "cosmosportals.gui.dock.entity_all") : CosmosCompHelper.locComp(CosmosColour.RED, true, "cosmosportals.gui.dock.entity_player"))
				};
				
				this.renderComponentTooltip(matrixStack, Arrays.asList(comp), mouseX, mouseY);
			}

			if (this.toggleParticlesButton.isMouseOver(mouseX, mouseY)) {
				IFormattableTextComponent[] comp = new IFormattableTextComponent[] { CosmosCompHelper.locComp(CosmosColour.WHITE, false, "cosmosportals.gui.dock.particle_info"), 
					CosmosCompHelper.locComp(CosmosColour.GRAY, false, "cosmosportals.gui.dock.particle_value", " ")
					.append(tileEntity.showParticles ? CosmosCompHelper.locComp(CosmosColour.GREEN, true, "cosmosportals.gui.dock.particle_shown") : CosmosCompHelper.locComp(CosmosColour.RED, true, "cosmosportals.gui.dock.particle_hidden"))
				};
				
				this.renderComponentTooltip(matrixStack, Arrays.asList(comp), mouseX, mouseY);
			}
		}
	}
		
	@Override
	protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
		drawCenteredString(matrixStack, font, this.title, this.imageWidth / 2, 17, ScreenUtil.DEFAULT_COLOUR_FONT_LIST);
		
		this.font.draw(matrixStack, this.inventory.getDisplayName(), (float) this.inventoryLabelX + 1, (float) this.inventoryLabelY + 94, ScreenUtil.DEFAULT_COLOUR_BACKGROUND);
	}
	
	private void drawButtons(int[] screen_coords) {
		this.buttons.clear();
		
		ContainerPortalDock container = this.menu;
		World world = container.getWorld();
		BlockPos pos = container.getBlockPos();
		TileEntity tile = world.getBlockEntity(pos);
		
		if (tile instanceof TileEntityPortalDock) {
			TileEntityPortalDock tileEntity = (TileEntityPortalDock) tile;
			
			this.toggleLabelButton  	= this.addButton(new CosmosButtonCustom(TYPE.GENERAL, screen_coords[0] + indexL[0], screen_coords[1] + indexL[1], 18, true, true, tileEntity.renderLabel   ? 1 : 2, CosmosCompHelper.locComp(""), (button) -> { this.pushButton(this.toggleLabelButton);	  }));
			this.toggleSoundButton   	= this.addButton(new CosmosButtonCustom(TYPE.GENERAL, screen_coords[0] + indexS[0], screen_coords[1] + indexS[1], 18, true, true, tileEntity.playSound     ? 1 : 2, CosmosCompHelper.locComp(""), (button) -> { this.pushButton(this.toggleSoundButton); 	  }));
			this.toggleEntityButton  	= this.addButton(new CosmosButtonCustom(TYPE.GENERAL, screen_coords[0] + indexE[0], screen_coords[1] + indexE[1], 18, true, true, tileEntity.allowEntities ? 1 : 2, CosmosCompHelper.locComp(""), (button) -> { this.pushButton(this.toggleEntityButton);	  }));
			this.toggleParticlesButton  = this.addButton(new CosmosButtonCustom(TYPE.GENERAL, screen_coords[0] + indexP[0], screen_coords[1] + indexP[1], 18, true, true, tileEntity.showParticles ? 1 : 2, CosmosCompHelper.locComp(""), (button) -> { this.pushButton(this.toggleParticlesButton);  }));
			
		}
	}
	
	public void pushButton(Button button) {
		ContainerPortalDock container = this.menu;
		World world = container.getWorld();
		BlockPos pos = container.getBlockPos();
		TileEntity tile = world.getBlockEntity(pos);
		
		if (tile instanceof TileEntityPortalDock) {
			TileEntityPortalDock tileEntity = (TileEntityPortalDock) tile;
			
			if (button.equals(this.toggleLabelButton)) {
				CoreNetworkManager.sendToServer(new PacketPortalDock(pos, 0));
				tileEntity.toggleRenderLabel();
			}
			
			if (button.equals(this.toggleSoundButton)) {
				CoreNetworkManager.sendToServer(new PacketPortalDock(pos, 1));
				tileEntity.togglePlaySound();
			}
			
			if (button.equals(this.toggleEntityButton)) {
				CoreNetworkManager.sendToServer(new PacketPortalDock(pos, 2));
				tileEntity.toggleEntities();
			}

			if (button.equals(this.toggleParticlesButton)) {
				CoreNetworkManager.sendToServer(new PacketPortalDock(pos, 3));
				tileEntity.toggleParticles();
			}
		}
	}
}