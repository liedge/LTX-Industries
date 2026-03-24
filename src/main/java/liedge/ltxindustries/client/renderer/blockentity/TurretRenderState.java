package liedge.ltxindustries.client.renderer.blockentity;

import liedge.limacore.lib.LimaColor;
import liedge.ltxindustries.client.model.custom.EnergyBoltData;
import liedge.ltxindustries.client.renderer.LockOnRenderData;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;

public final class TurretRenderState extends BlockEntityRenderState
{
    float xRot;
    float yRot;
    boolean lookingAtTarget;
    double targetDistance;

    List<LockOnRenderData> rocketTargets = List.of();
    LimaColor railgunBeamColor = LimaColor.WHITE;

    @Nullable EnergyBoltData primaryBolt;
    @Nullable Vec3 chainOffset;
    List<EnergyBoltData> secondaryBolts = List.of();
}