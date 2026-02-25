package com.p1nero.p1nero_ec.gameassets;

import com.p1nero.p1nero_ec.PEpicCataclysmMod;
import com.p1nero.p1nero_ec.skills.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.skill.Skill;

@Mod.EventBusSubscriber(modid = PEpicCataclysmMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PECSkills {

    public static Skill STEP;
    public static Skill CERAUNUS_INNATE;
    public static Skill INFERNAL_INNATE;
    public static Skill WRATH_OF_THE_DESERT_INNATE;
    public static Skill THE_INCINERATOR_INNATE;
    public static Skill SOUL_RENDER_INNATE;
    public static Skill ASTRAPE_INNATE;
    public static Skill TIDAL_CLAW_INNATE;
    public static Skill GAUNTLET_GUARD_INNATE;
    public static Skill ANNIHILATOR_INNATE;

    @SubscribeEvent
    public static void buildSkills(SkillBuildEvent event) {
        SkillBuildEvent.ModRegistryWorker registryWorker = event.createRegistryWorker(PEpicCataclysmMod.MOD_ID);
//        STEP = registryWorker.build("step", DefaultDodgeSkill::new, DefaultDodgeSkill.createDodgeBuilder()
//                .setAnimations(PECAnimations.STEP_F, PECAnimations.STEP_B, PECAnimations.STEP_L, PECAnimations.STEP_R));

        CERAUNUS_INNATE = registryWorker.build("ceraunus_innate", CeraunusInnateSkill::new, PECWeaponInnateSkillBase.createBuilder());
        INFERNAL_INNATE = registryWorker.build("infernal_innate", InfernalForgeInnateSkill::new, PECWeaponInnateSkillBase.createBuilder());
        WRATH_OF_THE_DESERT_INNATE = registryWorker.build("wrath_of_the_desert_innate", Wrath_of_the_desertInnateSkill::new, PECWeaponInnateSkillBase.createBuilder());
        SOUL_RENDER_INNATE = registryWorker.build("soul_render_innate", SoulRenderInnateSkill::new, PECWeaponInnateSkillBase.createBuilder());
        ASTRAPE_INNATE = registryWorker.build("astrape_innate", AstrapeInnateSkill::new, PECWeaponInnateSkillBase.createBuilder());
        THE_INCINERATOR_INNATE = registryWorker.build("the_incinerator_innate", TheIncineratorInnateSkill::new, PECWeaponInnateSkillBase.createBuilder());
        TIDAL_CLAW_INNATE = registryWorker.build("tidal_claw_innate", TidalClawsInnateSkill::new, PECWeaponInnateSkillBase.createBuilder());
        GAUNTLET_GUARD_INNATE = registryWorker.build("gauntlet_guard_innate", GauntletGuardInnateSkill::new, PECWeaponInnateSkillBase.createBuilder());
        ANNIHILATOR_INNATE = registryWorker.build("annihilator_innate", AnnihilatorInnateSkill::new, PECWeaponInnateSkillBase.createBuilder());
    }
}
