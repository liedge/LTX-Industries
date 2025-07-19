package liedge.ltxindustries;

import liedge.ltxindustries.entity.BubbleShieldUser;
import net.neoforged.neoforge.capabilities.EntityCapability;

public final class LTXICapabilities
{
    private LTXICapabilities() {}

    public static final EntityCapability<BubbleShieldUser, Void> ENTITY_BUBBLE_SHIELD = EntityCapability.createVoid(LTXIndustries.RESOURCES.location("bubble_shield"), BubbleShieldUser.class);
}