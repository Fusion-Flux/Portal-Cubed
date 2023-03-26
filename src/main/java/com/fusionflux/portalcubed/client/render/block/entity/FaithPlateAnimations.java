package com.fusionflux.portalcubed.client.render.block.entity;

import net.minecraft.client.render.animation.Animation;
import net.minecraft.client.render.animation.AnimationKeyframe;
import net.minecraft.client.render.animation.Animator;
import net.minecraft.client.render.animation.PartAnimation;

public class FaithPlateAnimations {
    public static final Animation FP_FORWARD = Animation.Builder.withLength(2.34f)
        .addPartAnimation(
            "bone3",
            new PartAnimation(
                PartAnimation.AnimationTargets.TRANSLATE,
                new AnimationKeyframe(0f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(0.14f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(1.22f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(1.56f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(2.34f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                )
            )
        )
        .addPartAnimation(
            "bone3",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(0f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(0.14f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(0.26f, Animator.rotate(-60f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.48f, Animator.rotate(-32.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.68f, Animator.rotate(-60f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.84f, Animator.rotate(-47.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.96f, Animator.rotate(-60f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(1.12f, Animator.rotate(-52.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(1.22f, Animator.rotate(-60f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(1.56f, Animator.rotate(-60f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(2.34f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                )
            )
        )
        .addPartAnimation(
            "bone2",
            new PartAnimation(
                PartAnimation.AnimationTargets.TRANSLATE,
                new AnimationKeyframe(0.14f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(0.26f, Animator.translate(0f, 0.5f, -1f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(1.22f, Animator.translate(0f, 0.5f, -1f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(1.56f, Animator.translate(0f, 0.5f, -1f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(2.34f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                )
            )
        )
        .addPartAnimation(
            "bone2",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(0.14f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(0.26f, Animator.rotate(-92.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.48f, Animator.rotate(-57.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.68f, Animator.rotate(-92.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.84f, Animator.rotate(-75f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.96f, Animator.rotate(-92.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(1.12f, Animator.rotate(-87.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(1.22f, Animator.rotate(-92.5f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(1.56f, Animator.rotate(-92.5f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(2.34f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                )
            )
        )
        .addPartAnimation(
            "bone",
            new PartAnimation(
                PartAnimation.AnimationTargets.TRANSLATE,
                new AnimationKeyframe(0.14f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(2.34f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                )
            )
        )
        .addPartAnimation(
            "bone",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(0.14f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(0.26f, Animator.rotate(30f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.48f, Animator.rotate(15f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.68f, Animator.rotate(30f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.84f, Animator.rotate(17.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.96f, Animator.rotate(30f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(1.12f, Animator.rotate(25f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(1.22f, Animator.rotate(30f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(1.56f, Animator.rotate(30f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(2.34f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                )
            )
        ).build();
    public static final Animation FP_UPWARD = Animation.Builder.withLength(2.34f)
        .addPartAnimation(
            "bone3",
            new PartAnimation(
                PartAnimation.AnimationTargets.TRANSLATE,
                new AnimationKeyframe(0f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(0.14f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(0.18f, Animator.translate(0f, 2.33f, -1f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.22f, Animator.translate(0f, 4.67f, -1.5f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.26f, Animator.translate(0f, 7f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.48f, Animator.translate(0f, 4.67f, -1.5f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.68f, Animator.translate(0f, 7f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.84f, Animator.translate(0f, 5.54f, -0.94f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.96f, Animator.translate(0f, 7f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(1.12f, Animator.translate(0f, 6.27f, -0.47f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(1.22f, Animator.translate(0f, 7f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(1.56f, Animator.translate(0f, 7f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(1.7f, Animator.translate(0f, 6.07f, -1.1f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(1.84f, Animator.translate(0f, 4.67f, -1.75f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(1.96f, Animator.translate(0f, 3.89f, -1.83f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(2.1f, Animator.translate(0f, 2.33f, -1.75f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(2.24f, Animator.translate(0f, 1.16f, -1.25f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(2.34f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                )
            )
        )
        .addPartAnimation(
            "bone3",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(0f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(0.14f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(0.26f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.48f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.68f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.84f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.96f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(1.12f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(1.22f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(1.56f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(2.34f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                )
            )
        )
        .addPartAnimation(
            "bone2",
            new PartAnimation(
                PartAnimation.AnimationTargets.TRANSLATE,
                new AnimationKeyframe(0.14f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(0.26f, Animator.translate(0f, 1f, -1f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(1.56f, Animator.translate(0f, 1f, -1f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(2.34f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                )
            )
        )
        .addPartAnimation(
            "bone2",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(0.14f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(0.26f, Animator.rotate(-92.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.48f, Animator.rotate(-57.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.68f, Animator.rotate(-92.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.84f, Animator.rotate(-75f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.96f, Animator.rotate(-92.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(1.12f, Animator.rotate(-87.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(1.22f, Animator.rotate(-92.5f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(1.56f, Animator.rotate(-92.5f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(2.34f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                )
            )
        )
        .addPartAnimation(
            "bone",
            new PartAnimation(
                PartAnimation.AnimationTargets.TRANSLATE,
                new AnimationKeyframe(0.14f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(2.34f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                )
            )
        )
        .addPartAnimation(
            "bone",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(0.14f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(0.26f, Animator.rotate(32f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.48f, Animator.rotate(15f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.68f, Animator.rotate(32f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.84f, Animator.rotate(17.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.96f, Animator.rotate(32f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(1.12f, Animator.rotate(25f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(1.22f, Animator.rotate(32f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(1.56f, Animator.rotate(30f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(2.34f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                )
            )
        ).build();
}
