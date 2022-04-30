package com.fusionflux.portalcubed.accessor;

import com.fusionflux.portalcubed.util.PortalCubedComponent;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentProvider;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class CalledValues {

    public static final ComponentKey<PortalCubedComponent> GRAVITY_TIMER =
            ComponentRegistry.getOrCreate(new Identifier("portalcubed", "gravitytimer"), PortalCubedComponent.class);

    // workaround for a CCA bug; maybeGet throws an NPE in internal code if the DataTracker isn't initialized
    // null check the component container to avoid it
    private static <C extends Component, V> Optional<C> maybeGetSafe(ComponentKey<C> key, @Nullable V provider) {
        if (provider instanceof ComponentProvider p) {
            var cc = p.getComponentContainer();
            if (cc != null) {
                return Optional.ofNullable(key.getInternal(cc));
            }
        }
        return Optional.empty();
    }

    public static boolean getSwapTimer(Entity entity) {
        return maybeGetSafe(GRAVITY_TIMER, entity).map(PortalCubedComponent::getSwapGravity).orElse(false);
    }

    public static void setSwapTimer(Entity entity, boolean setValue) {
        maybeGetSafe(GRAVITY_TIMER, entity).ifPresent(gc -> gc.setSwapGravity(setValue));
    }


}
