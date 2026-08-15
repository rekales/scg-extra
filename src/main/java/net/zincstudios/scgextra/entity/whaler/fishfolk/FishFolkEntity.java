package net.zincstudios.scgextra.entity.whaler.fishfolk;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.fluids.FluidType;
import net.zincstudios.scgextra.entity.common.GunnerEntity;
import net.zincstudios.scgextra.entity.common.goal.HurtByNonFactionGoal;
import net.zincstudios.scgextra.entity.common.goal.TridentAttackGoal;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.zincstudios.scgextra.entity.Faction;
import net.zincstudios.scgextra.entity.whaler.salmonsaur.SalmonsaurEntity;
import top.ribs.scguns.entity.ai.AIType;
import top.ribs.scguns.entity.ai.GunAttackGoal;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FishFolkEntity extends GunnerEntity implements GeoEntity, RangedAttackMob, VariantHolder<Integer> {

    private static final EntityDataAccessor<Integer> TEXTURE_VARIANT =
            SynchedEntityData.defineId(FishFolkEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SITTING =
            SynchedEntityData.defineId(FishFolkEntity.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public FishFolkEntity(EntityType<? extends Monster> entity, Level level) {
        super(entity, level);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @org.jetbrains.annotations.Nullable SpawnGroupData spawnData, @org.jetbrains.annotations.Nullable CompoundTag dataTag) {
        this.setVariant(this.getRandom().nextIntBetweenInclusive(1,2));
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
        .add(Attributes.FOLLOW_RANGE, 35.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.23F)
        .add(Attributes.ATTACK_DAMAGE, 3.0D)
        .add(Attributes.ARMOR, 4.0D)
        .add(Attributes.MAX_HEALTH, 20.0D)
        .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new GunAttackGoal<>(this, this.getMainHandItem(), 1.0F, AIType.RECKLESS, 3));
        this.goalSelector.addGoal(2, new TridentAttackGoal<>(this, 1.0D, 40, 10.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(2, new HurtByNonFactionGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                entity -> Faction.isEnemies(this, entity)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            if(state.getAnimatable().isSitting()){
                state.setAndContinue(RawAnimation.begin().thenLoop("sitting"));
            }else if (state.isMoving()) {
                state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            } else {
                state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
            }
            return PlayState.CONTINUE;
        }).triggerableAnim("attack", RawAnimation.begin().thenPlay("attack")));
        controllers.add(new AnimationController<>(this, "special", 0, state -> PlayState.CONTINUE)
        .triggerableAnim("attack", RawAnimation.begin().thenPlay("attack")));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float pDistanceFactor) {
        this.triggerAnim("special", "attack");
        ThrownTrident throwntrident = new ThrownTrident(this.level(), this, new ItemStack(Items.TRIDENT));
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333D) - throwntrident.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        throwntrident.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - this.level().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(throwntrident);
    }

    @Override
    public void setVariant(Integer variant) {
        this.entityData.set(TEXTURE_VARIANT, variant);
    }

    @Override
    public Integer getVariant() {
        return this.entityData.get(TEXTURE_VARIANT);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TEXTURE_VARIANT, 1);
        this.entityData.define(SITTING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("variant", this.entityData.get(TEXTURE_VARIANT));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(TEXTURE_VARIANT, tag.getInt("variant"));
    }

    @Override
    protected boolean canRide(Entity pVehicle) {
        return pVehicle instanceof SalmonsaurEntity && super.canRide(pVehicle);
    }

    @Override
    public boolean isPassenger() {
        if(this.getVehicle()==null)return false;
        return this.getVehicle() instanceof SalmonsaurEntity;
    }

    @Override
    public void tick() {
        super.tick();
        this.entityData.set(SITTING, this.isPassenger());
        if(tickCount%20==0){
            if(this.entityData.get(SITTING)){
                this.triggerAnim("sit", "sitting");
            }
        }
    }

    public boolean isSitting(){
        return this.entityData.get(SITTING);
    }
    @Override
    public boolean canDrownInFluidType(FluidType type) {
        return false;
    }
}