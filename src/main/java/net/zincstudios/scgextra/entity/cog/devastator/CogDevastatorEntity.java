package net.zincstudios.scgextra.entity.cog.devastator;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.entity.Faction;
import net.zincstudios.scgextra.entity.cog.ApproachTargetGoal;
import net.zincstudios.scgextra.entity.common.GunnerEntity;
import net.zincstudios.scgextra.entity.common.ai.HurtByNonFactionGoal;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.ribs.scguns.init.ModItems;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CogDevastatorEntity extends GunnerEntity implements GeoEntity {

    public static final Vec3 MACHINE_GUN_OFFSET = new Vec3(-0.7,2.1,0.4);
    public static final Vec3 SHOTGUN_OFFSET = new Vec3(-0.7,2.6,0.5);
    public static final Vec3 GATLING_GUN_OFFSET = new Vec3(0.7,2.6,0.3);

    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Integer> TARGET_ID =
            SynchedEntityData.defineId(CogDevastatorEntity.class, EntityDataSerializers.INT);

    @Nullable
    private LivingEntity clientSideCachedTarget;

    public CogDevastatorEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new ApproachTargetGoal(this, 4, 4, 1.0));
        this.goalSelector.addGoal(4, new CogDevastatorMountedGunGoal(this, ModItems.PRUSH_GUN.get())
                .burstAmount(16)
                .burstIntervalTicks(2)
                .maxRange(15)
                .attackInterval(60)
                .accuracyModifier(1.5F)
                .spawnOffset(MACHINE_GUN_OFFSET)
        );
        this.goalSelector.addGoal(4, new CogDevastatorMountedGunGoal(this, ModItems.JACKHAMMER.get())
                .burstAmount(3)
                .burstIntervalTicks(6)
                .maxRange(10)
                .attackInterval(120)
                .spawnOffset(SHOTGUN_OFFSET)
        );
        this.goalSelector.addGoal(4, new CogDevastatorMountedGunGoal(this, ModItems.GATTALER.get())
                .burstAmount(32)
                .burstIntervalTicks(1)
                .maxRange(10)
                .accuracyModifier(1F)
                .attackInterval(80)
                .spawnOffset(GATLING_GUN_OFFSET)
        );
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(2, new HurtByNonFactionGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                entity -> Faction.isEnemies(this, entity)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2F)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.MAX_HEALTH, 400.0D);
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity target = this.getTarget();
        if (target != null) {
            this.getLookControl().setLookAt(target, 90.0F, 90.0F);
        }
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target instanceof Player player && (player.isCreative() || player.isSpectator())) {
            target = null;
        }
        super.setTarget(target);
        this.entityData.set(TARGET_ID, target == null ? 0 : target.getId());
    }

    @Override
    public @Nullable LivingEntity getTarget() {
        if (this.level().isClientSide()) {
            if (this.clientSideCachedTarget != null) {
                return this.clientSideCachedTarget;
            } else {
                Entity entity = this.level().getEntity(this.entityData.get(TARGET_ID));
                if (entity instanceof LivingEntity livingEntity) {
                    this.clientSideCachedTarget = livingEntity;
                    return this.clientSideCachedTarget;
                } else {
                    return null;
                }
            }
        } else {
            return super.getTarget();
        }
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (TARGET_ID.equals(key)) {
            this.clientSideCachedTarget = null;
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TARGET_ID, 0);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geocache;
    }
}
