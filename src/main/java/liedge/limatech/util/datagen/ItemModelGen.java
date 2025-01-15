package liedge.limatech.util.datagen;

import liedge.limacore.data.generation.LimaItemModelProvider;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.limatech.LimaTech;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.stream.Stream;

import static liedge.limatech.registry.LimaTechItems.*;

class ItemModelGen extends LimaItemModelProvider
{
    ItemModelGen(PackOutput output, ExistingFileHelper helper)
    {
        super(output, LimaTech.RESOURCES, helper);
    }

    @Override
    protected void registerModels()
    {
        generated(RAW_TITANIUM, TITANIUM_INGOT, TITANIUM_NUGGET, RAW_NIOBIUM, NIOBIUM_INGOT, NIOBIUM_NUGGET,
                WHITE_PIGMENT, LIME_PIGMENT);

        handheld(TITANIUM_SWORD, TITANIUM_SHOVEL, TITANIUM_PICKAXE, TITANIUM_AXE, TITANIUM_HOE, TITANIUM_SHEARS);

        orePebbles(COAL_ORE_PEBBLES, COPPER_ORE_PEBBLES, IRON_ORE_PEBBLES, LAPIS_ORE_PEBBLES, REDSTONE_ORE_PEBBLES, GOLD_ORE_PEBBLES, DIAMOND_ORE_PEBBLES, EMERALD_ORE_PEBBLES, QUARTZ_ORE_PEBBLES, NETHERITE_ORE_PEBBLES, TITANIUM_ORE_PEBBLES, NIOBIUM_ORE_PEBBLES);

        generated(itemFolderLocation("tech_salvage"), EXPLOSIVES_WEAPON_TECH_SALVAGE, TARGETING_TECH_SALVAGE);

        handheld(MACHINE_WRENCH, itemFolderLocation("wrench"));

        generated(DEEPSLATE_POWDER, SLATE_ALLOY_INGOT, BEDROCK_ALLOY_INGOT, SLATE_ALLOY_NUGGET, COPPER_CIRCUIT, GOLD_CIRCUIT, NIOBIUM_CIRCUIT, AUTO_AMMO_CANISTER, SPECIALIST_AMMO_CANISTER, EXPLOSIVES_AMMO_CANISTER, ROCKET_LAUNCHER_AMMO, MAGNUM_AMMO_CANISTER);
        generated(EMPTY_UPGRADE_MODULE, EQUIPMENT_UPGRADE_MODULE, MACHINE_UPGRADE_MODULE);

        // Patchouli guidebook model
        getBuilder("guidebook").parent(generatedModel).texture("layer0", itemFolderLocation("guidebook"));
    }

    private void orePebbles(ItemLike... pebbles)
    {
        for (ItemLike item : pebbles)
        {
            String name = LimaRegistryUtil.getItemName(item.asItem()).split("_")[0];
            generated(item, itemFolderLocation("ore_pebbles/" + name));
        }
    }

    private void generated(ResourceLocation texture, ItemLike... items)
    {
        Stream.of(items).forEach(i -> generated(i, texture));
    }
}