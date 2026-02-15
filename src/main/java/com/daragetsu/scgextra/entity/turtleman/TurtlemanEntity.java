package com.daragetsu.scgextra.entity.turtleman;

import com.daragetsu.scgextra.Faction;
import com.daragetsu.scgextra.SCGExtra;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

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
import top.ribs.scguns.entity.ai.AIType;
import top.ribs.scguns.entity.ai.GunAttackGoal;
import top.ribs.scguns.init.ModEffects;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TurtlemanEntity extends Monster implements RangedAttackMob, GeoEntity {

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    protected StunnedGoal stunnedGoal;

    public TurtlemanEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        stunnedGoal = new StunnedGoal(this);
    }

    @Override
    protected void registerGoals() {
        ItemStack mainHandItem = this.getMainHandItem();

        // TODO: approach enemy while walking backwards behaviour goal
        // Stunned Goal added on finalizeSpawn
        this.goalSelector.addGoal(2, new TurtlemanGunAttackGoal<>(this, mainHandItem, 1.0F, AIType.RECKLESS, 3));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.9));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                // Avoid retaliation from friendly fire
                if (this.mob.getLastHurtByMob() != null && Faction.isFriendlies(this.mob, this.mob.getLastHurtByMob())) {
                    return false;
                }
                return super.canUse();
            }
        });
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                entity -> Faction.isEnemies(this, entity)));
    }

    // NOTE: maybe just use populateDefaultEquipmentSlots to avoid using this deprecated methods after figuring out EntityEquipmentConfig
    @SuppressWarnings("deprecation")
    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        EntityEquipmentConfig.equipEntity(this, "scgextra:turtleman");  // NOTE: using raw string
        this.goalSelector.addGoal(1, this.stunnedGoal);
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    public void performRangedAttack(LivingEntity livingEntity, float v) {
        this.doHurtTarget(livingEntity);
    }

    //somewhat moon walks towards the player
    //leaving comments just in case
    @Override
    public void tick() {
        super.tick();

//        if (this.getTarget() instanceof Player p) {
//            //minimum AABB that's basically a 10 by 10 box centered on the turtleman
//            AABB min = new AABB(
//                this.getX()-5,
//                this.getY()-5,
//                this.getZ()-5,
//                this.getX()+5,
//                this.getY()+5,
//                this.getZ()+5
//            );
//            //only do the custom movement if the player isn't in the box
//            if(!min.contains(p.getEyePosition())){
//                //----------------Rotation------------
//                // Direction from turtleman to player
//                double dx = p.getX() - this.getX();
//                double dz = p.getZ() - this.getZ();
//
//                // yaw toward player, i just copied this from chatgpt cause i can't do maths lol
//                float yawToPlayer = (float)(Math.toDegrees(Math.atan2(-dx, dz)));
//
//                // back faces the player
//                float yawFromPlayer = yawToPlayer + 180f;
//
//                // Apply rotation
//                this.setYRot(yawFromPlayer);
//                this.yBodyRot = yawFromPlayer;
//                this.yHeadRot = yawFromPlayer;
//                //----------------Rotation------------
//
//                //----------------movement-------------
//                //check if the player is looking at the back of the turtleman
//                Vec3 forward = new Vec3(this.getForward().x, 0, this.getForward().z).normalize();
//                Vec3 toPlayer = p.position().subtract(this.position()).normalize();
//                double dot = forward.dot(toPlayer);
//
//                //-0.5 is basically a mid point cone so it's a average
//                if (dot < -0.5) {
//                    Vec3 toPlayerXZ = new Vec3(toPlayer.x, 0, toPlayer.z).normalize();
//                    Vec3 awayFromPlayer = toPlayerXZ.scale(-1);
//                    Vec3 backward = awayFromPlayer.scale(-0.1);
//                    backward = new Vec3(backward.x, this.getDeltaMovement().y, backward.z);
//                    this.setDeltaMovement(backward);
//                    this.move(MoverType.SELF, backward);
//                }
//                //----------------movement-------------
//            }
//        }
    }

    public @Nullable StunnedGoal getStunnedGoal() {
        return this.stunnedGoal;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return super.hurt(source, amount);
    }

    @Override
    public boolean addEffect(MobEffectInstance effectInstance, @Nullable Entity entity) {
        boolean result = super.addEffect(effectInstance, entity);

        if (result
                && (effectInstance.getEffect() == ModEffects.BLINDED.get()
                || effectInstance.getEffect() == ModEffects.DEAFENED.get())
                && this.getStunnedGoal() != null) {
            this.getStunnedGoal().stun(60);
        }

        return result;
    }

    public static final RawAnimation TRANSITION_IDLE_STUNNED = RawAnimation.begin().then("transition.idle_stunned", Animation.LoopType.PLAY_ONCE);
    public static final RawAnimation STUNNED = RawAnimation.begin().then("misc.stunned", Animation.LoopType.LOOP);
    public static final RawAnimation TRANSITION_STUNNED_IDLE = RawAnimation.begin().then("transition.stunned_idle", Animation.LoopType.PLAY_ONCE);



    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Walk/Idle", 0,
                state -> {
                    if (this.stunnedGoal != null && this.stunnedGoal.isStunned()) {
                        return PlayState.STOP;
                    }
                    return state.setAndContinue(state.isMoving() ? DefaultAnimations.WALK : DefaultAnimations.IDLE);
                }
        ));

        controllers.add(new AnimationController<>(this, "stunned", 0,
                state -> {
                    if (this.stunnedGoal != null && this.stunnedGoal.isStunned()) {
                        return PlayState.STOP;
                    }
                    return PlayState.CONTINUE;
                })
                .triggerableAnim("start_stun", TRANSITION_IDLE_STUNNED)
                .triggerableAnim("stunned", STUNNED)
                .triggerableAnim("end_stun", TRANSITION_STUNNED_IDLE)
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

    public static class TurtlemanGunAttackGoal <T extends TurtlemanEntity> extends GunAttackGoal<T> {

        public TurtlemanGunAttackGoal(T shooter, ItemStack gunStack, float speedModifier, AIType aiType, int difficulty) {
            super(shooter, gunStack, speedModifier, aiType, difficulty);
        }

        @Override
        public void tick() {
            if (this.shooter.stunnedGoal != null && this.shooter.stunnedGoal.isStunned()) {
                SCGExtra.LOGGER.debug("stopping");
                return;
            }

            super.tick();
        }
    }

    public static class StunnedGoal extends Goal {

        protected TurtlemanEntity mob;
        protected int stunTimer = 0;
        protected int stunDuration = 0;

        public StunnedGoal(TurtlemanEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public void stun(int ticks) {
            this.stunDuration = ticks;
            this.stunTimer = ticks;
        }

        public boolean isStunned() {
            return this.stunTimer > 0;
        }

        @Override
        public boolean canUse() {
            return this.stunTimer > 0;
        }

        @Override
        public void start() {
            this.mob.getNavigation().stop();
            this.mob.triggerAnim("stunned", "start_stun");
        }

        @Override
        public void stop() {
            this.stunTimer = 0;
            this.mob.removeEffect(ModEffects.DEAFENED.get());
            this.mob.removeEffect(ModEffects.BLINDED.get());  // No panic
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            this.stunTimer--;
            this.mob.getNavigation().stop();

            if (this.stunTimer == stunDuration - 10) {
                this.mob.triggerAnim("stunned", "stunned");
            } else if (this.stunTimer == 10) {
                this.mob.triggerAnim("stunned", "end_stun");
            }

            SCGExtra.LOGGER.debug("stunned: " + this.stunTimer);
        }
    }
}
