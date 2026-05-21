package net.zincstudios.scgextra.entity.neutral.end_pod;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.zincstudios.scgextra.CommonConfig;
import net.zincstudios.scgextra.entity.neutral.NeutralCombatUtil;
import net.zincstudios.scgextra.sounds.NeutralSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class EndPodEntity extends Chicken implements GeoEntity {
    private static final int NO_EGG_TIMER = Integer.MAX_VALUE;
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public EndPodEntity(EntityType<? extends Chicken> type, Level level) {
        super(type, level);
        this.eggTime = NO_EGG_TIMER;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.18D);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.CHORUS_FRUIT);
    }

    @Nullable
    @Override
    public Chicken getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        this.playSound(NeutralSounds.NEUTRAL_END_POD_BREED.get(), 1.0F, this.getVoicePitch());
        return net.zincstudios.scgextra.entity.neutral.NeutralEntities.END_POD.get().create(level);
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return true;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.eggTime = NO_EGG_TIMER;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        int roll = this.random.nextInt(5);
        if (roll == 0) return NeutralSounds.NEUTRAL_END_POD_IDLE_01.get();
        if (roll == 1) return NeutralSounds.NEUTRAL_END_POD_IDLE_02.get();
        if (roll == 2) return NeutralSounds.NEUTRAL_END_POD_IDLE_03.get();
        if (roll == 3) return NeutralSounds.NEUTRAL_END_POD_IDLE_04.get();
        return NeutralSounds.NEUTRAL_END_POD_IDLE_05.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return this.random.nextBoolean()
                ? NeutralSounds.NEUTRAL_END_POD_DEATH_01.get()
                : NeutralSounds.NEUTRAL_END_POD_DEATH_02.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.random.nextBoolean()
                ? NeutralSounds.NEUTRAL_END_POD_DEATH_01.get()
                : NeutralSounds.NEUTRAL_END_POD_DEATH_02.get();
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnReason) {
        if (NeutralCombatUtil.isManualSpawn(spawnReason)) {
            return true;
        }
        BlockPos pos = this.blockPosition();
        if (NeutralCombatUtil.isWaterAtOrBelow(level, pos)) {
            return false;
        }
        return NeutralCombatUtil.passesSpawnChance(this.random, CommonConfig.spawnChanceEndPod)
                && NeutralCombatUtil.canSpawnEndSurface(level, pos);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 2, state -> {
            if (state.isMoving()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}




