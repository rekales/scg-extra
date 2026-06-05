package net.zincstudios.scgextra.entity.cog.venator;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class FleeTargetGoal extends Goal {

    protected final PathfinderMob mob;
    protected final double fleeDist;
    @Nullable
    protected Path path;

    public FleeTargetGoal(PathfinderMob mob, double fleeDist) {
        this.mob = mob;
        this.fleeDist = fleeDist;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        if (target != null
                && !target.isDeadOrDying()
                && this.mob.position().closerThan(target.position(), this.fleeDist)) {
            Vec3 posAway = DefaultRandomPos.getPosAway(this.mob, 16, 5, target.position());
            if (posAway == null) {
                return false;
            } else if (target.distanceToSqr(posAway.x, posAway.y, posAway.z) < target.distanceToSqr(this.mob)) {
                return false;
            } else {
                this.path = this.mob.getNavigation().createPath(posAway.x, posAway.y, posAway.z, 0);
                return this.path != null;
            }
        }

        return false;
    }

    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone();
    }

    @Override
    public void start() {
        this.mob.setSprinting(true);
        this.mob.getNavigation().moveTo(this.path, 1);
    }

    @Override
    public void stop() {
        this.mob.setSprinting(false);
    }
}
