package liedge.ltxindustries.client.renderer.item;

import liedge.ltxindustries.client.model.custom.EnergyDisplayModel;

import java.util.List;

public final class WeaponRenderState
{
    public List<EnergyDisplayModel> energyDisplays = List.of();
    public float energyFill;
    public int energyTint = -1;

    public boolean inMainHand;
    public float recoilA;
    public float recoilB;
}