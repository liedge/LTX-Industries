package liedge.limatech.util.datagen;

import liedge.limacore.data.generation.LimaItemModelProvider;
import liedge.limatech.LimaTech;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Supplier;
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
        generated(RAW_TITANIUM, TITANIUM_INGOT, TITANIUM_NUGGET, RAW_NIOBIUM, NIOBIUM_INGOT, NIOBIUM_NUGGET);

        handheld(TITANIUM_SWORD, TITANIUM_SHOVEL, TITANIUM_PICKAXE, TITANIUM_AXE, TITANIUM_HOE, TITANIUM_SHEARS);

        generated(itemFolderLocation("tech_salvage"), EXPLOSIVES_WEAPON_TECH_SALVAGE, TARGETING_TECH_SALVAGE);

        generated(DEEPSLATE_POWDER, SLATE_ALLOY_INGOT, SLATE_ALLOY_NUGGET, COPPER_CIRCUIT, GOLD_CIRCUIT, NIOBIUM_CIRCUIT,
                AUTO_AMMO_CANISTER, SPECIALIST_AMMO_CANISTER, EXPLOSIVES_AMMO_CANISTER, LEGENDARY_AMMO_CANISTER);

        generated(INFINITE_ENERGY_CARD, itemFolderLocation("lime_digital_card"));
    }

    @SafeVarargs
    private void generated(ResourceLocation texture, Supplier<? extends Item>... suppliers)
    {
        Stream.of(suppliers).forEach(o -> generated(o, texture));
    }
}