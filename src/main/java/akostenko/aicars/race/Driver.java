package akostenko.aicars.race;

public abstract class Driver {
    private Car car;

    public void setCar(Car car) {
        this.car = car;
    }

    public Car getCar() {
        return car;
    }


    public abstract boolean accelerates();
    public abstract boolean breaks();
    public abstract boolean turnesLeft();
    public abstract boolean turnesRight();

    public abstract String getName();

    public abstract void update(double dTime);
}
