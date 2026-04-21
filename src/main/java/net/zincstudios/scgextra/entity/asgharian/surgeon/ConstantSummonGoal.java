package net.zincstudios.scgextra.entity.asgharian.surgeon;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

public class ConstantSummonGoal extends Goal {

    protected final PathfinderMob mob;
    private final int cooldownDuration;
    private final EntityType<? extends Mob> summonType;

    private long cooldownEnd = 0;  // level timestamp

    public ConstantSummonGoal(PathfinderMob mob, int cooldownDuration, EntityType<? extends Mob> summonType) {
        this.mob = mob;
        this.cooldownDuration = cooldownDuration;
        this.summonType = summonType;
    }

    @Override
    public boolean canUse() {
        // Against player targets only, seems excessive when used on non-player targets
        return this.mob.getTarget() instanceof Player;
    }

    @Override
    public void start() {
        this.cooldownEnd = this.mob.level().getGameTime() + this.cooldownDuration;
    }

    @Override
    public void tick() {
        if (this.mob.level().getGameTime() > this.cooldownEnd) {
            this.cooldownEnd = this.mob.level().getGameTime() + this.cooldownDuration;
            summonMobs();
        }
    }

    @SuppressWarnings({"deprecation", "OverrideOnly"})  // TODO: figure out a solution later
    public void summonMobs() {
        LivingEntity target = this.mob.getTarget();
        if (target != null && this.mob.level() instanceof ServerLevel level) {
            BlockPos blockpos = this.mob.blockPosition().offset(-2 + this.mob.getRandom().nextInt(5), 1, -2 + this.mob.getRandom().nextInt(5));
            Mob summonedMob = this.summonType.create(level);
            if (summonedMob != null) {
                summonedMob.moveTo(blockpos, 0.0F, 0.0F);
                summonedMob.finalizeSpawn(level, level.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
//                        summonedMob.setLimitedLife(20 * (30 + this.mob.getRandom().nextInt(90)));
                level.addFreshEntityWithPassengers(summonedMob);
            }
        }
    }
}
