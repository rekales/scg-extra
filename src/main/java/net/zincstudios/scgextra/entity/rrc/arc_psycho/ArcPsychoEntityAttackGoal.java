package net.zincstudios.scgextra.entity.rrc.arc_psycho;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import top.ribs.scguns.entity.projectile.LightningProjectileEntity;
import top.ribs.scguns.init.ModEntities;
import top.ribs.scguns.init.ModItems;
import top.ribs.scguns.init.ModSounds;

public class ArcPsychoEntityAttackGoal extends Goal{
    private final ArcPsychoEntity parent;
    private int cooldown = 0;
    private final int cooldownDur;
    private final int dist;
    private final float shootingSpeed;
    public ArcPsychoEntityAttackGoal(ArcPsychoEntity mob, int cooldown, int dist, float shootingSpeed){
        this.parent = mob;
        this.cooldownDur = cooldown;
        this.dist = dist;
        this.shootingSpeed = shootingSpeed;
    }
    @Override
    public boolean canUse() {
        if(this.cooldown>0)this.cooldown--;
        return this.parent.getTarget()!=null && this.cooldown==0 && this.parent.distanceToSqr(this.parent.getTarget().getX(), this.parent.getY(), this.parent.getTarget().getZ())<this.dist*this.dist;
    }
    @Override
    public boolean canContinueToUse() {
        return false;
    }
    @Override
    public void start() {
        super.start();
        this.cooldown = this.cooldownDur;
        LivingEntity target = this.parent.getTarget();
        if (target == null) return;
        double dx = target.getX() - this.parent.getX();
        double dy = (target.getY()-1) - this.parent.getY();
        double dz = target.getZ() - this.parent.getZ();
        double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
        this.parent.triggerAnim("attack", "attack");
        LightningProjectileEntity en = new LightningProjectileEntity(
            ModEntities.PROJECTILE.get(),
            this.parent.level(),
            this.parent,
            new ItemStack(ModItems.TESLOCK_RIFLE.get()),
            ModItems.TESLOCK_RIFLE.get(),
            ModItems.TESLOCK_RIFLE.get().getGun()
        );
        en.setPos(this.parent.position().add(0, 3, 0));
        en.setDeltaMovement(
            dx / dist * this.shootingSpeed,
            dy / dist * this.shootingSpeed,
            dz / dist * this.shootingSpeed
        );
        this.parent.level().addFreshEntity(en);
        this.parent.level().playSound(null, this.parent.getX(), this.parent.getY(), this.parent.getZ(), ModSounds.SHOCK_FIRE.get(), SoundSource.HOSTILE, 0.8F, 1.2F);
        if(!this.parent.level().isClientSide()){
            ServerLevel sl = (ServerLevel) this.parent.level();
            sl.sendParticles(
                ParticleTypes.ELECTRIC_SPARK,
                this.parent.getX(),
                this.parent.getY(),
                this.parent.getZ(),
                40,
                0.8,
                0.8,
                0.8,
                0.1
            );
        }
    }
}