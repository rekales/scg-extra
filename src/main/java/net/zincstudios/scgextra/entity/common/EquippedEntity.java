package net.zincstudios.scgextra.entity.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;
import top.ribs.scguns.config.EntityEquipmentConfig;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Base class for entities that will be given items and their corresponding default goals by EntityEquipmentConfig
 * To avoid having to override finalizeSpawn and accidentally messing up
 *
 * @see top.ribs.scguns.config.EntityEquipmentConfig
 */
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
public class EquippedEntity extends Monster {

    private final ResourceLocation equipmentResLoc;

    /**
     * Uses the registered name of the entity as the equipment resource location
     */
    public EquippedEntity(EntityType<? extends Monster> entity, Level level) {
        this(entity, level, defaultEquipmentResLoc(entity));
    }

    public EquippedEntity(EntityType<? extends Monster> entity, Level level, ResourceLocation equipmentResLoc) {
        super(entity, level);
        this.equipmentResLoc = equipmentResLoc;
    }

    private static ResourceLocation defaultEquipmentResLoc(EntityType<? extends LivingEntity> entity) {
        return ForgeRegistries.ENTITY_TYPES.getKey(entity);
    }

    public ResourceLocation getEquipmentResLoc() {
        return this.equipmentResLoc;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        EntityEquipmentConfig.equipEntity(this, this.equipmentResLoc.toString());
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    public boolean isLeftHanded() {
        return false;  // Made explicit because it sometimes spawns left-handed by slim chance
    }
}
