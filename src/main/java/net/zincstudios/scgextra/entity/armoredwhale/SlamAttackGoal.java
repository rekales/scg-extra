package net.zincstudios.scgextra.entity.armoredwhale;

import java.util.List;
import java.util.Random;

import net.zincstudios.scgextra.Faction;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;

public class SlamAttackGoal extends Goal{

    public ArmoredWhaleEntity entity;
    private int cooldown = 0;
    private int ticks = 0;
    private Random random = new Random();

    public SlamAttackGoal(ArmoredWhaleEntity entity){
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        if(cooldown>0){
            --cooldown;
        }
        return cooldown <= 0 && entity.getTarget() != null && random.nextInt(100)<10;
    }

    @Override
    public void start() {
        ticks = 0;
        super.start();
    }

    @Override
    public boolean canContinueToUse() {
        if(entity.getTarget()==null)entity.setEyeFlash(false);
        return ticks <= 60 && entity.getTarget() != null && cooldown<=0;//~22 ticks for going up, ~22 ticks coming down and then apply the damage,the few ticks extra are just to be sure
    }
    @Override
    public void tick() {
        super.tick();
        ticks++;
        if(ticks==1){
            entity.triggerAnim("special", "slam");
        }
        if(ticks <= 10){
            entity.setEyeFlash(true);
        }if(ticks > 10 && ticks<=30){
            entity.setEyeFlash(false);
            entity.setDeltaMovement(
                entity.getDeltaMovement().x(), 
                0.3, 
                entity.getDeltaMovement().z()
            );
            entity.setDidSlam(true);
        }else if(ticks > 30 && entity.onGround()){//only runs after the jump and once it's on the ground
            AABB fiveBlockRangeAABB = new AABB(
                entity.getX()-5,
                entity.getY()-5,
                entity.getZ()-5,
                entity.getX()+5,
                entity.getY()+5,
                entity.getZ()+5
            );
            AABB twelveBlockRangeAABB = new AABB(
                entity.getX()-12,
                entity.getY()-12,
                entity.getZ()-12,
                entity.getX()+12,
                entity.getY()+12,
                entity.getZ()+12
            );
            List<LivingEntity> entitiesInFiveBlocks = entity.level().getEntitiesOfClass(LivingEntity.class, fiveBlockRangeAABB, e -> !Faction.isFriendlies(entity, e) && !e.is(entity));
            List<LivingEntity> entitiesInTwelveBlocks = entity.level().getEntitiesOfClass(LivingEntity.class, twelveBlockRangeAABB, e -> !Faction.isFriendlies(entity, e) && !e.is(entity));
            // entities within 5 blocks get 30 damage,
            // entities within 12 blocks get 10 damage,

            // so i made the 5 block one deal 20 damage, and the 12 block one deals the remaining 10 damage to the 5 block range and the entities in the outer layer just gets the 10 damage, so i don't have to specifically make a outer box, yes I'm lazy

            for(LivingEntity en : entitiesInFiveBlocks){
                en.hurt(en.damageSources().mobAttack(entity), 20);
            }
            for(LivingEntity en : entitiesInTwelveBlocks){
                en.hurt(en.damageSources().mobAttack(entity), 10);
            }
            this.stop();
        }
    }

    @Override
    public void stop() {
        cooldown=600;//start cooldown after the whole attack finishes
        ticks = 0;
        if(!this.entity.level().isClientSide){
            double radius = 0.0;
            for(int i = 0; i < 12; i++){
                for (int j = 0; j < 360; j += 10) {
                    double rad = Math.toRadians(j);
                    double x = this.entity.getX() + Math.cos(rad) * radius;
                    double z = this.entity.getZ() + Math.sin(rad) * radius;
                    ServerLevel level = (ServerLevel) this.entity.level();
                    level.sendParticles(
                        ParticleTypes.SMOKE, 
                        x, 
                        this.entity.getY()+1,
                        z,
                        20,
                        0.4,
                        0.4,
                        0.4,
                        0
                    );
                }
                radius+=1;
            }
        }
    }
}