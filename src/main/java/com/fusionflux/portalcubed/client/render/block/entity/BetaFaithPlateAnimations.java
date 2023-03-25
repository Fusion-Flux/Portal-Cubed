package com.fusionflux.portalcubed.client.render.block.entity;

import net.minecraft.client.render.animation.Animation;
import net.minecraft.client.render.animation.AnimationKeyframe;
import net.minecraft.client.render.animation.Animator;
import net.minecraft.client.render.animation.PartAnimation;

public class BetaFaithPlateAnimations {
    public static final Animation BFP_FORWARD = Animation.Builder.withLength(0.5f)
        .addPartAnimation(
            "bone",
            new PartAnimation(
                PartAnimation.AnimationTargets.TRANSLATE,
                new AnimationKeyframe(0f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.24f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "bone",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(0f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.06f, Animator.rotate(22.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.18f, Animator.rotate(20f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.24f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "bone2",
            new PartAnimation(
                PartAnimation.AnimationTargets.TRANSLATE,
                new AnimationKeyframe(0f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.24f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "bone2",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(0f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.06f, Animator.rotate(-75f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.18f, Animator.rotate(-70f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.24f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "bone3",
            new PartAnimation(
                PartAnimation.AnimationTargets.TRANSLATE,
                new AnimationKeyframe(0f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.24f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "bone3",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(0f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.06f, Animator.rotate(30f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.18f, Animator.rotate(20f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.24f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        ).build();
    public static final Animation BFP_UPWARD = Animation.Builder.withLength(0.5f)
        .addPartAnimation(
            "bone",
            new PartAnimation(
                PartAnimation.AnimationTargets.TRANSLATE,
                new AnimationKeyframe(0f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.24f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "bone",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(0f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.06f, Animator.rotate(32.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.18f, Animator.rotate(32.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.24f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "bone2",
            new PartAnimation(
                PartAnimation.AnimationTargets.TRANSLATE,
                new AnimationKeyframe(0f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.24f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "bone2",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(0f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.06f, Animator.rotate(-77.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.18f, Animator.rotate(-77.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.24f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "bone3",
            new PartAnimation(
                PartAnimation.AnimationTargets.TRANSLATE,
                new AnimationKeyframe(0f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.26f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "bone3",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(0f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.06f, Animator.rotate(42.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.18f, Animator.rotate(42.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.24f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        ).build();
}
