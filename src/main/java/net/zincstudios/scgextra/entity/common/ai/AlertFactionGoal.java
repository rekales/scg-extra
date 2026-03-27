package net.zincstudios.scgextra.entity.common.ai;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.zincstudios.scgextra.Faction;
import software.bernie.geckolib.animatable.GeoEntity;

import java.util.List;


public class AlertFactionGoal extends Goal {

    private final PathfinderMob mob;
    private final boolean instantAlert;
    private final int cooldownDuration;
    private long cooldownEnd = 0;  // level timestamp

    public AlertFactionGoal(PathfinderMob mob, int cooldownDuration) {
        this(mob, cooldownDuration, false);
    }

    public AlertFactionGoal(PathfinderMob mob, int cooldownDuration, boolean instantAlert) {
        this.mob = mob;
        this.cooldownDuration = cooldownDuration;
        this.instantAlert = instantAlert;
    }

    @Override
    public boolean canUse() {
        LivingEntity livingentity = this.mob.getTarget();
        return livingentity != null && livingentity.isAlive();
    }

    @Override
    public void start() {
        if (this.instantAlert) {
            alertFaction();
            this.cooldownEnd = this.mob.level().getGameTime() + this.cooldownDuration;
        } else {
            this.cooldownEnd = this.mob.level().getGameTime() + this.cooldownDuration/2;  // Half cooldown at start
        }
    }

    @Override
    public void tick() {
        if (this.mob.level().getGameTime() > this.cooldownEnd) {
            alertFaction();
            this.cooldownEnd = this.mob.level().getGameTime() + this.cooldownDuration;
        }
    }

    // Copied and heavily modified from HurtByTargetGoal.alertOthers
    public void alertFaction() {
        double range = 32; //TODO: config
        AABB aabb = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(range, 20.0D, range);

        List<? extends Mob> list = this.mob.level().getEntities(this.mob, aabb,
                entity -> {
                    if (entity instanceof PathfinderMob pathfinderMob) {
                        return Faction.isFriendlies(this.mob, pathfinderMob);
                    }
                    return false;
                }
        ).stream().map(entity -> (Mob) entity).toList();

        boolean alertedSomeone = false;
        for (Mob mob : list) {
            if (this.mob != mob && mob.getTarget() == null) {
                mob.setTarget(this.mob.getTarget());
                alertedSomeone = true;
            }
        }

        if (alertedSomeone && this.mob instanceof GeoEntity geoEntity) {
            geoEntity.triggerAnim("behaviour", "alert");
        }
    }
}
