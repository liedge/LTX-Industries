package liedge.ltxindustries.client;

import com.mojang.blaze3d.platform.InputConstants;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.item.weapon.WeaponItem;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public final class LTXIKeyMappings
{
    public static final String CATEGORY_LTXI = LTXIndustries.RESOURCES.translationKey("key.categories.{}");
    public static final String RELOAD_KEY_SUBTITLE = LTXIndustries.RESOURCES.translationKey("key.{}.reload_weapon");

    private static final IKeyConflictContext RELOAD_CONFLICT_CONTEXT = new IKeyConflictContext()
    {
        @Override
        public boolean isActive()
        {
            return KeyConflictContext.IN_GAME.isActive() && LimaCoreClientUtil.getClientMainHandItem().getItem() instanceof WeaponItem;
        }

        @Override
        public boolean conflicts(IKeyConflictContext other)
        {
            return this == other;
        }
    };

    public static final KeyMapping RELOAD_KEY = new KeyMapping(RELOAD_KEY_SUBTITLE, RELOAD_CONFLICT_CONTEXT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY_LTXI);
}