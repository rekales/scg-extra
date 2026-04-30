package net.zincstudios.scgextra.entity.asgharian.soulripper;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Vex;
import net.zincstudios.scgextra.entity.asgharian.AbilityGoal;

public class SoulRipperSummonVexGoal extends AbilityGoal<SoulRipperEntity> {

    private boolean summoned = false;

    public SoulRipperSummonVexGoal(SoulRipperEntity mob) {
        super(mob);
    }

    @Override
    public boolean canUse() {
        return super.canUse()
                && this.mob.canSummon()
                && !this.summoned;
    }

    @Override
    public boolean activate() {
        this.summonVexes();
        this.summoned = true;
        return false;
    }

    private void summonVexes() {
        if (this.mob.level() instanceof ServerLevel serverlevel) {
            for(int i = 0; i < 3; ++i) {
                BlockPos blockpos = this.mob.blockPosition().offset(-2 + this.mob.getRandom().nextInt(5), 1, -2 + this.mob.getRandom().nextInt(5));
                Vex vex = EntityType.VEX.create(this.mob.level());
                if (vex != null) {
                    vex.moveTo(blockpos, 0.0F, 0.0F);
                    vex.finalizeSpawn(serverlevel, this.mob.level().getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
                    vex.setOwner(this.mob);
                    vex.setBoundOrigin(blockpos);
                    vex.setLimitedLife(20 * (30 + this.mob.getRandom().nextInt(90)));
                    serverlevel.addFreshEntityWithPassengers(vex);
                }
            }
        }
    }
}
