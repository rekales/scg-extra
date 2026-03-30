package net.zincstudios.scgextra.entity.rrc.drone;

import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.entity.projectile.ArmoredWhaleProjectileEntity;
import top.ribs.scguns.entity.projectile.EnemyProjectileEntity;
import top.ribs.scguns.init.ModSounds;

public class MountedGunAttackGoal extends Goal{
    protected final float range;
    protected final DroneEntity mob;

    private int cooldown = 0;
    private int tick = 0;

    public MountedGunAttackGoal(DroneEntity mob, float range) {
        this.mob = mob;
        this.range = range;
    }

    public Vec3 getProjectileSpawnPos() {
        return new Vec3(-1.3,1.7,3).yRot(-this.mob.getYRot() * Mth.DEG_TO_RAD).add(this.mob.position());
    }

    public void triggerGunFlash() {}

    @Override
    public boolean canUse() {
        if(this.cooldown>0)this.cooldown--;
        return this.mob.getTarget() != null && this.cooldown==0 && !this.mob.isStunned();
    }

    @Override
    public boolean canContinueToUse() {
        return this.tick<=80 && !this.mob.isStunned();
    }

    @Override
    public void start() {
        this.mob.getNavigation().stop();
        this.tick = 0;
        this.cooldown = 100;
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
            if (this.tick%2==0) {
                fireGun(target);
            }
            if(tick == 0){
                this.mob.triggerAnim("attack", "gun_firing");
            }
            if(tick % 20 == 0){
                this.mob.lowerInaccuracy();
            }
        }else{this.stop();}
        this.tick++;
    }

    private void fireGun(LivingEntity target) {
        Vec3 spawnVec = this.getProjectileSpawnPos();
        EnemyProjectileEntity bolt = new ArmoredWhaleProjectileEntity(this.mob.level(), this.mob);
        bolt.setPos(spawnVec);
        double dx = target.getX() - spawnVec.x;
        double dy = target.getEyeY() - spawnVec.y;
        double dz = target.getZ() - spawnVec.z;
        bolt.shoot(dx, dy, dz, 3.0F, this.mob.getInaccuracy());
        this.mob.level().addFreshEntity(bolt);
        this.mob.level().playSound(null, spawnVec.x, spawnVec.y, spawnVec.z, ModSounds.BRUISER_SILENCED_FIRE.get(), SoundSource.HOSTILE, 0.8F, 1.2F);
        this.triggerGunFlash();
    }
}