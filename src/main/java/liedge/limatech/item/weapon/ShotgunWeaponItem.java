package liedge.limatech.item.weapon;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import liedge.limacore.util.LimaNetworkUtil;
import liedge.limatech.entity.WeaponRayTrace;
import liedge.limatech.lib.weapons.WeaponDamageSource;
import liedge.limatech.registry.LimaTechDamageTypes;
import liedge.limatech.registry.LimaTechDataComponents;
import liedge.limatech.registry.LimaTechItems;
import liedge.limatech.registry.LimaTechParticles;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

import static liedge.limatech.LimaTech.RESOURCES;

public class ShotgunWeaponItem extends SemiAutoWeaponItem
{
    private final ItemAttributeModifiers attributeModifiers;

    public ShotgunWeaponItem(Properties properties)
    {
        super(properties);

        this.attributeModifiers = ItemAttributeModifiers.builder()
                .add(Attributes.MOVEMENT_SPEED, new AttributeModifier(RESOURCES.location("shotgun_speed_boost"), 0.25d, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.STEP_HEIGHT, new AttributeModifier(RESOURCES.location("shotgun_step_boost"), 1d, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public void weaponFired(ItemStack heldItem, Player player, Level level)
    {
        if (!level.isClientSide())
        {
            Object2IntMap<Entity> pelletHits = new Object2IntOpenHashMap<>();

            for (int i = 0; i < 7; i++)
            {
                WeaponRayTrace rayTrace = WeaponRayTrace.traceWithDynamicMagnetism(level, player, 10d, 6.5d, entity -> entity.getBoundingBox().getSize() <= 1d ? 0.625d : 0.325d, 10);

                rayTrace.hits().forEach(e -> {
                    int n = pelletHits.getOrDefault(e, 0) + 1;
                    pelletHits.put(e, n);
                });

                LimaNetworkUtil.spawnAlwaysVisibleParticle(level, LimaTechParticles.LIGHTFRAG_TRACER.get(), rayTrace.start(), rayTrace.end());
            }

            pelletHits.forEach((hit, pellets) -> hit.hurt(WeaponDamageSource.directEntityDamage(player, LimaTechDamageTypes.LIGHTFRAG, this),  9f * pellets));
        }
    }

    @Override
    public Item getAmmoItem()
    {
        return LimaTechItems.SPECIALIST_AMMO_CANISTER.asItem();
    }

    @Override
    public int getFireRate(ItemStack stack)
    {
        return 12;
    }

    @Override
    public int getAmmoCapacity(ItemStack stack)
    {
        return 5;
    }

    @Override
    public int getReloadSpeed(ItemStack stack)
    {
        return 30;
    }

    @Override
    public int getRecoilA(ItemStack stack)
    {
        return 5;
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack)
    {
        return stack.getOrDefault(LimaTechDataComponents.WEAPON_AMMO, 0) > 0 ? attributeModifiers : super.getDefaultAttributeModifiers(stack);
    }
}