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

import javafx.scene.shape.Polygon;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Helper {
    public  static final double  MIN_FONT_SIZE = 5;
    public  static final double  HALF_PI       = Math.PI * 0.5;
    public  static final double  TWO_PI        = Math.PI + Math.PI;
    public  static final double  THREE_PI      = TWO_PI + Math.PI;
    public  static final double  EPSILON       = 1E-6;
    private static final Pattern INT_PATTERN   = Pattern.compile("[0-9]+");
    private static final Matcher INT_MATCHER   = INT_PATTERN.matcher("");
    private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");
    private static final Matcher FLOAT_MATCHER = FLOAT_PATTERN.matcher("");
    private static final Pattern HEX_PATTERN   = Pattern.compile("#?([A-Fa-f0-9]{8}|[A-Fa-f0-9]{6})");
    private static final Matcher HEX_MATCHER   = HEX_PATTERN.matcher("");

    public enum CardinalDirection {
        N("North", 348.75, 11.25),
        NNE("North North-East", 11.25, 33.75),
        NE("North-East", 33.75, 56.25),
        ENE("East North-East", 56.25, 78.75),
        E("East", 78.75, 101.25),
        ESE("East South-East", 101.25, 123.75),
        SE("South-East", 123.75, 146.25),
        SSE("South South-East", 146.25, 168.75),
        S("South", 168.75, 191.25),
        SSW("South South-West", 191.25, 213.75),
        SW("South-West", 213.75, 236.25),
        WSW("West South-West", 236.25, 258.75),
        W("West", 258.75, 281.25),
        WNW("West North-West", 281.25, 303.75),
        NW("North-West", 303.75, 326.25),
        NNW("North North-West", 326.25, 348.75);

        public String direction;
        public double from;
        public double to;

        CardinalDirection(final String DIRECTION, final double FROM, final double TO) {
            direction = DIRECTION;
            from      = FROM;
            to        = TO;
        }
    }

    public static final <T extends Number> T clamp(final T min, final T max, final T value) {
        if (value.doubleValue() < min.doubleValue()) return min;
        if (value.doubleValue() > max.doubleValue()) return max;
        return value;
    }

    public static final int clamp(final int min, final int max, final int value) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
    public static final long clamp(final long min, final long max, final long value) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
    public static final double clamp(final double min, final double max, final double value) {
        if (Double.compare(value, min) < 0) return min;
        if (Double.compare(value, max) > 0) return max;
        return value;
    }

    public static final double clampMin(final double min, final double value) {
        if (value < min) return min;
        return value;
    }
    public static final double clampMax(final double max, final double value) {
        if (value > max) return max;
        return value;
    }

    public static final boolean almostEqual(final double value1, final double value2, final double epsilon) {
        return Math.abs(value1 - value2) < epsilon;
    }

    public static final double round(final double value, final int precision) {
        final int SCALE = (int) Math.pow(10, precision);
        return (double) Math.round(value * SCALE) / SCALE;
    }

    public static final double roundTo(final double value, final double target) { return target * (Math.round(value / target)); }

    public static final double roundToHalf(final double value) { return Math.round(value * 2) / 2.0; }

    public static final double nearest(final double smaller, final double value, final double larger) {
        return (value - smaller) < (larger - value) ? smaller : larger;
    }

    public static final int roundDoubleToInt(final double value){
        double dAbs = Math.abs(value);
        int    i      = (int) dAbs;
        double result = dAbs - (double) i;
        if (result < 0.5) {
            return value < 0 ? -i : i;
        } else {
            return value < 0 ? -(i + 1) : i + 1;
        }
    }

    public static final double[] calcAutoScale(final double minValue, final double maxValue) {
        double maxNoOfMajorTicks = 10;
        double maxNoOfMinorTicks = 10;
        double niceMinValue;
        double niceMaxValue;
        double niceRange;
        double majorTickSpace;
        double minorTickSpace;
        niceRange      = (calcNiceNumber((maxValue - minValue), false));
        majorTickSpace = calcNiceNumber(niceRange / (maxNoOfMajorTicks - 1), true);
        niceMinValue   = (Math.floor(minValue / majorTickSpace) * majorTickSpace);
        niceMaxValue   = (Math.ceil(maxValue / majorTickSpace) * majorTickSpace);
        minorTickSpace = calcNiceNumber(majorTickSpace / (maxNoOfMinorTicks - 1), true);
        return new double[]{ niceMinValue, niceMaxValue, majorTickSpace, minorTickSpace };
    }

    /**
     * Can be used to implement discrete steps e.g. on a slider.
     * @param minValue          The min value of the range
     * @param maxValue          The max value of the range
     * @param value             The value to snap
     * @param newMinorTickCount The number of ticks between 2 major tick marks
     * @param newMajorTickUnit  The distance between 2 major tick marks
     * @return The value snapped to the next tick mark defined by the given parameters
     */
    public static final double snapToTicks(final double minValue, final double maxValue, final double value, final int newMinorTickCount, final double newMajorTickUnit) {
        double v = value;
        int    minorTickCount = clamp(0, 10, newMinorTickCount);
        double majorTickUnit  = Double.compare(newMajorTickUnit, 0.0) <= 0 ? 0.25 : newMajorTickUnit;
        double tickSpacing;

        if (minorTickCount != 0) {
            tickSpacing = majorTickUnit / (Math.max(minorTickCount, 0) + 1);
        } else {
            tickSpacing = majorTickUnit;
        }

        int    prevTick      = (int) ((v - minValue) / tickSpacing);
        double prevTickValue = prevTick * tickSpacing + minValue;
        double nextTickValue = (prevTick + 1) * tickSpacing + minValue;

        v = nearest(prevTickValue, v, nextTickValue);

        return clamp(minValue, maxValue, v);
    }

    /**
     * Returns a "niceScaling" number approximately equal to the range.
     * Rounds the number if ROUND == true.
     * Takes the ceiling if ROUND = false.
     *
     * @param range the value range (maxValue - minValue)
     * @param round whether to round the result or ceil
     * @return a "niceScaling" number to be used for the value range
     */
    public static final double calcNiceNumber(final double range, final boolean round) {
        double niceFraction;
        double exponent = Math.floor(Math.log10(range));   // exponent of range
        double fraction = range / Math.pow(10, exponent);  // fractional part of range

        if (round) {
            if (Double.compare(fraction, 1.5) < 0) {
                niceFraction = 1;
            } else if (Double.compare(fraction, 3)  < 0) {
                niceFraction = 2;
            } else if (Double.compare(fraction, 7) < 0) {
                niceFraction = 5;
            } else {
                niceFraction = 10;
            }
        } else {
            if (Double.compare(fraction, 1) <= 0) {
                niceFraction = 1;
            } else if (Double.compare(fraction, 2) <= 0) {
                niceFraction = 2;
            } else if (Double.compare(fraction, 5) <= 0) {
                niceFraction = 5;
            } else {
                niceFraction = 10;
            }
        }
        return niceFraction * Math.pow(10, exponent);
    }

    public static final boolean equals(final double a, final double b) { return a == b || Math.abs(a - b) < EPSILON; }
    public static final boolean biggerThan(final double a, final double b) { return (a - b) > EPSILON; }
    public static final boolean lessThan(final double a, final double b) { return (b - a) > EPSILON; }

    private static final Pair<Point[], Point[]> calcCurveControlPoints(Point[] dataPoints) {
        Point[] firstControlPoints;
        Point[] secondControlPoints;
        int n = dataPoints.length - 1;
        if (n == 1) { // Special case: Bezier curve should be a straight line.
            firstControlPoints     = new Point[1];
            // 3P1 = 2P0 + P3
            firstControlPoints[0]  = new Point((2 * dataPoints[0].getX() + dataPoints[1].getX()) / 3, (2 * dataPoints[0].getY() + dataPoints[1].getY()) / 3);
            secondControlPoints    = new Point[1];
            // P2 = 2P1 – P0
            secondControlPoints[0] = new Point(2 * firstControlPoints[0].getX() - dataPoints[0].getX(), 2 * firstControlPoints[0].getY() - dataPoints[0].getY());
            return new Pair<>(firstControlPoints, secondControlPoints);
        }

        // Calculate first Bezier control points
        // Right hand side vector
        double[] rhs = new double[n];

        // Set right hand side X values
        for (int i = 1; i < n - 1; ++i) {
            rhs[i] = 4 * dataPoints[i].getX() + 2 * dataPoints[i + 1].getX();
        }
        rhs[0]     = dataPoints[0].getX() + 2 * dataPoints[1].getX();
        rhs[n - 1] = (8 * dataPoints[n - 1].getX() + dataPoints[n].getX()) / 2.0;
        // Get first control points X-values
        double[] x = getFirstControlPoints(rhs);

        // Set right hand side Y values
        for (int i = 1; i < n - 1; ++i) {
            rhs[i] = 4 * dataPoints[i].getY() + 2 * dataPoints[i + 1].getY();
        }
        rhs[0]     = dataPoints[0].getY() + 2 * dataPoints[1].getY();
        rhs[n - 1] = (8 * dataPoints[n - 1].getY() + dataPoints[n].getY()) / 2.0;
        // Get first control points Y-values
        double[] y = getFirstControlPoints(rhs);

        // Fill output arrays.
        firstControlPoints  = new Point[n];
        secondControlPoints = new Point[n];
        for (int i = 0; i < n; ++i) {
            // First control point
            firstControlPoints[i] = new Point(x[i], y[i]);
            // Second control point
            if (i < n - 1) {
                secondControlPoints[i] = new Point(2 * dataPoints[i + 1].getX() - x[i + 1], 2 * dataPoints[i + 1].getY() - y[i + 1]);
            } else {
                secondControlPoints[i] = new Point((dataPoints[n].getX() + x[n - 1]) / 2, (dataPoints[n].getY() + y[n - 1]) / 2);
            }
        }
        return new Pair<>(firstControlPoints, secondControlPoints);
    }
    private static final double[] getFirstControlPoints(double[] rhs) {
        int      n   = rhs.length;
        double[] x   = new double[n]; // Solution vector.
        double[] tmp = new double[n]; // Temp workspace.
        double   b   = 2.0;

        x[0] = rhs[0] / b;

        for (int i = 1; i < n; i++) {// Decomposition and forward substitution.
            tmp[i] = 1 / b;
            b      = (i < n - 1 ? 4.0 : 3.5) - tmp[i];
            x[i]   = (rhs[i] - x[i - 1]) / b;
        }
        for (int i = 1; i < n; i++) {
            x[n - i - 1] -= tmp[n - i] * x[n - i]; // Backsubstitution.
        }
        return x;
    }

    public static final boolean isInRectangle(final double x, final double y,
                                              final double minX, final double minY,
                                              final double maxX, final double maxY) {
        return (Double.compare(x, minX) >= 0 &&
                Double.compare(y, minY) >= 0 &&
                Double.compare(x, maxX) <= 0 &&
                Double.compare(y, maxY) <= 0);
    }

    public static final boolean isInEllipse(final double x, final double y,
                                            final double centerX, final double centerY,
                                            final double radiusX, final double radiusY) {
        return Double.compare(((((x - centerX) * (x - centerX)) / (radiusX * radiusX)) +
                               (((y - centerY) * (y - centerY)) / (radiusY * radiusY))), 1) <= 0.0;
    }

    public static final boolean isInCircle(final double x, final double y, final double centerX, final double centerY, final double radius) {
        double deltaX = centerX - x;
        double deltaY = centerY - y;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY) < radius;
    }

    public static final boolean isInPolygon(final double x, final double y, final List<Point> pointsOfPolygon) {
        int noOfPointsInPolygon = pointsOfPolygon.size();
        double[] pointsX = new double[noOfPointsInPolygon];
        double[] pointsY = new double[noOfPointsInPolygon];
        for ( int i = 0 ; i < noOfPointsInPolygon ; i++) {
            Point p = pointsOfPolygon.get(i);
            pointsX[i] = p.getX();
            pointsY[i] = p.getY();
        }
        return isInPolygon(x, y, noOfPointsInPolygon, pointsX, pointsY);
    }
    public static final boolean isInPolygon(final double x, final double y, final Polygon polygon) {
        List<Double> points              = polygon.getPoints();
        int          size                = points.size();
        int          noOfPointsInPolygon = size / 2;
        double[]     pointsX             = new double[noOfPointsInPolygon];
        double[]     pointsY             = new double[noOfPointsInPolygon];
        int          pointCounter        = 0;

        for (int i = 0 ; i < size - 1 ; i += 2) {
            pointsX[pointCounter] = points.get(i);
            pointsY[pointCounter] = points.get(i + 1);
            pointCounter++;
        }
        return isInPolygon(x, y, noOfPointsInPolygon, pointsX, pointsY);
    }
    public static final boolean isInPolygon(final double x, final double y, final int noOfPointsInPolygon, final double[] pointsX, final double[] pointsY) {
        if (noOfPointsInPolygon != pointsX.length || noOfPointsInPolygon != pointsY.length) { return false; }
        boolean inside = false;
        for (int i = 0, j = noOfPointsInPolygon - 1; i < noOfPointsInPolygon ; j = i++) {
            if (((pointsY[i] > y) != (pointsY[j] > y)) && (x < (pointsX[j] - pointsX[i]) * (y - pointsY[i]) / (pointsY[j] - pointsY[i]) + pointsX[i])) {
                inside = !inside;
            }
        }
        return inside;
    }

    public static final <T extends Point> boolean isPointInPolygon(final T p, final List<T> points) {
        boolean inside = false;
        int     size   = points.size();
        double  x      = p.getX();
        double  y      = p.getY();

        for (int i = 0, j = size - 1 ; i < size ; j = i++) {
            if ((points.get(i).getY() > y) != (points.get(j).getY() > y) &&
                (x < (points.get(j).getX() - points.get(i).getX()) * (y - points.get(i).getY()) / (points.get(j).getY() - points.get(i).getY()) + points.get(i).getX())) {
                inside = !inside;
            }
        }
        return inside;
    }

    public static final boolean isInRingSegment(final double x, final double y,
                                                final double centerX, final double centerY,
                                                final double outerRadius, final double innerRadius,
                                                final double newStartAngle, final double segmentAngle) {
        double angleOffset = 90.0;
        double pointRadius = Math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY));
        double pointAngle  = getAngleFromXY(x, y, centerX, centerY, angleOffset);
        double startAngle  = angleOffset - newStartAngle;
        double endAngle    = startAngle + segmentAngle;

        return (Double.compare(pointRadius, innerRadius) >= 0 &&
                Double.compare(pointRadius, outerRadius) <= 0 &&
                Double.compare(pointAngle, startAngle) >= 0 &&
                Double.compare(pointAngle, endAngle) <= 0);
    }

    public static final boolean isPointOnLine(final Point p, final Point p1, final Point p2) {
        return (distanceFromPointToLine(p, p1, p2) < EPSILON);
    }

    public static final double distanceFromPointToLine(final Point p, final Point p1, final Point p2) {
        double A = p.getX() - p1.getX();
        double B = p.getY() - p1.getY();
        double C = p2.getX() - p1.getX();
        double D = p2.getY() - p1.getY();

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = dot / len_sq;

        double xx, yy;

        if (param < 0 || (p1.getX() == p2.getX() && p1.getY() == p2.getY())) {
            xx = p1.getX();
            yy = p1.getY();
        } else if (param > 1) {
            xx = p2.getX();
            yy = p2.getY();
        } else {
            xx = p1.getX() + param * C;
            yy = p1.getY() + param * D;
        }

        double dx = p.getX() - xx;
        double dy = p.getY() - yy;

        return Math.sqrt(dx * dx + dy * dy);
    }

    public static final double distance(final Point p1, final Point p2) {
        return distance(p1.x, p1.y, p2.x, p2.y);
    }
    public static final double distance(final double p1X, final double p1Y, final double p2X, final double p2Y) {
        return Math.sqrt((p2X - p1X) * (p2X - p1X) + (p2Y - p1Y) * (p2Y - p1Y));
    }

    public static final double euclideanDistance(final Point p1, final Point p2) { return euclideanDistance(p1.getX(), p1.getY(), p2.getX(), p2.getY()); }
    public static final double euclideanDistance(final double x1, final double y1, final double x2, final double y2) {
        double deltaX = (x2 - x1);
        double deltaY = (y2 - y1);
        return (deltaX * deltaX) + (deltaY * deltaY);
    }

    public static final Point pointOnLine(final double p1X, final double p1Y, final double p2X, final double p2Y, final double distanceToP2) {
        double distanceP1P2 = distance(p1X, p1Y, p2X, p2Y);
        double t = distanceToP2 / distanceP1P2;
        return new Point((1 - t) * p1X + t * p2X, (1 - t) * p1Y + t * p2Y);
    }

    public static final int checkLineCircleCollision(final Point p1, final Point p2, final double centerX, final double centerY, final double radius) {
        return checkLineCircleCollision(p1.x, p1.y, p2.x, p2.y, centerX, centerY, radius);
    }
    public static final int checkLineCircleCollision(final double p1X, final double p1Y, final double p2X, final double p2Y, final double centerX, final double centerY, final double radius) {
        double A = (p1Y - p2Y);
        double B = (p2X - p1X);
        double C = (p1X * p2Y - p2X * p1Y);

        return checkCollision(A, B, C, centerX, centerY, radius);
    }
    public static final int checkCollision(final double a, final double b, final double c, final double centerX, final double centerY, final double radius) {
        // Finding the distance of line from center.
        double dist = (Math.abs(a * centerX + b * centerY + c)) / Math.sqrt(a * a + b * b);
        dist = round(dist, 1);
        if (radius > dist) {
            return 1;  // intersect
        } else if (radius < dist) {
            return -1; // outside
        } else {
            return 0;  // touch
        }
    }

    public static final double getAngleFromXY(final double x, final double y, final double centerX, final double centerY) {
        return getAngleFromXY(x, y, centerX, centerY, 90.0);
    }
    public static final double getAngleFromXY(final double x, final double y, final double centerX, final double centerY, final double angleOffset) {
        // For ANGLE_OFFSET =  0 -> Angle of 0 is at 3 o'clock
        // For ANGLE_OFFSET = 90  ->Angle of 0 is at 12 o'clock
        double deltaX      = x - centerX;
        double deltaY      = y - centerY;
        double radius      = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
        double nx          = deltaX / radius;
        double ny          = deltaY / radius;
        double theta       = Math.atan2(ny, nx);
        theta              = Double.compare(theta, 0.0) >= 0 ? Math.toDegrees(theta) : Math.toDegrees((theta)) + 360.0;
        double angle       = (theta + angleOffset) % 360;
        return angle;
    }

    public static final double[] rotatePointAroundRotationCenter(final double x, final double y, final double rX, final double rY, final double angleDeg) {
        final double rad = Math.toRadians(angleDeg);
        final double sin = Math.sin(rad);
        final double cos = Math.cos(rad);
        final double nX  = rX + (x - rX) * cos - (y - rY) * sin;
        final double nY  = rY + (x - rX) * sin + (y - rY) * cos;
        return new double[] { nX, nY };
    }

    public static final Point getPointBetweenP1AndP2(final Point p1, final Point p2) {
        double[] xy = getPointBetweenP1AndP2(p1.x, p1.y, p2.x, p2.y);
        return new Point(xy[0], xy[1]);
    }
    public static final double[] getPointBetweenP1AndP2(final double p1X, final double p1Y, final double p2X, final double p2Y) {
        return new double[] { (p1X + p2X) * 0.5, (p1Y + p2Y) * 0.5 };
    }

    public static final double getDecimalDeg(final int degrees, final int minutes, final double seconds) {
        return (((seconds / 60) + minutes) / 60) + degrees;
    }

    private static final <T extends Point> double distance(final T P1, final T P2, final T P3) {
        double deltaX = P2.getX() - P1.getX();
        double deltaY = P2.getY() - P1.getY();
        double num = deltaX * (P1.getY() - P3.getY()) - deltaY * (P1.getX() - P3.getX());
        return Math.abs(num);
    }
    private static final <T extends Point> int pointLocation(final T P1, final T P2, final T P3) {
        double cp1 = (P2.getX() - P1.getX()) * (P3.getY() - P1.getY()) - (P2.getY() - P1.getY()) * (P3.getX() - P1.getX());
        return cp1 > 0 ? 1 : Double.compare(cp1, 0) == 0 ? 0 : -1;
    }

    public static final int noOfDiagonalEdges(final List<Point> polygonPoints) {
        int noOfDiagonalEdges = 0;
        int pointsInPolygon = polygonPoints.size();
        for (int i = 0 ; i < pointsInPolygon - 1 ; i++) {
            Point p1 = polygonPoints.get(i);
            Point p2 = polygonPoints.get(i + 1);
            if (isHorizontal(p1, p2) || isVertical(p1, p2)) { continue; }
            noOfDiagonalEdges++;
        }
        if (!isHorizontal(polygonPoints.get(pointsInPolygon - 1), polygonPoints.get(0)) &&
            !isVertical(polygonPoints.get(pointsInPolygon - 1), polygonPoints.get(0))) {
            noOfDiagonalEdges++;
        }
        return noOfDiagonalEdges;
    }
    public static final boolean isHorizontal(final Point p1, final Point p2) { return Math.abs(p1.getY() - p2.getY()) < EPSILON; }
    public static final boolean isVertical(final Point p1, final Point p2)   { return Math.abs(p1.getX() - p2.getX()) < EPSILON; }

    /**
     * Sort a list of points by it's distance from each other. The algorithm starts with the point closest to
     * 0,0 and from there always adds the point closest to the last point
     * @param points
     * @return
     */
    public static final List<Point> sortByDistance(final List<Point> points) {
        return sortByDistance(points, true);
    }
    public static final List<Point> sortByDistance(final List<Point> points, final boolean counterClockWise) {
        if (points.isEmpty()) { return points; }
        List<Point> output = new ArrayList<>();
        output.add(points.get(nearestPoint(new Point(0, 0), points)));
        points.remove(output.get(0));
        int x = 0;
        for (int i = 0; i < points.size() + x; i++) {
            output.add(points.get(nearestPoint(output.get(output.size() - 1), points)));
            points.remove(output.get(output.size() - 1));
            x++;
        }
        if (counterClockWise) { Collections.reverse(output); }
        return output;
    }
    public static final int nearestPoint(final Point p, final List<Point> points) {
        Pair<Double, Integer> smallestDistance = new Pair<>(0d,0);
        for (int i = 0; i < points.size(); i++) {
            double distance = distance(p.getX(), p.getY(), points.get(i).getX(), points.get(i).getY());
            if (i == 0) {
                smallestDistance = new Pair<>(distance, i);
            } else {
                if (distance < smallestDistance.getKey()) {
                    smallestDistance = new Pair<>(distance, i);
                }
            }
        }
        return smallestDistance.getValue();
    }

    public static final List<Point> removeDuplicatePoints(final List<Point> points, final double tolerance) {
        final double tol  = tolerance < 0 ? 0 : tolerance;
        final int    size = points.size();

        List<Point> reducedPoints  = new ArrayList<>(points);
        Set<Point>  pointsToRemove = new HashSet<>();

        for (int i = 0 ; i < size - 2 ; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);

            double distP1P2  = Helper.distance(p1, p2);

            // Remove duplicates
            if (distP1P2 <= tol) { pointsToRemove.add(p2); }
        }
        reducedPoints.removeAll(pointsToRemove);

        return reducedPoints;
    }

    public static final double bearing(final Point p1, final Point p2) {
        return bearing(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }
    public static final double bearing(final double x1, final double y1, final double x2, final double y2) {
        double bearing = Math.toDegrees(Math.atan2(y2 - y1, x2 - x1)) + 90;
        if (bearing < 0) { bearing += 360.0; }
        return bearing;
    }

    public static final String getCardinalDirectionFromBearing(final double BEARING) {
        double bearing = BEARING % 360.0;
        if (0 == bearing || 360 == bearing || (bearing > CardinalDirection.N.from && bearing < 360)) {
            return CardinalDirection.N.direction;
        } else if (90 == bearing) {
            return CardinalDirection.E.direction;
        } else if (180 == bearing) {
            return CardinalDirection.S.direction;
        } else if (270 == bearing) {
            return CardinalDirection.W.direction;
        } else {
            for (CardinalDirection cardinalDirection : CardinalDirection.values()) {
                if (bearing >= cardinalDirection.from && bearing <= cardinalDirection.to) {
                    return cardinalDirection.direction;
                }
            }
        }
        return "";
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
