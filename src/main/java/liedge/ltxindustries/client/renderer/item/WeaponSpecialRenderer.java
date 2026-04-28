package liedge.ltxindustries.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.client.model.StaticQuads;
import liedge.limacore.data.LimaEnumCodec;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.client.model.custom.EnergyDisplayModel;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.ClientExtendedInput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public abstract class WeaponSpecialRenderer implements SpecialModelRenderer<WeaponRenderState>
{
    private final StaticQuads frame;
    private final StaticQuads chamber;
    private final Vector3fc chamberOrigin;
    private final RecoilAnimation recoilAnimation;
    private final float recoilDistance;
    private final float recoilAngle;

    WeaponSpecialRenderer(StaticQuads frame, StaticQuads chamber, Vector3fc chamberOrigin, RecoilAnimation recoilAnimation, float recoilDistance, float recoilAngle)
    {
        this.frame = frame;
        this.chamber = chamber;
        this.chamberOrigin = chamberOrigin;
        this.recoilAnimation = recoilAnimation;
        this.recoilDistance = recoilDistance;
        this.recoilAngle = recoilAngle;
    }

    public @Nullable WeaponRenderState extractArgument(ItemStack stack, ItemDisplayContext displayContext, @Nullable ItemOwner owner)
    {
        if (!(stack.getItem() instanceof WeaponItem item)) return null;

        WeaponRenderState state = new WeaponRenderState();

        // Extract static argument properties
        state.energyFill = Math.min(LimaCoreMath.divideFloat(item.getAmmoLoaded(stack), item.getAmmoCapacity(stack)), 1f);

        // Extract main 'animated' properties
        if (owner instanceof LocalPlayer player && inMainHand(player, displayContext))
        {
            float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
            ClientExtendedInput controls = ClientExtendedInput.of(player);

            state.inMainHand = true;

            int fireRate = item.getFireRate(stack);
            if (fireRate > 0)
            {
                state.recoilA = recoilAnimation.apply(controls.getAnimationTimerA().lerpProgressNotPaused(partialTick));
            }
            else if (controls.isTriggerHeld())
            {
                state.recoilA = partialTick;
            }

            state.recoilB = Mth.rotLerp(partialTick, controls.spinAnimation0, controls.spinAnimation);
        }

        return state;
    }

    @Override
    public void submit(@Nullable WeaponRenderState argument, PoseStack poseStack, SubmitNodeCollector nodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor)
    {
        if (argument == null) return;

        if (argument.inMainHand)
        {
            submitMainHand(argument, poseStack, nodeCollector, lightCoords);
        }
        else
        {
            frame.submit(poseStack, nodeCollector, lightCoords);
            submitChamber(poseStack, nodeCollector, argument.energyTint, lightCoords);

            submitEnergyDisplays(argument, poseStack, nodeCollector);
        }
    }

    private void submitMainHand(WeaponRenderState argument, PoseStack poseStack, SubmitNodeCollector nodeCollector, int lightCoords)
    {
        if (argument.recoilA > 0)
        {
            poseStack.translate(0f, 0f, argument.recoilA * recoilDistance);
            poseStack.mulPose(Axis.XP.rotationDegrees(argument.recoilA * recoilAngle));
        }

        frame.submit(poseStack, nodeCollector, lightCoords);
        submitAdditionalFrame(argument, poseStack, nodeCollector, lightCoords);

        poseStack.pushPose();

        float cx = chamberOrigin.x();
        float cy = chamberOrigin.y();
        float cz = chamberOrigin.z();

        poseStack.translate(cx, cy, cz);
        poseStack.mulPose(Axis.ZP.rotationDegrees(argument.recoilB));
        poseStack.translate(-cx, -cy, -cz);

        submitChamber(poseStack, nodeCollector, argument.energyTint, lightCoords);

        poseStack.popPose();

        submitEnergyDisplays(argument, poseStack, nodeCollector);
    }

    protected void submitAdditionalFrame(WeaponRenderState argument, PoseStack poseStack, SubmitNodeCollector nodeCollector, int lightCoords) { }

    protected void submitEnergyDisplays(WeaponRenderState argument, PoseStack poseStack, SubmitNodeCollector nodeCollector)
    {
        int tint = argument.energyTint != -1 ? argument.energyTint : LTXIConstants.LIME_GREEN.argb32();
        for (EnergyDisplayModel model : argument.energyDisplays)
        {
            model.submit(poseStack, nodeCollector, argument.energyFill, tint, 0.8f);
        }
    }

    private void submitChamber(PoseStack poseStack, SubmitNodeCollector nodeCollector, int energyTint, int lightCoords)
    {
        if (energyTint != -1)
        {
            chamber.submitTinted(poseStack, nodeCollector, new int[] {energyTint}, lightCoords);
        }
        else
        {
            chamber.submit(poseStack, nodeCollector, lightCoords);
        }
    }

    private boolean inMainHand(LocalPlayer player, ItemDisplayContext context)
    {
        HumanoidArm mainArm = player.getMainArm();
        return mainArm == HumanoidArm.LEFT ? context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND : context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
    }

    @Override
    public final void getExtents(Consumer<Vector3fc> output)
    {
        frame.extents().forEach(output);
        chamber.extents().forEach(output);
    }

    @Deprecated
    @Override
    public final @Nullable WeaponRenderState extractArgument(ItemStack stack)
    {
        return null;
    }

    public record Unbaked(Identifier frame, Identifier chamber, Vector3fc chamberOrigin, RecoilAnimation recoilAnimation, float recoilDistance, float recoilAngle, Type type)
        implements ResolvableModel
    {
        public static final Codec<Unbaked> CODEC = RecordCodecBuilder.create(i -> i.group(
                Identifier.CODEC.fieldOf("frame").forGetter(Unbaked::frame),
                Identifier.CODEC.fieldOf("chamber").forGetter(Unbaked::chamber),
                ExtraCodecs.VECTOR3F.fieldOf("chamber_origin").forGetter(Unbaked::chamberOrigin),
                RecoilAnimation.CODEC.fieldOf("recoil_animation").forGetter(Unbaked::recoilAnimation),
                Codec.FLOAT.fieldOf("recoil_distance").forGetter(Unbaked::recoilDistance),
                Codec.floatRange(0f, 360f).fieldOf("recoil_angle").forGetter(Unbaked::recoilAngle),
                Type.CODEC.fieldOf("type").forGetter(Unbaked::type))
                .apply(i, Unbaked::new));

        public WeaponSpecialRenderer bakeRenderer(ModelBaker baker)
        {
            StaticQuads bakedFrame = StaticQuads.create(frame).bake(baker);
            StaticQuads bakedChamber = StaticQuads.create(chamber).bake(baker);

            return type.provider.create(bakedFrame, bakedChamber, chamberOrigin, recoilAnimation, recoilDistance, recoilAngle);
        }

        @Override
        public void resolveDependencies(Resolver resolver)
        {
            resolver.markDependency(frame);
            resolver.markDependency(chamber);
        }
    }

    public enum Type implements StringRepresentable
    {
        SIMPLE_RECOIL("simple_recoil", SimpleRecoilWeaponRenderer::new),
        STARGAZER_SIGHT("stargazer_sight", StargazerRenderer::new);

        private static final Codec<Type> CODEC = LimaEnumCodec.create(Type.class);

        private final String name;
        private final Provider provider;

        Type(String name, Provider provider)
        {
            this.name = name;
            this.provider = provider;
        }

        @Override
        public String getSerializedName()
        {
            return name;
        }
    }

    private interface Provider
    {
        WeaponSpecialRenderer create(StaticQuads frame, StaticQuads chamber, Vector3fc chamberOrigin, RecoilAnimation recoilAnimation, float recoilDistance, float recoilAngle);
    }
}