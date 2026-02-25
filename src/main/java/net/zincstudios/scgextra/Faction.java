package net.zincstudios.scgextra;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record Faction(String name, List<EntityType<?>> entities) {

    private static final Map<String, Faction> factions = new HashMap<>();

    public static void clearData() {
        factions.clear();
    }

    public static void addFaction(Faction faction) {
        factions.put(faction.name(), faction);
    }

    public static @Nullable Faction getFaction(String name) {
        return factions.get(name);
    }

    // Maybe cache or different structure to optimize this frequent call
    public static @Nullable Faction getFaction(EntityType<?> entityType) {
        return factions.values().stream()
                .filter(faction -> faction.entities.contains(entityType))
                .findFirst()
                .orElse(null);
    }

    public static boolean isEnemies(LivingEntity entity1, LivingEntity entity2) {
        Faction f1 = getFaction(entity1.getType());
        Faction f2 = getFaction(entity2.getType());

        return f1 != null && f2 != null && f1 != f2;
    }

    public static boolean isFriendlies(LivingEntity entity1, LivingEntity entity2) {
        Faction f1 = getFaction(entity1.getType());
        Faction f2 = getFaction(entity2.getType());

        return f1 != null && f1 == f2;
    }
}