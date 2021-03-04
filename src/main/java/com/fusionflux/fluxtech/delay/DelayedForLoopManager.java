package com.fusionflux.fluxtech.delay;

import java.util.ArrayList;
import java.util.List;

public class DelayedForLoopManager {

    private static final List<DelayedForLoop> RUNNING_LOOPS = new ArrayList<>();

    public static void tick() {
        List<DelayedForLoop> terminateds = new ArrayList<>();
        for (DelayedForLoop l : RUNNING_LOOPS) {
            if (!(l.iValue < l.maxIValue)) terminateds.add(l);
            else {
                l.tick();
            }
        }
        for (DelayedForLoop t : terminateds) {
            RUNNING_LOOPS.remove(t);
        }
    }

    public static void add(DelayedForLoop loop) {
        RUNNING_LOOPS.add(loop);
    }

}
