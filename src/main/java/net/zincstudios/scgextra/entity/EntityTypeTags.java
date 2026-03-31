package net.zincstudios.scgextra.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.zincstudios.scgextra.SCGExtra;

public class EntityTypeTags {

    // Enemy Rank
    public static final TagKey<EntityType<?>> INFANTRY = tag("infantry");
    public static final TagKey<EntityType<?>> ELITE = tag("elite");
    public static final TagKey<EntityType<?>> BOSS = tag("boss");

    // Faction, NOTE: don't forget to add
    public static final TagKey<EntityType<?>> ASGHARIAN = tag("asgharian");
    public static final TagKey<EntityType<?>> COG = tag("cog");
    public static final TagKey<EntityType<?>> RRC = tag("rrc");
    public static final TagKey<EntityType<?>> WHALER = tag("whaler");

    public static TagKey<EntityType<?>> tag(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, SCGExtra.asResource(name));
    }
}