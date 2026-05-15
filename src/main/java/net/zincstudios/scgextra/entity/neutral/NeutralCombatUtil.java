package net.zincstudios.scgextra.entity.neutral;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import top.ribs.scguns.init.ModEffects;
import top.ribs.scguns.item.GunItem;

import java.util.List;
import java.util.Set;

public final class NeutralCombatUtil {
    private static final String NATURAL_SPAWN_TAG = "scgextra.natural_spawn";
    private static final String PROGRESSION_DATA_NAME = "scgextra_neutral_progression";
    private static final String OVERWORLD_GUN_UNLOCK_TAG = "OverworldGunProgressionUnlocked";
    private static final Set<TagKey<Item>> PROGRESSION_GUN_TIER_TAGS = Set.of(
            scgGunsTierTag("iron_gun_tier"),
            scgGunsTierTag("frontier_gun_tier"),
            scgGunsTierTag("treated_brass_gun_tier"),
            scgGunsTierTag("diamond_steel_gun_tier"),
            scgGunsTierTag("wrecker_gun_tier"),
            scgGunsTierTag("deep_dark_gun_tier"),
            scgGunsTierTag("end_gun_tier"),
            scgGunsTierTag("piglin_gun_tier"),
            scgGunsTierTag("ocean_gun_tier"),
            scgGunsTierTag("scorched_gun_tier")
    );
    private static final AABB FULL_LEVEL_BOUNDS = new AABB(-30_000_000, -64, -30_000_000, 30_000_000, 320, 30_000_000);
    private static final Set<String> IRON_TIER_GUN_IDS = Set.of(
            "iron_javelin",
            "iron_spear",
            "m3_carabine",
            "mk43_rifle",
            "combat_shotgun"
    );

    private NeutralCombatUtil() {
    }

    private static TagKey<Item> scgGunsTierTag(String path) {
        return TagKey.create(
                net.minecraft.core.registries.Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath("scguns", path)
        );
    }

    public static void applyLacerate(LivingEntity target, int durationTicks) {
        target.addEffect(new MobEffectInstance(ModEffects.LACERATED.get(), durationTicks));
    }

    public static List<Player> nearbyPlayers(Mob mob, double radius) {
        AABB box = mob.getBoundingBox().inflate(radius);
        return mob.level().getEntitiesOfClass(Player.class, box, p -> !p.isCreative() && !p.isSpectator());
    }

    public static boolean canSpawnEndSurface(LevelAccessor level, BlockPos pos) {
        BlockState ground = level.getBlockState(pos.below());
        if (!ground.is(Blocks.END_STONE)) {
            return false;
        }

        return level.getBlockState(pos).isAir() && level.getBlockState(pos.above()).isAir();
    }

    public static boolean canSpawnEndSurfaceMob(LevelAccessor level, BlockPos pos, Entity entity) {
        if (!canSpawnEndSurface(level, pos)) {
            return false;
        }
        if (level instanceof Level vanillaLevel && vanillaLevel.getDifficulty() == Difficulty.PEACEFUL) {
            return false;
        }
        return level.noCollision(entity);
    }

    public static <T extends Monster> boolean canSpawnEndMonster(EntityType<T> type, ServerLevelAccessor level, MobSpawnType spawnReason, BlockPos pos, RandomSource random) {
        if (spawnReason == MobSpawnType.SPAWN_EGG || spawnReason == MobSpawnType.COMMAND) {
            return true;
        }
        if (!Monster.checkMonsterSpawnRules(type, level, spawnReason, pos, random)) {
            return false;
        }
        return canSpawnEndSurface(level, pos);
    }

    public static boolean isWaterAtOrBelow(LevelAccessor level, BlockPos pos) {
        return level.getFluidState(pos).is(FluidTags.WATER)
                || level.getFluidState(pos.below()).is(FluidTags.WATER);
    }

    public static void markNaturalSpawn(Mob mob, MobSpawnType spawnType) {
        if (spawnType == MobSpawnType.NATURAL) {
            mob.getPersistentData().putBoolean(NATURAL_SPAWN_TAG, true);
        }
    }

    public static boolean hasReachedNaturalSpawnCap(ServerLevelAccessor level, Class<? extends Mob> mobClass, int maxCount) {
        if (maxCount <= 0) {
            return true;
        }
        int count = level.getLevel()
                .getEntitiesOfClass(mobClass, FULL_LEVEL_BOUNDS, entity -> entity.getPersistentData().getBoolean(NATURAL_SPAWN_TAG))
                .size();
        return count >= maxCount;
    }

    public static boolean hasOverworldGunProgression(ServerLevelAccessor level) {
        ServerLevel overworld = level.getLevel().getServer().overworld();
        OverworldProgressionData progressionData = OverworldProgressionData.get(overworld);
        if (progressionData.isUnlocked()) {
            return true;
        }

        for (ServerPlayer player : level.getLevel().getServer().getPlayerList().getPlayers()) {
            if (player.isSpectator()) {
                continue;
            }

            for (ItemStack stack : player.getInventory().items) {
                if (isIronTierGun(stack)) {
                    progressionData.setUnlocked();
                    return true;
                }
            }
            for (ItemStack stack : player.getInventory().offhand) {
                if (isIronTierGun(stack)) {
                    progressionData.setUnlocked();
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isIronTierGun(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        for (TagKey<Item> tierTag : PROGRESSION_GUN_TIER_TAGS) {
            if (stack.is(tierTag)) {
                return true;
            }
        }
        if (!(stack.getItem() instanceof GunItem)) {
            return false;
        }
        Item item = stack.getItem();
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
        if (id == null || !"scguns".equals(id.getNamespace())) {
            return false;
        }
        String path = id.getPath();
        return path.contains("iron") || IRON_TIER_GUN_IDS.contains(path);
    }

    private static final class OverworldProgressionData extends SavedData {
        private boolean unlocked;

        static OverworldProgressionData get(ServerLevel level) {
            return level.getDataStorage().computeIfAbsent(
                    OverworldProgressionData::load,
                    OverworldProgressionData::new,
                    PROGRESSION_DATA_NAME
            );
        }

        static OverworldProgressionData load(CompoundTag tag) {
            OverworldProgressionData data = new OverworldProgressionData();
            data.unlocked = tag.getBoolean(OVERWORLD_GUN_UNLOCK_TAG);
            return data;
        }

        boolean isUnlocked() {
            return this.unlocked;
        }

        void setUnlocked() {
            if (!this.unlocked) {
                this.unlocked = true;
                this.setDirty();
            }
        }

        @Override
        public CompoundTag save(CompoundTag tag) {
            tag.putBoolean(OVERWORLD_GUN_UNLOCK_TAG, this.unlocked);
            return tag;
        }
    }
}

