package net.zincstudios.scgextra;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@SuppressWarnings("unused")
public class CommonConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // Ability Enables
    private static final ForgeConfigSpec.BooleanValue ENABLE_ABILITY_ALERT = BUILDER
            .comment("enables alert ability, aggroing nearby mobs to the their targets")
            .define("enableAbilityAlert", true);

    private static final ForgeConfigSpec.BooleanValue ENABLE_ABILITY_FLARE = BUILDER
            .comment("enables flare ability, summoning 3 infantry mobs")
            .define("enableAbilityFlare", true);

    private static final ForgeConfigSpec.BooleanValue ENABLE_ABILITY_DIG = BUILDER
            .comment("enables dig ability, allowing mobs to destroy dirt like blocks")
            .define("enableAbilityDig", true);

    private static final ForgeConfigSpec.BooleanValue ENABLE_ABILITY_WEAKNESS = BUILDER
            .comment("enables weakness exposed state, summoning 3 infantry mobs")
            .define("enableAbilityWeakness", true);

    private static final ForgeConfigSpec.BooleanValue ENABLE_ABILITY_BULLETPROOF = BUILDER
            .comment("enables bulletproof capabilities, enables certain mobs to have bulletproof parts")
            .define("enableAbilityBulletproof", true);

    private static final ForgeConfigSpec.BooleanValue ENABLE_ABILITY_BAD_AIM = BUILDER
            .comment("enables bad aim state, certain mobs will fire guns with bad aim at the start but improves over time")
            // TODO: figure out if the disabled state would make accuracy constantly good or bad.
            .define("enableAbilityBadAim", true);

    private static final ForgeConfigSpec.BooleanValue ENABLE_ABILITY_WARNING = BUILDER
            .comment("enables warning state, flash of light appears before a massive attack. disabling would just remove the flash.")
            .define("enableAbilityWarning", true);


    private static final ForgeConfigSpec.IntValue ABILITY_ALERT_RANGE = BUILDER
            .comment("range for how far are mobs aggroed")
            .defineInRange("abilityAlertRange", 64, 1, 100);

    private static final ForgeConfigSpec.IntValue ABILITY_ALERT_COOLDOWN = BUILDER
            .comment("alert ability cooldown in seconds")
            .defineInRange("abilityAlertCooldown", 10, 1, 1000);


    private static final ForgeConfigSpec.IntValue ABILITY_FLARE_CHARGE = BUILDER
            .comment("flare ability cooldown in ticks")
            .defineInRange("abilityFlareCharge", 40, 1, 1000);

    private static final ForgeConfigSpec.IntValue ABILITY_FLARE_COOLDOWN = BUILDER
            .comment("flare ability cooldown in seconds")
            .defineInRange("abilityFlareCooldown", 10, 1, 1000);


    private static final ForgeConfigSpec.IntValue ABILITY_WEAKNESS_HEADSHOTS = BUILDER
            .comment("amount of headshots needed to trigger weakness exposed state")
            .defineInRange("abilityWeaknessHeadshots", 5, 1, 100);

    private static final ForgeConfigSpec.DoubleValue ABILITY_WEAKNESS_DAMAGE_MULT = BUILDER
            .comment("weakness exposed state incoming damage multiplier")
            .defineInRange("abilityWeaknessMult", 2, 0.1, 100);

    private static final ForgeConfigSpec.DoubleValue ABILITY_WEAKNESS_MIN = BUILDER
            .comment("percent threshold before weakness exposed is allowed to be triggered")
            .defineInRange("abilityWeaknessHealthThreshold", 100, 1.0, 100);


    // Might not be possible actually
//    private static final ForgeConfigSpec.IntValue ABILITY_WARNING_LENGTH = BUILDER
//            .comment("how long would the warning flash be in ticks")
//            .defineInRange("abilityWeaknessHealthThreshold", 10, 4, 100);


    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean enableAbilityAlert;
    public static boolean enableAbilityFlare;
    public static boolean enableAbilityDig;
    public static boolean enableAbilityWeakness;
    public static boolean enableAbilityBulletproof;
    public static boolean enableAbilityBadAim;
    public static boolean enableAbilityWarning;

    public static int abilityAlertRange;
    public static int abilityAlertCooldown;

    public static int abilityFlareCharge;
    public static int abilityFlareCooldown;

    public static int abilityWeaknessHeadshots;
    public static double abilityWeaknessDamageMult;
    public static double abilityWeaknessMinHealth;

    private static void updateConfigs() {
        enableAbilityAlert = ENABLE_ABILITY_ALERT.get();
        enableAbilityFlare = ENABLE_ABILITY_FLARE.get();
        enableAbilityDig = ENABLE_ABILITY_DIG.get();
        enableAbilityWeakness = ENABLE_ABILITY_WEAKNESS.get();
        enableAbilityBulletproof = ENABLE_ABILITY_BULLETPROOF.get();
        enableAbilityBadAim = ENABLE_ABILITY_BAD_AIM.get();
        enableAbilityWarning = ENABLE_ABILITY_WARNING.get();

        abilityAlertRange = ABILITY_ALERT_RANGE.get();
        abilityAlertCooldown = ABILITY_ALERT_COOLDOWN.get();

        abilityFlareCharge = ABILITY_FLARE_CHARGE.get();
        abilityFlareCooldown = ABILITY_FLARE_COOLDOWN.get();

        abilityWeaknessHeadshots = ABILITY_WEAKNESS_HEADSHOTS.get();
        abilityWeaknessDamageMult = ABILITY_WEAKNESS_DAMAGE_MULT.get();
        abilityWeaknessMinHealth = ABILITY_WEAKNESS_MIN.get();
    }

    static void onLoad(final ModConfigEvent.Loading event) {
        updateConfigs();
    }

    static void onReload(final ModConfigEvent.Reloading event) {
        updateConfigs();
    }
}
