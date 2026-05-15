package net.zincstudios.scgextra.entity.neutral.end_stone_crab;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.zincstudios.scgextra.sounds.NeutralSounds;
import net.zincstudios.scgextra.CommonConfig;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;

public class EndStoneCrabEntity extends Monster implements GeoEntity {
    private static final int ATTACK_ANIM_TICKS = 20;
    private static final int ATTACK_HIT_DELAY_TICKS = 6;
    private static final int ATTACK_COOLDOWN_MIN_TICKS = 28;
    private static final int ATTACK_COOLDOWN_MAX_TICKS = 36;
    private static final int DEATH_ANIM_TICKS = 20;
    private static final int DIG_COOLDOWN_TICKS = 8;
    private static final int DIG_MAX_WIDTH = 5;
    private static final int DIG_MAX_HEIGHT = 4;
    private static final int DIG_SCAN_DEPTH = 6;
    private static final int AGGRO_LOCK_TICKS = 200;
    private static final int IDLE_EATING_DURATION_TICKS = 80;
    private static final int IDLE_EATING_COOLDOWN_MIN_TICKS = 180;
    private static final int IDLE_EATING_COOLDOWN_MAX_TICKS = 280;
    private static final int TARGET_MEMORY_TICKS = 80;
    private static final double BASE_ATTACK_REACH = 3.6D;

    private static final EntityDataAccessor<Integer> ATTACK_ANIM =
            SynchedEntityData.defineId(EndStoneCrabEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private int digCooldown;
    private int idleEatingTicks;
    private int idleEatingCooldown;
    private int attackCooldownTicks;
    private int targetMemoryTicks;
    private int digRecoverTicks;
    private int forcedAggroTicks;
    private LivingEntity pendingAttackTarget;
    private LivingEntity forcedAggroTarget;
    private int pendingAttackTicks = -1;

    public EndStoneCrabEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 80;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 300.0D)
                .add(Attributes.ARMOR, 16.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 15.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.27D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_ANIM, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new EndStoneCrabMeleeGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.75D));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 1, false, false,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.syncHeadWithBody();

        if (this.level().isClientSide()) {
            return;
        }

        this.tickAttackAnimation();
        this.tickAttackCooldown();
        this.tickTargetMemory();
        this.tickForcedAggro();
        this.tickDigRecover();
        this.tickPendingAttack();
        this.tickIdleEating();
        this.tickDigging();

        if (this.isAttackAnimationActive()) {
            this.getNavigation().stop();
            LivingEntity attackTarget = this.getTarget();
            if (attackTarget != null && attackTarget.isAlive()) {
                this.faceTarget(attackTarget, 12.0F);
            }
        }
    }

    private void tickAttackAnimation() {
        int attackAnim = this.entityData.get(ATTACK_ANIM);
        if (attackAnim > 0) {
            this.entityData.set(ATTACK_ANIM, attackAnim - 1);
        }
    }

    private void tickAttackCooldown() {
        if (this.attackCooldownTicks > 0) {
            this.attackCooldownTicks--;
        }
    }

    private void tickTargetMemory() {
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive()) {
            this.targetMemoryTicks = TARGET_MEMORY_TICKS;
            return;
        }
        if (this.targetMemoryTicks > 0) {
            this.targetMemoryTicks--;
        }
    }

    private void tickForcedAggro() {
        if (this.forcedAggroTicks > 0) {
            this.forcedAggroTicks--;
        }

        LivingEntity forced = this.forcedAggroTarget;
        if (forced == null || !forced.isAlive()) {
            if (this.forcedAggroTicks <= 0) {
                this.forcedAggroTarget = null;
            }
            return;
        }

        if (this.forcedAggroTicks > 0) {
            if (this.getTarget() != forced) {
                this.setTarget(forced);
            }
            this.targetMemoryTicks = Math.max(this.targetMemoryTicks, TARGET_MEMORY_TICKS);
        }
    }

    private void tickPendingAttack() {
        if (this.isBreakingOut()) {
            this.pendingAttackTarget = null;
            this.pendingAttackTicks = -1;
            return;
        }

        if (this.pendingAttackTicks < 0) {
            return;
        }
        if (this.pendingAttackTicks > 0) {
            this.pendingAttackTicks--;
            return;
        }

        LivingEntity target = this.pendingAttackTarget;
        this.pendingAttackTarget = null;
        this.pendingAttackTicks = -1;
        this.performMeleeHit(target);
    }

    private void tickIdleEating() {
        if (this.idleEatingTicks > 0) {
            this.idleEatingTicks--;
        }
        if (this.idleEatingCooldown > 0) {
            this.idleEatingCooldown--;
        }

        LivingEntity target = this.getTarget();
        boolean canTrigger = target == null
                && this.targetMemoryTicks <= 0
                && !this.isDeadOrDying()
                && !this.isAttackAnimationActive()
                && this.pendingAttackTicks < 0
                && this.idleEatingCooldown <= 0
                && !this.getNavigation().isInProgress();
        if (canTrigger && this.random.nextInt(90) == 0) {
            this.idleEatingTicks = IDLE_EATING_DURATION_TICKS;
            this.idleEatingCooldown = this.random.nextInt(
                    IDLE_EATING_COOLDOWN_MAX_TICKS - IDLE_EATING_COOLDOWN_MIN_TICKS + 1
            ) + IDLE_EATING_COOLDOWN_MIN_TICKS;
        }
    }

    private void tickDigging() {
        if (this.digCooldown > 0) {
            this.digCooldown--;
            return;
        }

        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive() || this.distanceTo(target) > 20.0F) {
            return;
        }
        if (!this.shouldDigToward(target)) {
            return;
        }

        this.faceTarget(target, 14.0F);
        boolean brokeAny = this.breakDiggingVolume(target);
        if (brokeAny) {
            this.digCooldown = DIG_COOLDOWN_TICKS;
            this.digRecoverTicks = 10;
            this.pendingAttackTarget = null;
            this.pendingAttackTicks = -1;
        }
    }

    private boolean breakDiggingVolume(LivingEntity target) {
        return this.processBlockingVolume(target, true);
    }

    private boolean processBlockingVolume(LivingEntity target, boolean breakBlocks) {
        boolean foundAny = this.processLocalBlockers(breakBlocks);
        if (!breakBlocks && foundAny) {
            return true;
        }

        Vec3 toTarget = target.position().subtract(this.position());
        double horizontalLen = Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z);
        if (horizontalLen < 0.001D) {
            return foundAny;
        }

        double nx = toTarget.x / horizontalLen;
        double nz = toTarget.z / horizontalLen;
        double px = -nz;
        double pz = nx;
        int baseY = Mth.floor(this.getBoundingBox().minY);
        double startForward = Math.max(0.6D, this.getBbWidth() * 0.25D);
        int depthSteps = Mth.clamp((int) Math.ceil(Math.min(horizontalLen, DIG_SCAN_DEPTH)), 1, DIG_SCAN_DEPTH);

        for (int depth = 0; depth < depthSteps; depth++) {
            double progress = depthSteps <= 1 ? 1.0D : (double) depth / (double) (depthSteps - 1);
            int width = Mth.clamp(1 + (int) Math.round(progress * (DIG_MAX_WIDTH - 1)), 1, DIG_MAX_WIDTH);
            int height = DIG_MAX_HEIGHT;
            int halfWidth = width / 2;
            double forwardDistance = startForward + depth * 0.95D;
            double centerX = this.getX() + nx * forwardDistance;
            double centerZ = this.getZ() + nz * forwardDistance;

            for (int y = 0; y < height; y++) {
                for (int side = -halfWidth; side <= halfWidth; side++) {
                    double sx = centerX + px * side * 0.95D;
                    double sz = centerZ + pz * side * 0.95D;
                    BlockPos pos = BlockPos.containing(sx, baseY + y, sz);
                    if (!this.canBreakMovementBlock(pos)) {
                        continue;
                    }

                    foundAny = true;
                    if (breakBlocks) {
                        this.tryBreakSoftBlock(pos);
                    } else {
                        return true;
                    }
                }
            }
        }
        return foundAny;
    }

    private boolean processLocalBlockers(boolean breakBlocks) {
        AABB box = this.getBoundingBox().inflate(0.2D, 0.0D, 0.2D);
        int minX = Mth.floor(box.minX);
        int maxX = Mth.floor(box.maxX);
        int minY = Mth.floor(box.minY);
        int maxY = Math.max(minY + DIG_MAX_HEIGHT - 1, Mth.floor(box.maxY) + 1);
        int minZ = Mth.floor(box.minZ);
        int maxZ = Mth.floor(box.maxZ);
        boolean foundAny = false;

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (!this.canBreakMovementBlock(pos)) {
                        continue;
                    }

                    foundAny = true;
                    if (breakBlocks) {
                        this.tryBreakSoftBlock(pos);
                    } else {
                        return true;
                    }
                }
            }
        }
        return foundAny;
    }

    private void startAttackAnimation() {
        int current = this.entityData.get(ATTACK_ANIM);
        if (current < ATTACK_ANIM_TICKS) {
            this.entityData.set(ATTACK_ANIM, ATTACK_ANIM_TICKS);
            this.triggerAnim("attack", "attack");
        }
    }

    private void scheduleMeleeHit(LivingEntity target) {
        this.pendingAttackTarget = target;
        this.pendingAttackTicks = ATTACK_HIT_DELAY_TICKS;
        this.attackCooldownTicks = this.random.nextInt(
                ATTACK_COOLDOWN_MAX_TICKS - ATTACK_COOLDOWN_MIN_TICKS + 1
        ) + ATTACK_COOLDOWN_MIN_TICKS;
    }

    private void performMeleeHit(LivingEntity target) {
        if (!this.canHitTarget(target)) {
            return;
        }
        boolean hit = super.doHurtTarget(target);
        if (!hit) {
            return;
        }
        this.playSound(NeutralSounds.NEUTRAL_END_CRAB_ATTACK_01.get(), 1.1F, this.getVoicePitch());
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2));
    }

    private boolean canHitTarget(LivingEntity target) {
        if (target == null || !target.isAlive()) {
            return false;
        }
        if (this.digRecoverTicks > 0 || this.isBreakingOut()) {
            return false;
        }
        if (!this.hasLineOfSight(target) || this.isPathObstructedToTarget(target)) {
            return false;
        }
        double targetRadius = target.getBbWidth() * 0.5D;
        double selfRadius = this.getBbWidth() * 0.5D;
        double reach = BASE_ATTACK_REACH + targetRadius + selfRadius;
        return this.distanceToSqr(target) <= reach * reach;
    }

    private boolean isAttackAnimationActive() {
        return this.entityData.get(ATTACK_ANIM) > 0;
    }

    private void tickDigRecover() {
        if (this.digRecoverTicks > 0) {
            this.digRecoverTicks--;
        }
    }

    private boolean shouldDigToward(LivingEntity target) {
        if (this.processLocalBlockers(false)) {
            return true;
        }
        if (this.canPassToTarget(target)) {
            return false;
        }
        return this.processBlockingVolume(target, false);
    }

    private boolean isBreakingOut() {
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        if (this.processLocalBlockers(false)) {
            return true;
        }
        if (this.canPassToTarget(target)) {
            return false;
        }
        return this.horizontalCollision || this.processBlockingVolume(target, false);
    }

    private boolean isPathObstructedToTarget(LivingEntity target) {
        Vec3 chestFrom = this.position().add(0.0D, this.getBbHeight() * 0.55D, 0.0D);
        Vec3 chestTo = target.position().add(0.0D, target.getBbHeight() * 0.55D, 0.0D);
        if (this.rayHitsBlock(chestFrom, chestTo)) {
            return true;
        }

        Vec3 lowFrom = this.position().add(0.0D, 0.55D, 0.0D);
        Vec3 lowTo = target.position().add(0.0D, 0.55D, 0.0D);
        return this.rayHitsBlock(lowFrom, lowTo);
    }

    private boolean rayHitsBlock(Vec3 from, Vec3 to) {
        if (from.distanceToSqr(to) < 1.0E-4D) {
            return false;
        }
        HitResult hit = this.level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        return hit.getType() == HitResult.Type.BLOCK;
    }

    private boolean canPassToTarget(LivingEntity target) {
        if (target == null || !target.isAlive()) {
            return true;
        }
        if (this.distanceToSqr(target) <= this.getAttackReachSqr(target)) {
            return true;
        }
        Path path = this.getNavigation().createPath(target, 0);
        return path != null && path.canReach();
    }

    private double getAttackReachSqr(LivingEntity target) {
        double targetRadius = target.getBbWidth() * 0.5D;
        double selfRadius = this.getBbWidth() * 0.5D;
        double reach = BASE_ATTACK_REACH + targetRadius + selfRadius;
        return reach * reach;
    }

    private boolean canBreakMovementBlock(BlockPos pos) {
        if (!this.canBreakSoftBlock(pos)) {
            return false;
        }
        BlockState state = this.level().getBlockState(pos);
        return !state.getCollisionShape(this.level(), pos, CollisionContext.of(this)).isEmpty();
    }

    private void faceTarget(LivingEntity target, float turnStep) {
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        float desiredYaw = (float) (Math.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        float nextYaw = Mth.approachDegrees(this.getYRot(), desiredYaw, turnStep);
        this.setYRot(nextYaw);
        this.setYBodyRot(nextYaw);
        this.setYHeadRot(nextYaw);
    }

    private void syncHeadWithBody() {
        float bodyYaw = this.yBodyRot;
        this.setYHeadRot(bodyYaw);
        this.yHeadRotO = bodyYaw;
    }

    @Override
    protected void tickDeath() {
        this.deathTime++;
        if (this.deathTime >= DEATH_ANIM_TICKS && !this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 2, state -> {
            if (this.isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("death"));
            }
            if (this.isAttackAnimationActive()) {
                return PlayState.STOP;
            }
            if (state.isMoving() || this.getNavigation().isInProgress()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            if (this.idleEatingTicks > 0) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("idle_eating"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
        controllers.add(new AnimationController<>(this, "attack", 0, state -> PlayState.STOP)
                .triggerableAnim("attack", RawAnimation.begin().thenPlay("attack")));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    private boolean tryBreakSoftBlock(BlockPos pos) {
        if (!this.canBreakSoftBlock(pos)) {
            return false;
        }
        return this.level().destroyBlock(pos, true, this);
    }

    private boolean canBreakSoftBlock(BlockPos pos) {
        BlockState state = this.level().getBlockState(pos);
        if (state.isAir() || state.getDestroySpeed(this.level(), pos) < 0.0F) {
            return false;
        }
        float hardness = state.getDestroySpeed(this.level(), pos);
        if (hardness > 12.0F) {
            return false;
        }
        if (state.is(Blocks.BEDROCK)
                || state.is(Blocks.END_PORTAL_FRAME)
                || state.is(Blocks.OBSIDIAN)
                || state.is(Blocks.CRYING_OBSIDIAN)) {
            return false;
        }
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE)
                && !state.is(BlockTags.NEEDS_DIAMOND_TOOL);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.random.nextBoolean()
                ? NeutralSounds.NEUTRAL_END_CRAB_IDLE_01.get()
                : NeutralSounds.NEUTRAL_END_CRAB_IDLE_02.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return this.random.nextBoolean()
                ? NeutralSounds.NEUTRAL_END_CRAB_HURT_01.get()
                : NeutralSounds.NEUTRAL_END_CRAB_HURT_02.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return NeutralSounds.NEUTRAL_END_CRAB_DEATH.get();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean damaged = super.hurt(source, amount);
        if (!damaged || this.level().isClientSide()) {
            return damaged;
        }

        Entity attacker = source.getEntity();
        if (!(attacker instanceof LivingEntity living) || !living.isAlive() || living == this) {
            return damaged;
        }

        this.forcedAggroTarget = living;
        this.forcedAggroTicks = AGGRO_LOCK_TICKS;
        this.setTarget(living);
        this.targetMemoryTicks = TARGET_MEMORY_TICKS;
        return damaged;
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnReason) {
        if (spawnReason == MobSpawnType.SPAWN_EGG || spawnReason == MobSpawnType.COMMAND) {
            return true;
        }
        if (net.zincstudios.scgextra.entity.neutral.NeutralCombatUtil.isWaterAtOrBelow(level, this.blockPosition())) {
            return false;
        }
        return this.random.nextFloat() * 100.0F < CommonConfig.spawnChanceEndStoneCrab
                && net.zincstudios.scgextra.entity.neutral.NeutralCombatUtil.canSpawnEndSurface(level, this.blockPosition());
    }

    private static final class EndStoneCrabMeleeGoal extends Goal {
        private static final int REPATH_INTERVAL_TICKS = 4;
        private static final double TARGET_MOVE_REPATH_DISTANCE_SQR = 1.0D;

        private final EndStoneCrabEntity crab;
        private int repathCooldown;
        private double lastTargetX;
        private double lastTargetY;
        private double lastTargetZ;

        private EndStoneCrabMeleeGoal(EndStoneCrabEntity crab) {
            this.crab = crab;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.crab.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.crab.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public void tick() {
            LivingEntity target = this.crab.getTarget();
            if (target == null || !target.isAlive()) {
                return;
            }

            this.crab.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (!this.crab.isAttackAnimationActive()) {
                boolean canDirectChase = this.crab.hasLineOfSight(target)
                        && Math.abs(target.getY() - this.crab.getY()) < 1.5D;
                if (canDirectChase) {
                    this.crab.getNavigation().stop();
                    this.crab.getMoveControl().setWantedPosition(target.getX(), this.crab.getY(), target.getZ(), 0.95D);
                    this.repathCooldown = 0;
                } else {
                    if (this.repathCooldown > 0) {
                        this.repathCooldown--;
                    }
                    if (shouldRepath(target)) {
                        this.crab.getNavigation().moveTo(target, 0.95D);
                        this.repathCooldown = REPATH_INTERVAL_TICKS;
                        this.lastTargetX = target.getX();
                        this.lastTargetY = target.getY();
                        this.lastTargetZ = target.getZ();
                    }
                }
            } else {
                this.crab.getNavigation().stop();
                this.crab.faceTarget(target, 12.0F);
            }

            if (this.crab.attackCooldownTicks > 0 || this.crab.pendingAttackTicks >= 0 || this.crab.isAttackAnimationActive()) {
                return;
            }
            if (!this.crab.canHitTarget(target)) {
                return;
            }

            this.crab.swing(InteractionHand.MAIN_HAND);
            this.crab.startAttackAnimation();
            this.crab.scheduleMeleeHit(target);
        }

        private boolean shouldRepath(LivingEntity target) {
            if (!this.crab.getNavigation().isInProgress()) {
                return true;
            }
            if (this.repathCooldown <= 0) {
                return true;
            }
            double dx = target.getX() - this.lastTargetX;
            double dy = target.getY() - this.lastTargetY;
            double dz = target.getZ() - this.lastTargetZ;
            return (dx * dx + dy * dy + dz * dz) >= TARGET_MOVE_REPATH_DISTANCE_SQR;
        }
    }
}

