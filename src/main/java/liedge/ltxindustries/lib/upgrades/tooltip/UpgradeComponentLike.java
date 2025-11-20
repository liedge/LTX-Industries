package liedge.ltxindustries.lib.upgrades.tooltip;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public interface UpgradeComponentLike
{
    Codec<UpgradeComponentLike> CODEC = Codec.lazyInitialized(() -> Type.CODEC.dispatch(UpgradeComponentLike::getType, Type::getCodec));

    Component get(int upgradeRank);

    Type getType();

    enum Type implements StringRepresentable
    {
        STATIC_COMPONENT("static", StaticTooltip.CODEC),
        UPGRADE_VALUE("value", ValueComponent.CODEC),
        CUSTOM_TRANSLATABLE("custom_args", TranslatableTooltip.CODEC);

        public static final LimaEnumCodec<Type> CODEC = LimaEnumCodec.create(Type.class);

        private final String name;
        private final MapCodec<? extends UpgradeComponentLike> codec;

        Type(String name, MapCodec<? extends UpgradeComponentLike> codec)
        {
            this.name = name;
            this.codec = codec;
        }

        @Override
        public String getSerializedName()
        {
            return name;
        }

        public MapCodec<? extends UpgradeComponentLike> getCodec()
        {
            return codec;
        }
    }
}