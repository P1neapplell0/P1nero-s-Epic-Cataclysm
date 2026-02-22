package com.p1nero.p1nero_ec.events;

import com.github.L_Ender.cataclysm.init.ModItems;
import com.mojang.blaze3d.systems.RenderSystem;
import com.p1nero.p1nero_ec.PECMod;
import com.p1nero.p1nero_ec.capability.PECDataManager;
import com.p1nero.p1nero_ec.capability.PECCapabilityProvider;
import com.p1nero.p1nero_ec.capability.PECPlayer;
import com.p1nero.p1nero_ec.client.gui.CustomGuiRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.client.forgeevent.UpdatePlayerMotionEvent;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.main.EpicFightMod;

@Mod.EventBusSubscriber(modid = PECMod.MOD_ID, value = Dist.CLIENT)
public class ClientForgeEvents {

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay().id().equals(ResourceLocation.fromNamespaceAndPath(EpicFightMod.MODID, "weapon_innate"))) {
            LocalPlayer localPlayer = Minecraft.getInstance().player;
            if (localPlayer != null && PECPlayer.isValidWeapon(localPlayer.getMainHandItem())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Pre event) {
        RenderSystem.enableBlend();
        if (Minecraft.getInstance().screen == null && !Minecraft.getInstance().isPaused()) {
            CustomGuiRenderer.renderSkillPoints(event.getGuiGraphics(), event.getWindow(), event.getPartialTick());
        }
        RenderSystem.disableBlend();
    }

    @SubscribeEvent
    public static void onUpdateCompositeLayer(UpdatePlayerMotionEvent.CompositeLayer event) {
        ItemStack itemStack = event.getPlayerPatch().getOriginal().getMainHandItem();
        if (itemStack.is(ModItems.TIDAL_CLAWS.get()) && event.getPlayerPatch().getOriginal().isUsingItem()) {
            event.setMotion(LivingMotions.SHOT);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        LocalPlayerPatch localPlayerPatch = ClientEngine.getInstance().getPlayerPatch();
        if (localPlayerPatch != null) {
            LocalPlayer localPlayer = localPlayerPatch.getOriginal();
            PECPlayer pecPlayer = PECCapabilityProvider.getPlayer(localPlayer);
            boolean currentLockOn = localPlayerPatch.isTargetLockedOn();
            if (pecPlayer.isClientLockOn() != currentLockOn) {
                PECDataManager.isLockOn.put(localPlayer, currentLockOn);
                pecPlayer.setClientLockOn(currentLockOn);
            }


            CustomGuiRenderer.update();
            int currentSkillPoint = PECDataManager.skillPoint.get(localPlayer).intValue();
            if (pecPlayer.getLastSkillPoints() != currentSkillPoint) {
                pecPlayer.setLastSkillPoints(currentSkillPoint);
                for (int i = 0; i < PECPlayer.MAX_SKILL_POINTS; i++) {
                    if (i < currentSkillPoint) {
                        if (CustomGuiRenderer.isSkillPointEmpty(i)) {
                            CustomGuiRenderer.addPoint(i);
                        }
                    } else if (!CustomGuiRenderer.isSkillPointEmpty(i)) {
                        CustomGuiRenderer.remove(i);
                    }
                }
            }
        }
    }

}
