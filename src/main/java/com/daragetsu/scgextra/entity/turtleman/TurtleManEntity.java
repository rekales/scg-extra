package com.daragetsu.scgextra.entity.turtleman;

import com.daragetsu.scgextra.entity.ModEntities;
import net.minecraft.nbt.CompoundTag;
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
        this.goalSelector.addGoal(1, new GunAttackGoal<>(this, mainHandItem, 1.0F, AIType.TACTICAL, 3));
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
}
