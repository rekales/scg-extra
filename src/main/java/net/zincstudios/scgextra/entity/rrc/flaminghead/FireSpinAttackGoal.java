package net.zincstudios.scgextra.entity.rrc.flaminghead;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.goal.Goal;
import net.zincstudios.scgextra.entity.projectile.FireProjectile;
import net.zincstudios.scgextra.sounds.ModSounds;

public class FireSpinAttackGoal extends Goal{
    private final FlamingHeadEntity parent;
    private final int range;
    private int cooldown = 0;
    private int ticks = 0;
    private int startTicks = 10;
    private SoundEvent[] spinSounds = {
        ModSounds.RRC_FLAMING_HEAD_SPIN_1.get(),
        ModSounds.RRC_FLAMING_HEAD_SPIN_2.get(),
        ModSounds.RRC_FLAMING_HEAD_SPIN_3.get()
    };
    public FireSpinAttackGoal(FlamingHeadEntity mob, int pRange){
        this.parent = mob;
        this.range = pRange;
    }
    @Override
    public boolean canUse() {
        if(this.cooldown>0){
            this.cooldown--;
        }
        return this.cooldown==0 && (this.parent.getTarget() !=null && this.parent.distanceToSqr(this.parent.getTarget())<=(this.range*this.range));
    }
    @Override
    public boolean canContinueToUse() {
        return ticks <= 30;
    }
    @Override
    public void start() {
        super.start();
        this.cooldown = 200;
        this.ticks = 0;
        this.parent.triggerAnim("fAttack", "fire_attack");
    }
    @Override
    public void tick() {
        super.tick();
        ticks++;
        if(ticks<=startTicks){
            if(ticks==startTicks){
                this.parent.playSound(spinSounds[this.parent.getRandom().nextInt(spinSounds.length)], this.parent.getSoundVolume(), 1F);
            }
            return;
        }
        if(this.ticks%5==0){
            for (int i = 0; i < 360; i += 10) {
                double rad = Math.toRadians(i);
                double x = this.parent.getX() + Math.cos(rad) * 8;
                double z = this.parent.getZ() + Math.sin(rad) * 8;
                FireProjectile en = new FireProjectile(
                    this.parent.level(),
                    this.parent
                );
                en.setPos(this.parent.position().add(0, 1.5, 0));
                double dx = x - this.parent.getX();
                double dy = this.parent.getY() - (this.parent.getY()+1.5);
                double dz = z - this.parent.getZ();
                en.shoot(
                    dx,
                    dy,
                    dz,
                    2.5F,
                    0F
                );
                this.parent.level().addFreshEntity(en);
            }
        }
    }
    @Override
    public void stop() {
        super.stop();
    }
}