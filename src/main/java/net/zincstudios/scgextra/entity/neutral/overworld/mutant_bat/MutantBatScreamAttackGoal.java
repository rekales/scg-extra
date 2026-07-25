package net.zincstudios.scgextra.entity.neutral.overworld.mutant_bat;

import java.util.EnumSet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
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
        if(this.entity.getTarget()!=null){
            this.entity.getLookControl().setLookAt(this.entity.getTarget());
        }
        if(ticks == 1){
            this.entity.triggerAnim("controller", "scream_attack");
        }
        if(ticks == 10){
            this.entity.playSound(NeutralSounds.MUTANT_BAT_SCREAM.get(), 3, this.entity.getVoicePitch());
        }
        if(ticks > 10 && ticks < 20){
            if(ticks % 2 == 0){
                BlockPos start = entity.blockPosition();
                BlockPos end = entity.getTarget().blockPosition();
                double startX = start.getX() + 0.5;
                double startY = start.getY() + 0.5;
                double startZ = start.getZ() + 0.5;
            
                double endX = end.getX() + 0.5;
                double endY = end.getY() + 0.5;
                double endZ = end.getZ() + 0.5;
            
                double dx = endX - startX;
                double dy = endY - startY;
                double dz = endZ - startZ;
            
                double distance = Math.sqrt(dx*dx + dy*dy + dz*dz);
            
                double stepX = dx / distance;
                double stepY = dy / distance;
                double stepZ = dz / distance;
                for (double i = 0; i < distance; i += 0.5) {
                    double px = startX + stepX * i;
                    double py = startY + stepY * i;
                    double pz = startZ + stepZ * i;
                    if (entity.level() instanceof ServerLevel serverLevel)
                        serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, px, py, pz,1,0,0,0,0.1);
                }
            }
        }
        if(ticks == 20){
            if(this.entity.getTarget()!=null){
                this.entity.getTarget().hurt(this.entity.damageSources().generic(), 3);
                this.entity.getTarget().addEffect(new MobEffectInstance(MobEffects.CONFUSION, 240, 255));
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