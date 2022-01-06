package com.tcn.cosmosportals.client.screen.button;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuideChangeButton extends Button {
	private final boolean isForward;
	private final boolean playTurnSound;

	private ResourceLocation texture;

	public GuideChangeButton(int x, int y, boolean isForward, Button.OnPress onPress, boolean playTurnSound, ResourceLocation location) {
		super(x, y, 25, 13, ComponentHelper.locComp(""), onPress);
		this.isForward = isForward;
		this.playTurnSound = playTurnSound;

		this.texture = location;
	}
	
	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (this.visible) {
			this.renderButton(matrixStack, mouseX, mouseY, partialTicks);
		}
	}
	
	@Override
	public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		CosmosUISystem.setTextureWithColourAlpha(matrixStack, texture, 1.0F, 1.0F, 1.0F, this.alpha);
		
		int i = 206;
		int j = 230;

		if (this.isHoveredOrFocused()) {
			i += 25;
		}

		if (!this.isForward) {
			j += 13;
		}

		this.blit(matrixStack, this.x, this.y, i, j, 25, 13);
	}

	@Override
	public void playDownSound(SoundManager handler) {
		if (this.playTurnSound) {
			handler.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 5F));
		}
	}
}