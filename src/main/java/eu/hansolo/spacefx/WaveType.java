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

import java.util.ArrayList;
import java.util.List;

import static eu.hansolo.spacefx.Config.*;


public enum WaveType {
    NONE(120),
    TYPE_1_SLOW(240), TYPE_1_MEDIUM(180), TYPE_1_FAST(120),
    TYPE_2_SLOW(240), TYPE_2_MEDIUM(180), TYPE_2_FAST(120),
    TYPE_3_SLOW(240), TYPE_3_MEDIUM(180), TYPE_3_FAST(120),
    TYPE_4_SLOW(240), TYPE_4_MEDIUM(180), TYPE_4_FAST(120),
    TYPE_5_SLOW(460), TYPE_5_MEDIUM(420), TYPE_5_FAST(360);
    
    private static final double        ENEMY_SIZE = 50;
    public  final double               totalFrames;
    //public record WaveCoordinate1(double x, double y, double r) {}
    public  final List<WaveCoordinate> coordinates;


    WaveType(final double totalFrames) {
        this.totalFrames = totalFrames;
        this.coordinates = new ArrayList<>();

        double[] ap0;
        double[] cp1;
        double[] cp2;
        double[] ap3;
        switch(this.name()) {
            case "TYPE_1_SLOW":
            case "TYPE_1_MEDIUM":
            case "TYPE_1_FAST":
                ap0  = new double[] { WIDTH + ENEMY_SIZE, -ENEMY_SIZE };
                cp1  = new double[] { 0, HEIGHT * 0.35 };
                cp2  = new double[] { WIDTH * 0.5, HEIGHT };
                ap3  = new double[] { WIDTH + ENEMY_SIZE, HEIGHT * 0.5 };
                coordinates.addAll(addCoordinates(totalFrames, ap0, cp1, cp2, ap3));
                break;
            case "TYPE_2_SLOW":
            case "TYPE_2_MEDIUM":
            case "TYPE_2_FAST":
                ap0  = new double[] { -ENEMY_SIZE, -ENEMY_SIZE };
                cp1  = new double[] { WIDTH, HEIGHT * 0.35 };
                cp2  = new double[] { WIDTH * 0.5, HEIGHT };
                ap3  = new double[] { -ENEMY_SIZE, HEIGHT * 0.5 };
                coordinates.addAll(addCoordinates(totalFrames, ap0, cp1, cp2, ap3));
                break;
            case "TYPE_3_SLOW":
            case "TYPE_3_MEDIUM":
            case "TYPE_3_FAST":
                ap0  = new double[] { -ENEMY_SIZE, -ENEMY_SIZE };
                cp1  = new double[] { WIDTH * 0.5, HEIGHT * 0.35 };
                cp2  = new double[] { 0, HEIGHT };
                ap3  = new double[] { WIDTH + ENEMY_SIZE, HEIGHT * 0.5 };
                coordinates.addAll(addCoordinates(totalFrames, ap0, cp1, cp2, ap3));
                break;
            case "TYPE_4_SLOW":
            case "TYPE_4_MEDIUM":
            case "TYPE_4_FAST":
                ap0  = new double[] { WIDTH + ENEMY_SIZE, -ENEMY_SIZE };
                cp1  = new double[] { WIDTH * 0.5, HEIGHT * 0.35 };
                cp2  = new double[] { WIDTH, HEIGHT };
                ap3  = new double[] { -ENEMY_SIZE, HEIGHT * 0.5 };
                coordinates.addAll(addCoordinates(totalFrames, ap0, cp1, cp2, ap3));
                break;
            case "TYPE_5_SLOW":
            case "TYPE_5_MEDIUM":
            case "TYPE_5_FAST":
                ap0  = new double[] { -ENEMY_SIZE, -ENEMY_SIZE };
                cp1  = new double[] { WIDTH * 0.11428571, HEIGHT * 0.08888889 };
                cp2  = new double[] { WIDTH * 0.09285714, HEIGHT * 0.53333333 };
                ap3  = new double[] { WIDTH * 0.3, HEIGHT * 0.63888889 };
                coordinates.addAll(addCoordinates(totalFrames / 3, ap0, cp1, cp2, ap3));

                ap0  = new double[] { WIDTH * 0.3, HEIGHT * 0.63888889 };
                cp1  = new double[] { WIDTH, HEIGHT };
                cp2  = new double[] { WIDTH * 1.08571429, HEIGHT * 0.22777778 };
                ap3  = new double[] { WIDTH * 0.54285714, HEIGHT * 0.35555556 };
                coordinates.addAll(addCoordinates(totalFrames / 3, ap0, cp1, cp2, ap3));

                ap0  = new double[] { WIDTH * 0.54285714, HEIGHT * 0.35555556 };
                cp1  = new double[] { WIDTH * 0.3, HEIGHT * 0.41111111 };
                cp2  = new double[] { WIDTH * -0.37539683, HEIGHT * 0.8 };
                ap3  = new double[] { WIDTH * 0.76, HEIGHT + ENEMY_SIZE };
                coordinates.addAll(addCoordinates(totalFrames / 3, ap0, cp1, cp2, ap3));
                break;
            case "NONE":
            default:
                ap0  = new double[] { 0, 0 };
                cp1  = new double[] { 0, 0 };
                cp2  = new double[] { 0, 0 };
                ap3  = new double[] { 0, 0 };
                break;
        }
    }

    private List<WaveCoordinate> addCoordinates(final double totalFrames, final double[] ap0, final double[] cp1, final double[] cp2, final double[] ap3) {
        double               oldX        = ap0[0];
        double               oldY        = ap0[1];
        List<WaveCoordinate> coordinates = new ArrayList<>();
        for (double frameCount = 0; frameCount < totalFrames; frameCount++) {
            double   t  = frameCount / totalFrames;
            double[] p  = Helper.bezierCurve4Points(t, ap0[0], ap0[1], cp1[0], cp1[1], cp2[0], cp2[1], ap3[0], ap3[1]);
            double   vX = p[0] - oldX;
            double   vY = p[1] - oldY;
            double   r  = Math.toDegrees(Math.atan2(vY, vX)) - 90.0;
            coordinates.add(new WaveCoordinate(p[0], p[1], r));
            oldX = p[0];
            oldY = p[1];
        }
        return coordinates;
    }
}
