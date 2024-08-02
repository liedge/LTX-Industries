package liedge.limatech;

import liedge.limatech.entity.BubbleShieldUser;
import net.neoforged.neoforge.capabilities.EntityCapability;

public final class LimaTechCapabilities
{
    private LimaTechCapabilities() {}

    public static final EntityCapability<BubbleShieldUser, Void> ENTITY_BUBBLE_SHIELD = EntityCapability.createVoid(LimaTech.RESOURCES.location("bubble_shield"), BubbleShieldUser.class);
}