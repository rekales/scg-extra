package net.zincstudios.scgextra.entity.common.ai;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.zincstudios.scgextra.entity.common.Stunnable;
import software.bernie.geckolib.animatable.GeoEntity;
import top.ribs.scguns.init.ModEffects;

import java.util.EnumSet;

// NOTE: should we transfer the logic to this goal instead and only use the interface for hooks?
public class StunnedGoal<T extends PathfinderMob & Stunnable> extends Goal {

    // Only relevant for GeoEntities that has recovery triggers anims.
    private final int endAnimDuration;

    protected T mob;
    private int stunTimer = 0;
    private int headshotCounter = 0;

    public StunnedGoal(T mob) {
        this.mob = mob;
        this.endAnimDuration = -1;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public StunnedGoal(T mob, int endAnimDuration) {
        this.mob = mob;
        this.endAnimDuration = endAnimDuration;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        this.mob.getNavigation().stop();
        if (this.mob instanceof GeoEntity geoEntity) {
            geoEntity.triggerAnim("behaviour", "stun");
        }
    }

    @Override
    public void stop() {
        this.stunTimer = 0;
        // These effects causes panic by the GunAttackGoal
        this.mob.removeEffect(ModEffects.DEAFENED.get());
        this.mob.removeEffect(ModEffects.BLINDED.get());
    }

    @Override
    public boolean canUse() {
        return this.stunTimer > 0;
    }

    public void stun(int stunTicks) {
        this.stunTimer = stunTicks;
    }

    public int getStunTicksLeft() {
        return this.stunTimer;
    }

    @Override
    public void tick() {
        this.stunTimer--;
        this.mob.getNavigation().stop();

        if (this.endAnimDuration >= 0 && this.stunTimer == endAnimDuration && this.mob instanceof GeoEntity geoEntity) {
            geoEntity.triggerAnim("behaviour", "end_stun");
        }
    }

    @SuppressWarnings("unused")
    public void handleHeadshot(DamageSource source, float amount) {
        this.headshotCounter++;

        // TODO: config headshot count
        if (this.headshotCounter >= 5) {
            this.mob.stun(this.mob.getDefaultStunDuration());
            this.headshotCounter = 0;
        }
    }
}
