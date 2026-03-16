package net.zincstudios.scgextra.entity.common.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.animatable.GeoEntity;

public class FlareSummonGoal extends Goal {

    protected final PathfinderMob mob;
    protected final int cooldownTime;
    protected final int chargeTime;
    protected final EntityType<? extends Mob>[] summonTypes;
    protected int activeTicks = 0;
    protected int cooldownTicks = 0;

    @SafeVarargs
    public FlareSummonGoal(PathfinderMob mob, int cooldownTime, int chargeTime, EntityType<? extends Mob>... summonTypes) {
        this.mob = mob;
        this.cooldownTime = cooldownTime;
        this.chargeTime = chargeTime;
        this.summonTypes = summonTypes;
    }


    @Override
    public boolean canUse() {
        // Against player targets only, seems excessive when used on non-player targets
        return this.mob.getTarget() instanceof Player;
    }

    @Override
    public void stop() {
        super.stop();
        this.activeTicks = 0;
        this.cooldownTicks = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @SuppressWarnings({"deprecation", "OverrideOnly"})  // TODO: figure out a solution later
    @Override
    public void tick() {
        if (this.activeTicks > 0) {
            this.activeTicks--;
        }
        if (this.activeTicks == 1) {
            LivingEntity target = this.mob.getTarget();
            if (target != null && this.mob.level() instanceof ServerLevel level) {

                for(int i = 0; i < 3; ++i) {
                    EntityType<? extends Mob> summonType = summonTypes[this.mob.getRandom().nextInt(summonTypes.length)];
                    BlockPos blockpos = this.mob.blockPosition().offset(-2 + this.mob.getRandom().nextInt(5), 1, -2 + this.mob.getRandom().nextInt(5));
                    Mob summonedMob = summonType.create(level);
                    if (summonedMob != null) {
                        summonedMob.moveTo(blockpos, 0.0F, 0.0F);
                        summonedMob.finalizeSpawn(level, level.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
//                        summonedMob.setOwner(this.mob);
//                        summonedMob.setBoundOrigin(blockpos);
//                        summonedMob.setLimitedLife(20 * (30 + this.mob.getRandom().nextInt(90)));
                        level.addFreshEntityWithPassengers(summonedMob);
                    }
                }
            }
        }

        if (this.cooldownTicks > 0) {
            this.cooldownTicks--;
        } else {
            this.cooldownTicks = cooldownTime + this.chargeTime;
            this.activeTicks = this.chargeTime;  // match with animation frames
            if (this.mob instanceof GeoEntity geoEntity) {
                geoEntity.triggerAnim("throw", "action");
            }
        }
    }
}