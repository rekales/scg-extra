package net.zincstudios.scgextra.entity.asgharian.soulripper;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.zincstudios.scgextra.entity.Faction;
import net.zincstudios.scgextra.entity.common.MobUtil;
import net.zincstudios.scgextra.entity.common.ai.HurtByNonFactionGoal;
import net.zincstudios.scgextra.entity.common.client.ExpandedAnimationController;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.ribs.scguns.init.ModEffects;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SoulRipperEntity extends Monster implements GeoEntity {

    private static final EntityDataAccessor<Boolean> CHARGING =
            SynchedEntityData.defineId(SoulRipperEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> LIVES =
            SynchedEntityData.defineId(SoulRipperEntity.class, EntityDataSerializers.INT);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");

    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    // Serverside only
    private BlockPos boundOrigin = null;

    public SoulRipperEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new SoulRipperMoveControl(this);

        if (!level.isClientSide) {
            this.setLives(3);
        }
    }

    public void tick() {
        this.noPhysics = !this.isDeadOrDying();
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.getLives() > 0) {
            if (this.deathTime > 60) {
                this.deathTime = 0;
                this.setLives(this.getLives() - 1);
                this.setHealth(this.getMaxHealth());
                this.dead = false;

                if (this.getLives() == 1) {
                    this.summonVexes();
                }
            }
        } else {
            if (this.deathTime >= 35 && !this.level().isClientSide() && !this.isRemoved()) {
                this.level().broadcastEntityEvent(this, (byte)60);
                this.remove(Entity.RemovalReason.KILLED);
            }
        }
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        this.setDeltaMovement(this.getDeltaMovement().add(0,-0.25,0));
    }

    @Override
    protected boolean shouldDropLoot() {
        return this.getLives() == 0;
    }

    private void summonVexes() {
        if (this.level() instanceof ServerLevel serverlevel) {
            for(int i = 0; i < 3; ++i) {
                BlockPos blockpos = this.blockPosition().offset(-2 + this.random.nextInt(5), 1, -2 + this.random.nextInt(5));
                Vex vex = EntityType.VEX.create(this.level());
                if (vex != null) {
                    vex.moveTo(blockpos, 0.0F, 0.0F);
                    vex.finalizeSpawn(serverlevel, this.level().getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
                    vex.setOwner(this);
                    vex.setBoundOrigin(blockpos);
                    vex.setLimitedLife(20 * (30 + this.random.nextInt(90)));
                    serverlevel.addFreshEntityWithPassengers(vex);
                }
            }
        }
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        if (entity instanceof LivingEntity target) {
            target.addEffect(new MobEffectInstance(ModEffects.LACERATED.get(), 60));
            if (this.getLives() == 0) {
                target.setSecondsOnFire(3);
            }
        }
        return super.doHurtTarget(entity);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        this.boundOrigin = BlockPos.containing(this.position());
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    public @Nullable BlockPos getBoundOrigin() {
        return this.boundOrigin;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(3, new SoulRipperChargeAttackGoal(this, 80));
        this.goalSelector.addGoal(5, new SoulRipperThrowFireballGoal(this, 200));
        this.goalSelector.addGoal(8, new SoulRipperRandomMoveGoal(this));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));

        // Bosses should prioritize players
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(3, new HurtByNonFactionGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                entity -> Faction.isEnemies(this, entity)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.ATTACK_DAMAGE, 15.0D)
                .add(Attributes.ARMOR, 7.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1)
                .add(Attributes.MAX_HEALTH, 40.0D);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new ExpandedAnimationController<>(this, "main", 2,
                state -> state.setAndContinue(IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geocache;
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.boundOrigin = MobUtil.getBlocKPosFromTag("Bound", tag);
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.boundOrigin != null) {
            MobUtil.putBlockPosToTag(this.boundOrigin, "Bound", tag);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CHARGING, false);
        this.entityData.define(LIVES, 3);
    }

    public boolean canFireball() {
        return !isCharging();  // TODO: state checks
    }

    // TODO: Use GoalStateHandler
    public boolean isCharging() {
        return this.entityData.get(CHARGING);
    }

    public void setCharging(boolean charging) {
        this.entityData.set(CHARGING, charging);
    }

    public int getLives() {
        return this.entityData.get(LIVES);
    }

    private void setLives(int lives) {
        this.entityData.set(LIVES, lives);
    }

}
