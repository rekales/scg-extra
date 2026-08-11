package net.zincstudios.scgextra.item;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;

// To isolate the method from the rest of the class and avoid serverside crash
// TODO: find and use a better event pattern.
public class WreckingToolItemHandler {
    public static void onKnockback(LivingKnockBackEvent event) {
        if (event.getEntity().getLastHurtByMob() instanceof Player p
                && p.getMainHandItem().getItem() instanceof WreckingToolItem) {
            event.setStrength(event.getStrength()*0.8F);
        }
    }
}
