package com.hci.gesturedetection;

import android.graphics.Path;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;


class Point implements Comparable<Point> {
    int x, y;

    public int compareTo(Point p) {
        if (this.x == p.x) {
            return this.y - p.y;
        } else {
            return this.x - p.x;
        }
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

}


public class ConvexHull {

    public static long cross(Point O, Point A, Point B) {
        return (A.x - O.x) * (long) (B.y - O.y) - (A.y - O.y) * (long) (B.x - O.x);
    }

    /*
    public static Point[] convex_hull(Point[] P) {

        if (P.length > 1) {
            int n = P.length, k = 0;
            Point[] H = new Point[2 * n];

            Arrays.sort(P);

            // Build lower hull
            for (int i = 0; i < n; ++i) {
                while (k >= 2 && cross(H[k - 2], H[k - 1], P[i]) <= 0)
                    k--;
                H[k++] = P[i];
            }

            // Build upper hull
            for (int i = n - 2, t = k + 1; i >= 0; i--) {
                while (k >= t && cross(H[k - 2], H[k - 1], P[i]) <= 0)
                    k--;
                H[k++] = P[i];
            }
            if (k > 1) {
                H = Arrays.copyOfRange(H, 0, k - 1); // remove non-hull vertices after k; remove k - 1 which is a duplicate
            }
            return H;
        } else if (P.length <= 1) {
            return P;
        } else {
            return null;
        }
    }
    */

    public static int orientation(Point p, Point q, Point r)
    {
        int val = (q.y - p.y) * (r.x - q.x) -
                (q.x - p.x) * (r.y - q.y);

        if (val == 0) return 0;  // collinear
        return (val > 0)? 1: 2; // clock or counter-clock wise
    }

    public static Point[] convex_hull(Point[] P) {

        if (P.length > 1) {
            int n = P.length, k = 0;
            Point[] H = new Point[2 * n];

            // There must be at least 3 points
            if (n < 3)
                return P;

            // Initialize Result
            Vector<Point> hull = new Vector<Point>();

            // Find the leftmost point
            int l = 0;
            for (int i = 1; i < n; i++)
                if (P[i].x < P[l].x)
                    l = i;

            // Start from leftmost point, keep moving
            // counterclockwise until reach the start point
            // again. This loop runs O(h) times where h is
            // number of points in result or output.
            int p = l, q;
            do
            {
                // Add current point to result
                hull.add(P[p]);

                // Search for a point 'q' such that
                // orientation(p, x, q) is counterclockwise
                // for all points 'x'. The idea is to keep
                // track of last visited most counter-clock-
                // wise point in q. If any point 'i' is more
                // counter-clockwise than q, then update q.
                q = (p + 1) % n;

                for (int i = 0; i < n; i++)
                {
                    // If i is more counterclockwise than
                    // current q, then update q
                    if (orientation(P[p], P[i], P[q]) == 2)
                        q = i;
                }

                // Now q is the most counterclockwise with
                // respect to p. Set p as q for next iteration,
                // so that q is added to result 'hull'
                p = q;

            } while (p != l);  // While we don't come to first point

            int initial = 0;
            for (Point temp : hull) {
                H[initial] = new Point();
                H[initial].x = temp.x;
                H[initial].y = temp.y;
                initial++;
            }

            return H;
        } else {
            return P;
        }
    }

    public static List<Float> main(List<Float> points) throws IOException {

//        BufferedReader f = new BufferedReader(new FileReader("hull.in"));    // "hull.in"  Input Sample => size x y x y x y x y
//        StringTokenizer st = new StringTokenizer(f.readLine());
//        Point[] p = new Point[Integer.parseInt(st.nextToken())];
        int initial = 0;
        Point[] p = new Point[points.size()/4];
        for (int i = 0; i < p.length; i++) {
            p[i] = new Point();
//            p[i].x = Integer.parseInt(st.nextToken()); // Read X coordinate
            p[i].x = (int) (float) points.get(initial++);
//            p[i].y = Integer.parseInt(st.nextToken()); // Read y coordinate
            p[i].y = (int) (float) points.get(initial++);
            initial++;
            initial++;
        }

        Point[] hull = convex_hull(p).clone();
        List<Float> pointsHull = new ArrayList<Float>();

        for (int i = 0; i < hull.length; i++) {
            if (hull[i] != null) {
//                System.out.print(hull[i]);
                pointsHull.add((float) hull[i].x);
                pointsHull.add((float) hull[i].y);
            }
        }

        // commented code below gives limited points
//        while (pointsHull.size() > 6) {
//            List<Float> recurCall = ConvexHull.main(pointsHull);
//            if (recurCall.size() <= pointsHull.size() && recurCall.size() >= 6) {
//                pointsHull = recurCall;
//            }
//            else {
//                break;
//            }
//        }
        return pointsHull;
    }
}