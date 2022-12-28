package common;

import java.io.Serializable;


public class Treatment implements Serializable {
    private long idTreatment;
    private double price;
    private int duration; // the number of minutes
    private final int maxCapacity;

    public Treatment(long idTreatment, double price, int duration, int maxCapacity) {
        this.idTreatment = idTreatment;
        this.price = price;
        this.duration = duration;
        this.maxCapacity = maxCapacity;
    }

    public long getIdTreatment() {
        return idTreatment;
    }

    public void setIdTreatment(long idTreatment) {
        this.idTreatment = idTreatment;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    @Override
    public String toString() {
        return "Treatment{" +
                "idTreatment=" + idTreatment +
                ", price=" + price +
                ", duration=" + duration +
                ", maxCapacity=" + maxCapacity +
                '}';
    }
}
