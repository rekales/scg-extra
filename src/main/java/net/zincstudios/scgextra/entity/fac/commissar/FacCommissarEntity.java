package net.zincstudios.scgextra.entity.fac.commissar;

import com.mojang.serialization.Dynamic;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.zincstudios.scgextra.entity.AbilityState;
import net.zincstudios.scgextra.entity.ModBrainMemories;
import net.zincstudios.scgextra.entity.common.EquippedEntity;
import net.zincstudios.scgextra.entity.common.Gunner;
import net.zincstudios.scgextra.entity.common.GunnerEntity;
import net.zincstudios.scgextra.entity.common.MobUtil;
import net.zincstudios.scgextra.entity.common.brain.BrainCommons;
import net.zincstudios.scgextra.entity.common.brain.FlareSummon;
import net.zincstudios.scgextra.entity.common.client.ExpandedAnimationController;
import net.zincstudios.scgextra.sounds.FACSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FacCommissarEntity extends EquippedEntity implements GeoEntity, Gunner {

    static final int FLARE_DURATION = 200;
    static final int ALERT_ANIM_TICKS = 30;

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation IDLE_AIM = RawAnimation.begin().thenLoop("idle_aim");
    private static final RawAnimation WALK_AIM = RawAnimation.begin().thenLoop("walk_aim");
//    private static final RawAnimation IDLE_LOOKOUT = RawAnimation.begin().thenLoop("idle_lookout");
    private static final RawAnimation MELEE = RawAnimation.begin().thenPlay("melee");
//    private static final RawAnimation MELEE_NO_LEGS = RawAnimation.begin().thenPlay("melee_no_legs");
    private static final RawAnimation FLARE = RawAnimation.begin().thenPlay("flare");
    private static final RawAnimation ALERT = RawAnimation.begin().thenPlay("alert");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public FacCommissarEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 48.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.22F)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.MAX_HEALTH, 100.0D);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) return;

        Brain<?> brain = this.getBrain();
        if (brain.getTimeUntilExpiry(ModBrainMemories.TO_ALERT.get()) == ALERT_ANIM_TICKS) {
            this.triggerAnim("behavior", "alert");
        }
        Optional<AbilityState> optAbility = brain.getMemory(ModBrainMemories.ABILITY_STATE.get())
                .filter(abilityState -> abilityState.isSame(FlareSummon.ABILITY_ID))
                .filter(abilityState -> abilityState.getTicksLeft(this.level()) == FLARE_DURATION-1);
        if (optAbility.isPresent()) {
            this.triggerAnim("behavior", "flare");
        }
    }

    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return FacCommissarAi.makeBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @SuppressWarnings("unchecked")
    public Brain<FacCommissarEntity> getBrain() {
        return (Brain<FacCommissarEntity>) super.getBrain();
    }

    protected Brain.Provider<FacCommissarEntity> brainProvider() {
        return FacCommissarAi.brainProvider();
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("facCommissarBrain");
        this.getBrain().tick((ServerLevel)this.level(), this);
        FacCommissarAi.updateActivity(this);
        BrainCommons.updateAimingAggressive(this);
        if (this.getBrain().getMemory(ModBrainMemories.AIM_TICKS.get()).filter(aim -> aim > 5).isPresent()) {
            this.setYBodyRot(this.getYHeadRot());
        }
        this.level().getProfiler().pop();
        super.customServerAiStep();
    }

//    @Override
//    public void tick() {
//        super.tick();
//    }

    @Override
    public void swing(InteractionHand hand, boolean updateSelf) {
        this.triggerAnim("attack", "melee");
        this.setYBodyRot(this.getYHeadRot());
    }

    public double getMeleeAttackRangeSqr(LivingEntity target) {
        return this.getBbWidth() * this.getBbWidth() * 3.0F * 3.0F + target.getBbWidth();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 6, state -> {
            if (state.getAnimatable().isAggressive()) {
                return state.setAndContinue(state.isMoving() ? WALK_AIM : IDLE_AIM);
            } else {
                if (state.isMoving()) {
                    return state.setAndContinue(WALK);
                } else {
                    return state.setAndContinue(IDLE);  // TODO: look out anims
                }
            }
        }));

        controllers.add(new AnimationController<>(this, "attack", 2, state -> PlayState.STOP)
                .triggerableAnim("melee", MELEE)
        );

        controllers.add(new ExpandedAnimationController<>(this, "behavior", 0, state -> PlayState.STOP)
                .triggerableAnim("alert", ALERT)
                .triggerableAnim("flare", FLARE)
                .setCustomInstructionKeyframeHandler(event -> {
                    if (event.getKeyframeData().getInstructions().equals("spawn_flare;")) {
                        FacCommissarEntity self = event.getAnimatable();
                        FireworkRocketEntity firework = new FireworkRocketEntity(self.level(),
                                self.getX(), self.getY() + self.getBbHeight(), self.getZ(), createFireworkItem());
                        if (self.level().isClientSide()) {
                            self.level().addFreshEntity(firework);
                        }
                    }
                })
        );
    }

    // TODO: custom flare entity
    private ItemStack createFireworkItem() {  // integrate to behavior
        ItemStack firework = new ItemStack(Items.FIREWORK_ROCKET);
        CompoundTag tag = firework.getOrCreateTag();
        CompoundTag fireworks = new CompoundTag();

        // Flight duration (1-3)
        fireworks.putByte("Flight", (byte) 1);

        // Explosions
        ListTag explosions = new ListTag();
        CompoundTag explosion = new CompoundTag();
        explosion.putByte("Type", (byte) 0); // 0=small, 1=large, 2=star, 3=creeper, 4=burst
        explosion.putIntArray("Colors", new int[]{0xFFFFFF}); // RGB colors
        explosion.putBoolean("Flicker", false);
        explosion.putBoolean("Trail", true);
        explosions.add(explosion);

        fireworks.put("Explosions", explosions);
        tag.put("Fireworks", fireworks);

        return firework;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return MobUtil.getSound(
                this.random,
                FACSounds.FAC_COMMISSAR_HURT_1.get(),
                FACSounds.FAC_COMMISSAR_HURT_2.get(),
                FACSounds.FAC_COMMISSAR_HURT_3.get(),
                FACSounds.FAC_COMMISSAR_HURT_4.get()
        );
    }

    protected SoundEvent getAmbientSound() {
        return MobUtil.getSound(
                this.random,
                FACSounds.FAC_COMMISSAR_IDLE_1.get(),
                FACSounds.FAC_COMMISSAR_IDLE_2.get(),
                FACSounds.FAC_COMMISSAR_IDLE_3.get(),
                FACSounds.FAC_COMMISSAR_IDLE_4.get(),
                FACSounds.FAC_COMMISSAR_LINE_2.get()
        );
    }

    protected SoundEvent getDeathSound() {
        return MobUtil.getSound(
                this.random,
                FACSounds.FAC_COMMISSAR_DEATH_1.get(),
                FACSounds.FAC_COMMISSAR_DEATH_2.get()
        );
    }
}
