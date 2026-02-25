package com.p1nero.p1nero_ec.mixin.weapon_desc;

import com.github.L_Ender.cataclysm.items.Wrath_of_the_desert;
import com.p1nero.invincible.api.animation.types.ScanAttackAnimation;
import com.p1nero.p1nero_ec.PEpicCataclysmMod;
import com.p1nero.p1nero_ec.capability.PECDataManager;
import com.p1nero.p1nero_ec.client.KeyMappings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;

import java.util.List;

import static com.github.L_Ender.cataclysm.items.Wrath_of_the_desert.getUseTime;

@Mixin(Wrath_of_the_desert.class)
public abstract class Wrath_of_the_desertClientMixin extends Item {
    public Wrath_of_the_desertClientMixin(Properties p_41383_) {
        super(p_41383_);
    }

    @Shadow(remap = false)
    public static void setUseTime(ItemStack stack, int useTime) {
    }

    @Shadow(remap = false)
    private static int getMaxLoadTime() {
        return 0;
    }

    @Shadow
    public abstract int getUseDuration(@NotNull ItemStack stack);

    @Inject(method = "inventoryTick", at = @At("HEAD"), cancellable = true)
    private void pec$inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean held, CallbackInfo ci) {
        super.inventoryTick(stack, level, entity, i, held);
        boolean using = entity instanceof LivingEntity living && living.getUseItem().equals(stack);
        int useTime = getUseTime(stack);
        if (level.isClientSide) {

            CompoundTag tag = stack.getOrCreateTag();
            if (tag.getInt("PrevUseTime") != tag.getInt("UseTime")) {
                tag.putInt("PrevUseTime", getUseTime(stack));
            }

            LocalPlayerPatch localPlayerPatch = ClientEngine.getInstance().getPlayerPatch();
            if (localPlayerPatch != null && localPlayerPatch.getEntityState().attacking()) {
                AnimationPlayer animationPlayer = localPlayerPatch.getAnimator().getPlayerFor(null);
                if (animationPlayer != null && animationPlayer.getAnimation().get() instanceof ScanAttackAnimation) {
                    setUseTime(stack, (int) (animationPlayer.getElapsedTime() * 40.0F));
                }
            } else {
                int maxLoadTime = getMaxLoadTime();
                if (using && useTime < maxLoadTime) {
                    int set = useTime + 1;
                    setUseTime(stack, set);
                }
            }
        }
        if (!using && useTime > 0.0F) {
            setUseTime(stack, Math.max(0, useTime - 5));
        }
        ci.cancel();
    }

    @Inject(method = "appendHoverText", at = @At("HEAD"), cancellable = true)
    public void pec$appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn, CallbackInfo ci) {
        ci.cancel();
        if(PECDataManager.WRATH_OF_THE_DESERT_LOCK.get(Minecraft.getInstance().player)) {
            tooltip.add(PEpicCataclysmMod.wrathOfTheDesertLock);
            return;
        }
        tooltip.add(Component.translatable("skill.p1nero_ec.desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("skill.p1nero_ec.wrath_of_the_desert.desc1", KeyMappings.SKILL_1.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.GOLD), 1).withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.translatable("skill.p1nero_ec.wrath_of_the_desert.desc2", KeyMappings.SKILL_2.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.GOLD), 2).withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.translatable("skill.p1nero_ec.wrath_of_the_desert.desc3", KeyMappings.SKILL_3.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.GOLD), 4).withStyle(ChatFormatting.YELLOW));
    }

}
