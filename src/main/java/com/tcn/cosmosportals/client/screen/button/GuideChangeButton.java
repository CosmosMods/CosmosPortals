package com.tcn.cosmosportals.client.screen.button;

import java.util.function.Supplier;

import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuideChangeButton extends Button {
	private final boolean isForward;
	private final boolean playTurnSound;

	private ResourceLocation TEXTURE;

	public GuideChangeButton(int x, int y, boolean isForward, Button.OnPress onPress, boolean playTurnSound, ResourceLocation location) {
		super(x, y, 25, 13, ComponentHelper.empty(), onPress, new Button.CreateNarration() {
			
			@Override
			public MutableComponent createNarrationMessage(Supplier<MutableComponent> p_253695_) {
				// TODO Auto-generated method stub
				return ComponentHelper.empty();
			}
		});
		this.isForward = isForward;
		this.playTurnSound = playTurnSound;

		this.TEXTURE = location;
	}
	
	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		if (this.visible) {
			this.renderWidget(graphics, mouseX, mouseY, partialTicks);
		}
	}
	
	@Override
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		CosmosUISystem.setTextureWithColourAlpha(graphics.pose(), TEXTURE, 1.0F, 1.0F, 1.0F, this.alpha);
		
		int i = 206;
		int j = 230;

		if (this.isHoveredOrFocused()) {
			i += 25;
		}

		if (!this.isForward) {
			j += 13;
		}

		graphics.blit(TEXTURE, this.getX(), this.getY(), i, j, 25, 13);
	}

	@Override
	public void playDownSound(SoundManager handler) {
		if (this.playTurnSound) {
			handler.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 5F));
		}
	}
}