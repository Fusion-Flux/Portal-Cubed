package com.fusionflux.portalcubed.client.render.block.entity;

import net.minecraft.client.render.animation.Animation;
import net.minecraft.client.render.animation.AnimationKeyframe;
import net.minecraft.client.render.animation.Animator;
import net.minecraft.client.render.animation.PartAnimation;
import org.quiltmc.loader.api.minecraft.ClientOnly;

// This code was generated in Mojmap, manually converted to QM, and then formatted with IntelliJ's "Reformat Code".
@ClientOnly
public class RocketTurretAnimations {
    public static final Animation ACTIVATE = Animation.Builder.withLength(2.5f)
        .addPartAnimation(
            "turret",
            new PartAnimation(
                PartAnimation.AnimationTargets.TRANSLATE,
                new AnimationKeyframe(0.36f, Animator.translate(0f, -11f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.88f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(2.48f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "turret",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(0.88f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(2.48f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "chassis",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(0f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(0.36f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(2.48f, Animator.rotate(0f, 360f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "segment_1",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(1.44f, Animator.rotate(52.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(1.84f, Animator.rotate(-10f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(2f, Animator.rotate(17.560000000000002f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(2.48f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "segment_2",
            new PartAnimation(
                PartAnimation.AnimationTargets.TRANSLATE,
                new AnimationKeyframe(1.52f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(2.48f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "segment_2",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(1.32f, Animator.rotate(-62.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(1.52f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(2.48f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "neck",
            new PartAnimation(
                PartAnimation.AnimationTargets.TRANSLATE,
                new AnimationKeyframe(1.28f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "neck",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(0.96f, Animator.rotate(-135f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(1.28f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "hatch",
            new PartAnimation(
                PartAnimation.AnimationTargets.TRANSLATE,
                new AnimationKeyframe(1.32f, Animator.translate(-4f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(1.64f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(2.48f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "hatch",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(1.64f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(2.48f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "barrel",
            new PartAnimation(
                PartAnimation.AnimationTargets.TRANSLATE,
                new AnimationKeyframe(1.8f, Animator.translate(0f, 0f, 4.5f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(2.08f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(2.48f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "barrel",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(2.08f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(2.48f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "segment_3",
            new PartAnimation(
                PartAnimation.AnimationTargets.TRANSLATE,
                new AnimationKeyframe(1.4f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "segment_3",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(1.08f, Animator.rotate(57.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(1.4f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "head",
            new PartAnimation(
                PartAnimation.AnimationTargets.TRANSLATE,
                new AnimationKeyframe(0f, Animator.translate(0f, -0.052358382285375926f, 3.999657310296029f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(0.36f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                )
            )
        ).build();

    public static final Animation DEACTIVATE = Animation.Builder.withLength(2f)
        .addPartAnimation(
            "turret",
            new PartAnimation(
                PartAnimation.AnimationTargets.TRANSLATE,
                new AnimationKeyframe(0f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(1.28f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(1.72f, Animator.translate(0f, -11f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "turret",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(0f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(1.28f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "segment_1",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(0f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.32f, Animator.rotate(-10f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.52f, Animator.rotate(-10f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.84f, Animator.rotate(52.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "segment_2",
            new PartAnimation(
                PartAnimation.AnimationTargets.TRANSLATE,
                new AnimationKeyframe(0f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.76f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "segment_2",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(0f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.76f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.92f, Animator.rotate(-62.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "neck",
            new PartAnimation(
                PartAnimation.AnimationTargets.TRANSLATE,
                new AnimationKeyframe(0.96f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "neck",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(0.96f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(1.24f, Animator.rotate(-135f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "hatch",
            new PartAnimation(
                PartAnimation.AnimationTargets.TRANSLATE,
                new AnimationKeyframe(0f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.68f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.92f, Animator.translate(-4f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "hatch",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(0f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.68f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "barrel",
            new PartAnimation(
                PartAnimation.AnimationTargets.TRANSLATE,
                new AnimationKeyframe(0f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.32f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.56f, Animator.translate(0f, 0f, 4.5f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "barrel",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(0f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(0.32f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "segment_3",
            new PartAnimation(
                PartAnimation.AnimationTargets.TRANSLATE,
                new AnimationKeyframe(0.88f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "segment_3",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(0.88f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(1.12f, Animator.rotate(57.5f, 0f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                )
            )
        )
        .addPartAnimation(
            "head",
            new PartAnimation(
                PartAnimation.AnimationTargets.TRANSLATE,
                new AnimationKeyframe(1.72f, Animator.translate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(2f, Animator.translate(0f, -0.052358382285375926f, 3.999657310296029f),
                                      PartAnimation.Interpolators.LINEAR
                )
            )
        )
        .addPartAnimation(
            "chassis",
            new PartAnimation(
                PartAnimation.AnimationTargets.ROTATE,
                new AnimationKeyframe(0f, Animator.rotate(0f, 360f, 0f),
                                      PartAnimation.Interpolators.SPLINE
                ),
                new AnimationKeyframe(1.6f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                ),
                new AnimationKeyframe(1.96f, Animator.rotate(0f, 0f, 0f),
                                      PartAnimation.Interpolators.LINEAR
                )
            )
        ).build();
}
