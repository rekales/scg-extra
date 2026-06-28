package net.zincstudios.scgextra.entity.fac.tank;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

public class FacBossSummonGoal extends Goal {

    private final PathfinderMob mob;
    private final int cooldownDuration;
    private final int summonDelay;
    private final int summonCount;
    private final EntityType<? extends Mob>[] summonTypes;

    private long summonTrigger = -1;
    private long cooldownEnd = 0;

    @SafeVarargs
    public FacBossSummonGoal(PathfinderMob mob, int cooldownDuration, int summonDelay, int summonCount,
                             EntityType<? extends Mob>... summonTypes) {
        this.mob = mob;
        this.cooldownDuration = cooldownDuration;
        this.summonDelay = summonDelay;
        this.summonCount = summonCount;
        this.summonTypes = summonTypes;
    }

    @Override
    public boolean canUse() {
        if (this.mob instanceof FacTankEntity tank && tank.isStompLocked()) {
            return false;
        }
        LivingEntity target = this.mob.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void start() {
        this.cooldownEnd = this.mob.level().getGameTime() + this.cooldownDuration;
    }

    @Override
    public void tick() {
        FacTankEntity tank = this.mob instanceof FacTankEntity facTank ? facTank : null;
        if (tank != null && tank.isStompLocked()) {
            return;
        }

        if (this.mob.level().getGameTime() > this.cooldownEnd) {
            this.cooldownEnd = this.mob.level().getGameTime() + this.cooldownDuration;
            this.summonTrigger = this.mob.level().getGameTime() + this.summonDelay;
            if (tank != null) {
                tank.startSummonLock(this.summonDelay);
            }
        }

        if (tank != null && this.summonTrigger != -1) {
            tank.getNavigation().stop();
        }

        if (this.summonTrigger != -1 && this.mob.level().getGameTime() > this.summonTrigger) {
            this.summonTrigger = -1;
            summonMobs();
            if (tank != null) {
                tank.clearSummonLock();
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void summonMobs() {
        LivingEntity target = this.mob.getTarget();
        if (target == null || !(this.mob.level() instanceof ServerLevel level)) {
            return;
        }

        for (int i = 0; i < this.summonCount; ++i) {
            EntityType<? extends Mob> summonType = this.summonTypes[this.mob.getRandom().nextInt(this.summonTypes.length)];
            BlockPos spawnPos = this.mob.blockPosition().offset(
                    -3 + this.mob.getRandom().nextInt(7),
                    1,
                    -3 + this.mob.getRandom().nextInt(7)
            );
            Mob summonedMob = summonType.create(level);
            if (summonedMob != null) {
                summonedMob.moveTo(spawnPos, 0.0F, 0.0F);
                summonedMob.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), MobSpawnType.MOB_SUMMONED, null, null);
                level.addFreshEntityWithPassengers(summonedMob);
                summonedMob.setTarget(target);
            }
        }
    }
}
