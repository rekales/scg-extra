package net.zincstudios.scgextra.entity.neutral.nether.netherite_eater;

import java.util.EnumSet;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.sounds.NeutralSounds;
import top.ribs.scguns.init.ModParticleTypes;

public class NetheriteEaterFireBreathGoal extends Goal{
    private final NetheriteEaterEntity mob;
    private int cooldown = 0;
    private int ticks = 0;

    public NetheriteEaterFireBreathGoal(NetheriteEaterEntity entity){
        this.mob = entity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if(this.cooldown>0)this.cooldown--;
        return this.mob.getTarget()!=null && this.cooldown == 0 && this.mob.distanceToSqr(this.mob.getTarget())<=64;
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.getTarget()!=null && this.ticks <= 50  && this.mob.distanceToSqr(this.mob.getTarget())<=64;
    }

    @Override
    public void start() {
        super.start();
        this.cooldown = 160;
        this.ticks = 0;
        this.mob.triggerAnim("controller", "fire_breath");
        this.mob.playSound(NeutralSounds.NETHERITE_EATER_BREATH.get());
        this.mob.setBreathingFire(true);
    }
    
    @Override
    public void tick() {
        super.tick();
        this.ticks++;
        this.mob.getNavigation().stop();
        this.mob.setDeltaMovement(Vec3.ZERO);
        if(this.ticks<10)return;
        if(this.mob.getTarget()!=null){
            this.mob.getLookControl().setLookAt(this.mob.getTarget());
            BlockPos start = this.mob.blockPosition();
            BlockPos end = this.mob.getTarget().blockPosition();
            double startX = start.getX() + 0.5;
            double startY = start.getY() + 2.5;
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
            for (double i = 0; i < distance; i += 0.1) {
                double px = startX + stepX * i;
                double py = startY + stepY * i;
                double pz = startZ + stepZ * i;
                if (this.mob.level() instanceof ServerLevel serverLevel)serverLevel.sendParticles(ModParticleTypes.SOUL_FIREBALL.get(), px, py, pz,1,0.3,0.3,0.3,0.1);
            }
            if(this.ticks%10==0){
                this.mob.getTarget().hurt(this.mob.damageSources().generic(), 5);
                this.mob.getTarget().setSecondsOnFire(1);
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        if(this.mob.getTarget()!=null){
            this.mob.getNavigation().moveTo(this.mob.getTarget(), 0.5);
        }
        this.mob.setBreathingFire(false);
    }
}
