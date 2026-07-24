package net.zincstudios.scgextra.entity.neutral.end.end_scorpion;

import net.zincstudios.scgextra.sounds.NeutralSounds;
import top.ribs.scguns.init.ModEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class EndScorpionStingAttackGoal extends Goal{

    private final EndScorpionEntity mob;
    private int cooldown = 0;
    private int ticks = 0;
    private int hurtDelay = -1;

    public EndScorpionStingAttackGoal(EndScorpionEntity entity){
        this.mob = entity;
    }

    @Override
    public boolean canUse() {
        if(this.cooldown>0)this.cooldown--;
        return this.mob.getTarget()!=null && this.cooldown==0 && this.mob.distanceToSqr(this.mob.getTarget()) <= 9.0D;
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.isStinging()!=false && this.ticks <= 60;
    }

    @Override
    public void start() {
        super.start();
        hurtDelay = -1;
        this.cooldown = 120;
        this.ticks = 0;
        this.mob.setStinging(true);
    }
    @Override
    public void tick() {
        super.tick();
        this.ticks++;
        LivingEntity target = this.mob.getTarget();
        if(target == null)return;
        double reach = this.mob.getAttackReachSqr(target);
        double distToEnemySqr = this.mob.getPerceivedTargetDistanceSquareForMeleeAttack(target);
        if (distToEnemySqr <= reach) {
            if(this.hurtDelay<0){
                this.hurtDelay = 30;
                this.mob.triggerAnim("controller","attack_sting");
                this.mob.playSound(NeutralSounds.END_SCORPION_STING.get());
            }
        }else{
            this.mob.getNavigation().moveTo(target.getX(), target.getY()+1, target.getZ(), 0.6);
            this.mob.getLookControl().setLookAt(target);
        }
        this.hurtDelay--;
        if(this.hurtDelay>0){
            target.sendSystemMessage(Component.literal("HURT DELAY: "+this.hurtDelay));
        }else if (this.hurtDelay == 0) {
            target.hurt(this.mob.damageSources().generic(), 10);
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 120));
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 120));
            target.addEffect(new MobEffectInstance(ModEffects.LACERATED.get(), 120));
            this.stop();
        }
    }
    @Override
    public void stop() {
        super.stop();
        this.mob.setStinging(false);
    }
}
