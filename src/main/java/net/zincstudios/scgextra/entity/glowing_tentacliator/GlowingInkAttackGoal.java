package net.zincstudios.scgextra.entity.glowing_tentacliator;

import net.zincstudios.scgextra.effects.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;

public class GlowingInkAttackGoal extends Goal{
    private final GlowingTentacliatorEntity entity;
    private int cooldown = 0;

    public GlowingInkAttackGoal(GlowingTentacliatorEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        if(cooldown>0){
            --cooldown;
        }
        return cooldown <= 0 && entity.getTarget() != null && entity.random.nextInt(50) < 3;
    }
    
    @Override
    public void start() {
        entity.triggerAnim("special", "special_attack"); 
        cooldown = 600;
        entity.getTarget().addEffect(new MobEffectInstance(ModEffects.GLOWING_INK_EFFECT.get(), 100));
        entity.getTarget().addEffect(new MobEffectInstance(MobEffects.GLOWING, 100));
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
        for (double i = 0; i < distance; i += 0.1) {
            double px = startX + stepX * i;
            double py = startY + stepY * i;
            double pz = startZ + stepZ * i;
            entity.level().getServer().getLevel(Level.OVERWORLD).sendParticles(ParticleTypes.FALLING_WATER, px, py, pz,1,0,0,0,0);
        }
    }
    
    @Override
    public boolean canContinueToUse() {
        return cooldown > 0 && entity.getTarget() != null;
    }

    @Override
    public void stop() {
        super.stop();
    }
}
