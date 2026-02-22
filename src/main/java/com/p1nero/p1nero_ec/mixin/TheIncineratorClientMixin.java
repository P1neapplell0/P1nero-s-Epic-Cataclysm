package com.p1nero.p1nero_ec.mixin;

import com.github.L_Ender.cataclysm.items.The_Incinerator;
import com.p1nero.p1nero_ec.client.KeyMappings;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(The_Incinerator.class)
public class TheIncineratorClientMixin {

    @Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
    private void pec$releaseUsing(ItemStack p_77661_1_, CallbackInfoReturnable<UseAnim> cir) {
        cir.setReturnValue(UseAnim.NONE);
    }

    @Inject(method = "appendHoverText", at = @At("HEAD"), cancellable = true)
    public void pec$appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn, CallbackInfo ci) {
        tooltip.add(Component.translatable("skill.p1nero_ec.desc").withStyle(ChatFormatting.GRAY));
        if(ModList.get().isLoaded("tcrcore")) {
            tooltip.add(Component.translatable("skill.p1nero_ec.the_incinerator.desc1", KeyMappings.SKILL_1.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.GOLD), 5).withStyle(ChatFormatting.AQUA));
        }
        tooltip.add(Component.translatable("skill.p1nero_ec.the_incinerator.desc2", KeyMappings.SKILL_2.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.GOLD), 2).withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("skill.p1nero_ec.the_incinerator.desc3", KeyMappings.SKILL_3.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.GOLD), 3).withStyle(ChatFormatting.AQUA));
        ci.cancel();
    }

}
