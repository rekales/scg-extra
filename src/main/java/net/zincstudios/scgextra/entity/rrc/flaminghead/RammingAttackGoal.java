package net.zincstudios.scgextra.entity.rrc.flaminghead;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.entity.Faction;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

// TODO: edit the old goals to follow this pattern, make abstract class if possible
// Designed around FlamingHeadEntity, make generic later if needed using behaviour augmentation
public class RammingAttackGoal extends Goal {

    protected final FlamingHeadEntity mob;
    private final int cooldownDuration;
    private final int maxDuration;
    private final float speedMultiplier;
    private final List<LivingEntity> affectedEntities = new ArrayList<>();
    private long cooldownEnd = 0;  // level timestamp
    public int duration;
    private Vec3 ramDirection = new Vec3(1,0,0);
    private boolean hadTarget = false;

    public RammingAttackGoal(FlamingHeadEntity mob, int cooldownDuration, int maxDuration, float speedMultiplier) {
        this.mob = mob;
        this.cooldownDuration = cooldownDuration;
        this.maxDuration = maxDuration;
        this.speedMultiplier = speedMultiplier;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity == null || !livingentity.isAlive()) {
            this.hadTarget = false;
            return false;
        }

        if (!this.hadTarget) {
            this.hadTarget = true;
            this.cooldownEnd = this.mob.level().getGameTime() + this.cooldownDuration/2;  // Half cooldown at start
        }

        return (this.mob.getBehaviorState() == FlamingHeadEntity.BehaviorState.NONE)
                && this.mob.level().getGameTime() > this.cooldownEnd;
    }

    @Override
    public boolean canContinueToUse() {
        return this.duration < this.maxDuration;
    }

    @Override
    public void start() {
        this.mob.setBehaviorState(FlamingHeadEntity.BehaviorState.RAMMING);
        assert this.mob.getTarget() != null;
        this.ramDirection = this.mob.getTarget().position().subtract(this.mob.position()).normalize();
        this.ramDirection = new Vec3(this.ramDirection.x, 0, this.ramDirection.z);
        this.mob.setRamYaw((float) Mth.wrapDegrees(Math.toDegrees(Mth.atan2(this.ramDirection.z, this.ramDirection.x)) - 90.0));
        this.affectedEntities.clear();
        this.affectedEntities.add(this.mob);  // don't damage and knockback self
        this.duration = 0;
    }

    @Override
    public void stop() {
        if (this.mob.getBehaviorState() == FlamingHeadEntity.BehaviorState.RAMMING) {
            this.mob.setBehaviorState(FlamingHeadEntity.BehaviorState.NONE);
        }
        this.cooldownEnd = this.mob.level().getGameTime() + this.cooldownDuration;
        this.mob.resetRamYaw();
        this.mob.setAnimateRamming(false);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.duration++;

        if (this.duration == 12) this.mob.triggerAnim("effects", "eye_flash");

        if (this.duration < 20) return;

        this.mob.setAnimateRamming(true);

        List<LivingEntity> nearbyTargets = this.mob.level().getEntitiesOfClass(LivingEntity.class, this.mob.getBoundingBox().inflate(0.5));
        for (LivingEntity entity : nearbyTargets) {
            if (this.affectedEntities.contains(entity)) continue;

            if (!Faction.isFriendlies(this.mob, entity)) {
                entity.hurt(this.mob.level().damageSources().noAggroMobAttack(this.mob), 20);
            }
            entity.knockback(0.8F, this.ramDirection.x, this.ramDirection.z);
            this.affectedEntities.add(entity);
        }

        this.mob.setDeltaMovement(
                this.ramDirection.x * this.mob.getSpeed() * this.speedMultiplier,
                this.mob.getDeltaMovement().y ,
                this.ramDirection.z * this.mob.getSpeed() * this.speedMultiplier);
        this.mob.position().add(this.ramDirection.x, 0, this.ramDirection.y);
    }
}
