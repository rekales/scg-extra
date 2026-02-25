package com.daragetsu.scgextra.entity.salmonsaur;

import java.util.ArrayList;
import java.util.Random;

import com.daragetsu.scgextra.Faction;
import com.daragetsu.scgextra.entity.ModEntities;
import com.daragetsu.scgextra.entity.fishfolk.FishFolkEntity;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.registries.RegistryObject;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.ribs.scguns.init.ModItems;
import top.ribs.scguns.item.animated.AnimatedUnderWaterGunItem;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SalmonsaurEntity extends Hoglin implements GeoEntity {

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private boolean riderSpawned = false;
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    public SalmonsaurEntity(EntityType<? extends SalmonsaurEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        
    }
    public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
      .add(Attributes.MOVEMENT_SPEED, 0.5F)
      .add(Attributes.KNOCKBACK_RESISTANCE, 0.6F)
      .add(Attributes.ATTACK_KNOCKBACK, 1.0D)
      .add(Attributes.ATTACK_DAMAGE, 8.0D)
      .add(Attributes.MAX_HEALTH, 30.0D);
    }
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && !riderSpawned && this.getPassengers().isEmpty()) {
            FishFolkEntity rider = new FishFolkEntity(ModEntities.FISH_FOLK.get(), this.level());
            int i = new Random().nextInt(20);
            if (i < 10) {
                rider.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
            } else {
                ArrayList<Item> guns = new ArrayList<>();
                for(RegistryObject<Item> item : ModItems.REGISTER.getEntries()){
                    if(item.get() instanceof AnimatedUnderWaterGunItem){
                        guns.add(item.get());
                    }
                }
                ItemStack gun = new ItemStack(guns.get(new Random().nextInt(guns.size())));
                gun.getOrCreateTag().putBoolean("IgnoreAmmo", true);
                rider.setItemSlot(EquipmentSlot.MAINHAND, gun);
            }
            rider.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
            this.level().addFreshEntity(rider);
            rider.startRiding(this, true);
            riderSpawned = true;
        }
        if (this.getLastHurtByMob() != null &&
            !Faction.isFriendlies(this, this.getLastHurtByMob())) {
            this.setTarget((LivingEntity)this.getLastHurtByMob());
        }
        if(this.level().isClientSide){
            setupAnimationStates();
        }
    }

    private void setupAnimationStates(){
        if(this.idleAnimationTimeout <=0 ){
            this.idleAnimationTimeout = this.random.nextInt(40)+80;
            this.idleAnimationState.start(this.tickCount);
        }else{
            --this.idleAnimationTimeout;
        }
    }

    @Override
    protected void updateWalkAnimation(float pPartialTick) {
        float f = 0;
        if(this.getPose() == Pose.STANDING){
            f = Math.min(pPartialTick * 6F, 1f);
        }else {
            f = 0f;
        }
        this.walkAnimation.update(f, 0.2F);
    }

    @Override
    public boolean isConverting() {
        return false;
    }
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
    }
    @Override
    public boolean canBeLeashed(Player pPlayer) {
        return false;
    }
    @Override
    public void setTarget(@Nullable LivingEntity pTarget) {
        if(pTarget!=null && !Faction.isFriendlies(this, pTarget)){
            super.setTarget(pTarget);
        }
    }
    @Override
    public boolean isBaby() {
        return false;
    }
    @Override
    public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
        long time = pLevel.dayTime() % 24000;
        return time >= 13000 && time < 23000;
    }
    @Override
    public boolean checkSpawnObstruction(LevelReader pLevel) {
        return pLevel.isUnobstructed(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericWalkIdleController(this));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}