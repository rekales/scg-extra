package com.daragetsu.scgextra.entity.armored_whale;

import java.util.ArrayList;
import java.util.Random;

import com.daragetsu.scgextra.entity.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.Goal;

public class SpawnReinforcementsGoal extends Goal{
    private int cooldown = 0;
    private Random random = new Random();
    private final ArmoredWhaleEntity entity;
    
    public SpawnReinforcementsGoal(ArmoredWhaleEntity entity){
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        if(cooldown>0)--cooldown;
        return cooldown <= 0 && random.nextInt(100) < 20 && entity.onGround();
    }

    @Override
    public boolean canContinueToUse() {
        return false;//only run once
    }
    @Override
    public void start() {
        super.start();
        cooldown = 600;
        if(!entity.level().isClientSide()){
            BlockPos pos = entity.blockPosition();
            ArrayList<EntityType<?>> entities = new ArrayList<>();
            entities.add(ModEntities.FISH_FOLK.get());
            entities.add(ModEntities.PUFFICUS.get());
            entities.add(ModEntities.SALMONSAURS.get());
            entities.add(ModEntities.TENTACLIATOR.get());
            entities.add(ModEntities.TURTLEMAN.get());
            ServerLevel level = (ServerLevel)entity.level();
            int num = random.nextInt(1,11);
            for(int i = 0; i < num; i++){
                EntityType<?> en = entities.get(random.nextInt(entities.size()));
                en.spawn(level, pos, MobSpawnType.MOB_SUMMONED);
            }
        }
    }
    @Override
    public void stop() {
        super.stop();
    }
}
