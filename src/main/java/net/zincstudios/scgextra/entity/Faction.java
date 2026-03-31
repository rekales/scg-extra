package net.zincstudios.scgextra.entity;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record Faction(String name) {

    public static final Faction NO_FACTION = new Faction("none");
    private static final Map<TagKey<EntityType<?>>, Faction> KEY_FACTION_MAP = new HashMap<>();
    private static final Map<EntityType<?>, Faction> ENTITY_FACTION_CACHE = new HashMap<>();


    public static void registerFaction(TagKey<EntityType<?>> tag, String name) {
        KEY_FACTION_MAP.put(tag, new Faction(name));
    }

    public static void clearFactionCache() {
        ENTITY_FACTION_CACHE.clear();
    }

    public static List<Faction> getFactions() {
        return new ArrayList<>(KEY_FACTION_MAP.values());
    }

    public static Faction getFaction(EntityType<?> entityType) {
        Faction faction = ENTITY_FACTION_CACHE.get(entityType);
        if (faction == null) {
            for (Map.Entry<TagKey<EntityType<?>>, Faction> entry : KEY_FACTION_MAP.entrySet()) {
                if (entityType.is(entry.getKey())) {
                    ENTITY_FACTION_CACHE.put(entityType, entry.getValue());
                    return entry.getValue();
                }
            }
            ENTITY_FACTION_CACHE.put(entityType, NO_FACTION);
            return NO_FACTION;
        }
        return faction;
    }

    public static boolean isEnemies(LivingEntity entity1, LivingEntity entity2) {
        Faction f1 = getFaction(entity1.getType());
        Faction f2 = getFaction(entity2.getType());

        return f1 != NO_FACTION && f2 != NO_FACTION && f1 != f2;
    }

    public static boolean isFriendlies(LivingEntity entity1, LivingEntity entity2) {
        Faction f1 = getFaction(entity1.getType());
        Faction f2 = getFaction(entity2.getType());

        return f1 != NO_FACTION && f1 == f2;
    }
}