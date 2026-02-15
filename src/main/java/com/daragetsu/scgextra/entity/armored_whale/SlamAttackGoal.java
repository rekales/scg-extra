package com.daragetsu.scgextra.entity.armored_whale;

import java.util.Random;

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
        return cooldown <= 0 && entity.getTarget() != null && random.nextInt(10)<4;
    }

    @Override
    public void start() {
        cooldown=600;
        super.start();
    }

    @Override
    public boolean canContinueToUse() {
        return ticks <= 55 && entity.getTarget() != null;//~22 ticks for going up, ~22 ticks coming down and then apply the damage,the few ticks extra are just to be sure
    }
    @Override
    public void tick() {
        super.tick();
        ticks++;
        if(ticks<=20){
            entity.setDeltaMovement(
                entity.getDeltaMovement().x(), 
                0.5, 
                entity.getDeltaMovement().z()
            );
        }
        if(entity.onGround()){
            AABB fiveBlockRangeAABB = new AABB(
                0,
                0,
                0,
                0,
                0,
                0
            );
            AABB twelveBlockRangeAABB = new AABB(
                0,
                0,
                0,
                0,
                0,
                0
            );
        }   
    }

    @Override
    public void stop() {
        ticks = 0;
    }
}