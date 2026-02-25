package com.p1nero.p1nero_ec.mixin.weapon_desc;

import com.github.L_Ender.cataclysm.items.Astrape;
import com.p1nero.p1nero_ec.PEpicCataclysmMod;
import com.p1nero.p1nero_ec.capability.PECDataManager;
import com.p1nero.p1nero_ec.client.KeyMappings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = Astrape.class)
public class AstrapeClientMixin {

    @Inject(method = "appendHoverText", at = @At("HEAD"), cancellable = true)
    public void pec$appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn, CallbackInfo ci) {
        ci.cancel();
        if(PECDataManager.ASTRAPE_LOCK.get(Minecraft.getInstance().player)) {
            tooltip.add(PEpicCataclysmMod.astrapeLock);
            return;
        }
        tooltip.add(Component.translatable("skill.p1nero_ec.desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("skill.p1nero_ec.astrape.desc1",
                KeyMappings.SKILL_1.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.GOLD),
                2).withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("skill.p1nero_ec.astrape.desc2",
                KeyMappings.SKILL_2.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.GOLD),
                3).withStyle(ChatFormatting.AQUA));
    }
}
