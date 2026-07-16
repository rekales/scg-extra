package net.zincstudios.scgextra.entity.neutral.overworld.big_lump;

import net.zincstudios.scgextra.entity.projectile.BigLumpProjectileEntity;
import net.zincstudios.scgextra.entity.common.gun.SimulatedGun;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import top.ribs.scguns.entity.projectile.EnemyProjectileEntity;
import top.ribs.scguns.init.ModSounds;

public class BigLumpGunAttackGoal extends Goal{
    protected final int range;
    protected final BigLumpEntity mob;

    private int cooldown = 0;
    private int tick = 0;

    public BigLumpGunAttackGoal(BigLumpEntity mob, int range) {
        this.mob = mob;
        this.range = range;
    }

    public void triggerGunFlash() {}

    @Override
    public boolean canUse() {
        if(this.cooldown>0)this.cooldown--;
        return this.mob.getTarget() != null && this.cooldown==0 && this.mob.distanceToSqr(this.mob.getTarget())<=(range*range);
    }

    @Override
    public boolean canContinueToUse() {
        return this.tick<=50;
    }

    @Override
    public void start() {
        this.mob.getNavigation().stop();
        this.tick = 0;
        this.cooldown = 60+50;
        super.start();
    }
    
    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if(target != null){
            this.mob.getNavigation().stop();
            this.mob.getLookControl().setLookAt(
                this.mob.getTarget().getX(), 
                this.mob.getTarget().getY(), 
                this.mob.getTarget().getZ()
            );
            double dx = target.getX() - mob.getX();
            double dz = target.getZ() - mob.getZ();
            float yaw = (float)(Math.atan2(dz, dx) * (180F / Math.PI)) - 90F;
            this.mob.setYBodyRot(yaw);
            this.mob.setYRot(yaw);
            if (this.tick%2==0) {
                this.mob.getCustomGun().tickFire(this.mob, SimulatedGun.getCenterMassPos(target), this.mob.getInaccuracy(), true);
            }
            if(tick == 0){
                this.mob.triggerAnim("controller", "shoot_attack");
            }
            if(tick % 20 == 0){
                this.mob.lowerInaccuracy();
            }
        }else{this.stop();}
        this.tick++;
    }

    public Vec3 getProjectileSpawnPos() {
        return new Vec3(0,1.5,2).yRot(-this.mob.getYRot() * Mth.DEG_TO_RAD).add(this.mob.position());
    }
}