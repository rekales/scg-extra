package net.zincstudios.scgextra.entity.whaler.armoredwhale;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.zincstudios.scgextra.CommonConfig;
import net.zincstudios.scgextra.entity.Faction;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;
import net.zincstudios.scgextra.entity.common.ai.HurtByNonFactionGoal;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ArmoredWhaleEntity extends Monster implements GeoEntity {

    public static final Vec3 LEFT_GUN_OFFSET = new Vec3(-2,2.9,2);
    public static final Vec3 RIGHT_GUN_OFFSET = new Vec3(2,2.9,2);

    private static final EntityDataAccessor<Boolean> WATER_SPLASH =
            SynchedEntityData.defineId(ArmoredWhaleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> LEFT_GUN_Y_ROT =
            SynchedEntityData.defineId(ArmoredWhaleEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> RIGHT_GUN_Y_ROT =
            SynchedEntityData.defineId(ArmoredWhaleEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> WATER_LEVEL =
            SynchedEntityData.defineId(ArmoredWhaleEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private final ArmoredWhalePart[] subEntities;

    private boolean didSlam = false;

    public ArmoredWhaleEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        ArmoredWhalePart head1 = new ArmoredWhalePart(this, "head1", this.getBbWidth() / 2, this.getBbHeight());
        ArmoredWhalePart head2 = new ArmoredWhalePart(this, "head2", this.getBbWidth() / 2, this.getBbHeight());
        ArmoredWhalePart body = new ArmoredWhalePart(this, "body", this.getBbWidth(), this.getBbHeight());
        ArmoredWhalePart tail1 = new ArmoredWhalePart(this, "tail1", 1.5F, 3F);
        ArmoredWhalePart tail2 = new ArmoredWhalePart(this, "tail2", 2.5F, 3F);
        ArmoredWhalePart tail3 = new ArmoredWhalePart(this, "tail3", 2.5F, 3F);
        ArmoredWhalePart tail4 = new ArmoredWhalePart(this, "tail4", 1.5F, 2F);
        ArmoredWhalePart tail5 = new ArmoredWhalePart(this, "tail5", 1.5F, 2F);
        ArmoredWhalePart tail6 = new ArmoredWhalePart(this, "tail6", 4.5F, 0.5F);
        ArmoredWhalePart gem = new ArmoredWhalePart(this, "gem", 1F, 1.1F);
        this.subEntities = new ArmoredWhalePart[]{head1, head2, body, tail1, tail2, tail3, tail4, tail5, tail6, gem };
        this.setHealth(this.getMaxHealth());
        this.noCulling = true;
        this.setId(ENTITY_COUNTER.getAndAdd(this.subEntities.length + 1) + 1); 
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            if (state.isMoving()) {
                state.setAndContinue(RawAnimation.begin().thenLoop("movement"));
            } else {
                state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
            }
            return PlayState.CONTINUE;
        }));

        if (CommonConfig.enableAbilityWarning) {
            controllers.add(new AnimationController<>(this, "special", 0, state -> PlayState.CONTINUE)
                    .triggerableAnim("slam", RawAnimation.begin()
                            .thenPlay("effect.eye_flash")
                            .thenPlay("slam")
                    ));
        } else {
            controllers.add(new AnimationController<>(this, "special", 0, state -> PlayState.CONTINUE)
                    .triggerableAnim("slam", RawAnimation.begin().thenPlay("slam")));
        }
        controllers.add(new AnimationController<>(this, "water", 0, state -> PlayState.CONTINUE)
                    .triggerableAnim("water_spray", RawAnimation.begin().thenPlay("water_spray")));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 700)
                .add(Attributes.ARMOR, 6)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FOLLOW_RANGE, 48);
    }

    @Override
    public void tick() {
        super.tick();
        updateSubentities();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(3, new DeployMinesGoal(this, 800, 1.1F, 4, 6));
        this.goalSelector.addGoal(4, new SlamAttackGoal(this));
        this.goalSelector.addGoal(4, new SplashWaterGoal(this));
        this.goalSelector.addGoal(4, new SpawnReinforcementsGoal(this));
        // range 24 to compensate for the whale's size
        this.goalSelector.addGoal(5, new MountedGunAttackGoal.Left(this, 2, 24));
        this.goalSelector.addGoal(5, new MountedGunAttackGoal.Right(this, 2, 24));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));

        // Bosses will prioritize players and does not require line of sight to maintain targeting to avoid cheese
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(2, new HurtByNonFactionGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                entity -> Faction.isEnemies(this, entity)));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    public void setDidSlam(boolean slam){
        this.didSlam = slam;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(this.didSlam){
            setDidSlam(false);
            return false;
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    public void setId(int pId) {
        super.setId(pId);
        for (int i = 0; i < this.subEntities.length; i++)
            this.subEntities[i].setId(pId + i + 1);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        updateSubentities();
    }

    @SuppressWarnings("unused")
    public ArmoredWhalePart[] getSubEntities() {
        return this.subEntities;
    }

    @Override
    public net.minecraftforge.entity.PartEntity<?>[] getParts() {
        return this.subEntities;
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    public boolean isPickable() {
        return false;
    }

    public boolean getWaterSplash(){
        return this.entityData.get(WATER_SPLASH);
    }

    public void setWaterSplash(boolean splash){
        this.entityData.set(WATER_SPLASH, splash);
    }

    // relative rotation to body in radians
    public float getLeftGunYRot() {
        return this.entityData.get(LEFT_GUN_Y_ROT);
    }

    public void setLeftGunYRot(float rot) {
        this.entityData.set(LEFT_GUN_Y_ROT, rot);
    }

    public float getRightGunYRot() {
        return this.entityData.get(RIGHT_GUN_Y_ROT);
    }

    public void setRightGunYRot(float rot) {
        this.entityData.set(RIGHT_GUN_Y_ROT, rot);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WATER_SPLASH, false);
        this.entityData.define(LEFT_GUN_Y_ROT, 0F);
        this.entityData.define(RIGHT_GUN_Y_ROT, 0F);
        this.entityData.define(WATER_LEVEL, 1);
    }

    public void updateSubentities(){
        for (ArmoredWhalePart part : this.subEntities) {
            part.xo = part.getX();
            part.yo = part.getY();
            part.zo = part.getZ();
            part.xOld = part.getX();
            part.yOld = part.getY();
            part.zOld = part.getZ();
        }

        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();

        float bbWidth = this.getBbWidth();

        float[] offsets = new float[] { 
            -4.5f, 
            -2.5f, 
            1.5F, 
            (bbWidth*1.65F)-bbWidth, 
            (bbWidth*2F)-bbWidth, 
            (bbWidth*2.2F)-bbWidth, 
            (bbWidth*2.55F)-bbWidth,
            (bbWidth*2.85F)-bbWidth,
            (bbWidth*3.2F)-bbWidth,
            -7.6F
        };

        double yawRad = Math.toRadians(this.yBodyRot);

        for (int i = 0; i < this.subEntities.length; i++) {
            if(i > 2){
                x = this.subEntities[2].getX();
                y = this.subEntities[2].getY();
                z = this.subEntities[2].getZ();
            }
            ArmoredWhalePart part = this.subEntities[i];
            float distance = offsets[i];

            double offsetX = -Math.sin(yawRad) * distance;
            double offsetZ = Math.cos(yawRad) * distance;
            if(i==6 || i==7){
                part.setPosRaw(x + offsetX, y+1, z + offsetZ);
            }else if(i==8){   
                part.setPosRaw(x + offsetX, y+2, z + offsetZ);
            }else if(i==9){   
                part.setPosRaw(x + offsetX, y+3.8, z + offsetZ);
            }else{
                part.setPosRaw(x + offsetX, y, z + offsetZ);
            }
            part.setOldPosAndRot();
            part.refreshDimensions();
        }
    }

    public Vec3 getLeftGunPos() {
        return LEFT_GUN_OFFSET.yRot(-this.getYRot() * Mth.DEG_TO_RAD).add(this.position());
    }

    public Vec3 getRightGunPos() {
        return RIGHT_GUN_OFFSET.yRot(-this.getYRot() * Mth.DEG_TO_RAD).add(this.position());
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        return false;
    }

    public void setLayerN(int n){
        this.entityData.set(WATER_LEVEL, n);
    }

    public int getLayerN(){
        return this.entityData.get(WATER_LEVEL);
    }
}