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

import java.util.List;


public class Point implements Comparable<Point> {
    public double x;
    public double y;
    public double r;


    // ******************** Constructors **************************************
    public Point() {
        this(-1, -1);
    }
    public Point(final Point point) {
        this(point.getX(), point.getY());
    }
    public Point(final double x, final double y) {
        this.x = x;
        this.y = y;
        this.r = 0.5;
    }


    // ******************** Methods *******************************************
    public double getX() { return x; }
    public void setX(final double x) { this.x = x; }
    public int getXAsInt() { return (int) x; }

    public double getY() { return y; }
    public void setY(final double y) { this.y = y; }
    public int getYAsInt() { return (int) y; }

    public void set(final double[] xy) {
        if (xy.length != 2) throw new IllegalArgumentException("Array must contain 2 values");
        set(xy[0], xy[1]);
    }
    public void set(final Point point) {
        set(point.x, point.y);
    }
    public void set(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    public void translateBy(final double dx, final double dy) {
        this.x += dx;
        this.y += dy;
    }

    public double euclideanDistanceTo(final Point p) { return euclideanDistance(p.getX(), p.getY(), this.x, this.y); }
    public double euclideanDistanceTo(final double x, final double y) { return euclideanDistance(x, y, this.x, this.y); }

    public double distanceTo(final Point p) { return distance(p.getX(), p.getY(), x, y); }
    public double distanceTo(final double x, final double y) { return distance(x, y, this.x, this.y); }

    public Point fromPolar(final double length, final double angleRad) {
        return new Point(length * Math.cos(angleRad), length * Math.sin(angleRad));
    }

    public Point add(final Point point) { return add(point.getX(), point.getY()); }
    public Point add(final double x, final double y) { return new Point(getX() + x, getY() + y); }

    public Point subtract(Point point) { return subtract(point.getX(), point.getY()); }
    public Point subtract(final double x, final double y) { return new Point(getX() - x, getY() - y); }

    public Point multiply(final double factor) { return new Point(getX() * factor, getY() * factor); }

    public Point normalize() {
        final double mag = magnitude();
        if (mag == 0.0) { return new Point(0.0, 0.0); }
        return new Point(getX() / mag, getY() / mag);
    }

    public Point midpoint(final Point point) { return midpoint(point.getX(), point.getY()); }
    public Point midpoint(final double x, final double y) { return new Point(x + (getX() - x) / 2.0, y + (getY() - y) / 2.0); }

    public double angle(final Point point) { return angle(point.getX(), point.getY()); }
    public double angle(final double x, final double y) {
        final double ax = this.x;
        final double ay = this.y;

        final double delta = (ax * x + ay * y) / Math.sqrt((ax * ax + ay * ay) * (x * x + y * y));

        if (delta > 1.0)  { return 0.0; }
        if (delta < -1.0) { return 180.0; }

        return Math.toDegrees(Math.acos(delta));
    }
    public double angle(final Point p1, final Point p2) {
        final double ax = p1.getX() - x;
        final double ay = p1.getY() - y;
        final double bx = p2.getX() - x;
        final double by = p2.getY() - y;

        final double delta = (ax * bx + ay * by) / Math.sqrt((ax * ax + ay * ay) * (bx * bx + by * by));

        if (delta > 1.0) { return 0.0; }
        if (delta < -1.0) { return 180.0; }

        return Math.toDegrees(Math.acos(delta));
    }

    public double angleBetween(final Point p) { return angleBetween(p.getX(), p.getY()); }
    public double angleBetween(final double x, final double y) {
        final double y0 = this.x * y - this.y * x;
        final double x0 = this.x * x - this.y * y;
        return Math.atan2(y0, x0);
    }

    public boolean isColinearTo(final Point point) { return crossProductLength(point) == 0; }


    public double magnitude() { return Math.sqrt(x * x + y * y); }

    public double length() { return Math.sqrt(x * x + y * y); }

    public double lengthSquared() { return (x * x + y * y); }

    public double dotProductLength(final Point point) { return dotProductLength(point.getX(), point.getY()); }
    public double dotProductLength(final double x, final double y) { return this.x * x + this.y * y; }

    public double crossProductLength(final Point point) { return crossProductLength(point.getX(), point.getY()); }
    public double crossProductLength(final double x, final double y) {
        return (this.x * y - x * this.y);
    }


    public static Point nearestWithinRadius(final Point point, final List<Point> points, final double radius) {
        final double radiusSquare = radius * radius;
        Point p = points.get(0);
        for (int i = 0; i < points.size(); i++) {
            double distanceSquare = points.get(i).euclideanDistanceTo(point);
            if (distanceSquare < radiusSquare && distanceSquare < p.euclideanDistanceTo(point)) {
                p = points.get(i);
            }
        }
        return p;
    }

    public static Point nearest(final Point point, final List<Point> points) {
        Point p = points.get(0);
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).euclideanDistanceTo(point) < p.euclideanDistanceTo(point)) {
                p = points.get(i);
            }
        }
        return p;
    }

    public static double distance(final Point p1, final Point p2) { return distance(p1.getX(), p1.getY(), p2.getX(), p2.getY()); }
    public static double distance(final double x1, final double y1, final double x2, final double y2) {
        double deltaX = (x2 - x1);
        double deltaY = (y2 - y1);
        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
    }

    public static double euclideanDistance(final Point p1, final Point p2) { return euclideanDistance(p1.getX(), p1.getY(), p2.getX(), p2.getY()); }
    public static double euclideanDistance(final double x1, final double y1, final double x2, final double y2) {
        double deltaX = (x2 - x1);
        double deltaY = (y2 - y1);
        return (deltaX * deltaX) + (deltaY * deltaY);
    }

    public static double dotProductLength(final Point p1, final Point p2) { return p1.getX() * p2.getX() + p1.getY() * p2.getY(); }

    public static double crossProductLength(final Point p1, final Point p2) {
        return (p1.getX() * p2.getY() - p2.getX() * p1.getY());
    }


    public Point clone() {
        return new Point(this.x, this.y);
    }

    public int compareTo(final Point point) {
        return x != point.getX() ? Double.compare(x, point.x) : Double.compare(y, point.y);
    }

    @Override public int hashCode() {
        int tmp = (int) (y + ((x + 1) / 2));
        return Math.abs((int) (x + (tmp * tmp)));
    }

    @Override public boolean equals(final Object obj) {
        if (obj == this) { return true; }
        if (obj instanceof Point) {
            Point p = (Point) obj;
            return (Math.abs(x - p.getX()) < Helper.EPSILON && Math.abs(y - p.getY()) < Helper.EPSILON);
            //return (Double.compare(x, p.getX()) == 0 && Double.compare(y, p.getY()) == 0);
        }
        return false;
    }

    @Override public String toString() {
        return new StringBuilder().append("{ ")
                                  .append("\"x\": ").append(x).append(", ")
                                  .append("\"y\": ").append(y)
                                  .append(" }").toString();
    }
}
