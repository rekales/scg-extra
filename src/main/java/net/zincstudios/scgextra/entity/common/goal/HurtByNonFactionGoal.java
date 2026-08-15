package net.zincstudios.scgextra.entity.common.goal;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.zincstudios.scgextra.entity.Faction;

public class HurtByNonFactionGoal extends HurtByTargetGoal {

    public HurtByNonFactionGoal(PathfinderMob mob, Class<?>... toIgnoreDamage) {
        super(mob, toIgnoreDamage);
    }

    @Override
    public boolean canUse() {
        if (this.mob.getLastHurtByMob() instanceof Player player && (player.isCreative() || player.isSpectator())) {
            return false;
        }
        // Avoid retaliation from friendly fire
        if (this.mob.getLastHurtByMob() != null && Faction.isFriendlies(this.mob, this.mob.getLastHurtByMob())) {
            return false;
        }
        return super.canUse();
    }
}
