package net.zincstudios.scgextra.entity.neutral.overworld.big_lump;

import net.zincstudios.scgextra.entity.common.MobUtil;
import net.zincstudios.scgextra.entity.common.part.RotatedWeakPointPartEntity;

import javax.annotation.Nullable;

import net.zincstudios.scgextra.entity.common.gun.CustomScorchedSimGun;
import net.zincstudios.scgextra.network.GunFlashMessage;
import net.zincstudios.scgextra.network.SCGEPacketHandler;
import net.zincstudios.scgextra.entity.asgharian.BulletSpawnOffset;
import net.zincstudios.scgextra.entity.common.Gunner;
import net.zincstudios.scgextra.entity.common.gun.CustomGunHolder;
import net.zincstudios.scgextra.entity.common.gun.SimulatedGun;
import net.zincstudios.scgextra.sounds.NeutralSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.ribs.scguns.ScorchedGuns;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.init.ModItems;

public class BigLumpEntity extends Monster implements GeoEntity, Gunner, CustomGunHolder, BulletSpawnOffset{
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final Vec3 GUN_OFFSET = new Vec3(0,2,2.5);
    private final SimulatedGun customGun;

    private final PartEntity<?>[] subEntities;

    private static final EntityDataAccessor<Float> INACCURACY = SynchedEntityData.defineId(BigLumpEntity.class, EntityDataSerializers.FLOAT);

    public BigLumpEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.subEntities = new PartEntity[] {
                new RotatedWeakPointPartEntity<>(this, new Vec3(-0.6, 2.3, 1.5), 0.5f, 0.5f),
                new RotatedWeakPointPartEntity<>(this, new Vec3(0.6, 2.3, 1.5), 0.5f, 0.5f),
        };
        this.customGun = new CustomScorchedSimGun.Builder(ModItems.BIRDFEEDER.get().getGun())
                .projectileDamage(4f)
                .fireRate(2)
                .maxRange(16)
                .idealRange(12)
                .noGunFlash() // handled on onGunFire instead
                .velocityModifier(vec -> vec.scale(1/2f))
                .build();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.5));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.goalSelector.addGoal(2, new BigLumpMeleeAttackGoal(this, 0.5, true));
        this.goalSelector.addGoal(2, new BigLumpGunAttackGoal(this, 8));
        this.goalSelector.addGoal(3, new MoveTowardsTargetGoal(this, 0.5, 20));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
        .add(Attributes.MAX_HEALTH, 150.0)
        .add(Attributes.MOVEMENT_SPEED, 0.5)
        .add(Attributes.KNOCKBACK_RESISTANCE, 0.6)
        .add(Attributes.ATTACK_KNOCKBACK, 1)
        .add(Attributes.ATTACK_DAMAGE, 6)
        .add(Attributes.ARMOR, 0);
    }

    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            if (state.isMoving()) {
                state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            } else {
                state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
            }
            return PlayState.CONTINUE;
        })
        .triggerableAnim("melee_attack", RawAnimation.begin().thenPlay("melee_attack"))
        .triggerableAnim("shoot_attack", RawAnimation.begin().thenPlay("shoot_attack")));
        controllers.add(new AnimationController<>(this, "death", 2, state -> {
            if (state.getAnimatable().isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("death"));
            } else {
                return PlayState.STOP;
            }
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(INACCURACY, 10F);
    }
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
    }
    @Override
    protected void tickDeath() {
        MobUtil.tickDeath(this, 30);
    }
    protected void tickSubEntities() {
        for(PartEntity<?> partEntity : this.getParts()) {
            partEntity.tick();
        }
    }
    @Override
    public void tick() {
        super.tick();
        this.tickSubEntities();
    }
    @Override
    public PartEntity<?>[] getParts() {
        return this.subEntities;
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }
    public void lowerInaccuracy(){
        float inaccuracy = getInaccuracy();
        if(inaccuracy>0){
            this.entityData.set(INACCURACY, getInaccuracy()-0.2F);
        }
    }
    public float getInaccuracy(){
        return this.entityData.get(INACCURACY);
    }
    @Override
    protected float getSoundVolume() {
        return 2;
    }
    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return NeutralSounds.BIG_LUMP_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return NeutralSounds.BIG_LUMP_HURT.get();
    }
    @Override
    protected SoundEvent getDeathSound() {
        return NeutralSounds.BIG_LUMP_DEAD.get();
    }

    @Override
    public SimulatedGun getCustomGun() {
        return this.customGun;
    }
    @Override
    public void onGunFire(SimulatedGun gun, Vec3 targetPos) {
        Gun.Display.Flash flash = ModItems.BIRDFEEDER.get().getGun().getDisplay().getFlash();
        if (flash == null) return;
        ResourceLocation flashTexture = ResourceLocation.fromNamespaceAndPath(ScorchedGuns.MODID,
                "textures/effect/" + flash.getTextureLocation() + ".png");
        SCGEPacketHandler.sendToNearbyPlayers(() -> MobUtil.levelLocationFromEntity(this),
                new GunFlashMessage(this.getId(), 0, flashTexture, false, 1.2F));
    }
    @Override
    public Vec3 getBulletSpawnOffset(int gunIndex) {
        return GUN_OFFSET.yRot(-this.yHeadRot * Mth.DEG_TO_RAD);
    }
}