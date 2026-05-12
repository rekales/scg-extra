package net.zincstudios.scgextra.entity.neutral.head_hunter;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.zincstudios.scgextra.CommonConfig;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class HeadHunterEntity extends Monster implements GeoEntity {
    private static final int ATTACK_LOCK_TICKS = 40;
    private static final int ATTACK_COOLDOWN_TICKS = 46;
    private static final int ATTACK_FIRST_HIT_DELAY_TICKS = 10;  
    private static final int ATTACK_SECOND_HIT_DELAY_TICKS = 29; 
    private static final int DEATH_ANIM_TICKS = 56;
    private static final EntityDataAccessor<Integer> ATTACK_LOCK =
            SynchedEntityData.defineId(HeadHunterEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> RUN_ANIM =
            SynchedEntityData.defineId(HeadHunterEntity.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private int attackCooldown;
    private LivingEntity pendingAttackTarget;
    private int pendingFirstHitTicks = -1;
    private int pendingSecondHitTicks = -1;

    public HeadHunterEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 200.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.ATTACK_DAMAGE, 12.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.27D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 1, true, false,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractPiglin.class, true));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_LOCK, 0);
        this.entityData.define(RUN_ANIM, false);
    }

    @Override
    protected void tickDeath() {
        this.deathTime++;
        if (this.deathTime >= DEATH_ANIM_TICKS && !this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide()) {
            return;
        }

        int attackTicks = this.entityData.get(ATTACK_LOCK);
        if (attackTicks > 0) {
            this.entityData.set(ATTACK_LOCK, attackTicks - 1);
            this.getNavigation().stop();
            this.faceTargetDuringAttack();
        }

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }
        this.tickPendingAttack();

        LivingEntity target = this.getTarget();
        boolean run = this.entityData.get(ATTACK_LOCK) <= 0
                && target != null
                && target.isAlive()
                && this.distanceTo(target) > 2.8F
                && (this.getNavigation().isInProgress() || this.distanceTo(target) > 4.0F);
        this.entityData.set(RUN_ANIM, run);
    }

    private boolean isAttackLocked() {
        return this.entityData.get(ATTACK_LOCK) > 0;
    }

    private void startAttackLock() {
        this.entityData.set(ATTACK_LOCK, ATTACK_LOCK_TICKS);
        this.attackCooldown = ATTACK_COOLDOWN_TICKS;
        this.triggerAnim("attack", "attack");
    }

    private void faceTargetDuringAttack() {
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        this.getLookControl().setLookAt(target, 30.0F, 30.0F);
    }

    private void scheduleDelayedAttack(LivingEntity target) {
        if (target == null || !target.isAlive()) {
            this.pendingAttackTarget = null;
            this.pendingFirstHitTicks = -1;
            this.pendingSecondHitTicks = -1;
            return;
        }
        this.pendingAttackTarget = target;
        this.pendingFirstHitTicks = ATTACK_FIRST_HIT_DELAY_TICKS;
        this.pendingSecondHitTicks = ATTACK_SECOND_HIT_DELAY_TICKS;
    }

    private void tickPendingAttack() {
        if (this.pendingAttackTarget == null || !this.pendingAttackTarget.isAlive()) {
            this.pendingAttackTarget = null;
            this.pendingFirstHitTicks = -1;
            this.pendingSecondHitTicks = -1;
            return;
        }

        if (this.pendingFirstHitTicks > 0) {
            this.pendingFirstHitTicks--;
        } else if (this.pendingFirstHitTicks == 0) {
            performPendingHit(this.pendingAttackTarget);
            this.pendingFirstHitTicks = -1;
        }

        if (this.pendingSecondHitTicks > 0) {
            this.pendingSecondHitTicks--;
        } else if (this.pendingSecondHitTicks == 0) {
            performPendingHit(this.pendingAttackTarget);
            this.pendingSecondHitTicks = -1;
        }

        if (this.pendingFirstHitTicks < 0 && this.pendingSecondHitTicks < 0) {
            this.pendingAttackTarget = null;
        }
    }

    private void performPendingHit(LivingEntity target) {
        if (target == null || !target.isAlive()) {
            return;
        }

        double reachSqr = this.getBbWidth() * 2.0F * this.getBbWidth() * 2.0F + target.getBbWidth();
        if (this.distanceToSqr(target) > reachSqr) {
            return;
        }

        boolean hit = super.doHurtTarget(target);
        if (hit) {
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 0));
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        if (this.isAttackLocked() || this.attackCooldown > 0) {
            return false;
        }

        if (!(target instanceof LivingEntity livingTarget)) {
            return false;
        }

        this.startAttackLock();
        this.scheduleDelayedAttack(livingTarget);
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 2, state -> {
            if (this.isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("death"));
            }
            if (this.isAttackLocked()) {
                return PlayState.STOP;
            }
            if (state.isMoving()) {
                if (this.entityData.get(RUN_ANIM)) {
                    return state.setAndContinue(RawAnimation.begin().thenLoop("run"));
                }
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
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

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnReason) {
        if (spawnReason == MobSpawnType.SPAWN_EGG || spawnReason == MobSpawnType.COMMAND) {
            return true;
        }
        if (!super.checkSpawnRules(level, spawnReason)) {
            return false;
        }
        if (this.random.nextFloat() * 100.0F >= CommonConfig.spawnChanceHeadHunter) {
            return false;
        }
        if (level.getBlockState(this.blockPosition().below()).isAir()) {
            return false;
        }
        if (!(level instanceof ServerLevelAccessor serverLevel)) {
            return false;
        }
        if (!serverLevel.getLevel().dimension().equals(Level.NETHER)) {
            return false;
        }
        if (serverLevel.getLevel().structureManager()
                .getStructureWithPieceAt(this.blockPosition(), BuiltinStructures.FORTRESS).isValid()) {
            return true;
        }
        if (level.getBiome(this.blockPosition()).is(Biomes.SOUL_SAND_VALLEY)) {
            return true;
        }
        return false;
    }
}
