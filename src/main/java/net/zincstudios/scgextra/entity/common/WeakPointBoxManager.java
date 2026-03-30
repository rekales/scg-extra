package net.zincstudios.scgextra.entity.common;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeakPointBoxManager {

    private static final Map<EntityType<LivingEntity>, List<WeakPointBox<LivingEntity>>> WEAK_POINT_BOXES_LIST = new HashMap<>();

    public static void registerWeakPointBox(EntityType<LivingEntity> type, List<WeakPointBox<LivingEntity>> weakPointBox) {
        WEAK_POINT_BOXES_LIST.putIfAbsent(type, weakPointBox);
    }

    @SafeVarargs
    public static void registerWeakPointBox(EntityType<LivingEntity> type, WeakPointBox<LivingEntity>... weakPointBox) {
        if (weakPointBox.length == 0) return;
        WEAK_POINT_BOXES_LIST.putIfAbsent(type, Arrays.stream(weakPointBox).toList());
    }

    public static @Nullable List<WeakPointBox<LivingEntity>> getWeakPointBoxesList(EntityType<?> type) {
        return WEAK_POINT_BOXES_LIST.get(type);
    }
}
