package net.zincstudios.scgextra.entity.raid_summoner;

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
    private static ArrayList<EntityType<?>> elite = new ArrayList<>();

    public RaidSummonerEntity(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        elite.add(ModEntities.SALMONSAUR.get());
        elite.add(ModEntities.TURTLEMAN.get());
        elite.add(ModEntities.TENTACLIATOR.get());
        elite.add(ModEntities.GLOWING_TENTACLIATOR.get());
        elite.add(ModEntities.PUFFICUS.get());
    }

    @Override
    public void tick() {
        ArrayList<EntityType<?>> spawned = new ArrayList<>();
        super.tick();
        if(this.level().isClientSide())return;
        ServerLevel sLevel = (ServerLevel)this.level();
        for(int i = 0; i < 5; i++){
            ModEntities.FISH_FOLK.get().spawn(sLevel, this.blockPosition(), MobSpawnType.MOB_SUMMONED);
        }
        for(int i = 0; i < 3; i++){
            EntityType<?> entity = elite.get(this.random.nextInt(elite.size()));
            while(spawned.contains(entity)){
                entity = elite.get(this.random.nextInt(elite.size()));
            }
            entity.spawn(sLevel, this.blockPosition(), MobSpawnType.MOB_SUMMONED);
            spawned.add(entity);
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
}