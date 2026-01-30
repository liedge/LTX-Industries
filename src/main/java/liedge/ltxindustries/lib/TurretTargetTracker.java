package liedge.ltxindustries.lib;

import liedge.ltxindustries.registry.game.LTXIAttachmentTypes;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public abstract class TurretTargetTracker
{
    public static final TurretTargetTracker EMPTY = new Empty();

    public static TurretTargetTracker create()
    {
        return new SetBacked();
    }

    public static TurretTargetTracker getOrDefault(@Nullable Entity owner)
    {
        if (owner == null) return EMPTY;
        return owner.getData(LTXIAttachmentTypes.TURRET_TRACKER);
    }

    private TurretTargetTracker() {}

    public abstract boolean contains(Entity target);

    public abstract boolean add(Entity target);

    public abstract void remove(@Nullable Entity target);

    public abstract void removeAll(Collection<Entity> targets);

    private static class SetBacked extends TurretTargetTracker
    {
        private final Set<Entity> set = Collections.newSetFromMap(new WeakHashMap<>());

        @Override
        public boolean contains(Entity target)
        {
            return set.contains(target);
        }

        @Override
        public boolean add(Entity target)
        {
            return set.add(target);
        }

        @Override
        public void remove(@Nullable Entity target)
        {
            set.remove(target);
        }

        @Override
        public void removeAll(Collection<Entity> targets)
        {
            set.removeAll(targets);
        }
    }

    private static class Empty extends TurretTargetTracker
    {
        @Override
        public boolean contains(Entity target)
        {
            return false;
        }

        @Override
        public boolean add(Entity target)
        {
            return true;
        }

        @Override
        public void remove(@Nullable Entity target) { }

        @Override
        public void removeAll(Collection<Entity> targets) {}
    }
}