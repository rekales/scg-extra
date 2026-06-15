package net.zincstudios.scgextra.entity.cog.juggernaut;

import com.google.common.collect.ImmutableMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.AbilityState;
import net.zincstudios.scgextra.entity.ModBrainMemories;
import net.zincstudios.scgextra.entity.common.MobUtil;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JetBootsAbility extends Behavior<CogJuggernautEntity> {

    public static final int DEFAULT_COOLDOWN_DURATION_TICKS = 120;
    public static final String ABILITY_ID = "cog_juggernaut_jet_boots";

    private final int cooldownDuration;
    private final int jetStartTicks;

    private int recoveryTimer = 0;
    private Vec3 startPos = Vec3.ZERO;
    private boolean flee = false;
    private long startTime = 0;

    public JetBootsAbility() {
        this(DEFAULT_COOLDOWN_DURATION_TICKS);
    }

    public JetBootsAbility(int cooldownDuration) {
        super(ImmutableMap.of(
                MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryStatus.REGISTERED,
                ModBrainMemories.ABILITY_COOLING_DOWN.get(), MemoryStatus.VALUE_ABSENT,
                ModBrainMemories.ABILITY_STATE.get(), MemoryStatus.REGISTERED,
                ModBrainMemories.WEAPON_IDEAL_RANGE.get(), MemoryStatus.VALUE_PRESENT
        ), 120);
        this.cooldownDuration = cooldownDuration;
        this.jetStartTicks = 20;
    }

    @Override
    protected boolean canStillUse(ServerLevel level, CogJuggernautEntity mob, long gameTime) {
        if (gameTime-this.startTime > 20 && mob.onGround()) return false;

        Brain<?> brain = mob.getBrain();

//        if (!(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET)
//                && !brain.hasMemoryValue(ModBrainMemories.ABILITY_COOLING_DOWN.get())
//                && brain.hasMemoryValue(ModBrainMemories.ABILITY_STATE.get()))) {
//            SCGExtra.LOGGER.debug("");
//        }

        return brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET)
                && !brain.hasMemoryValue(ModBrainMemories.ABILITY_COOLING_DOWN.get())
                && brain.hasMemoryValue(ModBrainMemories.ABILITY_STATE.get());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent") // because already handled on hasRequiredMemories
    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, CogJuggernautEntity mob) {
        Brain<?> brain = mob.getBrain();
        LivingEntity target = brain.getMemory(MemoryModuleType.ATTACK_TARGET).get();
        float idealRange = brain.getMemory(ModBrainMemories.WEAPON_IDEAL_RANGE.get()).get();
//        return mob.closerThan(target, 4)
//                || !mob.closerThan(target, Math.min(idealRange*1.4, 25));
        return true;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent") // because already handled on hasRequiredMemories
    @Override
    protected void start(ServerLevel level, CogJuggernautEntity mob, long gameTime) {
        mob.setJetActive(true);
        Brain<?> brain = mob.getBrain();
        LivingEntity target = mob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
        this.recoveryTimer = 20;
        this.startPos = mob.position();
        this.flee = mob.closerThan(target, 4);
        this.startTime = gameTime;

        brain.setMemoryWithExpiry(
                ModBrainMemories.ABILITY_STATE.get(),
                new AbilityState(ABILITY_ID, mob.tickCount + 120),
                120
        );
    }

    @Override
    protected void stop(ServerLevel level, CogJuggernautEntity mob, long gameTime) {
        mob.setJetActive(false);
        Brain<?> brain = mob.getBrain();
        brain.setMemoryWithExpiry(ModBrainMemories.ABILITY_COOLING_DOWN.get(), true, this.cooldownDuration);
        brain.eraseMemory(ModBrainMemories.ABILITY_STATE.get());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent") // because already handled on canStillUse
    @Override
    protected void tick(ServerLevel level, CogJuggernautEntity mob, long gameTime) {
        Brain<?> brain = mob.getBrain();
        LivingEntity target = brain.getMemory(MemoryModuleType.ATTACK_TARGET).get();

        if (brain.hasMemoryValue(MemoryModuleType.LOOK_TARGET)) {
            brain.eraseMemory(MemoryModuleType.LOOK_TARGET);  // Because look control isn't working great when flying
        }

        float targetRot;
        if (this.flee) {
            targetRot = MobUtil.rotFromVec(mob.position().subtract(target.position()));
        } else {
            targetRot = MobUtil.rotFromVec(target.position().subtract(mob.position()));
        }
        MobUtil.turnEntityToYaw(mob, targetRot, 10f);

        long durationTicks = gameTime-this.startTime;
        if (durationTicks < this.jetStartTicks) return;

        double horizontalDist = getHorizontalDistance(this.startPos, mob.position());
        double verticalAccel = 0.04F + 0.06F * ((12 - getDistanceToGround(mob, 8)) / 12) * ((24 - horizontalDist) / 24);
        double horizontalAccel = 0.03F * ((24 - horizontalDist) / 24) * Mth.clamp((durationTicks - this.jetStartTicks) / 15f, 0, 1);

        Vec3 delta = target.position().subtract(mob.position());
        delta = new Vec3(delta.x, 0, delta.z).normalize();
        delta = delta.scale(this.flee ? -horizontalAccel : horizontalAccel);
        delta = delta.add(0, verticalAccel, 0);
        SCGExtra.LOGGER.debug("jet active: " + horizontalDist);
        mob.addDeltaMovement(delta);
    }

    @SuppressWarnings("SameParameterValue")
    private static double getDistanceToGround(LivingEntity entity, double maxDist) {
        Vec3 start = entity.position();
        Vec3 end = start.add(0, -maxDist, 0);

        BlockHitResult result = entity.level().clip(new ClipContext(
                start, end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.ANY,
                entity
        ));

        if (result.getType() == HitResult.Type.MISS) {
            return maxDist;
        }
        return start.y - result.getLocation().y;
    }

    private static double getHorizontalDistance(Vec3 start, Vec3 end) {
        return new Vec3(start.x, 0, start.z).subtract(new Vec3(end.x, 0, end.z)).length();
    }
}
