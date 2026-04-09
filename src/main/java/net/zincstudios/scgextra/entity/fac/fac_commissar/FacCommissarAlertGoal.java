package net.zincstudios.scgextra.entity.fac.fac_commissar;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.zincstudios.scgextra.entity.Faction;

import java.util.List;

public class FacCommissarAlertGoal extends Goal {

    private final FacCommissarEntity mob;
    private final boolean instantAlert;
    private final int cooldownDuration;
    private long cooldownEnd = 0;

    public FacCommissarAlertGoal(FacCommissarEntity mob, int cooldownDuration, boolean instantAlert) {
        this.mob = mob;
        this.cooldownDuration = cooldownDuration;
        this.instantAlert = instantAlert;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void start() {
        if (this.instantAlert) {
            alertFaction();
            this.cooldownEnd = this.mob.level().getGameTime() + this.cooldownDuration;
        } else {
            this.cooldownEnd = this.mob.level().getGameTime() + this.cooldownDuration / 2;
        }
    }

    @Override
    public void tick() {
        if (this.mob.level().getGameTime() > this.cooldownEnd) {
            alertFaction();
            this.cooldownEnd = this.mob.level().getGameTime() + this.cooldownDuration;
        }
    }

    private void alertFaction() {
        double range = 32.0D;
        AABB aabb = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(range, 20.0D, range);

        List<? extends Mob> friendlies = this.mob.level().getEntities(this.mob, aabb, entity -> {
            if (entity instanceof Mob mobEntity) {
                return Faction.isFriendlies(this.mob, mobEntity);
            }
            return false;
        }).stream().map(entity -> (Mob) entity).toList();

        for (Mob friendly : friendlies) {
            if (friendly != this.mob && friendly.getTarget() == null) {
                friendly.setTarget(this.mob.getTarget());
            }
        }
    }
}
