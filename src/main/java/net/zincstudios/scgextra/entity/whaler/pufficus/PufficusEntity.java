package net.zincstudios.scgextra.entity.whaler.pufficus;

import net.zincstudios.scgextra.Faction;
import net.zincstudios.scgextra.entity.HurtByNonFactionGoal;
import net.zincstudios.scgextra.entity.projectile.net.NetEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.fluids.FluidType;

import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.ribs.scguns.config.EntityEquipmentConfig;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class PufficusEntity extends Monster implements GeoEntity {

    public static final RawAnimation ATTACK_SWING_ONCE = RawAnimation.begin().then("attack.swing", Animation.LoopType.PLAY_ONCE);
    public static final RawAnimation ATTACK_THROW_ONCE = RawAnimation.begin().then("attack.throw", Animation.LoopType.PLAY_ONCE);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public PufficusEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0F, false));
        this.goalSelector.addGoal(4, new ThrowNetGoal(this, 200, 12F));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.9));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(2, new HurtByNonFactionGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                entity -> Faction.isEnemies(this, entity)));
    }

    // NOTE: maybe just use populateDefaultEquipmentSlots to avoid using this deprecated methods after figuring out EntityEquipmentConfig
    @SuppressWarnings("deprecation")
    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        EntityEquipmentConfig.equipEntity(this, "scgextra:pufficus");  // NOTE: using raw string
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericWalkIdleController(this));
        controllers.add(new AnimationController<>(this, "melee", 0,
                state -> {
                    if (this.swinging) return state.setAndContinue(ATTACK_SWING_ONCE);
                    state.getController().forceAnimationReset();
                    return PlayState.STOP;
                }).setAnimationSpeed(1.3)
        );
        controllers.add(new AnimationController<>(this, "throw", 0, state -> PlayState.CONTINUE)
                .triggerableAnim("throw_net", ATTACK_THROW_ONCE)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    public static class ThrowNetGoal extends Goal {

        protected final PathfinderMob mob;
        protected final int maxInterval;
        protected final float minThrowDistance;
        protected int cooldown = 0;
        protected int throwTicks = 0;

        public ThrowNetGoal(PathfinderMob mob, int maxInterval, float minThrowDistance) {
            this.mob = mob;
            this.maxInterval = maxInterval;
            this.minThrowDistance = minThrowDistance;
        }

        @Override
        public boolean canUse() {
            return this.mob.getTarget() != null;
        }

        @Override
        public void stop() {
            super.stop();
            this.throwTicks = 0;
            this.cooldown = 0;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (this.throwTicks > 0) {
                this.throwTicks--;
            }
            if (this.throwTicks == 7) {  // match with animation frames
                LivingEntity target = this.mob.getTarget();
                if (target != null && this.mob.closerThan(target, minThrowDistance)) {
                    AbstractArrow net = new NetEntity(this.mob, this.mob.level());
                    double d0 = target.getX() - this.mob.getX();
                    double d1 = target.getY(0.3333333333333333) - net.getY();
                    double d2 = target.getZ() - this.mob.getZ();
                    double d3 = Math.sqrt(d0 * d0 + d2 * d2);
                    net.shoot(d0, d1 + d3 * (double)0.2F, d2, 1F, (float)(14 - this.mob.level().getDifficulty().getId() * 4));
                    this.mob.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.mob.getRandom().nextFloat() * 0.4F + 0.8F));
                    this.mob.level().addFreshEntity(net);
                }
            }

            if (this.cooldown > 0) {
                this.cooldown--;
            } else {
                this.cooldown = maxInterval;
                this.throwTicks = 25;
                if (this.mob instanceof GeoEntity geoEntity) {
                    geoEntity.triggerAnim("throw", "throw_net");
                }
            }
        }
    }


    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 60)
                .add(Attributes.ARMOR, 2)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
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
    @Override
    public boolean canDrownInFluidType(FluidType type) {
        return false;
    }
}
