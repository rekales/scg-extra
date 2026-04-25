package net.zincstudios.scgextra.entity;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public enum EnemyRank {
    INFANTRY(EntityTypeTags.INFANTRY),
    ELITE(EntityTypeTags.ELITE),
    MINIBOSS(EntityTypeTags.MINIBOSS),
    BOSS(EntityTypeTags.BOSS),
    NONE();

    private static final Map<EntityType<?>, EnemyRank> ENTITY_RANK_CACHE = new HashMap<>();

    @Nullable
    private final TagKey<EntityType<?>> tag;

    EnemyRank(@Nonnull TagKey<EntityType<?>> tag) {
        this.tag = tag;
    }

    EnemyRank() {
        this.tag = null;
    }

    @Nullable
    public TagKey<EntityType<?>> getTag() {
        return this.tag;
    }

    public static EnemyRank getEnemyRank(EntityType<?> entityType) {
        EnemyRank rank = ENTITY_RANK_CACHE.get(entityType);
        if (rank == null) {
            for (EnemyRank value : EnemyRank.values()) {
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
