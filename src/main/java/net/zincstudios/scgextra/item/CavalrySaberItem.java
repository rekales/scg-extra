package net.zincstudios.scgextra.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.LogicalSide;
import top.ribs.scguns.init.ModEffects;
import top.ribs.scguns.init.ModTags;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class CavalrySaberItem extends SwordItem implements HurtEffects {

    private static final Map<UUID, ArrayDeque<Vec3>> POS_HISTORY = new HashMap<>();
    private static final int HISTORY_SIZE = 5;

    public CavalrySaberItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        attacker.resetFallDistance();
        this.hurtEffect(stack, target, attacker);
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void hurtEffect(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!target.level().isClientSide() && !target.getType().is(ModTags.Entities.CANNOT_BE_LACERATED)) {
            target.addEffect(new MobEffectInstance(ModEffects.LACERATED.get(), 100));
        }
    }

    public static void onEntityHurt(LivingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof CavalrySaberItem)) return;

        UUID id = player.getUUID();
        ArrayDeque<Vec3> history = POS_HISTORY.get(id);

        if (history.size() < 2) return;
        Vec3[] arr = history.toArray(new Vec3[0]);
        Vec3 sum = Vec3.ZERO;
        for (int i = 1; i < arr.length; i++) {
            sum = sum.add(arr[i].subtract(arr[i - 1]));
        }
        Vec3 avgDelta = sum.scale(1.0 / (arr.length - 1));
        avgDelta = new Vec3(avgDelta.x, avgDelta.y/1.8, avgDelta.z);

        float damageMult = (float)(Math.max(0, avgDelta.length()-0.17) * 2.3) + 1;
        event.setAmount(event.getAmount() * damageMult);
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.side != LogicalSide.SERVER) return;

        UUID id = event.player.getUUID();

        ArrayDeque<Vec3> history;
        if (POS_HISTORY.containsKey(id)) {
            history = POS_HISTORY.get(id);
        } else {
            history = new ArrayDeque<>(HISTORY_SIZE);
            POS_HISTORY.put(id, history);
        }
        if (history.size() >= HISTORY_SIZE) {
            history.pollFirst();
        }
        history.addLast(event.player.position());
    }
}
