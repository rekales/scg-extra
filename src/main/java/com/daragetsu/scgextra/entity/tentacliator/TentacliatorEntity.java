package com.daragetsu.scgextra.entity.tentacliator;

import java.util.ArrayList;
import java.util.Random;

import com.daragetsu.scgextra.Faction;
import com.daragetsu.scgextra.SCGExtra;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.registries.RegistryObject;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.ribs.scguns.entity.ai.AIType;
import top.ribs.scguns.entity.ai.GunAttackGoal;
import top.ribs.scguns.init.ModItems;
import top.ribs.scguns.item.animated.AnimatedUnderWaterGunItem;

public class TentacliatorEntity extends Drowned implements GeoEntity{
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(TentacliatorEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(TentacliatorEntity.class, EntityDataSerializers.INT);
    public Random random = new Random();
    public TentacliatorEntity(EntityType<? extends Drowned> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
        MobSpawnType pReason, SpawnGroupData pSpawnData, CompoundTag pDataTag) {
        if (this.getRandom().nextBoolean()) {
            this.entityData.set(TEXTURE, "textures/entity/tentacliator/tentacliator.png");
            this.entityData.set(TYPE, 1);
        } else {
            //TODO: change to the other texture
            this.entityData.set(TEXTURE, "textures/entity/tentacliator/tentacliator.png");
            this.entityData.set(TYPE, 0);
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }
    public ResourceLocation getLocation(){
        if(this.entityData!=null){
            return SCGExtra.asResource(this.entityData.get(TEXTURE));
        }
        return SCGExtra.asResource("textures/entity/tentacliator/tentacliator.png");
    }
    @Override
    protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
        int i = pRandom.nextInt(20);
        if (pRandom.nextFloat() < 0.5F) {
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
        }
        if (pRandom.nextFloat() < 0.5F) {
            this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
        }
        if (pRandom.nextFloat() < 0.5F) {
            this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.IRON_LEGGINGS));
        }
        if (pRandom.nextFloat() < 0.5F) {
            this.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
        }
        if (i < 10) {
           this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
        } else {
            ArrayList<Item> guns = new ArrayList<>();
            for(RegistryObject<Item> item : ModItems.REGISTER.getEntries()){
                if(item.get() instanceof AnimatedUnderWaterGunItem){
                    guns.add(item.get());
                }
            }
            ItemStack gun = new ItemStack(guns.get(random.nextInt(guns.size())));
            gun.getOrCreateTag().putBoolean("IgnoreAmmo", true);
            this.setItemSlot(EquipmentSlot.MAINHAND, gun);
        }
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.LEFT;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
        .add(Attributes.FOLLOW_RANGE, 35.0D)
        .add(Attributes.MOVEMENT_SPEED, (double)0.23F)
        .add(Attributes.ATTACK_DAMAGE, 3.0D)
        .add(Attributes.ARMOR, 6.0D)
        .add(Attributes.MAX_HEALTH, 40.0D)
        .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
   }
   @Override
   protected void addBehaviourGoals() {
        super.addBehaviourGoals();
        this.goalSelector.addGoal(1, new GunAttackGoal<>(this, this.getMainHandItem(), 1.0F, AIType.RECKLESS, 3));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(2, new InkAttackGoal(this));
        this.goalSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
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
    
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
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
        }));
    }
    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        int frogDartsCount = random.nextInt(1,4);
        int advancedRoundsCount = random.nextInt(1,4);
        int sacCount = random.nextInt(1,4);
        for(int i = 0; i < frogDartsCount; i++){
            this.spawnAtLocation(new ItemStack(ModItems.FROG_DART.get()));
        }
        for(int i = 0; i < advancedRoundsCount; i++){
            this.spawnAtLocation(new ItemStack(ModItems.ADVANCED_ROUND.get()));
        }
        for(int i = 0; i < sacCount; i++){
            if(this.entityData!=null){
                int type = this.entityData.get(TYPE);
                if(type==0){
                    this.spawnAtLocation(new ItemStack(Items.INK_SAC));
                }else if(type==1){
                    this.spawnAtLocation(new ItemStack(Items.GLOW_INK_SAC));
                }
            }
        }
    }
    @Override
    public boolean isBaby() {
        return false;
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TEXTURE, "textures/entity/tentacliator/tentacliator.png");
        this.entityData.define(TYPE, 1);
    }
    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if(this.entityData!=null){
            pCompound.putString("texture", this.entityData.get(TEXTURE));
            pCompound.putInt("type", this.entityData.get(TYPE));
        }
    }
    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if(this.entityData!=null){
            this.entityData.set(TEXTURE, pCompound.getString("texture"));
            this.entityData.set(TYPE, pCompound.getInt("type"));
        }
    }
}