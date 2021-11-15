package com.fusionflux.portalcubed.delay;

public class DelayedForLoop {

    public final DelayedForLoopFunction function;
    public final double maxIValue;
    public int iValue;

    public DelayedForLoop(DelayedForLoopFunction function, int i, double maxI) {
        this.function = function;
        this.iValue = i;
        this.maxIValue = maxI;
    }

    public void tick() {
        this.function.iterate(this.iValue);
        this.iValue++;
    }
}
