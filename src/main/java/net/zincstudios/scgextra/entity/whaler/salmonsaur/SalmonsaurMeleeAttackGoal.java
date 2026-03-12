package net.zincstudios.scgextra.entity.whaler.salmonsaur;

import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class SalmonsaurMeleeAttackGoal extends MeleeAttackGoal{
    SalmonsaurEntity entity;
    public SalmonsaurMeleeAttackGoal(SalmonsaurEntity pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen) {
        super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
        this.entity = pMob;
    }
    @Override
    public void resetAttackCooldown() {
        super.resetAttackCooldown();
        entity.triggerAnim("attack", "bite");
    }
}