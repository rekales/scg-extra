package net.zincstudios.scgextra.entity.common.gun;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.common.ProjectileManager;
import top.ribs.scguns.item.GunItem;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

// Doesn't do reload logic
@ParametersAreNonnullByDefault
public class HeldScorchedSimGun extends ScorchedSimGun {

    private final ItemStack gunStack;

    public HeldScorchedSimGun(ItemStack gunStack) {
        super(
                gunFromStack(gunStack),
                false,
                (float)gunFromStack(gunStack).getIdealAttackRange(),
                (float)gunFromStack(gunStack).getIdealAttackRange()*1.5f,
                0,
                (level, entity, gunBase) -> ProjectileManager.getInstance()
                        .getFactory(ForgeRegistries.ITEMS.getKey(Objects.requireNonNull(gunBase.getProjectile().getItem())))
                        .create(level, entity, gunStack, (GunItem) gunStack.getItem(), gunBase),
                vec -> vec
        );
        this.gunStack = gunStack;
    }

    private static Gun gunFromStack(ItemStack gunStack) {
        if (!(gunStack.getItem() instanceof GunItem gunItem)) {
            throw new IllegalArgumentException("gunStack is not a GunItem");
        }
        return gunItem.getModifiedGun(gunStack);
    }

    @Override
    public boolean hasChanged(LivingEntity entity) {
        // NOTE: nbt tags not checked, mostly irrelevant and causes ItemStack.matches to be false.
        return entity.getMainHandItem().getItem() != this.gunStack.getItem();
    }
}
