package common;

import java.io.Serializable;
import java.util.List;


public class Treatment implements Serializable {
    private Long idTreatment;
    private double price;
    private int duration; // the number of minutes
    private List<Integer> maxCapacity;

    public Treatment(){

    }

    public Treatment(Long idTreatment, double price, int duration, List<Integer> maxCapacity) {
        this.idTreatment = idTreatment;
        this.price = price;
        this.duration = duration;
        this.maxCapacity = maxCapacity;
    }

    public Long getIdTreatment() {
        return idTreatment;
    }

    public void setIdTreatment(Long idTreatment) {
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

    public List<Integer> getMaxCapacity() {
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
