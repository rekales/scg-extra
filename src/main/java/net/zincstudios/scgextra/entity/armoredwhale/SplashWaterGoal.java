package net.zincstudios.scgextra.entity.armoredwhale;

import java.util.HashSet;
import java.util.Random;

import net.zincstudios.scgextra.Faction;
import net.zincstudios.scgextra.effects.ModEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class SplashWaterGoal extends Goal{

    private final ArmoredWhaleEntity mob;
    private int cooldown = 0;
    private int ticks = 0;
    private Random random = new Random();
    private double radius = 0.0;
    private HashSet<LivingEntity> entities = new HashSet<>();

    public SplashWaterGoal(ArmoredWhaleEntity entity){
        mob = entity;
    }

    @Override
    public void start() {
        mob.setLayerN(0);
        radius = 0.0;
        entities.clear();
        ticks = 0;
        this.mob.setWaterSplash(true);
        super.start();
    }

    @Override
    public boolean canUse() {
        if(cooldown>0)--cooldown;
        return cooldown <= 0 && random.nextInt(100) < 20;
    }
    
    @Override
    public boolean canContinueToUse() {
        return ticks <= 16;
    }

    @Override
    public void tick() {
        super.tick();
        ticks++;
        if(ticks % 3 == 0){
            this.mob.setLayerN(this.mob.getLayerN()+1);
        }
        if(!this.mob.level().isClientSide){
            this.radius = this.radius+0.96;
            for (LivingEntity e : this.mob.level().getEntitiesOfClass(LivingEntity.class, this.mob.getBoundingBox().inflate(radius))) {
                if (e == mob) continue;
                double dx = e.getX() - this.mob.getX();
                double dz = e.getZ() - this.mob.getZ();
                
                double distSq = dx * dx + dz * dz;
                
                if (distSq <= radius * radius) {                    
                    if(!entities.contains(e) && Faction.isFriendlies(this.mob, e)){
                        e.addEffect(new MobEffectInstance(ModEffects.WHALER_REGEN_EFFECT.get(), 100));
                        entities.add(e);
                    }
                }
            }
            for (int i = 0; i < 360; i += 10) {
                double rad = Math.toRadians(i);
                double x = this.mob.getX() + Math.cos(rad) * this.radius;
                double z = this.mob.getZ() + Math.sin(rad) * this.radius;
                ServerLevel level = (ServerLevel) this.mob.level();
                for(int j = 1; j <= 2; j++){
                    level.sendParticles(
                        ParticleTypes.FALLING_WATER, 
                        x, 
                        this.mob.getY()+j, 
                        z,
                        10,
                        0.4,
                        0.4,
                        0.4,
                        0
                    );
                }
            }
        }
    }

    @Override
    public void stop() {
        mob.setLayerN(0);
        cooldown = 600;
        this.mob.setWaterSplash(false);
        super.stop();
    }
}
