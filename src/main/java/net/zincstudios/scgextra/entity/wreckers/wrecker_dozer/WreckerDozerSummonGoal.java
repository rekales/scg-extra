package net.zincstudios.scgextra.entity.wreckers.wrecker_dozer;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.zincstudios.scgextra.entity.wreckers.WreckersEntities;

public class WreckerDozerSummonGoal extends Goal {

    private final WreckerDozerEntity dozer;
    private final int cooldownTicks;
    private final int count;
    private long cooldownEnd;

    public WreckerDozerSummonGoal(WreckerDozerEntity dozer, int cooldownTicks, int count) {
        this.dozer = dozer;
        this.cooldownTicks = cooldownTicks;
        this.count = count;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.dozer.getTarget();
        return target != null && target.isAlive() && !this.dozer.isStunned();
    }

    @Override
    public void start() {
        this.cooldownEnd = this.dozer.level().getGameTime() + this.cooldownTicks / 2L;
    }

    @Override
    public void tick() {
        if (this.dozer.isStunned()) {
            return;
        }
        long now = this.dozer.level().getGameTime();
        if (now >= this.cooldownEnd) {
            this.cooldownEnd = now + this.cooldownTicks;
            this.summon();
        }
    }

    @SuppressWarnings("deprecation")
    private void summon() {
        LivingEntity target = this.dozer.getTarget();
        if (target == null || !(this.dozer.level() instanceof ServerLevel level)) {
            return;
        }
        for (int i = 0; i < this.count; i++) {
            BlockPos pos = this.dozer.blockPosition().offset(
                    -3 + this.dozer.getRandom().nextInt(7), 3, -3 + this.dozer.getRandom().nextInt(7));
            Mob helicube = WreckersEntities.WRECKER_HELICUBE.get().create(level);
            if (helicube != null) {
                helicube.moveTo(pos, 0.0F, 0.0F);
                helicube.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null, null);
                level.addFreshEntityWithPassengers(helicube);
                helicube.setTarget(target);
            }
        }
    }
}
