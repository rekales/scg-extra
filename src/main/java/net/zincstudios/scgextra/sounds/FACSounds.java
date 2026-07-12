package net.zincstudios.scgextra.sounds;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;
import net.zincstudios.scgextra.SCGExtra;

import static net.zincstudios.scgextra.sounds.ModSounds.SOUND_EVENTS;

public class FACSounds {

    public static final RegistryObject<SoundEvent> FAC_BLUECOAT_DEATH = SOUND_EVENTS.register("fac_bluecoat.death",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_bluecoat.death")));
    public static final RegistryObject<SoundEvent> FAC_BLUECOAT_HURT = SOUND_EVENTS.register("fac_bluecoat.hurt",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_bluecoat.hurt")));
    public static final RegistryObject<SoundEvent> FAC_BLUECOAT_IDLE = SOUND_EVENTS.register("fac_bluecoat.idle",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_bluecoat.idle")));

    public static final RegistryObject<SoundEvent> FAC_COMMISSAR_ATTACK = SOUND_EVENTS.register("fac_commissar.attack",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_commissar.attack")));
    public static final RegistryObject<SoundEvent> FAC_COMMISSAR_DEATH = SOUND_EVENTS.register("fac_commissar.death",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_commissar.death")));
    public static final RegistryObject<SoundEvent> FAC_COMMISSAR_FLARE = SOUND_EVENTS.register("fac_commissar.flare",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_commissar.flare")));
    public static final RegistryObject<SoundEvent> FAC_COMMISSAR_HURT = SOUND_EVENTS.register("fac_commissar.hurt",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_commissar.hurt")));
    public static final RegistryObject<SoundEvent> FAC_COMMISSAR_IDLE = SOUND_EVENTS.register("fac_commissar.idle",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_commissar.idle")));

    public static final RegistryObject<SoundEvent> FAC_LION_DEATH = SOUND_EVENTS.register("fac_lion.death",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_lion.death")));
    public static final RegistryObject<SoundEvent> FAC_LION_HURT = SOUND_EVENTS.register("fac_lion.hurt",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_lion.hurt")));
    public static final RegistryObject<SoundEvent> FAC_LION_IDLE = SOUND_EVENTS.register("fac_lion.idle",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_lion.idle")));

    public static final RegistryObject<SoundEvent> FAC_SHOVEL_KNIGHT_DEATH = SOUND_EVENTS.register("fac_shovel_knight.death",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_shovel_knight.death")));
    public static final RegistryObject<SoundEvent> FAC_SHOVEL_KNIGHT_HURT = SOUND_EVENTS.register("fac_shovel_knight.hurt",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_shovel_knight.hurt")));
    public static final RegistryObject<SoundEvent> FAC_SHOVEL_KNIGHT_IDLE = SOUND_EVENTS.register("fac_shovel_knight.idle",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_shovel_knight.idle")));

    public static final RegistryObject<SoundEvent> FAC_TANK_CANNON_CHARGE = SOUND_EVENTS.register("fac_tank.cannon_charge",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_tank.cannon_charge")));
    public static final RegistryObject<SoundEvent> FAC_TANK_DEATH = SOUND_EVENTS.register("fac_tank.death",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_tank.death")));
    public static final RegistryObject<SoundEvent> FAC_TANK_HURT = SOUND_EVENTS.register("fac_tank.hurt",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_tank.hurt")));
    public static final RegistryObject<SoundEvent> FAC_TANK_IDLE = SOUND_EVENTS.register("fac_tank.idle",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_tank.idle")));
    public static final RegistryObject<SoundEvent> FAC_TANK_STUN = SOUND_EVENTS.register("fac_tank.stun",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_tank.stun")));
    public static final RegistryObject<SoundEvent> FAC_TANK_WALK = SOUND_EVENTS.register("fac_tank.walk",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_tank.walk")));

    public static final RegistryObject<SoundEvent> FAC_TANK_BUSTER_DEATH = SOUND_EVENTS.register("fac_tank_buster.death",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_tank_buster.death")));
    public static final RegistryObject<SoundEvent> FAC_TANK_BUSTER_HURT = SOUND_EVENTS.register("fac_tank_buster.hurt",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_tank_buster.hurt")));
    public static final RegistryObject<SoundEvent> FAC_TANK_BUSTER_IDLE = SOUND_EVENTS.register("fac_tank_buster.idle",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_tank_buster.idle")));

    public static final RegistryObject<SoundEvent> FAC_TRENCHER_DEATH = SOUND_EVENTS.register("fac_trencher.death",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_trencher.death")));
    public static final RegistryObject<SoundEvent> FAC_TRENCHER_HURT = SOUND_EVENTS.register("fac_trencher.hurt",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_trencher.hurt")));
    public static final RegistryObject<SoundEvent> FAC_TRENCHER_IDLE = SOUND_EVENTS.register("fac_trencher.idle",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_trencher.idle")));

    public static final RegistryObject<SoundEvent> FAC_TRENCH_GOBLIN_ATTACK = SOUND_EVENTS.register("fac_trench_goblin.attack",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_trench_goblin.attack")));
    public static final RegistryObject<SoundEvent> FAC_TRENCH_GOBLIN_DEATH = SOUND_EVENTS.register("fac_trench_goblin.death",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_trench_goblin.death")));
    public static final RegistryObject<SoundEvent> FAC_TRENCH_GOBLIN_HURT = SOUND_EVENTS.register("fac_trench_goblin.hurt",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_trench_goblin.hurt")));
    public static final RegistryObject<SoundEvent> FAC_TRENCH_GOBLIN_IDLE = SOUND_EVENTS.register("fac_trench_goblin.idle",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_trench_goblin.idle")));

    public static final RegistryObject<SoundEvent> FAC_TRENCH_SNIPER_ALERT = SOUND_EVENTS.register("fac_trench_sniper.alert",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_trench_sniper.alert")));
    public static final RegistryObject<SoundEvent> FAC_TRENCH_SNIPER_DEATH = SOUND_EVENTS.register("fac_trench_sniper.death",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_trench_sniper.death")));
    public static final RegistryObject<SoundEvent> FAC_TRENCH_SNIPER_IDLE = SOUND_EVENTS.register("fac_trench_sniper.idle",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_trench_sniper.idle")));

    public static final RegistryObject<SoundEvent> FAC_WALKER_DEATH = SOUND_EVENTS.register("fac_walker.death",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_walker.death")));
    public static final RegistryObject<SoundEvent> FAC_WALKER_HURT = SOUND_EVENTS.register("fac_walker.hurt",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_walker.hurt")));
    public static final RegistryObject<SoundEvent> FAC_WALKER_IDLE = SOUND_EVENTS.register("fac_walker.idle",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_walker.idle")));
    public static final RegistryObject<SoundEvent> FAC_WALKER_RUN = SOUND_EVENTS.register("fac_walker.run",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_walker.run")));
    public static final RegistryObject<SoundEvent> FAC_WALKER_WALK = SOUND_EVENTS.register("fac_walker.walk",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("fac_walker.walk")));

    public static void init() {}
}