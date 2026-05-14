package net.zincstudios.scgextra.entity.neutral.end_dweller;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.zincstudios.scgextra.CommonConfig;
import net.zincstudios.scgextra.sounds.NeutralSounds;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class EndDwellerEntity extends Monster implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private int wanderTicks;
    private double wanderX;
    private double wanderY;
    private double wanderZ;
    private int flightCyclesSinceGroundWalk;
    private int flightCyclesRequiredBeforeGroundWalk = 2;
    private int forcedGroundWalkTicks;
    private int explodeAnimTicks;
    private boolean pendingExplosion;
    private boolean explosionTriggered;
    private DamageSource pendingDeathSource;

    public EndDwellerEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ARMOR, 0.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.18D);
    }

    @Override
    protected void registerGoals() {
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.setTarget(null);

        if (!this.level().isClientSide()) {
            if (pendingExplosion) {
                this.setNoGravity(true);
                if (explodeAnimTicks > 0) {
                    explodeAnimTicks--;
                    this.setDeltaMovement(this.getDeltaMovement().scale(0.6D));
                    ((ServerLevel) this.level()).sendParticles(
                            new DustParticleOptions(new Vector3f(0.25F, 1.0F, 0.45F), 1.2F),
                            this.getX(), this.getY() + 0.5D, this.getZ(),
                            8, 0.4D, 0.25D, 0.4D, 0.01D
                    );
                } else {
                    if (!this.explosionTriggered) {
                        this.explosionTriggered = true;
                        explodeNow();
                        super.die(pendingDeathSource == null ? this.damageSources().magic() : pendingDeathSource);
                        this.pendingExplosion = false;
                        this.level().broadcastEntityEvent(this, (byte) 60);
                        this.remove(RemovalReason.KILLED);
                    }
                }
                return;
            }

            if (this.forcedGroundWalkTicks > 0) {
                this.forcedGroundWalkTicks--;
                if (this.forcedGroundWalkTicks == 0) {
                    this.flightCyclesSinceGroundWalk = 0;
                    this.flightCyclesRequiredBeforeGroundWalk = 2 + this.random.nextInt(2);
                    this.wanderTicks = 0;
                }
            }

            this.setNoGravity(this.forcedGroundWalkTicks <= 0);
            tickFloatingWander();
        }
    }

    private void tickFloatingWander() {
        boolean groundWalkPhase = this.forcedGroundWalkTicks > 0;
        if (!groundWalkPhase && !hasSolidGroundBelow(this.getX(), this.getZ(), 80)) {
            setVoidEscapeTarget();
        }

        if (wanderTicks <= 0 || this.distanceToSqr(wanderX, wanderY, wanderZ) < 2.5D) {
            if (groundWalkPhase) {
                wanderTicks = 55 + this.random.nextInt(35);
                setGroundWalkTarget();
            } else {
                wanderTicks = 70 + this.random.nextInt(60);
                setFlightTargetWithGround();

                this.flightCyclesSinceGroundWalk++;
                if (this.flightCyclesSinceGroundWalk >= this.flightCyclesRequiredBeforeGroundWalk && hasWalkableSurfaceNearby()) {
                    startGroundWalkPhase();
                    setGroundWalkTarget();
                    groundWalkPhase = true;
                }
            }
        } else {
            wanderTicks--;
        }

        double dx = wanderX - this.getX();
        double dy = wanderY - this.getY();
        double dz = wanderZ - this.getZ();
        double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len < 0.001D) {
            return;
        }

        if (groundWalkPhase) {
            double speed = 0.017D;
            this.setDeltaMovement(
                    this.getDeltaMovement().scale(0.80D).add(dx / len * speed, dy / len * speed * 0.32D, dz / len * speed)
            );
        } else {
            double speed = 0.028D;
            this.setDeltaMovement(
                    this.getDeltaMovement().scale(0.86D).add(dx / len * speed, dy / len * speed * 1.05D, dz / len * speed)
            );
            if (this.horizontalCollision) {
                Vec3 motion = this.getDeltaMovement();
                this.setDeltaMovement(motion.x, Math.max(motion.y, 0.30D), motion.z);
            }
        }

        float desiredYaw = (float) (Math.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        float nextYaw = Mth.approachDegrees(this.getYRot(), desiredYaw, 3.0F);
        this.setYRot(nextYaw);
        this.setYBodyRot(nextYaw);
        this.setYHeadRot(nextYaw);
    }

    private void startGroundWalkPhase() {
        this.forcedGroundWalkTicks = 65 + this.random.nextInt(40);
        this.wanderTicks = 0;
    }

    private void setGroundWalkTarget() {
        double groundY = findGroundSurfaceY(this.blockPosition(), 12);
        this.wanderX = this.getX() + (this.random.nextDouble() - 0.5D) * 3.8D;
        this.wanderY = groundY;
        this.wanderZ = this.getZ() + (this.random.nextDouble() - 0.5D) * 3.8D;
    }

    private boolean hasWalkableSurfaceNearby() {
        return findGroundSurfaceY(this.blockPosition(), 12) > this.getY() - 4.0D;
    }

    private double findGroundSurfaceY(BlockPos origin, int maxDown) {
        for (int offset = 0; offset <= maxDown; offset++) {
            BlockPos check = origin.below(offset);
            BlockState state = this.level().getBlockState(check);
            if (!state.isAir() && state.blocksMotion()) {
                return check.getY() + 1.02D;
            }
        }
        return this.getY();
    }

    private void setFlightTargetWithGround() {
        for (int i = 0; i < 24; i++) {
            double candidateX = this.getX() + randomFlightOffset();
            double candidateZ = this.getZ() + randomFlightOffset();
            if (!hasSolidGroundBelow(candidateX, candidateZ, 80)) {
                continue;
            }
            this.wanderX = candidateX;
            this.wanderY = this.getY() + (this.random.nextDouble() - 0.5D) * 1.1D;
            this.wanderZ = candidateZ;
            return;
        }

        setVoidEscapeTarget();
    }

    private void setVoidEscapeTarget() {
        for (int i = 0; i < 32; i++) {
            double radius = 8.0D + this.random.nextDouble() * 6.0D;
            double angle = this.random.nextDouble() * Math.PI * 2.0D;
            double candidateX = this.getX() + Math.cos(angle) * radius;
            double candidateZ = this.getZ() + Math.sin(angle) * radius;
            if (!hasSolidGroundBelow(candidateX, candidateZ, 90)) {
                continue;
            }
            this.wanderX = candidateX;
            this.wanderY = this.getY() + 0.6D;
            this.wanderZ = candidateZ;
            this.wanderTicks = 45 + this.random.nextInt(25);
            return;
        }

        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(-motion.x * 0.6D, Math.max(motion.y, 0.25D), -motion.z * 0.6D);
        this.wanderX = this.getX() - motion.x * 10.0D;
        this.wanderY = this.getY() + 0.8D;
        this.wanderZ = this.getZ() - motion.z * 10.0D;
        this.wanderTicks = 35;
    }

    private boolean hasSolidGroundBelow(double x, double z, int maxDown) {
        int startY = Mth.floor(this.getY());
        int bx = Mth.floor(x);
        int bz = Mth.floor(z);
        for (int offset = 0; offset <= maxDown; offset++) {
            BlockPos check = new BlockPos(bx, startY - offset, bz);
            BlockState state = this.level().getBlockState(check);
            if (!state.isAir() && state.blocksMotion()) {
                return true;
            }
        }
        return false;
    }

    private double randomFlightOffset() {
        double distance = 7.0D + this.random.nextDouble() * 3.0D;
        return this.random.nextBoolean() ? distance : -distance;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource source) {
        return false;
    }

    private void explodeNow() {
        this.playSound(NeutralSounds.NEUTRAL_END_DWELLER_CHARGING.get(), 1.3F, this.getVoicePitch());
        this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(10.0D), e -> e != this && e.isAlive())
                .forEach(living -> {
                    if (living instanceof Player player && (player.isCreative() || player.isSpectator())) {
                        return;
                    }
                    living.hurt(this.damageSources().magic(), 18.0F);
                    living.setSecondsOnFire(4);
                    double dx = living.getX() - this.getX();
                    double dz = living.getZ() - this.getZ();
                    living.knockback(1.8D, dx, dz);
                });
        if (this.level() instanceof ServerLevel server) {
            server.sendParticles(new DustParticleOptions(new Vector3f(0.25F, 1.0F, 0.45F), 1.5F),
                    this.getX(), this.getY() + 0.7D, this.getZ(), 80, 0.8D, 0.4D, 0.8D, 0.02D);
        }
        this.level().explode(this, this.getX(), this.getY(), this.getZ(), 3.0F, Level.ExplosionInteraction.MOB);
    }

    @Override
    public void die(DamageSource source) {
        if (this.level().isClientSide()) {
            super.die(source);
            return;
        }
        if (this.explosionTriggered) {
            super.die(source);
            return;
        }
        if (!pendingExplosion) {
            pendingExplosion = true;
            pendingDeathSource = source;
            explodeAnimTicks = 20;
            this.setHealth(1.0F);
            this.setDeltaMovement(0.0D, 0.0D, 0.0D);
            return;
        }
        super.die(source);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (pendingExplosion) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    @Override
    protected void tickDeath() {
        this.deathTime++;
        if (this.deathTime >= 20 && !this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 2, state -> {
            if (this.pendingExplosion || this.explodeAnimTicks > 0) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk_insect"));
            }
            if (this.isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk_insect"));
            }
            if (this.onGround() && state.isMoving()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk_2"));
            }
            if (!this.onGround() && state.isMoving()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        int roll = this.random.nextInt(3);
        if (roll == 0) return NeutralSounds.NEUTRAL_END_DWELLER_IDLE_01.get();
        if (roll == 1) return NeutralSounds.NEUTRAL_END_DWELLER_IDLE_02.get();
        return NeutralSounds.NEUTRAL_END_DWELLER_IDLE_03.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return this.random.nextBoolean()
                ? NeutralSounds.NEUTRAL_END_DWELLER_HURT_01.get()
                : NeutralSounds.NEUTRAL_END_DWELLER_HURT_02.get();
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnReason) {
        if (spawnReason == MobSpawnType.SPAWN_EGG || spawnReason == MobSpawnType.COMMAND) {
            return true;
        }
        if (net.zincstudios.scgextra.entity.neutral.NeutralCombatUtil.isWaterAtOrBelow(level, this.blockPosition())) {
            return false;
        }
        return this.random.nextFloat() * 100.0F < CommonConfig.spawnChanceEndDweller
                && net.zincstudios.scgextra.entity.neutral.NeutralCombatUtil.canSpawnEndSurface(level, this.blockPosition());
    }
}



