package net.zincstudios.scgextra.entity.common.raid_summoner;

import java.util.ArrayList;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.zincstudios.scgextra.entity.ModEntities;

public class RaidSummonerEntity extends Mob {
    private static ArrayList<EntityType<?>> WhalerElite = new ArrayList<>();
    private static ArrayList<EntityType<?>> RRCElite = new ArrayList<>();
    private static ArrayList<EntityType<?>> RRCInfantry = new ArrayList<>();
    public RaidSummonerEntity(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        WhalerElite.add(ModEntities.SALMONSAUR.get());
        WhalerElite.add(ModEntities.TURTLEMAN.get());
        WhalerElite.add(ModEntities.TENTACLIATOR.get());
        WhalerElite.add(ModEntities.GLOWING_TENTACLIATOR.get());
        WhalerElite.add(ModEntities.PUFFICUS.get());
        RRCElite.add(ModEntities.SPRING_JUNKIE.get());
        RRCElite.add(ModEntities.SCRAP_GUARD.get());
        RRCElite.add(ModEntities.ARC_PSYCHO.get());
        RRCInfantry.add(ModEntities.COPPER_KNIGHT.get());
        RRCInfantry.add(ModEntities.TALLMAN.get());
        RRCInfantry.add(ModEntities.SCOUT.get());
    }

    @Override
    public void tick() {
        super.tick();
        if(this.level().isClientSide())return;
        int rand = this.random.nextInt(2);
        if(rand==0){
            this.spawnWhaler();
        }else{
            this.spawnRRC();
        }
        this.remove(RemovalReason.DISCARDED);
    }
    @Override
    public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
        long time = pLevel.dayTime() % 24000;
        return time >= 13000 && time < 23000;
    }
    @Override
    public boolean checkSpawnObstruction(LevelReader pLevel) {
        return pLevel.isUnobstructed(this);
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10);
        }
    private void spawnWhaler(){
        ArrayList<EntityType<?>> spawned = new ArrayList<>();
        ServerLevel sLevel = (ServerLevel)this.level();
        for(int i = 0; i < 5; i++){
            ModEntities.FISH_FOLK.get().spawn(sLevel, this.blockPosition(), MobSpawnType.MOB_SUMMONED);
        }
        for(int i = 0; i < 3; i++){
            EntityType<?> entity = WhalerElite.get(this.random.nextInt(WhalerElite.size()));
            while(spawned.contains(entity)){
                entity = WhalerElite.get(this.random.nextInt(WhalerElite.size()));
            }
            entity.spawn(sLevel, this.blockPosition(), MobSpawnType.MOB_SUMMONED);
            spawned.add(entity);
        }
    }
    private void spawnRRC(){
        ServerLevel sLevel = (ServerLevel)this.level();
        int eliteCount = this.random.nextInt(1, 3);
        int infantryCount = this.random.nextInt(1, 5);
        for(int i = 0; i < eliteCount; i++){
            RRCElite.get(this.random.nextInt(RRCElite.size())).spawn(sLevel, this.blockPosition(), getSpawnType());
        }
        for(int i = 0; i < infantryCount; i++){
            RRCInfantry.get(this.random.nextInt(RRCInfantry.size())).spawn(sLevel, this.blockPosition(), getSpawnType());
        }
    }
}