package net.zincstudios.scgextra.entity.fishfolk;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.ribs.scguns.config.EntityEquipmentConfig;
import top.ribs.scguns.entity.ai.AIType;
import top.ribs.scguns.entity.ai.GunAttackGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.RandomSource;
import net.zincstudios.scgextra.Faction;
import net.zincstudios.scgextra.data.InfantryEquipmentDataLoader;
import net.zincstudios.scgextra.data.InfantryEquipmentItemLoader;
import net.zincstudios.scgextra.entity.armoredwhale.ArmoredWhaleEntity;
import net.zincstudios.scgextra.entity.salmonsaur.SalmonsaurEntity;

import java.io.InputStreamReader;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.Gson;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FishFolkEntity extends Drowned implements GeoEntity, VariantHolder<Integer> {

    private static final EntityDataAccessor<Integer> TEXTURE_VARIANT =
            SynchedEntityData.defineId(FishFolkEntity.class, EntityDataSerializers.INT);
    private static InfantryEquipmentDataLoader FISH_FOLK_DATA;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public FishFolkEntity(EntityType<? extends Drowned> entity, Level level) {
        super(entity, level);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        this.setVariant(this.getRandom().nextIntBetweenInclusive(1,2));
        EntityEquipmentConfig.equipEntity(this, "scgextra:fish_folk");  // NOTE: using raw string
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
        super.populateDefaultEquipmentSlots(pRandom, pDifficulty);
        if(this.getMainHandItem().is(Items.FISHING_ROD)){
            if (FISH_FOLK_DATA == null || FISH_FOLK_DATA.items == null) {
                try {
                    ResourceLocation loc = new ResourceLocation("scguns", "entity/equipment/fish_folk.json");
    
                    Resource resource = level().getServer()
                            .getResourceManager()
                            .getResourceOrThrow(loc);
    
                    try (InputStreamReader reader = new InputStreamReader(resource.open())) {
                        FISH_FOLK_DATA = new Gson().fromJson(reader, InfantryEquipmentDataLoader.class);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            InfantryEquipmentItemLoader[] items = FISH_FOLK_DATA.items;
            InfantryEquipmentItemLoader itemEq = items[this.random.nextInt(items.length)];
            Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(itemEq.item));
            this.setItemInHand(InteractionHand.MAIN_HAND, item.getDefaultInstance());
        }
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
    protected void addBehaviourGoals() {
        super.addBehaviourGoals();
        this.goalSelector.addGoal(2, new FishFolkAttackGoal(this, 1.0D, false));
        
        this.goalSelector.addGoal(1, new GunAttackGoal<>(this, this.getMainHandItem(), 1.0F, AIType.RECKLESS, 3));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
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
    static class FishFolkAttackGoal extends ZombieAttackGoal {
        private final FishFolkEntity fish_folk;
        public FishFolkAttackGoal(FishFolkEntity pFishFolk, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen) {
           super(pFishFolk, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
           this.fish_folk = pFishFolk;
        }

        public boolean canUse() {
           return super.canUse() && this.fish_folk.okTarget(this.fish_folk.getTarget());
        }

        public boolean canContinueToUse() {
           return super.canContinueToUse() && this.fish_folk.okTarget(this.fish_folk.getTarget());
        }
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
        }).triggerableAnim("attack", RawAnimation.begin().thenPlay("attack")));
        controllers.add(new AnimationController<>(this, "special", 0, state -> PlayState.CONTINUE)
        .triggerableAnim("attack", RawAnimation.begin().thenPlay("attack")));
    }
    @Override
    public void performRangedAttack(LivingEntity pTarget, float pDistanceFactor) {
        super.performRangedAttack(pTarget, pDistanceFactor);
        this.triggerAnim("special", "attack");
    }
    @Override
    public boolean isBaby() {
        return false;
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
        return this.getVehicle() instanceof SalmonsaurEntity;
    }
    @Override
    protected boolean isSunSensitive() {
        return false;
    }
    @Override
    public void setTarget(LivingEntity pTarget) {
        if(!(pTarget instanceof ArmoredWhaleEntity)){//should never attack the boss
            super.setTarget(pTarget);
        }
    }
}