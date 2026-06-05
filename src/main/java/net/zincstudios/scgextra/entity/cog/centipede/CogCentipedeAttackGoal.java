package net.zincstudios.scgextra.entity.cog.centipede;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.zincstudios.scgextra.entity.asgharian.GoalState;
import net.zincstudios.scgextra.entity.asgharian.SimpleGunAttackGoal;
import top.ribs.scguns.Config;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.entity.ai.AIGunEvent;
import top.ribs.scguns.init.ModItems;
import top.ribs.scguns.item.GunItem;

import java.util.List;
import java.util.Objects;

public class CogCentipedeAttackGoal extends SimpleGunAttackGoal<CogCentipedeEntity> {

    public static final GoalState SLAM_STATE = new GoalState("cog_centipede_slam_state");

    private static final int SLAM_DAMAGE_DELAY = 12;  // match with animation
    private static final int SLAM_FULL_DURATION = 25;  // match with animation
    private static final int SLAM_DAMAGE_RANGE = 3;

    private final int slamInterval;

    private int slamTicks = 0;
    private int slamCooldown = 0;

    public CogCentipedeAttackGoal(CogCentipedeEntity mob, int slamCooldownTicks) {
        super(mob);
        this.slamInterval = slamCooldownTicks;
    }

    @Override
    public void start() {
        this.mob.setAggressive(true);
        this.seeTime = 0;
        this.slamTicks = 0;
    }

    @Override
    public void tick() {
        this.handleSlamAttack();

        if (!Objects.equals(this.getGoalState(), SLAM_STATE)) {
            super.tick();
        }
    }

    protected void handleSlamAttack() {
        if (Objects.equals(this.getGoalState(), FIRING_STATE)) return;
        if (this.slamCooldown > 0) {
            this.slamCooldown--;
            return;
        }

        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            this.checkAndPerformAttack(target);

            if (Objects.equals(this.getGoalState(), SLAM_STATE)) {
                this.slamTicks++;
                if (this.slamTicks == SLAM_DAMAGE_DELAY) {
                    AABB aabb = this.mob.getBoundingBox().inflate(SLAM_DAMAGE_RANGE);
                    List<LivingEntity> entities = this.mob.level().getEntitiesOfClass(LivingEntity.class, aabb);
                    for (LivingEntity entity : entities) {
                        if (entity == this.mob) continue;
                        this.meleeDamageTarget(entity);
                    }
                } else if (this.slamTicks >= SLAM_FULL_DURATION) {
                    this.slamCooldown = this.slamInterval;
                    this.setGoalState(AIMING_STATE);
                }
            }
        }
    }

    protected void checkAndPerformAttack(LivingEntity target) {
        if (Objects.equals(this.getGoalState(), SLAM_STATE)) return;  // don't do another attack if already performing

        if (this.mob.position().closerThan(target.position(), this.mob.getBbWidth()/2 + SLAM_DAMAGE_RANGE)) {
            this.setGoalState(SLAM_STATE);
            this.slamTicks = 0;
        }
    }

    protected void meleeDamageTarget(LivingEntity target) {
        this.mob.doHurtTarget(target);
    }

    protected double getAttackReachSqr(LivingEntity attackTarget) {
        return (this.mob.getBbWidth() * 2.0F * this.mob.getBbWidth() * 2.0F + attackTarget.getBbWidth());
    }

    @Override
    protected boolean isHoldingGun() {
        return true;
    }

    @Override
    protected void fireGun(LivingEntity target) {
        ItemStack itemStack = new ItemStack(ModItems.LIBERTAS.get());
        // TODO: custom projectile
        if (itemStack.getItem() instanceof GunItem gunItem) {
            Gun gun = gunItem.getModifiedGun(itemStack);
            AIGunEvent.performGunAttack(this.mob, target, itemStack, gun, this.getAccuracyModifier());

            ResourceLocation fireSound = gun.getSounds().getFire();
            if (fireSound != null) {
                double posX = this.mob.getX();
                double posY = this.mob.getY() + (double)this.mob.getEyeHeight();
                double posZ = this.mob.getZ();
                float volume = (float) Config.COMMON.gameplay.mobGunfireVolume.get();
                float pitch = 0.9F + this.mob.level().random.nextFloat() * 0.2F;
                this.mob.level().playSound(null, posX, posY, posZ, SoundEvent.createVariableRangeEvent(fireSound), SoundSource.HOSTILE, volume - 0.5F, pitch);
            }
        }
    }
}
