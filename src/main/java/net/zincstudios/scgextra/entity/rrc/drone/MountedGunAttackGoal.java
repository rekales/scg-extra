package net.zincstudios.scgextra.entity.rrc.drone;

import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
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
        return new Vec3(-1.5,1.7,0).yRot(-this.mob.getYRot() * Mth.DEG_TO_RAD).add(this.mob.position());
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
        this.mob.setDeltaMovement(this.mob.getDeltaMovement());
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
            this.mob.setDeltaMovement(0, this.mob.getDeltaMovement().y, 0);
            this.mob.lookAt(Anchor.EYES, this.mob.getTarget().position());
            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.yBodyRot = this.mob.getYRot();
            if (this.tick%2==0) {
                fireGun(target);
            }
            if(tick % 20 == 0){
                this.mob.triggerAnim("attack", "gun_firing");
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
        bolt.shoot(dx, dy, dz, 3.0F, 1.5F);
        this.mob.level().addFreshEntity(bolt);
        this.mob.level().playSound(null, spawnVec.x, spawnVec.y, spawnVec.z, ModSounds.BRUISER_SILENCED_FIRE.get(), SoundSource.HOSTILE, 0.8F, 1.2F);
        this.triggerGunFlash();
    }
}