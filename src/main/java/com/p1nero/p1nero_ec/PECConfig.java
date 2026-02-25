package com.p1nero.p1nero_ec;

import net.minecraftforge.common.ForgeConfigSpec;

public class PECConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.IntValue UI_X = BUILDER.comment("自定义技能图标的x位置偏移").defineInRange("ui_x", 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
    public static final ForgeConfigSpec.IntValue UI_Y = BUILDER.comment("自定义技能图标的y位置偏移").defineInRange("ui_y", 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
    public static final ForgeConfigSpec.BooleanValue GLOBAL_CHARGE = BUILDER.comment("是否启用全局的充能设置").define("global_charge", false);
    public static final ForgeConfigSpec.BooleanValue CUSTOM_CHARGE = BUILDER.comment("是否自定义充能").define("custom_charge", false);
    public static final ForgeConfigSpec.BooleanValue DISABLE_ABYSS_BLAST_BREAK = BUILDER.comment("是否禁用利维坦激光破坏方块").define("disable_abyss_blast_break", false);
    public static final ForgeConfigSpec SPEC = BUILDER.build();
}
