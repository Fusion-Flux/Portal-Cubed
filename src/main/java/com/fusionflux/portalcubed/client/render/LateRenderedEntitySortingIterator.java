package com.fusionflux.portalcubed.client.render;

import com.fusionflux.portalcubed.entity.LateRenderedEntity;
import com.google.common.collect.AbstractIterator;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LateRenderedEntitySortingIterator extends AbstractIterator<Entity> {

    private final Iterator<Entity> wrapped;
    private List<Entity> lateRendered = null;
    private Iterator<Entity> lateRenderedIterator = null;

    public LateRenderedEntitySortingIterator(Iterator<Entity> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    protected Entity computeNext() {
        if (wrapped.hasNext()) {
            Entity next = wrapped.next();
            if (next instanceof LateRenderedEntity) {
                if (lateRendered == null) // avoid allocating a list when none are present
                    lateRendered = new ArrayList<>();
                lateRendered.add(next);
                return computeNext();
            } else {
                return next;
            }
        } else if (lateRendered != null) {
            List<Entity> late = lateRendered;
            // only go down this branch once
            lateRendered = null;
            if (late.size() == 1) {
                return late.get(0); // avoid allocating another iterator when only 1 left
            } else {
                lateRenderedIterator = late.iterator();
                return computeNext();
            }
        } else if (lateRenderedIterator != null && lateRenderedIterator.hasNext()) {
            return lateRenderedIterator.next();
        } else {
            return endOfData();
        }
    }
}
