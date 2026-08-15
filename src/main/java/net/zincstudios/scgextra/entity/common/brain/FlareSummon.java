package net.zincstudios.scgextra.entity.common.brain;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraftforge.event.ForgeEventFactory;
import net.zincstudios.scgextra.entity.AbilityState;
import net.zincstudios.scgextra.entity.ModBrainMemories;

public class FlareSummon extends Behavior<LivingEntity> {

    public static final String ABILITY_ID = "flare_summon_mobs";
    public static int DEFAULT_COOLDOWN = 400;

    private final int duration;
    private final int delay;
    private final int cooldown;
    private final EntityType<? extends Mob>[] summonTypes;

    private long summonTick = 0;
    private long cooldownEnd = 0;  // I highly doubt other behaviors needs this, turn to a memory if so.

    @SafeVarargs
    public FlareSummon(int duration, int delay, int cooldown, EntityType<? extends Mob>... summonTypes) {
        super(ImmutableMap.of(
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                ModBrainMemories.ABILITY_STATE.get(), MemoryStatus.VALUE_ABSENT
        ), duration);
        this.duration = duration;
        this.delay = delay;
        this.cooldown = cooldown;
        this.summonTypes = summonTypes;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, LivingEntity owner) {
        return level.getGameTime() > this.cooldownEnd;
    }

    @Override
    protected boolean canStillUse(ServerLevel level, LivingEntity entity, long gameTime) {
        return entity.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET);
    }

    @Override
    protected void start(ServerLevel level, LivingEntity entity, long gameTime) {
        this.cooldownEnd = gameTime + this.delay + cooldown;
        this.summonTick = gameTime + this.delay;
        entity.getBrain().setMemoryWithExpiry(ModBrainMemories.ABILITY_STATE.get(),
                new AbilityState(ABILITY_ID, gameTime, gameTime + this.duration), this.duration);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")  // already handled on canStillUse
    @Override
    protected void tick(ServerLevel level, LivingEntity entity, long gameTime) {
        if (gameTime != this.summonTick) return;
        this.summonMobs(level, entity, entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get());
    }

    public void summonMobs(ServerLevel level, LivingEntity entity, LivingEntity target) {
        for(int i = 0; i < 3; ++i) {
            EntityType<? extends Mob> summonType = this.summonTypes[level.getRandom().nextInt(this.summonTypes.length)];
            BlockPos blockpos = entity.blockPosition().offset(-2 + level.getRandom().nextInt(5), 1, -2 + level.getRandom().nextInt(5));
            Mob summonedMob = summonType.create(level);
            if (summonedMob != null) {
                summonedMob.moveTo(blockpos, 0.0F, 0.0F);
                ForgeEventFactory.onFinalizeSpawn(summonedMob, level, level.getCurrentDifficultyAt(BlockPos.containing(entity.position())),
                        MobSpawnType.MOB_SUMMONED, null, null);
                BrainUtils.setTarget(summonedMob, target);
                level.sendParticles(ParticleTypes.CLOUD, summonedMob.getX(), summonedMob.getY(), summonedMob.getZ(), 10, 0.5F, 0.2, 0.2, 0.1);
//                summonedMob.setLimitedLife(20 * (30 + this.mob.getRandom().nextInt(90)));
                level.addFreshEntityWithPassengers(summonedMob);
            }
        }
    }
}
