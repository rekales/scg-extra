package net.zincstudios.scgextra.entity.whaler.turtleman;

import net.zincstudios.scgextra.CommonConfig;
import net.zincstudios.scgextra.entity.Faction;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;

import net.zincstudios.scgextra.entity.common.GunnerEntity;
import net.zincstudios.scgextra.entity.common.HeadShotHandler;
import net.zincstudios.scgextra.entity.common.Stunnable;
import net.zincstudios.scgextra.entity.common.ai.HurtByNonFactionGoal;
import net.zincstudios.scgextra.entity.common.ai.StunnedWithVisualGoal;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.ribs.scguns.entity.ai.AIType;
import top.ribs.scguns.entity.ai.GunAttackGoal;
import top.ribs.scguns.init.ModEffects;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TurtlemanEntity extends GunnerEntity implements GeoEntity, Stunnable, HeadShotHandler {

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    // Server-side only for stunnable handling
    private boolean shouldStun = false;
    private int headshotCounter = 0;

    public TurtlemanEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public int shouldStun() {
        if (!CommonConfig.enableAbilityWeakness) return 0;

        if (this.shouldStun || this.headshotCounter >= CommonConfig.abilityWeaknessHeadshots) {
//            return CommonConfig.abilityWeaknessDuration;
            return 100;
        }
        return 0;
    }

    @Override
    public void setStunned(boolean stunned) {
        if (stunned) {
            this.triggerAnim("behaviour", "stun");
        } else {
            this.shouldStun = false;
            this.headshotCounter = 0;
        }
    }

    @Override
    public boolean tickStunned(int ticksLeft) {
        if (ticksLeft == 10) {
            this.triggerAnim("behaviour", "end_stun");
        }
        return false;
    }

    @Override
    public boolean headshot(DamageSource source, float amount) {
        this.headshotCounter++;
        return false;
    }

    @Override
    protected void registerGoals() {
        ItemStack mainHandItem = this.getMainHandItem();

        // TODO: approach enemy while walking backwards behaviour goal
        this.goalSelector.addGoal(1, new StunnedWithVisualGoal<>(this));
        this.goalSelector.addGoal(2, new TurtlemanGunAttackGoal<>(this, mainHandItem, 1.0F, AIType.RECKLESS, 3, 10F));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.9));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(2, new HurtByNonFactionGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                entity -> Faction.isEnemies(this, entity)));
    }

    @Override
    public void tick() {
        super.tick();

        this.goalSelector.getRunningGoals()
                .filter(wrappedGoal -> wrappedGoal.getGoal() instanceof TurtlemanGunAttackGoal<?>)
                .findFirst()
                .ifPresent(wrappedGoal -> {

                    // TODO: rewrite to put logic in the goal tick
                    // TODO: override move control or something I dunno. I'm tired of dealing with this.
                    if (wrappedGoal.getGoal() instanceof TurtlemanGunAttackGoal<?> goal && goal.active) {
                        LivingEntity target = this.getTarget();
                        if (target == null || this.isStunned()) return;

                        // Direction from turtleman to player
                        double dx = target.getX() - this.getX();
                        double dz = target.getZ() - this.getZ();

                        // yaw toward player, i just copied this from chatgpt cause i can't do maths lol
                        float yawToPlayer = (float)(Math.toDegrees(Math.atan2(-dx, dz)));

                        // back faces the player
                        float yawFromPlayer = yawToPlayer + 180f;

                        // Apply rotation
                        this.setYRot(yawFromPlayer);
                        this.yBodyRot = yawFromPlayer;
                        this.yHeadRot = yawFromPlayer;

                        //check if the player is looking at the back of the turtleman
                        Vec3 forward = new Vec3(this.getForward().x, 0, this.getForward().z).normalize();
                        Vec3 toPlayer = target.position().subtract(this.position()).normalize();
                        double dot = forward.dot(toPlayer);

                        //-0.5 is basically a mid point cone so it's a average
                        if (dot < -0.5) {
                            Vec3 toPlayerXZ = new Vec3(toPlayer.x, 0, toPlayer.z).normalize();
                            Vec3 awayFromPlayer = toPlayerXZ.scale(-1);
                            Vec3 backward = awayFromPlayer.scale(-0.1);
                            backward = new Vec3(backward.x, this.getDeltaMovement().y, backward.z);
                            this.setDeltaMovement(backward);
                            this.move(MoverType.SELF, backward);
                        }
                    }

                });
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (CommonConfig.enableAbilityBulletproof) {
            Vec3 attackVector = source.getSourcePosition();
            if (!this.isStunned() && attackVector != null) {
                attackVector = attackVector.subtract(this.position()).normalize();
                Vec3 lookVector = this.getLookAngle();

                double dotProduct = attackVector.dot(lookVector);
                if (dotProduct < 0) {
                    return false;
                }
            }
        }

        return super.hurt(source, amount);
    }

    @Override
    public boolean addEffect(MobEffectInstance effectInstance, @Nullable Entity entity) {
        if (effectInstance.getEffect() == ModEffects.BLINDED.get()
                || effectInstance.getEffect() == ModEffects.DEAFENED.get()) {
            this.shouldStun = true;
        }
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericWalkIdleController(this));
        controllers.add(new AnimationController<>(this, "behaviour", 0, state -> PlayState.STOP)
                .triggerableAnim("stun", RawAnimation.begin().thenPlay("transition.idle_stunned").thenLoop("misc.stunned"))
                .triggerableAnim("end_stun", RawAnimation.begin().thenPlay("transition.stunned_idle"))
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }


    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30)
                .add(Attributes.ARMOR, 12)
                .add(Attributes.FOLLOW_RANGE, 48)
                .add(Attributes.MOVEMENT_SPEED, 0.3);
    }

    private static class TurtlemanGunAttackGoal <T extends TurtlemanEntity> extends GunAttackGoal<T> {

        private final float minApproachDist;
        public boolean active = false;

        public TurtlemanGunAttackGoal(T shooter, ItemStack gunStack, float speedModifier, AIType aiType, int difficulty, float minApproachDist) {
            super(shooter, gunStack, speedModifier, aiType, difficulty);
            this.minApproachDist = minApproachDist;
        }

        @Override
        public void tick() {
            if (this.shooter.isStunned()) {
                return;
            }

            LivingEntity target = this.shooter.getTarget();
            if (target == null) return;

            if (!this.shooter.closerThan(target, minApproachDist)
                    && !this.isReloading
                    && this.aimingStabilityTimer < 10) {  // Started aiming

                this.active = true;

            } else {
                active = false;
                super.tick();
            }
        }
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        return false;
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
        if(!pLevel.isClientSide()){
            ServerLevel pServerLevel = (ServerLevel) pLevel;
            BlockPos pPos = this.blockPosition();
            if (!pServerLevel.getFluidState(pPos.below()).is(FluidTags.WATER)) {
                return false;
            } else {
                Holder<Biome> holder = pServerLevel.getBiome(pPos);
                boolean flag = pServerLevel.getDifficulty() != Difficulty.PEACEFUL && isDarkEnoughToSpawn(pServerLevel, pPos, this.random) && (pSpawnReason == MobSpawnType.SPAWNER || pServerLevel.getFluidState(pPos).is(FluidTags.WATER));
                if (holder.is(BiomeTags.MORE_FREQUENT_DROWNED_SPAWNS)) {
                    return this.random.nextInt(15) == 0 && flag;
                } else {
                    return this.random.nextInt(40) == 0 && isDeepEnoughToSpawn(pServerLevel, pPos) && flag;
                }
            }
        }else{
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    private static boolean isDeepEnoughToSpawn(LevelAccessor pLevel, BlockPos pPos) {
        return pPos.getY() < pLevel.getSeaLevel() - 5;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader pLevel) {
        return pLevel.isUnobstructed(this);
    }
}
