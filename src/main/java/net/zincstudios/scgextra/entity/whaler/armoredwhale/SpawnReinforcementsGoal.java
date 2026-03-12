package net.zincstudios.scgextra.entity.whaler.armoredwhale;

import java.util.Random;

import net.zincstudios.scgextra.entity.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
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
        return cooldown <= 0 && random.nextInt(100) < 20 && entity.onGround() && entity.getTarget()!=null;
    }

    @Override
    public boolean canContinueToUse() {
        return false;//only run once
    }
    @Override
    public void start() {
        super.start();
        cooldown = 200;
        if(!entity.level().isClientSide()){
            BlockPos pos = entity.blockPosition();
            ServerLevel level = (ServerLevel)entity.level();
            ModEntities.FISH_FOLK.get().spawn(level, pos, MobSpawnType.MOB_SUMMONED);
            ModEntities.FISH_FOLK.get().spawn(level, pos, MobSpawnType.MOB_SUMMONED);
        }
    }
    @Override
    public void stop() {
        super.stop();
    }
}
