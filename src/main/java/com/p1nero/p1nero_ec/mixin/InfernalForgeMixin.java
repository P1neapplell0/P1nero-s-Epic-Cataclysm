package com.p1nero.p1nero_ec.mixin;

import com.github.L_Ender.cataclysm.items.Infernal_forge;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Infernal_forge.class)
public class InfernalForgeMixin {
    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void cancelEarthQuake(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        cir.setReturnValue(InteractionResult.PASS);
    }
}
