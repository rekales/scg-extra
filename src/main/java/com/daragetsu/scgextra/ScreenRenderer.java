package com.daragetsu.scgextra;

import com.daragetsu.scgextra.effects.ModEffects;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SCGExtra.MOD_ID, value = Dist.CLIENT)
public class ScreenRenderer {
    private static final ResourceLocation INK =
            SCGExtra.asResource("textures/ink_splat/model.png");
    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (!mc.player.hasEffect(ModEffects.INK_EFFECT.get())) return;

        GuiGraphics g = event.getGuiGraphics();

        int screenW = event.getWindow().getGuiScaledWidth();
        int screenH = event.getWindow().getGuiScaledHeight();

        int imgW = 512;
        int imgH = 512;

        int x = (screenW - imgW) / 2;
        int y = (screenH - imgH) / 2;

        RenderSystem.enableBlend();

        g.blit(
            INK,
            x, 
            y,
            0, 
            0,
            imgW, 
            imgH,
            imgW, 
            imgH
        );

        RenderSystem.disableBlend();
    }
}
