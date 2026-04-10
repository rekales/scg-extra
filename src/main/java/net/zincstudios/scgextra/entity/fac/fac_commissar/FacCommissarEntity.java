package net.zincstudios.scgextra.entity.fac.fac_commissar;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
import net.zincstudios.scgextra.entity.Faction;
import net.zincstudios.scgextra.entity.ModEntities;
import net.zincstudios.scgextra.entity.common.GunnerEntity;
import net.zincstudios.scgextra.entity.common.MobUtil;
import net.zincstudios.scgextra.entity.common.ai.HurtByNonFactionGoal;
import net.zincstudios.scgextra.item.HurtEffects;
import net.zincstudios.scgextra.item.ModItems;
import net.zincstudios.scgextra.sounds.ModSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FacCommissarEntity extends GunnerEntity implements GeoEntity {

    private static final int FLARE_LOCK_TICKS = 40;
    private static final int SCAN_ANIMATION_TICKS = 80;
    private static final int SCAN_COOLDOWN_MIN_TICKS = 80;
    private static final int SCAN_COOLDOWN_MAX_TICKS = 180;
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation IDLE_2 = RawAnimation.begin().thenPlayAndHold("idle_lookout");
    private static final RawAnimation AIMING = RawAnimation.begin().thenPlayAndHold("firing stance");
    private static final RawAnimation FLARE = RawAnimation.begin().thenPlay("flare");
    private static final RawAnimation MELEE = RawAnimation.begin().thenPlayXTimes("melee", 1);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private int flareLockTicks = 0;
    private int scanTicks = 0;
    private int scanCooldownTicks = SCAN_COOLDOWN_MIN_TICKS;

    public FacCommissarEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean isLeftHanded() {
        return false;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(3, new FacCommissarAlertGoal(this, 200, true));
        this.goalSelector.addGoal(4, new FacCommissarFlareSummonGoal(this, 400, 60,
                ModEntities.FAC_TRENCHER.get(), ModEntities.FAC_BLUECOAT.get(), ModEntities.TRENCH_GOBLIN.get()));
        this.goalSelector.addGoal(5, new FacCommissarSaberMeleeGoal(this, 1.05D, false));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.95D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(0, new HurtByNonFactionGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                entity -> Faction.isEnemies(this, entity)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.22F)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.MAX_HEALTH, 100.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.flareLockTicks > 0) {
            this.flareLockTicks--;
            this.getNavigation().stop();
            this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
        }
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive() && this.isAiming()) {
            float facing = this.getYHeadRot();
            this.setYRot(facing);
            this.yBodyRot = facing;
            this.yBodyRotO = facing;
        }

        this.updateScanState();
    }

    public void startFlareLock() {
        this.flareLockTicks = Math.max(this.flareLockTicks, FLARE_LOCK_TICKS);
    }

    public boolean isFlareLocked() {
        return this.flareLockTicks > 0;
    }

    private boolean hasLiveTarget() {
        LivingEntity target = this.getTarget();
        return target != null && target.isAlive();
    }

    private boolean isActuallyMoving() {
        double dx = this.getX() - this.xo;
        double dz = this.getZ() - this.zo;
        return dx * dx + dz * dz > 0.000001D;
    }

    private int nextScanCooldown() {
        return SCAN_COOLDOWN_MIN_TICKS + this.random.nextInt(SCAN_COOLDOWN_MAX_TICKS - SCAN_COOLDOWN_MIN_TICKS + 1);
    }

    private void updateScanState() {
        if (this.scanTicks > 0) {
            this.scanTicks--;
            this.getNavigation().stop();
            this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
            return;
        }

        boolean canScan = !this.isFlareLocked()
                && !this.isAiming()
                && !this.hasLiveTarget()
                && !this.getNavigation().isInProgress()
                && !this.isActuallyMoving();

        if (!canScan) {
            return;
        }

        if (this.scanCooldownTicks > 0) {
            this.scanCooldownTicks--;
            return;
        }

        this.scanTicks = SCAN_ANIMATION_TICKS;
        this.scanCooldownTicks = this.nextScanCooldown();
    }

    private boolean shouldPlayIdleLookout() {
        return this.scanTicks > 0;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "walk/idle/aim", 2, state -> {
            if (state.getAnimatable().isFlareLocked()) {
                return state.setAndContinue(IDLE);
            }
            if (state.getAnimatable().isAiming()) {
                return state.setAndContinue(AIMING);
            }
            if (state.getAnimatable().shouldPlayIdleLookout()) {
                return state.setAndContinue(IDLE_2);
            }
            if (state.isMoving() || this.getNavigation().isInProgress()) {
                return state.setAndContinue(WALK);
            }
            return state.setAndContinue(IDLE);
        }).setAnimationSpeed(1.2));

        controllers.add(new AnimationController<>(this, "behaviour", 0, state -> PlayState.STOP)
                .triggerableAnim("flare", FLARE)
        );

        controllers.add(new AnimationController<>(this, "attack", 0, state -> PlayState.STOP)
                .triggerableAnim("melee", MELEE)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit) {
            this.playSound(MobUtil.getSound(
                    this.random,
                    ModSounds.FAC_COMMISSAR_ATTACK_1.get(),
                    ModSounds.FAC_COMMISSAR_ATTACK_2.get()
            ), 1.0F, 1.0F);
            if (this.getMainHandItem().is(ModItems.CAVALRY_SABER.get())
                    || this.getOffhandItem().is(ModItems.CAVALRY_SABER.get())) {
                this.triggerAnim("attack", "melee");
                this.applyHeldWeaponEffects(target);
            }
        }
        return hit;
    }

    private void applyHeldWeaponEffects(Entity target) {
        if (!(target instanceof LivingEntity livingTarget)) {
            return;
        }

        ItemStack main = this.getMainHandItem();
        if (main.getItem() instanceof HurtEffects item) {
            item.hurtEffect(main, livingTarget, this);
            return;
        }

        ItemStack off = this.getOffhandItem();
        if (off.getItem() instanceof HurtEffects item) {
            item.hurtEffect(off, livingTarget, this);
        }
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return MobUtil.getSound(
                this.random,
                ModSounds.FAC_COMMISSAR_HURT_1.get(),
                ModSounds.FAC_COMMISSAR_HURT_2.get(),
                ModSounds.FAC_COMMISSAR_HURT_3.get(),
                ModSounds.FAC_COMMISSAR_HURT_4.get()
        );
    }

    protected SoundEvent getAmbientSound() {
        return MobUtil.getSound(
                this.random,
                ModSounds.FAC_COMMISSAR_IDLE_1.get(),
                ModSounds.FAC_COMMISSAR_IDLE_2.get(),
                ModSounds.FAC_COMMISSAR_IDLE_3.get(),
                ModSounds.FAC_COMMISSAR_IDLE_4.get(),
                ModSounds.FAC_COMMISSAR_LINE_2.get()
        );
    }

    protected SoundEvent getDeathSound() {
        return MobUtil.getSound(
                this.random,
                ModSounds.FAC_COMMISSAR_DEATH_1.get(),
                ModSounds.FAC_COMMISSAR_DEATH_2.get()
        );
    }
}
