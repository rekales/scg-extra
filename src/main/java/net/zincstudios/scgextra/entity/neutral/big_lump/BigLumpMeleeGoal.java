package net.zincstudios.scgextra.entity.neutral.big_lump;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class BigLumpMeleeGoal extends Goal {
    private final BigLumpEntity mob;

    public BigLumpMeleeGoal(BigLumpEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.mob.shouldUseMeleeGoal();
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.isMeleeAnimationLocked();
    }

    @Override
    public void start() {
        LivingEntity target = this.mob.getTarget();
        if (!this.mob.isValidTarget(target)) {
            return;
        }
        this.mob.getNavigation().stop();
        this.mob.markMovementReason("melee_start_stop_for_attack");
        this.mob.markBodyTurnReason("melee_attack_lock");
        this.mob.startMeleeAttack(target);
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (this.mob.isValidTarget(target)) {
            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.mob.markHeadTurnReason("melee_track_target");
            this.mob.markMovementReason("melee_animation_active");
        }
    }
}

