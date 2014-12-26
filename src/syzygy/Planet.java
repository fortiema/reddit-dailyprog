package syzygy;

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

    public void setPositionAtYear(double yearsElapsed) {
        this.currentPosition = (PI * orbitRadiusAU) * (yearsElapsed % orbitalPeriodEY);
    }

    public void resetPosition() {
        this.currentPosition = 0.0;
    }

}
