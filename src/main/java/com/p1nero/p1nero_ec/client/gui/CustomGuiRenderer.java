package com.p1nero.p1nero_ec.client.gui;

import com.mojang.blaze3d.platform.Window;
import com.p1nero.p1nero_ec.PECConfig;
import com.p1nero.p1nero_ec.PEpicCataclysmMod;
import com.p1nero.p1nero_ec.capability.PECPlayer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class CustomGuiRenderer {
    private static final ResourceLocation BG_TEXTURE = ResourceLocation.fromNamespaceAndPath(PEpicCataclysmMod.MOD_ID, "textures/gui/skill_point/bg.png");
    private static final ResourceLocation IN_OUT_TEXTURE = ResourceLocation.fromNamespaceAndPath(PEpicCataclysmMod.MOD_ID, "textures/gui/skill_point/in_out.png");
    private static final ResourceLocation LOOP_TEXTURE = ResourceLocation.fromNamespaceAndPath(PEpicCataclysmMod.MOD_ID, "textures/gui/skill_point/loop.png");
    private static final List<SkillIcon> SKILL_ICONS = new ArrayList<>();
    private static final int MAX_INTERVAL = 1;
    private static int interval = MAX_INTERVAL;

    public static void renderSkillPoints(GuiGraphics guiGraphics, Window window, float partialTick) {
        LocalPlayerPatch localPlayerPatch = ClientEngine.getInstance().getPlayerPatch();
        if (localPlayerPatch != null) {
            if (!PECConfig.GLOBAL_CHARGE.get() && !PECPlayer.isValidWeapon(localPlayerPatch.getOriginal().getMainHandItem())) {
                return;
            }
            LocalPlayer localPlayer = localPlayerPatch.getOriginal();
            if (!localPlayer.isCreative() && !localPlayer.isSpectator()) {
                int x = (int) (window.getGuiScaledWidth() / 2.0F - 32);
                int y = window.getGuiScaledHeight() - 70;
                if (!SKILL_ICONS.isEmpty()) {
                    for (int i = 0; i < PECPlayer.MAX_SKILL_POINTS; i++) {
                        SkillIcon skillIcon = SKILL_ICONS.get(i);
                        skillIcon.updateXY(100 + PECConfig.UI_X.get() + x + 20 * i, PECConfig.UI_Y.get() + y);
                        skillIcon.render(guiGraphics);
                    }
                }
            }
        }
    }

    public static void addPoint(int index) {
        if (SKILL_ICONS.size() <= index) {
            return;
        }
        SKILL_ICONS.get(index).onAdd();
    }

    public static void remove(int index) {
        if (SKILL_ICONS.size() <= index) {
            return;
        }
        SKILL_ICONS.get(index).setDelete();
    }

    public static boolean isSkillPointEmpty(int index) {
        if (SKILL_ICONS.size() <= index) {
            return false;
        }
        return SKILL_ICONS.get(index).removed;
    }

    public static void update() {
        if (interval > 0) {
            interval--;
            if (interval == 0) {
                interval = MAX_INTERVAL;
                if (SKILL_ICONS.isEmpty()) {
                    for (int i = 0; i < PECPlayer.MAX_SKILL_POINTS; i++) {
                        SKILL_ICONS.add(new SkillIcon());
                    }
                }
                SKILL_ICONS.forEach(SkillIcon::update);
            }
        }
    }

    public static void reset() {
        SKILL_ICONS.forEach(SkillIcon::reset);
    }

    private static class SkillIcon {
        private final int IN = 0, LOOP = 1, OUT = 2, MAX_FRAME_INDEX = 7;
        private int x, y;
        private int currentType = IN;
        private int currentIndex = 0;
        private ResourceLocation currentTexture = IN_OUT_TEXTURE;
        private boolean removed = true;

        private void reset() {
            removed = true;
            currentType = IN;
            currentIndex = 0;
            currentTexture = IN_OUT_TEXTURE;
        }

        private void onAdd() {
            removed = false;
            currentType = IN;
            currentIndex = 0;
            currentTexture = IN_OUT_TEXTURE;
        }

        private void setDelete() {
            if (currentType == OUT) {
                //防止重复退出
                return;
            }
            currentTexture = IN_OUT_TEXTURE;
            currentType = OUT;
            currentIndex = MAX_FRAME_INDEX;
        }

        private void setLoop() {
            currentTexture = LOOP_TEXTURE;
            currentIndex = 0;
            currentType = LOOP;
        }

        private void updateXY(int x, int y) {
            this.x = x;
            this.y = y;
        }

        private void update() {
            if (currentType == IN) {
                currentIndex++;
                if (currentIndex > MAX_FRAME_INDEX) {
                    setLoop();
                }
            }

            if (currentType == LOOP) {
                currentIndex++;
                if (currentIndex > MAX_FRAME_INDEX) {
                    currentIndex = 0;
                }
            }

            if (currentType == OUT && currentIndex > 0) {
                currentIndex--;
                if (currentIndex == 0) {
                    removed = true;
                }
            }
        }

        private void render(GuiGraphics guiGraphics) {
            guiGraphics.blit(BG_TEXTURE, x, y, 64, 64, 0, 0, 64, 64, 64, 64);//画背景
            if (!removed) {
//                System.out.println(currentTexture + " " + currentIndex + " " + currentType);
                guiGraphics.blit(currentTexture, x, y, 64, 64, 64 * currentIndex, 0, 64, 64, 512, 64);
            }
        }

    }

}
