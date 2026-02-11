package com.daragetsu.scgextra;

import net.minecraft.world.entity.EntityType;

import java.util.List;

public record Faction(String name, List<EntityType<?>> entities) {

}