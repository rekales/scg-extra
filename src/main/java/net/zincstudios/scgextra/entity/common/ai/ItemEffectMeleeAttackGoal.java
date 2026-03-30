package net.zincstudios.scgextra.entity.common.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.item.ItemStack;
import net.zincstudios.scgextra.item.HurtEffects;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ItemEffectMeleeAttackGoal extends MeleeAttackGoal {

    public ItemEffectMeleeAttackGoal(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
    }

    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
        super.checkAndPerformAttack(enemy, distToEnemySqr);

        ItemStack stack = this.mob.getMainHandItem();
        if (stack.getItem() instanceof HurtEffects item) {
            item.hurtEffect(stack, enemy, this.mob);
        }
    }


}
