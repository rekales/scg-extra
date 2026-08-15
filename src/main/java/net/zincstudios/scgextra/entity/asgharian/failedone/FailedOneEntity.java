package net.zincstudios.scgextra.entity.asgharian.failedone;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.zincstudios.scgextra.entity.Faction;
import net.zincstudios.scgextra.entity.common.EquippedEntity;
import net.zincstudios.scgextra.entity.common.goal.HurtByNonFactionGoal;
import org.jetbrains.annotations.Nullable;

import net.zincstudios.scgextra.entity.common.MobUtil;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FailedOneEntity extends EquippedEntity implements GeoEntity {

    private static final int MELEE_DAMAGE_DELAY = 12;  // match with animation

    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    private int hurtDelay = -1;

    public FailedOneEntity(EntityType<? extends Monster> entity, Level level) {
        super(entity, level);
    }

    // TODO: add delayed hit melee goal for general use
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(2, new HurtByNonFactionGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                entity -> Faction.isEnemies(this, entity)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23F)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.MAX_HEALTH, 30.0D);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        SpawnGroupData ret =  super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
        if (this.getRandom().nextBoolean()) {
            this.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.SHIELD));
        }
        return ret;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) return;

        this.hurtDelay--;
        if (this.hurtDelay == 0) {
            LivingEntity target = this.getTarget();
            if (target != null) {
                double distToEnemySqr = this.getPerceivedTargetDistanceSquareForMeleeAttack(target);
                double reach = this.getAttackReachSqr(target) * 1.3;
                if (distToEnemySqr <= reach) {
                    super.doHurtTarget(target);
                }
            }
        }
    }

    private double getAttackReachSqr(LivingEntity attackTarget) {
        return (this.getBbWidth() * 2.0F * this.getBbWidth() * 2.0F + attackTarget.getBbWidth());
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        if (!this.level().isClientSide) {
            if (this.hurtDelay > 0) return false;

            this.triggerAnim("behaviour", "melee");
            this.hurtDelay = MELEE_DAMAGE_DELAY;
        }
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 2, state -> {
            if (state.isMoving()) {
                state.setAnimation(RawAnimation.begin().thenLoop("walk"));
            } else {
                state.setAnimation(RawAnimation.begin().thenLoop("idle"));
            }
            return PlayState.CONTINUE;
        }));

        controllers.add(new AnimationController<>(this, "behaviour", 2, state -> PlayState.STOP)
                .triggerableAnim("melee", RawAnimation.begin().thenPlay("attack"))
                .setAnimationSpeed(1.2f)
        );

        controllers.add(new AnimationController<>(this, "death", 2, state -> {
            if (state.getAnimatable().isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("death"));
            }
            return PlayState.STOP;
        }).setAnimationSpeed(1.5f));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geocache;
    }

    @Override
    protected void tickDeath() {
        MobUtil.tickDeath(this, 30);
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ZOMBIE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_DEATH;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.ZOMBIE_STEP;
    }

    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

}
