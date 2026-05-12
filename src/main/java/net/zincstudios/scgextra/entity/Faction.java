package net.zincstudios.scgextra.entity;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.zincstudios.scgextra.SCGExtra;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record Faction(String name) {

    private static final String FACTION_NAMESPACE = "scgextra";
    private static final String FACTION_PATH_PREFIX = "factions/";
    public static final Faction NO_FACTION = new Faction("none");
    private static final Map<TagKey<EntityType<?>>, Faction> KEY_FACTION_MAP = new HashMap<>();
    private static final Map<EntityType<?>, Faction> ENTITY_FACTION_CACHE = new HashMap<>();

    public static void onTagsUpdated(TagsUpdatedEvent event) {
        KEY_FACTION_MAP.clear();
        ENTITY_FACTION_CACHE.clear();

        Registry<EntityType<?>> registry = event.getRegistryAccess()
                .registryOrThrow(Registries.ENTITY_TYPE);

        registry.getTagNames().forEach(tagKey -> {
            ResourceLocation loc = tagKey.location();
            if (loc.getNamespace().equals(FACTION_NAMESPACE) && loc.getPath().startsWith(FACTION_PATH_PREFIX)) {
                String name = loc.getPath().substring(FACTION_PATH_PREFIX.length());
                KEY_FACTION_MAP.put(tagKey, new Faction(name));
                SCGExtra.LOGGER.info("Added Faction: {}", name);
            }
        });
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

    public static @Nullable Faction getFaction(String factionName) {
        return KEY_FACTION_MAP.values().stream()
                .filter(faction -> faction.name.equals(factionName))
                .findFirst()
                .orElse(null);
    }

    public static List<EntityType<?>> getFactionEntities(Faction faction) {
        return ENTITY_FACTION_CACHE.entrySet().stream()
                .filter(e -> e.getValue() == faction)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
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
