package net.zincstudios.scgextra.entity.asgharian.soulripper;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.zincstudios.scgextra.entity.Faction;
import net.zincstudios.scgextra.entity.common.MobUtil;
import net.zincstudios.scgextra.entity.common.ai.HurtByNonFactionGoal;
import net.zincstudios.scgextra.entity.common.client.ExpandedAnimationController;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.ribs.scguns.init.ModEffects;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SoulRipperEntity extends Monster implements GeoEntity {

    private static final EntityDataAccessor<Boolean> CHARGING =
            SynchedEntityData.defineId(SoulRipperEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> LIVES =
            SynchedEntityData.defineId(SoulRipperEntity.class, EntityDataSerializers.INT);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation MOVE = RawAnimation.begin().thenLoop("move");
    private static final RawAnimation MELEE = RawAnimation.begin().thenPlay("melee");
    private static final RawAnimation FIREBALL = RawAnimation.begin().thenPlay("fireball");
    private static final RawAnimation SUMMON = RawAnimation.begin().thenPlay("summon");
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("death");
    private static final RawAnimation REVIVE = RawAnimation.begin().thenPlay("revive");
    private static final RawAnimation LANTERN_1_OFF = RawAnimation.begin().thenPlayAndHold("lantern_1_flickering");
    private static final RawAnimation LANTERN_2_OFF = RawAnimation.begin().thenPlayAndHold("lantern_2_flickering");
    private static final RawAnimation LANTERN_3_OFF = RawAnimation.begin().thenPlayAndHold("lantern_3_flickering");

    // Match with anims
    private static final int DEATH_DURATION_TICKS = 40;
    private static final int REVIVE_DURATION_TICKS = 50;

    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);
    private final LanternPartEntity[] subEntities;

    private boolean reviving = false;

    // Serverside only
    private BlockPos boundOrigin = null;

    // Clientside only
    private int lanterns = 0;

    public SoulRipperEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.subEntities = new LanternPartEntity[] {
                new LanternPartEntity(this, new Vec3(-1.25, 2.3, -0.025), 5/16f, 8/16f),
                new LanternPartEntity(this, new Vec3(0.725, 2.25, -0.2), 5/16f, 8/16f),
                new LanternPartEntity(this, new Vec3(0, 2.825, -0.275), 5/16f, 8/16f)
        };
        this.moveControl = new SoulRipperMoveControl(this);

        if (!level.isClientSide) {
            this.setLives(3);
        }
    }

    public void updateBoundOrigin() {
        Vec3 start = this.position().add(0,2,0);
        Vec3 end = start.add(0, -64, 0);

        BlockHitResult result = this.level().clip(new ClipContext(
                start, end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.ANY,
                this
        ));

        if (result.getType() != HitResult.Type.MISS) {
            this.boundOrigin = result.getBlockPos();
        }
    }

    public void tick() {


//        this.setYRot(this.getYRot()+1f);
//        this.setYHeadRot(this.getYRot());
//        this.setYBodyRot(this.getYRot());

        this.noPhysics = !this.isDeadOrDying();
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);
        for (LanternPartEntity subEntity : this.subEntities) {
            subEntity.updatePos();
        }
        if (this.tickCount % 10 == 0) {
            this.updateBoundOrigin();
        }
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.getLives() > 0) {
            if (this.deathTime >= DEATH_DURATION_TICKS + REVIVE_DURATION_TICKS - 15) {
                this.deathTime = 0;
                this.setLives(this.getLives() - 1);
                this.setHealth(this.getMaxHealth());
                this.dead = false;

//                if (this.getLives() == 1) {
//                    this.summonVexes();
//                }
            } else if (this.deathTime == DEATH_DURATION_TICKS && !this.level().isClientSide) {
                this.triggerAnim("revive", "revive");
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
        if (damageSource.getEntity() == null && damageSource.getDirectEntity() == null && damageSource.is(DamageTypes.GENERIC_KILL)) {
            this.setLives(0);  // Kill command, unsure if it could be caused by other things
        }
        super.die(damageSource);

        if (damageSource.getEntity() != null) {
            Vec3 sourcePos = damageSource.getEntity().position();
            Vec3 dir = sourcePos.subtract(this.position()).normalize();
//            Vec3 dir = this.position().subtract(sourcePos).normalize();
            this.setDeltaMovement(dir.scale(-0.5).add(0,-0.15,0));
        } else {
            this.setDeltaMovement(this.getDeltaMovement().add(0,-0.15,0));
        }
//        this.setDeltaMovement(MobUtil.getVecFromRot(this.getYRot())
//                .scale(0.5f)
//                .add(0,-0.15,0)
//        );
    }

    @Override
    protected boolean shouldDropLoot() {
        return this.getLives() == 0;
    }

    @Override
    public @Nullable PartEntity<?>[] getParts() {
        return this.subEntities;
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
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

    public BlockPos getBoundOrigin() {
        return Objects.requireNonNullElseGet(this.boundOrigin, () -> BlockPos.containing(this.position()));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(3, new SoulRipperChargeAttackGoal(this, 80));
        this.goalSelector.addGoal(5, new SoulRipperFireballGoal(this).cooldown(200));
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
        controllers.add(new ExpandedAnimationController<>(this, "main", 5, state -> {
            if (state.isMoving()) {
                return state.setAndContinue(MOVE);
            } else {
                return state.setAndContinue(IDLE);
            }
        }));

        controllers.add(new ExpandedAnimationController<>(this, "lantern", 0, state ->
                switch(state.getAnimatable().getLives()) {
                    case 2 -> state.setAndContinue(LANTERN_1_OFF);
                    case 1 -> state.setAndContinue(LANTERN_2_OFF);
                    case 0 -> state.setAndContinue(LANTERN_3_OFF);
                    default -> PlayState.STOP;
        }));

//        controllers.add(new AnimationController<>(this, "death", 2, state -> {
//            if (state.getAnimatable().isDeadOrDying()) {
//                return state.setAndContinue(DEATH);
//            }
//            return PlayState.STOP;
//        }).triggerableAnim("revive", REVIVE));

        controllers.add(new AnimationController<>(this, "revive", 2, state -> PlayState.STOP)
                .triggerableAnim("revive", REVIVE));

//                controllers.add(new ExpandedAnimationController<>(this, "lantern_1", 0,
//                state -> state.setAndContinue(state.getAnimatable().getLives() >= 2 ? LANTERN_1_ON : LANTERN_1_OFF)));
//        controllers.add(new ExpandedAnimationController<>(this, "lantern_2", 0,
//                state -> state.setAndContinue(state.getAnimatable().getLives() >= 3 ? LANTERN_2_ON : LANTERN_2_OFF)));
//        controllers.add(new ExpandedAnimationController<>(this, "lantern_3", 0,
//                state -> state.setAndContinue(state.getAnimatable().getLives() >= 1 ? LANTERN_3_ON : LANTERN_3_OFF)));

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geocache;
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.boundOrigin = MobUtil.getBlocKPosFromTag("Bound", tag);
        if (tag.contains("Lives")) {
            this.setLives(tag.getInt("Lives"));
        }
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.boundOrigin != null) {
            MobUtil.putBlockPosToTag(this.boundOrigin, "Bound", tag);
        }
        tag.putInt("Lives", this.getLives());
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

    public boolean canMelee() {
        return true;
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
