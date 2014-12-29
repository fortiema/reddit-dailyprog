package syzygy;

import javafx.geometry.Point2D;

import java.util.Vector;

/**
 * Title: Planet
 * <p/>
 * Description:
 * <p/>
 * Basic planet representation with orbital radius and period
 * <p/>
 * @author: Matt Fortier <fortiema@gmail.com>
 * <p/>
 * Created on: 2014-12-26.
 * Last Modified on: 2014-12-26.
 */
public class Planet {

    public static final double PI2 = 6.2832;

    private String name;
    private double orbitRadiusAU;
    private double orbitalPeriodEY;

    private double currentPosition;

    public Planet() {

    }

    public Planet(String name, double orbitRadius, double orbitalPeriod) {
        this.name = name;
        this.orbitRadiusAU = orbitRadius;
        this.orbitalPeriodEY = orbitalPeriod;
    }

    public String getName() {
        return name;
    }

    public double getCurrentPosition() {
        return currentPosition;
    }

    public Point2D getPositionCartesian() {
        return new Point2D(orbitRadiusAU * Math.cos(currentPosition * PI2),
                           orbitRadiusAU * Math.sin(currentPosition * PI2));
    }

    public void setElapsedYears(double yearsElapsed) {
        this.currentPosition = ((yearsElapsed % orbitalPeriodEY) / orbitalPeriodEY);
    }

    public void resetPosition() {
        this.currentPosition = 0.0;
    }

    public boolean equals(Planet p2) {
        return (this.name.equals(p2.getName()));
    }

    public String toString() {
        return name + " - " + String.format("%.2f", currentPosition) + " ("
                + String.format("%.3f", getPositionCartesian().getX()) + ","
                + String.format("%.3f", getPositionCartesian().getY()) + ")";
    }

}
