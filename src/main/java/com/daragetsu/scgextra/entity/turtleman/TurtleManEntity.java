package com.daragetsu.scgextra.entity.turtleman;

import com.daragetsu.scgextra.entity.ModEntities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;
import top.ribs.scguns.config.EntityEquipmentConfig;
import top.ribs.scguns.entity.ai.AIType;
import top.ribs.scguns.entity.ai.GunAttackGoal;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault

public class TurtleManEntity extends Monster implements RangedAttackMob {

    public TurtleManEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        ItemStack mainHandItem = this.getMainHandItem();

        // TODO: approach enemy while walking backwards behaviour goal
        this.goalSelector.addGoal(1, new GunAttackGoal<>(this, mainHandItem, 1.0F, AIType.RECKLESS, 3));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.9));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                // TODO: broaden to the entire whaler faction
                if (this.mob.getLastHurtByMob() instanceof TurtleManEntity) {
                    return false;
                }
                return super.canUse();
            }
        }.setAlertOthers());

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
    }

    // NOTE: maybe just use populateDefaultEquipmentSlots to avoid using this deprecated methods after figuring out EntityEquipmentConfig
    @SuppressWarnings("deprecation")
    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        EntityEquipmentConfig.equipEntity(this, "scgextra:turtleman");  // NOTE: using raw string
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

//    @Override
//    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
//        super.populateDefaultEquipmentSlots(random, difficulty);
//        int i = random.nextInt(20);
//    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30)
                .add(Attributes.ARMOR, 12)
                .add(Attributes.MOVEMENT_SPEED, 0.3);
    }

    @Override
    public void performRangedAttack(LivingEntity livingEntity, float v) {
        this.doHurtTarget(livingEntity);
    }
    //somewhat moon walks towards the player
    //leaving comments just in case
    @Override
    public void tick() {
        super.tick();

        if (this.getTarget() instanceof Player p) {

            // Direction from entity to player (horizontal only)
            double dx = p.getX() - this.getX();
            double dz = p.getZ() - this.getZ();

            // Compute yaw toward player
            float yawToPlayer = (float)(Math.toDegrees(Math.atan2(-dx, dz)));

            // Turn 180° so BACK faces the player
            float yawAwayFromPlayer = yawToPlayer + 180f;

            // Apply rotation
            this.setYRot(yawAwayFromPlayer);
            this.yBodyRot = yawAwayFromPlayer;
            this.yHeadRot = yawAwayFromPlayer;

            Vec3 forward = new Vec3(this.getForward().x, 0, this.getForward().z).normalize();
            Vec3 toPlayer = p.position().subtract(this.position()).normalize();
            double dot = forward.dot(toPlayer);

            if (dot > -0.6) {
                p.sendSystemMessage(Component.literal("Looking at player"));
            }else{
                p.sendSystemMessage(Component.literal("Looking away from player"));
            }
        }
    }
}
