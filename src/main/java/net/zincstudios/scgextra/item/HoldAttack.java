package net.zincstudios.scgextra.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface HoldAttack {

    /**
     * Called every tick the player holds the attack/mine button.
     */
    void onPlayerAttackTick(ItemStack stack, Level level, Player player);

}
