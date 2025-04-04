package liedge.limatech.lib;

import liedge.limatech.registry.game.LimaTechAttachmentTypes;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public abstract class TurretTargetList
{
    public static final TurretTargetList EMPTY = new Empty();

    public static TurretTargetList create()
    {
        return new SetBacked();
    }

    public static TurretTargetList getOrDefault(@Nullable Entity owner)
    {
        if (owner == null) return EMPTY;
        return owner.getData(LimaTechAttachmentTypes.TARGET_LIST);
    }

    private TurretTargetList() {}

    public abstract boolean containsTarget(Entity target);

    public abstract boolean addTarget(Entity target);

    public abstract void removeTarget(@Nullable Entity target);

    public abstract void removeTargets(Collection<Entity> targets);

    private static class SetBacked extends TurretTargetList
    {
        private final Set<Entity> set = Collections.newSetFromMap(new WeakHashMap<>());

        @Override
        public boolean containsTarget(Entity target)
        {
            return set.contains(target);
        }

        @Override
        public boolean addTarget(Entity target)
        {
            return set.add(target);
        }

        @Override
        public void removeTarget(@Nullable Entity target)
        {
            set.remove(target);
        }

        @Override
        public void removeTargets(Collection<Entity> targets)
        {
            set.removeAll(targets);
        }
    }

    private static class Empty extends TurretTargetList
    {
        @Override
        public boolean containsTarget(Entity target)
        {
            return false;
        }

        @Override
        public boolean addTarget(Entity target)
        {
            return true;
        }

        @Override
        public void removeTarget(@Nullable Entity target) { }

        @Override
        public void removeTargets(Collection<Entity> targets) {}
    }
}