package net.zincstudios.scgextra.entity.neutral.netherite_eater;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.zincstudios.scgextra.CommonConfig;
import net.zincstudios.scgextra.sounds.NeutralSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class NetheriteEaterEntity extends Monster implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final double MAX_CLIMB_HEIGHT = 3.0D;
    private static final int CLIMB_NO_DIG_TICKS = 20;
    private static final int CLIMB_COOLDOWN_TICKS = 10;
    private int breathCooldown;
    private int breathActiveTicks;
    private int digCooldown;
    private int climbNoDigTicks;
    private int climbCooldownTicks;
    private boolean runningForAnim;

    public NetheriteEaterEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setMaxUpStep(3.0F);
        this.xpReward = 60;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 500.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.ATTACK_DAMAGE, 15.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.27D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.9D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 1, true, false,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractPiglin.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Hoglin.class, true));
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living) {
            net.zincstudios.scgextra.entity.neutral.NeutralCombatUtil.applyLacerate(living, 60);
        }
        return hit;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide()) {
            return;
        }

        LivingEntity target = this.getTarget();
        if (breathActiveTicks > 0) {
            breathActiveTicks--;
            applyBreathPulse();
        } else if (breathCooldown > 0) {
            breathCooldown--;
        } else if (target != null && target.isAlive() && this.distanceTo(target) <= 5.0F) {
            breathCooldown = 120;
            breathActiveTicks = 20;
            this.playSound(NeutralSounds.NEUTRAL_NETHERITE_EATER_BREATH.get(), 1.4F, this.getVoicePitch());
        }

        this.runningForAnim = target != null && target.isAlive() && this.distanceTo(target) > 4.5F;
        this.setSprinting(this.runningForAnim);
        this.tickClimbCooldowns();
        boolean climbingThisTick = this.tryClimbTowardTarget(target);

        if (climbingThisTick || this.climbNoDigTicks > 0) {
            if (digCooldown > 0) {
                digCooldown--;
            }
            return;
        }

        if (digCooldown > 0) {
            digCooldown--;
        } else if (target != null && target.isAlive() && this.distanceTo(target) < 20.0F) {
            digCooldown = 10;
            breakBlocksToward(target);
        }
    }

    public boolean isRunningForAnim() {
        return this.runningForAnim;
    }

    private void applyBreathPulse() {
        ServerLevel server = (ServerLevel) this.level();
        server.sendParticles(net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME,
                this.getX(), this.getY() + 1.2D, this.getZ(), 10, 1.5D, 0.5D, 1.5D, 0.02D);

        if (!(breathActiveTicks == 19 || breathActiveTicks == 12 || breathActiveTicks == 5)) {
            return;
        }

        List<LivingEntity> targets = this.level().getEntitiesOfClass(
                LivingEntity.class,
                this.getBoundingBox().inflate(5.0D),
                e -> e != this && e.isAlive()
        );
        for (LivingEntity living : targets) {
            living.hurt(this.damageSources().mobAttack(this), 4.0F);
            living.setSecondsOnFire(4);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    private void tryBreakSoftBlock(BlockPos pos) {
        if (this.level().isClientSide()) return;
        BlockState state = this.level().getBlockState(pos);
        if (state.isAir() || state.getDestroySpeed(this.level(), pos) < 0.0F) return;
        if (state.is(net.minecraft.tags.BlockTags.WITHER_IMMUNE)) return;
        if (!canBreakWithIronPickaxeOnly(state)) return;
        this.level().destroyBlock(pos, true, this);
    }

    private boolean canBreakWithIronPickaxeOnly(BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE)
                && !state.is(BlockTags.NEEDS_DIAMOND_TOOL);
    }

    private void breakBlocksToward(LivingEntity target) {
        double vx = target.getX() - this.getX();
        double vz = target.getZ() - this.getZ();
        double len = Math.sqrt(vx * vx + vz * vz);
        if (len < 0.001D) {
            return;
        }

        double dx = vx / len;
        double dz = vz / len;
        double px = -dz;
        double pz = dx;

        double baseX = this.getX() + dx * 1.35D;
        double baseZ = this.getZ() + dz * 1.35D;
        int baseY = this.blockPosition().getY();

        for (int side = -1; side <= 1; side++) {
            double sx = baseX + px * side * 0.9D;
            double sz = baseZ + pz * side * 0.9D;
            BlockPos front = BlockPos.containing(sx, baseY, sz);
            tryBreakSoftBlock(front);
            tryBreakSoftBlock(front.above());
            tryBreakSoftBlock(front.above(2));
        }
    }

    private void tickClimbCooldowns() {
        if (this.climbNoDigTicks > 0) {
            this.climbNoDigTicks--;
        }
        if (this.climbCooldownTicks > 0) {
            this.climbCooldownTicks--;
        }
    }

    private boolean tryClimbTowardTarget(LivingEntity target) {
        if (target == null || !target.isAlive()) {
            return false;
        }

        double dy = target.getY() - this.getY();
        if (dy <= 0.6D || dy > MAX_CLIMB_HEIGHT + 0.6D) {
            return false;
        }

        double horizontalDistSqr = this.distanceToSqr(target.getX(), this.getY(), target.getZ());
        if (horizontalDistSqr > 16.0D || this.climbCooldownTicks > 0) {
            return false;
        }

        if (!this.onGround() || !this.horizontalCollision) {
            return false;
        }

        Vec3 toward = new Vec3(target.getX() - this.getX(), 0.0D, target.getZ() - this.getZ());
        if (toward.lengthSqr() < 1.0E-4D) {
            toward = this.getLookAngle();
        } else {
            toward = toward.normalize();
        }

        Vec3 current = this.getDeltaMovement();
        this.setDeltaMovement(
                current.x * 0.45D + toward.x * 0.10D,
                Math.max(current.y, 0.24D),
                current.z * 0.45D + toward.z * 0.10D
        );
        this.hasImpulse = true;
        this.climbNoDigTicks = CLIMB_NO_DIG_TICKS;
        this.climbCooldownTicks = CLIMB_COOLDOWN_TICKS;
        return true;
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnReason) {
        if (spawnReason == MobSpawnType.SPAWN_EGG || spawnReason == MobSpawnType.COMMAND) {
            return true;
        }
        if (!super.checkSpawnRules(level, spawnReason)) {
            return false;
        }
        if (this.random.nextFloat() * 100.0F >= CommonConfig.spawnChanceNetheriteEater) {
            return false;
        }
        if (!level.getBiome(this.blockPosition()).is(net.minecraft.world.level.biome.Biomes.NETHER_WASTES)) {
            return false;
        }
        if (level.getBlockState(this.blockPosition().below()).isAir()) {
            return false;
        }
        return true;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        int roll = this.random.nextInt(4);
        if (roll == 0) return NeutralSounds.NEUTRAL_NETHERITE_EATER_IDLE_01.get();
        if (roll == 1) return NeutralSounds.NEUTRAL_NETHERITE_EATER_IDLE_02.get();
        if (roll == 2) return NeutralSounds.NEUTRAL_NETHERITE_EATER_IDLE_03.get();
        return NeutralSounds.NEUTRAL_NETHERITE_EATER_IDLE_04.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return this.random.nextBoolean()
                ? NeutralSounds.NEUTRAL_NETHERITE_EATER_HURT_01.get()
                : NeutralSounds.NEUTRAL_NETHERITE_EATER_HURT_02.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return NeutralSounds.NEUTRAL_NETHERITE_EATER_DEATH.get();
    }
}

