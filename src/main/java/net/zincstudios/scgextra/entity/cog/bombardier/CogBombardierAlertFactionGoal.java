package net.zincstudios.scgextra.entity.cog.bombardier;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import net.zincstudios.scgextra.entity.Faction;
import net.zincstudios.scgextra.entity.asgharian.AbilityGoal;

import java.util.EnumSet;
import java.util.List;

public class CogBombardierAlertFactionGoal extends AbilityGoal<CogBombardierEntity> {

    protected boolean aggroed = false;

    public CogBombardierAlertFactionGoal(CogBombardierEntity mob) {
        super(mob);
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean activate() {
        this.mob.triggerAnim("behaviour", "alert");
        return false;
    }

    @Override
    public boolean canUse() {
        if (super.canUse()) {
            if (!this.aggroed) {
                this.alertFaction();
                this.aggroed = true;
                return true;
            } else {
                this.resetCooldown();
                return this.alertFaction();
            }
        }
        return false;
    }

    public boolean alertFaction() {
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
        return alertedSomeone;
    }

}
