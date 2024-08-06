package liedge.limatech.client.gui;

import liedge.limatech.upgradesystem.EquipmentUpgrade;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;

import static liedge.limatech.LimaTech.RESOURCES;

public final class EquipmentUpgradeTextures extends TextureAtlasHolder
{
    private static EquipmentUpgradeTextures instance;

    public static EquipmentUpgradeTextures getUpgradeSprites()
    {
        if (instance == null) instance = new EquipmentUpgradeTextures(Minecraft.getInstance().getTextureManager());

        return instance;
    }

    private EquipmentUpgradeTextures(TextureManager textureManager)
    {
        super(textureManager, RESOURCES.textureLocation("atlas", "equipment_upgrades"), RESOURCES.location("equipment_upgrades"));
    }

    public TextureAtlasSprite getSprite(EquipmentUpgrade upgrade)
    {
        return getSprite(upgrade.iconLocation());
    }
}