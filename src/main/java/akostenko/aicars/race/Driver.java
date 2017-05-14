package akostenko.aicars.race;

import akostenko.aicars.race.car.Car;

public abstract class Driver {
    private Car car;

    public void setCar(Car car) {
        this.car = car;
    }

    public Car getCar() {
        return car;
    }


    public abstract double accelerating();
    public abstract double breaking();

    /**
     * @return driver's desire to turn. Values are [-1,1] where -1 is full lock to left, +1 is full lock to right
     */
    public abstract double steering();

    public abstract String getName();

    public abstract void update(double dTime);
}
