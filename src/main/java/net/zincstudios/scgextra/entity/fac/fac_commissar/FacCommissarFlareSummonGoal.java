package net.zincstudios.scgextra.entity.fac.fac_commissar;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.entity.common.goal.FlareSummonGoal;
import top.ribs.scguns.entity.projectile.RaidFlareEntity;

public class FacCommissarFlareSummonGoal extends FlareSummonGoal {
    private static final String VISUAL_FLARE_ID = "fac_commissar_visual";
    private static final int FLARE_LAUNCH_DELAY_TICKS = 25;
    private long pendingLaunchTime = -1L;

    @SafeVarargs
    public FacCommissarFlareSummonGoal(FacCommissarEntity mob, int cooldownDuration, int summonDelay,
                                       EntityType<? extends Mob>... summonTypes) {
        super(mob, cooldownDuration, summonDelay, summonTypes);
    }

    @Override
    protected String getAnimationTrigger() {
        return "flare";
    }

    @Override
    protected boolean shouldUseFlarePistolInHand() {
        return false;
    }

    @Override
    protected void onFlareTriggered() {
        if (this.mob.level().isClientSide()) {
            return;
        }
        this.pendingLaunchTime = this.mob.level().getGameTime() + FLARE_LAUNCH_DELAY_TICKS;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.pendingLaunchTime == -1L || this.mob.level().isClientSide()) {
            return;
        }
        if (this.mob.level().getGameTime() < this.pendingLaunchTime) {
            return;
        }
        this.pendingLaunchTime = -1L;

        RaidFlareEntity flare = new RaidFlareEntity(this.mob.level(), this.mob, VISUAL_FLARE_ID);
        flare.setPos(this.mob.getX(), this.mob.getY() + 2.1D, this.mob.getZ());
        Vec3 look = this.mob.getLookAngle();
        flare.setDeltaMovement(look.x * 0.15D, 1.55D, look.z * 0.15D);
        this.mob.level().addFreshEntity(flare);
        this.mob.level().playSound(null, this.mob.getX(), this.mob.getY(), this.mob.getZ(),
                SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.HOSTILE, 1.2F, 0.95F);
    }

    @Override
    public void stop() {
        super.stop();
        this.pendingLaunchTime = -1L;
    }
}
