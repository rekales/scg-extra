package net.zincstudios.scgextra.entity;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public enum EnemyRanks {
    INFANTRY(EntityTypeTags.INFANTRY),
    ELITE(EntityTypeTags.ELITE),
    BOSS(EntityTypeTags.BOSS),
    NONE();

    private static final Map<EntityType<?>, EnemyRanks> ENTITY_RANK_CACHE = new HashMap<>();

    @Nullable
    private final TagKey<EntityType<?>> tag;

    EnemyRanks(@Nonnull TagKey<EntityType<?>> tag) {
        this.tag = tag;
    }

    EnemyRanks() {
        this.tag = null;
    }

    @Nullable
    public TagKey<EntityType<?>> getTag() {
        return this.tag;
    }

    public static EnemyRanks getEnemyRank(EntityType<?> entityType) {
        EnemyRanks rank = ENTITY_RANK_CACHE.get(entityType);
        if (rank == null) {
            for (EnemyRanks value : EnemyRanks.values()) {
                if (value.getTag() != null && entityType.is(value.getTag())) {
                    ENTITY_RANK_CACHE.put(entityType, value);
                    return value;
                }
            }
            ENTITY_RANK_CACHE.put(entityType, NONE);
            return NONE;
        }
        return rank;
    }
}
