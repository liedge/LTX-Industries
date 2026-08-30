package liedge.ltxindustries.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.client.model.StaticQuads;
import liedge.limacore.client.renderer.LimaSpecialModelRenderer;
import liedge.limacore.client.renderer.SpecialModelWithData;
import liedge.limacore.util.LimaStreamsUtil;
import liedge.ltxindustries.client.renderer.LimaKeyframeTrack;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.ClientExtendedInput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class WeaponSpecialRenderer implements SpecialModelRenderer<WeaponSpecialRenderer.State>
{
    private final StaticQuads frame;
    private final StaticQuads chamber;
    private final List<LimaSpecialModelRenderer<?>> frameExtras;
    private final Vector3fc chamberPos;
    private final LimaKeyframeTrack recoilAnimation;
    private final float recoilDistance;
    private final float recoilAngle;

    private WeaponSpecialRenderer(StaticQuads frame, StaticQuads chamber, List<LimaSpecialModelRenderer<?>> frameExtras, Vector3fc chamberPos, LimaKeyframeTrack recoilAnimation, float recoilDistance, float recoilAngle)
    {
        this.frame = frame;
        this.chamber = chamber;
        this.frameExtras = frameExtras;
        this.chamberPos = chamberPos;
        this.recoilAnimation = recoilAnimation;
        this.recoilDistance = recoilDistance;
        this.recoilAngle = recoilAngle;
    }

    public @Nullable State extract(ItemStack stack, Player player, ItemDisplayContext displayContext, @Nullable ClientLevel level)
    {
        if (!(stack.getItem() instanceof WeaponItem item)) return null;

        State state = new State();

        if (!frameExtras.isEmpty())
        {
            state.extraStates = new ObjectArrayList<>(frameExtras.size());
            for (LimaSpecialModelRenderer<?> renderer : frameExtras)
            {
                state.extraStates.add(extractExtra(renderer, stack, displayContext, level, player));
            }
        }

        float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        ClientExtendedInput controls = ClientExtendedInput.of(player);

        if (item.getFireRate(stack) > 0)
        {
            state.frameRecoil = recoilAnimation.apply(controls.getAnimationTimerA().lerpProgressNotPaused(partialTick));
        }
        else if (controls.isTriggerHeld())
        {
            state.frameRecoil = recoilAnimation.apply(partialTick);
        }

        state.chamberRecoil = Mth.rotLerp(partialTick, controls.spinAnimation0, controls.spinAnimation);

        return state;
    }

    private <T> SpecialModelWithData<T> extractExtra(LimaSpecialModelRenderer<T> extra, ItemStack stack, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner owner)
    {
        T data = extra.extractArgument(stack, displayContext, level, owner);
        return new SpecialModelWithData<>(extra, data);
    }

    @Override
    public void submit(@Nullable State argument, PoseStack poseStack, SubmitNodeCollector nodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor)
    {
        if (argument == null) return;

        if (argument.frameRecoil > 0)
        {
            poseStack.translate(0f, 0f, argument.frameRecoil * recoilDistance);
            poseStack.mulPose(Axis.XP.rotationDegrees(argument.frameRecoil * recoilAngle));
        }

        frame.submitTinted(poseStack, nodeCollector, argument.frameTint, lightCoords);
        for (SpecialModelWithData<?> extra : argument.extraStates)
        {
            extra.submit(poseStack, nodeCollector, lightCoords, overlayCoords, hasFoil, outlineColor);
        }

        poseStack.pushPose();

        float cx = chamberPos.x();
        float cy = chamberPos.y();
        float cz = chamberPos.z();

        poseStack.translate(cx, cy, cz);
        poseStack.mulPose(Axis.ZP.rotationDegrees(argument.chamberRecoil));
        poseStack.translate(-cx, -cy, -cz);

        chamber.submitTinted(poseStack, nodeCollector, argument.chamberTint, lightCoords);

        poseStack.popPose();
    }

    @Deprecated
    @Override
    public void getExtents(Consumer<Vector3fc> output) { }

    @Deprecated
    @Override
    public @Nullable State extractArgument(ItemStack stack)
    {
        return null;
    }

    public record SpecialUnbaked(Vector3fc chamberPos, LimaKeyframeTrack recoilAnimation, float recoilDistance, float recoilAngle)
    {
        public static final Codec<SpecialUnbaked> CODEC = RecordCodecBuilder.create(i -> i.group(
                ExtraCodecs.VECTOR3F.fieldOf("chamber_pos").forGetter(SpecialUnbaked::chamberPos),
                LimaKeyframeTrack.CODEC.fieldOf("recoil_animation").forGetter(SpecialUnbaked::recoilAnimation),
                Codec.FLOAT.fieldOf("recoil_distance").forGetter(SpecialUnbaked::recoilDistance),
                Codec.floatRange(0f, 360f).fieldOf("recoil_angle").forGetter(SpecialUnbaked::recoilAngle))
                .apply(i, SpecialUnbaked::new));

        public WeaponSpecialRenderer bake(ItemModel.BakingContext context, Identifier frame, Identifier chamber, List<LimaSpecialModelRenderer.LimaUnbaked<?>> frameExtras)
        {
            ModelBaker baker = context.blockModelBaker();

            StaticQuads bakedFrame = StaticQuads.create(frame).bake(baker);
            StaticQuads bakedChamber = StaticQuads.create(chamber).bake(baker);
            List<LimaSpecialModelRenderer<?>> bakedExtras = frameExtras.stream().map(o -> o.bake(context)).filter(Objects::nonNull).collect(LimaStreamsUtil.toUnmodifiableObjectList());

            return new WeaponSpecialRenderer(bakedFrame, bakedChamber, bakedExtras, chamberPos, recoilAnimation, recoilDistance, recoilAngle);
        }
    }

    public static final class State
    {
        public List<SpecialModelWithData<?>> extraStates = List.of();
        public float frameRecoil;
        public float chamberRecoil;
        public int[] frameTint = ItemStackRenderState.LayerRenderState.EMPTY_TINTS;
        public int[] chamberTint = ItemStackRenderState.LayerRenderState.EMPTY_TINTS;
    }
}