package net.zincstudios.scgextra.entity.common.ai;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.zincstudios.scgextra.Faction;

public class HurtByNonFactionGoal extends HurtByTargetGoal {

    public HurtByNonFactionGoal(PathfinderMob mob, Class<?>... toIgnoreDamage) {
        super(mob, toIgnoreDamage);
    }

    @Override
    public boolean canUse() {
        // Avoid retaliation from friendly fire
        if (this.mob.getLastHurtByMob() != null && Faction.isFriendlies(this.mob, this.mob.getLastHurtByMob())) {
            return false;
        }
        return super.canUse();
    }
}
