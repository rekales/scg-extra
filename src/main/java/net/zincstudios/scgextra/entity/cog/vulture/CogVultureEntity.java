package net.zincstudios.scgextra.entity.cog.vulture;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
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
import net.zincstudios.scgextra.entity.common.GunnerEntity;
import net.zincstudios.scgextra.entity.common.ai.HurtByNonFactionGoal;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CogVultureEntity extends GunnerEntity implements GeoEntity {

    public static final Vec3 LEFT_GUN_OFFSET = new Vec3(0.45,1.3,-0.4);
    public static final Vec3 RIGHT_GUN_OFFSET = new Vec3(-0.45,1.3,-0.4);

    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    public CogVultureEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();

//        this.setYRot(this.getYRot()+1);
//        this.setYBodyRot(this.getYRot());

//        if (this.level().isClientSide()) {
//            ClientLevel clientLevel = (ClientLevel) level();  // Dedicated Server doesn't like doing instanceof ClientLevel
//
//            Vec3 lgo = new Vec3(-0.45,1.3,-0.4);
//            Vec3 pos = this.position().add(lgo.yRot(-this.yBodyRot * Mth.DEG_TO_RAD));
//
//            clientLevel.addParticle(
//                    ParticleTypes.SOUL_FIRE_FLAME,
//                    pos.x, pos.y, pos.z,
//                    0, (this.getRandom().nextDouble()) * 0.02, 0
//            );
//        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new CogVultureAttackGoal(this)
                .burstAmount(16)
                .burstIntervalTicks(2)
//                .runAndGun()
                .approachDist(8)
                .maxRange(18)
                .attackInterval(80)
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
                .add(Attributes.MOVEMENT_SPEED, 0.4F)
                .add(Attributes.MAX_HEALTH, 15.0D);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geocache;
    }
}
