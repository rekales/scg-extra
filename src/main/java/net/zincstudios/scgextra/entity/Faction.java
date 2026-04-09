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

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record Faction(String name) {

    private static final String FACTION_NAMESPACE = "scgextra";
    private static final String FACTION_PATH_PREFIX = "factions/";
    public static final Faction NO_FACTION = new Faction("none");
    private static final Faction ASGHARIAN = new Faction("asgharian");
    private static final Faction COG = new Faction("cog");
    private static final Faction FAC = new Faction("fac");
    private static final Faction RRC = new Faction("rrc");
    private static final Faction WHALER = new Faction("whaler");
    private static final Map<TagKey<EntityType<?>>, Faction> KEY_FACTION_MAP = new HashMap<>();
    private static final Map<EntityType<?>, Faction> ENTITY_FACTION_CACHE = new HashMap<>();
    private static final Map<TagKey<EntityType<?>>, Faction> STATIC_FACTION_TAG_MAP = Map.of(
            EntityTypeTags.ASGHARIAN, ASGHARIAN,
            EntityTypeTags.COG, COG,
            EntityTypeTags.FAC, FAC,
            EntityTypeTags.RRC, RRC,
            EntityTypeTags.WHALER, WHALER
    );

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
            for (Map.Entry<TagKey<EntityType<?>>, Faction> entry : STATIC_FACTION_TAG_MAP.entrySet()) {
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

        if (f1.equals(NO_FACTION) || f2.equals(NO_FACTION)) {
            return false;
        }

        if (f1.equals(f2)) {
            return false;
        }

        return !isCrossFactionAlliance(f1, f2);
    }

    public static boolean isFriendlies(LivingEntity entity1, LivingEntity entity2) {
        Faction f1 = getFaction(entity1.getType());
        Faction f2 = getFaction(entity2.getType());

        if (f1.equals(NO_FACTION) || f2.equals(NO_FACTION)) {
            return false;
        }

        return f1.equals(f2) || isCrossFactionAlliance(f1, f2);
    }

    private static boolean isCrossFactionAlliance(Faction f1, Faction f2) {
        return (f1.name.equals("fac") && f2.name.equals("whaler"))
                || (f1.name.equals("whaler") && f2.name.equals("fac"));
    }
}
