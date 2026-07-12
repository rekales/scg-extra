package net.zincstudios.scgextra.entity.neutral.overworld.mutant_bat;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.goal.Goal;
import net.zincstudios.scgextra.sounds.NeutralSounds;

public class MutantBatScreamAttackGoal extends Goal{

    private final MutantBatEntity entity;
    private int cooldown = 0;
    private int ticks = 0;

    public MutantBatScreamAttackGoal(MutantBatEntity mob){
        this.entity = mob;
    }

    @Override
    public boolean canUse() {
        if(this.cooldown>0)this.cooldown--;
        return this.cooldown == 0 && this.entity.getTarget()!=null && this.entity.distanceToSqr(this.entity.getTarget())<=36;
    }

    @Override
    public boolean canContinueToUse() {
        return this.ticks <= 50 && this.entity.getTarget()!=null;
    }

    @Override
    public void start() {
        super.start();
        this.cooldown = 170;
        this.ticks = 0;
        this.entity.setScreaming(true);
        this.entity.getNavigation().stop();
    }

    @Override
    public void tick() {
        super.tick();
        this.entity.getNavigation().stop();
        if(ticks == 1){
            this.entity.triggerAnim("controller", "scream_attack");
        }
        if(ticks == 10){
            this.entity.playSound(NeutralSounds.MUTANT_BAT_SCREAM.get(), 3, this.entity.getVoicePitch());
        }
        if(ticks == 26){
            if(this.entity.getTarget()!=null){
                this.entity.getTarget().hurt(this.entity.damageSources().generic(), 3);
                this.entity.getTarget().addEffect(new MobEffectInstance(MobEffects.CONFUSION, 120));
                this.entity.getTarget().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120));
            }
        }
        this.ticks++;
    }

    @Override
    public void stop() {
        super.stop();
        this.entity.setScreaming(false);
    }
}