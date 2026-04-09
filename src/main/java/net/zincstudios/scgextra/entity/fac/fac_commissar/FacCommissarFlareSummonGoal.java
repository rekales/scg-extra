package net.zincstudios.scgextra.entity.fac.fac_commissar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

public class FacCommissarFlareSummonGoal extends Goal {

    private final FacCommissarEntity mob;
    private final int cooldownDuration;
    private final int summonDelay;
    private final EntityType<? extends Mob>[] summonTypes;

    private long summonTrigger = -1;
    private long cooldownEnd = 0;

    @SafeVarargs
    public FacCommissarFlareSummonGoal(FacCommissarEntity mob, int cooldownDuration, int summonDelay,
                                       EntityType<? extends Mob>... summonTypes) {
        this.mob = mob;
        this.cooldownDuration = cooldownDuration;
        this.summonDelay = summonDelay;
        this.summonTypes = summonTypes;
    }

    @Override
    public boolean canUse() {
        return this.mob.getTarget() instanceof Player;
    }

    @Override
    public void start() {
        this.cooldownEnd = this.mob.level().getGameTime() + this.cooldownDuration / 2;
    }

    @Override
    public void stop() {
        super.stop();
        this.summonTrigger = -1;
    }

    @Override
    public void tick() {
        if (this.mob.level().getGameTime() > this.cooldownEnd) {
            this.cooldownEnd = this.mob.level().getGameTime() + this.cooldownDuration;
            this.summonTrigger = this.mob.level().getGameTime() + this.summonDelay;
            this.mob.startFlareLock();
            this.mob.triggerAnim("behaviour", "flare");
            this.spawnFlareBurst();
        }

        if (this.summonTrigger != -1) {
            this.mob.getNavigation().stop();
            this.spawnFlareParticles();
        }

        if (this.summonTrigger != -1 && this.mob.level().getGameTime() > this.summonTrigger) {
            this.summonTrigger = -1;
            summonMobs();
        }
    }

    @SuppressWarnings("deprecation")
    private void summonMobs() {
        LivingEntity target = this.mob.getTarget();
        if (target != null && this.mob.level() instanceof ServerLevel level) {
            for (int i = 0; i < 3; ++i) {
                EntityType<? extends Mob> summonType = this.summonTypes[this.mob.getRandom().nextInt(this.summonTypes.length)];
                BlockPos blockPos = this.mob.blockPosition().offset(
                        -2 + this.mob.getRandom().nextInt(5),
                        1,
                        -2 + this.mob.getRandom().nextInt(5)
                );
                Mob summonedMob = summonType.create(level);
                if (summonedMob != null) {
                    summonedMob.moveTo(blockPos, 0.0F, 0.0F);
                    summonedMob.finalizeSpawn(level, level.getCurrentDifficultyAt(blockPos), MobSpawnType.MOB_SUMMONED, null, null);
                    level.addFreshEntityWithPassengers(summonedMob);
                }
            }
        }
    }

    private void spawnFlareBurst() {
        if (!(this.mob.level() instanceof ServerLevel level)) {
            return;
        }
        double x = this.mob.getX();
        double y = this.mob.getY() + 1.8D;
        double z = this.mob.getZ();
        level.sendParticles(ParticleTypes.FIREWORK, x, y, z, 28, 0.6D, 0.25D, 0.6D, 0.02D);
    }

    private void spawnFlareParticles() {
        if (!(this.mob.level() instanceof ServerLevel level)) {
            return;
        }

        double x = this.mob.getX();
        double y = this.mob.getY() + 1.8D;
        double z = this.mob.getZ();
        level.sendParticles(ParticleTypes.FIREWORK, x, y, z, 7, 0.28D, 0.1D, 0.28D, 0.01D);
    }
}
