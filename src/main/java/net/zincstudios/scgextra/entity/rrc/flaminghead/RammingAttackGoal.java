package net.zincstudios.scgextra.entity.rrc.flaminghead;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import software.bernie.geckolib.animatable.GeoEntity;

// Designed around FlamingHeadEntity, make generic later if needed using behaviour augmentation
public class RammingAttackGoal extends Goal {

    protected final PathfinderMob mob;
    private final int cooldownDuration;
    private final float ramMaxDistance;
    private long cooldownEnd = 0;  // level timestamp

    public RammingAttackGoal(PathfinderMob mob, int cooldownDuration, float ramMaxDistance) {
        this.mob = mob;
        this.cooldownDuration = cooldownDuration;
        this.ramMaxDistance = ramMaxDistance;
    }

    @Override
    public boolean canUse() {
        LivingEntity livingentity = this.mob.getTarget();
        return livingentity != null && livingentity.isAlive();
    }

    @Override
    public void start() {
        this.cooldownEnd = this.mob.level().getGameTime() + this.cooldownDuration/2;  // Half cooldown at start
    }

    @Override
    public void tick() {
        if (this.mob.level().getGameTime() > this.cooldownEnd) {
            this.cooldownEnd = this.mob.level().getGameTime() + this.cooldownDuration;
            if (this.mob instanceof GeoEntity geoEntity) {
                geoEntity.triggerAnim("behaviour", "ram_attack");
            }
        }
    }

    public void ramTarget() {

    }
}
