package net.zincstudios.scgextra.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.WeakHashMap;

public class ArmorPiercingHandler {

    private static final WeakHashMap<LivingEntity, Float> PENDING_DAMAGE = new WeakHashMap<>();

    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        if (event.getSource().getDirectEntity() instanceof LivingEntity attacker
                && attacker.getMainHandItem().getItem() instanceof ArmorPiercing) {
            PENDING_DAMAGE.put(event.getEntity(), event.getAmount());
        }
    }

    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        Float fullDamage = PENDING_DAMAGE.remove(event.getEntity());
        if (fullDamage != null && fullDamage > event.getAmount()) {
            event.setAmount(fullDamage);
        }
    }
}
