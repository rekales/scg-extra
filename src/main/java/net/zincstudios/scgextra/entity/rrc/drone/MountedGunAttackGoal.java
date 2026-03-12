package net.zincstudios.scgextra.entity.rrc.drone;

import net.minecraft.sounds.SoundSource;
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
        return this.mob.position().add(0, 1, 0);
    }

    public void triggerGunFlash() {}

    @Override
    public boolean canUse() {
        if(this.cooldown>0)this.cooldown--;
        return this.mob.getTarget() != null && this.cooldown==0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.tick<=1200;
    }

    @Override
    public void start() {
        this.tick = 0;
        super.start();
        this.cooldown = 100;
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
        if(this.tick <= 1200){
            this.mob.lookAt(this.mob.getTarget(), 20, 20);
            this.mob.setDeltaMovement(0, this.mob.getDeltaMovement().y, 0);
        }
        LivingEntity target = this.mob.getTarget();
        
        if (this.tick <= 1200 && this.tick%2==0) {
            if (target != null) {
                fireGun(target);
            }
        }
        this.tick++;
    }

    private void fireGun(LivingEntity target) {
        Vec3 spawnVec = this.getProjectileSpawnPos();
        EnemyProjectileEntity bolt = new ArmoredWhaleProjectileEntity(this.mob.level(), this.mob);
        bolt.setPos(spawnVec);
        double dx = target.getX() - spawnVec.x;
        double dy = target.getEyeY() - spawnVec.y;
        double dz = target.getZ() - spawnVec.z;
        bolt.shoot(dx, dy, dz, 3.0F, 1.5F);
        this.mob.level().addFreshEntity(bolt);
        this.mob.level().playSound(null, spawnVec.x, spawnVec.y, spawnVec.z, ModSounds.BRUISER_SILENCED_FIRE.get(), SoundSource.HOSTILE, 0.8F, 1.2F);
        this.triggerGunFlash();
    }
}