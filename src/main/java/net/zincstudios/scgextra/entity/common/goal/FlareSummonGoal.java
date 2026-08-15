package net.zincstudios.scgextra.entity.common.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoEntity;
import top.ribs.scguns.init.ModItems;

public class FlareSummonGoal extends Goal {

    protected final PathfinderMob mob;
    private final int cooldownDuration;
    private final int summonDelay;  // match with animation
    private final EntityType<? extends Mob>[] summonTypes;

    private long summonTrigger = -1;  // level timestamp
    private long cooldownEnd = 0;  // level timestamp

    private ItemStack itemInHand = null;
    private boolean usingFlarePistol = false;

    @SafeVarargs
    public FlareSummonGoal(PathfinderMob mob, int cooldownDuration, int summonDelay, EntityType<? extends Mob>... summonTypes) {
        this.mob = mob;
        this.cooldownDuration = cooldownDuration;
        this.summonDelay = summonDelay;
        this.summonTypes = summonTypes;
    }

    @Override
    public boolean canUse() {
        // Against player targets only, seems excessive when used on non-player targets
        return this.mob.getTarget() instanceof Player;
    }

    @Override
    public void start() {
        this.cooldownEnd = this.mob.level().getGameTime() + this.cooldownDuration/2;  // Half cooldown at start
        this.usingFlarePistol = this.shouldUseFlarePistolInHand();
        if (this.usingFlarePistol) {
            this.itemInHand = this.mob.getMainHandItem();
            this.mob.setItemInHand(InteractionHand.MAIN_HAND, ModItems.FLARE_PISTOL.get().getDefaultInstance());
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (this.usingFlarePistol) {
            this.mob.setItemInHand(InteractionHand.MAIN_HAND, this.itemInHand);
            this.itemInHand = null;
            this.usingFlarePistol = false;
        }
    }

    @Override
    public void tick() {
        if (this.mob.level().getGameTime() > this.cooldownEnd) {
            this.cooldownEnd = this.mob.level().getGameTime() + this.cooldownDuration;
            this.summonTrigger = this.mob.level().getGameTime() + this.summonDelay;
            if (this.mob instanceof GeoEntity geoEntity) {
                geoEntity.triggerAnim("behaviour", this.getAnimationTrigger());
            }
            this.onFlareTriggered();
        }

        if (this.summonTrigger != -1 && this.mob.level().getGameTime() > this.summonTrigger) {
            this.summonTrigger = -1;
            summonMobs();
        }
    }

    @SuppressWarnings({"deprecation", "OverrideOnly"})  // TODO: figure out a solution later
    public void summonMobs() {
        LivingEntity target = this.mob.getTarget();
        if (target != null && this.mob.level() instanceof ServerLevel level) {

            for(int i = 0; i < 3; ++i) {
                EntityType<? extends Mob> summonType = summonTypes[this.mob.getRandom().nextInt(summonTypes.length)];
                BlockPos blockpos = this.mob.blockPosition().offset(-2 + this.mob.getRandom().nextInt(5), 1, -2 + this.mob.getRandom().nextInt(5));
                Mob summonedMob = summonType.create(level);
                if (summonedMob != null) {
                    summonedMob.moveTo(blockpos, 0.0F, 0.0F);
                    summonedMob.finalizeSpawn(level, level.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
//                        summonedMob.setLimitedLife(20 * (30 + this.mob.getRandom().nextInt(90)));
                    level.addFreshEntityWithPassengers(summonedMob);
                }
            }
        }
    }

    protected String getAnimationTrigger() {
        return "flare";
    }

    protected boolean shouldUseFlarePistolInHand() {
        return true;
    }

    protected void onFlareTriggered() {
    }
}
