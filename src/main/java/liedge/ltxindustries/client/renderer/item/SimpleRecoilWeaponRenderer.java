package liedge.ltxindustries.client.renderer.item;

import liedge.limacore.client.model.StaticQuads;
import org.joml.Vector3fc;

public final class SimpleRecoilWeaponRenderer extends WeaponSpecialRenderer
{
    SimpleRecoilWeaponRenderer(StaticQuads frame, StaticQuads chamber, Vector3fc chamberOrigin, RecoilAnimation recoilAnimation, float recoilDistance, float recoilAngle)
    {
        super(frame, chamber, chamberOrigin, recoilAnimation, recoilDistance, recoilAngle);
    }
}