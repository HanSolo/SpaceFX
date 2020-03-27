/*
 * Copyright (c) 2020 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.spacefx;


import eu.hansolo.spacefx.SpaceFXView.Sprite;
import javafx.scene.Node;


public class Helper {

    public static final void enableNode(final Node node, final boolean enable) {
        node.setVisible(enable);
        node.setManaged(enable);
    }

    public static final boolean isInsideCircle(final double cx, final double cy, final double r, final double x, final double y) {
        return ((x - cx) * (x - cx) + (y - cy) * (y - cy) <= r * r);
    }

    public static final double[] rotatePointAroundRotationCenter(final double x, final double y, final double rX, final double rY, final double angleDeg) {
        final double rad = Math.toRadians(angleDeg);
        final double sin = Math.sin(rad);
        final double cos = Math.cos(rad);
        final double nX  = rX + (x - rX) * cos - (y - rY) * sin;
        final double nY  = rY + (x - rX) * sin + (y - rY) * cos;
        return new double[] { nX, nY };
    }

    public static final boolean isHitCircleCircle(final double c1x, final double c1y, final double c1r, final double c2x, final double c2y, final double c2r) {
        return ((c1x - c2x) * (c1x - c2x) + (c1y - c2y) * (c1y - c2y) < (c1r + c2r) * (c1r + c2r));
    }

    /*
     * Checks for a collision of two sprites and sets their new vX,vY related to their masses and speeds
     */
    public static final void checkElasticCollision(final Sprite sprite0, final Sprite sprite1) {
        final double dx = sprite1.x - sprite0.x;
        final double dy = sprite1.y - sprite0.y;
        final double sr = sprite0.radius + sprite1.radius;

        //collision handling code here
        if (((dx * dx) + (dy * dy)) < (sr * sr)) {
            //calculate angle, sine, and cosine
            double angle = Math.atan2(dy, dx);
            double sin   = Math.sin(angle);
            double cos   = Math.cos(angle);

            //rotate sprite0's position
            double[] pos0 = { 0, 0 }; //point

            //rotate sprite1's position
            double[] pos1 = rotate(dx, dy, sin, cos, true);

            //rotate sprite0's velocity
            double[] vel0 = rotate(sprite0.vX, sprite0.vY, sin, cos, true);

            //rotate sprite1's velocity
            double[] vel1 = rotate(sprite1.vX, sprite1.vY, sin, cos, true);

            //collision reaction
            double vxTotal = vel0[0] - vel1[0];
            vel0[0] = ((sprite0.m - sprite1.m) * vel0[0] + 2 * sprite1.m * vel1[0]) / (sprite0.m + sprite1.m);
            vel1[0] = vxTotal + vel0[0];

            //update position
            pos0[0] += vel0[0];
            pos1[0] += vel1[0];

            //rotate positions back
            double[] pos0F = rotate(pos0[0], pos0[1], sin, cos, false);
            double[] pos1F = rotate(pos1[0], pos1[1], sin, cos, false);

            //adjust positions to actual screen positions
            sprite1.x = sprite0.x + pos1F[0];
            sprite1.y = sprite0.y + pos1F[1];
            sprite0.x = sprite0.x + pos0F[0];
            sprite0.y = sprite0.y + pos0F[1];

            //rotate velocities back
            double[] vel0F = rotate(vel0[0], vel0[1], sin, cos, false), vel1F = rotate(vel1[0], vel1[1], sin, cos, false);
            sprite0.vX = vel0F[0];
            sprite0.vY = vel0F[1];
            sprite1.vX = vel1F[0];
            sprite1.vY = vel1F[1];
        }
    }
    private static double[] rotate(double x, double y, double sin, double cos, boolean reverse) {
        return new double[] {
            (reverse) ? (x * cos + y * sin) : (x * cos - y * sin),
            (reverse) ? (y * cos - x * sin) : (y * cos + x * sin)
        };
    }

    /*
     * t   -> Point in time  (Values between 0 - 1)
     * ap0 -> AnchorPoint  (Start point of the curve)
     * cp1 -> ControlPoint (Control point of the curve)
     * ap2 -> AnchorPoint  (End point of the curve)
     */
    public static final double[] bezierCurve3Points(final double t, final double ap0x, final double ap0y, final double cp1x, final double cp1y, final double ap2x, final double ap2y) {
        double oneMinusT         = (1 - t);
        double oneMinusTSquared2 = (oneMinusT * oneMinusT);
        double tSquared2         = t * t;

        double x = oneMinusTSquared2 * ap0x + 2 * oneMinusT * t * cp1x + tSquared2 * ap2x;
        double y = oneMinusTSquared2 * ap0y + 2 * oneMinusT * t * cp1y + tSquared2 * ap2y;
        //double x = (1 - t) * (1 - t) * ap0x + 2 * (1 - t) * t * cp1x + t * t * ap2x;
        //double y = (1 - t) * (1 - t) * ap0y + 2 * (1 - t) * t * cp1y + t * t * ap2y;
        return new double[] { x, y };
    }

    /*
     * t   -> Point in time  (Values between 0 - 1)
     * ap0 -> AnchorPoint    (Start point of the curve)
     * cp1 -> ControlPoint 1 (1st control point of the curve)
     * cp2 -> ControlPoint 2 (2nd control point of the curve)
     * ap3 -> AnchorPoint    (End point of the curve)
     */
    public static final double[] bezierCurve4Points(final double t, final double ap0x, final double ap0y, final double cp1x, final double cp1y, final double cp2x, final double cp2y, final double ap3x, final double ap3y) {
        double oneMinusT         = (1 - t);
        double oneMinusTSquared2 = (1 - t) * (1 - t);
        double oneMinusTSquared3 = (1 - t) * (1 - t) * (1 - t);
        double tSquared2         = t * t;
        double txSquared3        = t * t * t;

        double x = oneMinusTSquared3 * ap0x + 3 * oneMinusTSquared2 * t * cp1x + 3 * oneMinusT * tSquared2 * cp2x + txSquared3 * ap3x;
        double y = oneMinusTSquared3 * ap0y + 3 * oneMinusTSquared2 * t * cp1y + 3 * oneMinusT * tSquared2 * cp2y + txSquared3 * ap3y;

        return new double[] { x, y };
    }
}
