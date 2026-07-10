package net.zincstudios.scgextra.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import top.ribs.scguns.item.RaidFlareItem;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class SuperRaidFlareItem extends RaidFlareItem {

    private final String baseRaidId;

    public SuperRaidFlareItem(Properties properties, String raidId, String baseRaidId) {
        super(properties, raidId);
        this.baseRaidId = baseRaidId;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("raid.scguns." + this.baseRaidId + ".name").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.scguns.raid_flare.requires_pistol").withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC}));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
