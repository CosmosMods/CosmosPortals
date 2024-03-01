package com.tcn.cosmosportals.client.screen.button;

import java.util.function.Supplier;

import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuideButton extends Button {
	private ResourceLocation TEXTURE;
	
	protected int width;
	protected int height;
	public int x;
	public int y;
	private int colour;
	protected boolean isHovered;
	protected float alpha = 1.0F;
	protected long nextNarration = Long.MAX_VALUE;
	private int identifier;

	public GuideButton(int x, int y, int size, int identifier, int colour, ResourceLocation location, Button.OnPress pressedAction) {
		super(x, y, size, size, ComponentHelper.empty(), pressedAction, new Button.CreateNarration() {
			
			@Override
			public MutableComponent createNarrationMessage(Supplier<MutableComponent> p_253695_) {
				// TODO Auto-generated method stub
				return ComponentHelper.empty();
			}
		});
		this.x = x;
		this.y = y;
		this.width = size;
		this.height = size;
		
		this.colour = colour;
		this.TEXTURE = location;
		this.identifier = identifier;
	}
	
	public GuideButton(int x, int y, int colour, ResourceLocation location, Button.OnPress pressedAction) {
		super(x, y, 15, 25, ComponentHelper.empty(), pressedAction, new Button.CreateNarration() {
			
			@Override
			public MutableComponent createNarrationMessage(Supplier<MutableComponent> p_253695_) {
				// TODO Auto-generated method stub
				return ComponentHelper.empty();
			}
		});
		this.x = x;
		this.y = y;
		this.width = 15;
		this.height = 25;
		this.colour = colour;
		this.TEXTURE = location;
		this.identifier = -1;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		if (this.visible) {
			this.renderWidget(graphics, mouseX, mouseY, partialTicks);
		}
	}

	@Override
	public void onPress() {
		this.onPress.onPress(this);
	}

	@Override
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		this.setFGColor(this.colour);
		
		float[] rgb = ComponentColour.rgbFloatArray(this.colour);
		
		CosmosUISystem.setTextureWithColourAlpha(graphics.pose(), TEXTURE, rgb[0], rgb[1], rgb[2], this.alpha);
		
		this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

		if (this.isHovered) {
			if (this.identifier == 0) {
				graphics.blit(TEXTURE, this.x, this.y, 243, 216, this.width, this.height);
			} else if (this.identifier == 1) {
				graphics.blit(TEXTURE, this.x, this.y, 230, 216, this.width, this.height);
			} else {
				graphics.blit(TEXTURE, this.x, this.y, 241, 177, this.width, this.height);
			}
		} else {
			if (this.identifier == 0) {
				graphics.blit(TEXTURE, this.x, this.y, 243, 203, this.width, this.height);
			} else if (this.identifier == 1) {
				graphics.blit(TEXTURE, this.x, this.y, 230, 203, this.width, this.height);
			} else {
				graphics.blit(TEXTURE, this.x, this.y, 241, 152, this.width, this.height);
			}
		}
	}

	protected int getHoverState(boolean mouseOver) {
		int i = 0;

		if (!this.active) {
			i = 2;
		} else if (mouseOver) {
			i = 1;
		}
		return i;
	}
}