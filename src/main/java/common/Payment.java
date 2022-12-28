package common;

import java.io.Serializable;
import java.time.LocalDate;

public class Payment implements Serializable {
    private long idPayment;
    private LocalDate dateOfPayment;
    private String cnp;
    private double amountPayed;
    private long idReservation;

    public Payment(long idPayment, LocalDate dateOfPayment, String cnp, double amountPayed, long idReservation) {
        this.idPayment = idPayment;
        this.dateOfPayment = dateOfPayment;
        this.cnp = cnp;
        this.amountPayed = amountPayed;
        this.idReservation = idReservation;
    }


    public long getIdPayment() {
        return idPayment;
    }

    public void setIdPayment(long idPayment) {
        this.idPayment = idPayment;
    }

    public LocalDate getDateOfPayment() {
        return dateOfPayment;
    }

    public void setDateOfPayment(LocalDate dateOfPayment) {
        this.dateOfPayment = dateOfPayment;
    }

    public String getCnp() {
        return cnp;
    }

    public void setCnp(String cnp) {
        this.cnp = cnp;
    }

    public double getAmountPayed() {
        return amountPayed;
    }

    public void setAmountPayed(double amountPayed) {
        this.amountPayed = amountPayed;
    }

    public long getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(long idReservation) {
        this.idReservation = idReservation;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "idPayment=" + idPayment +
                ", dateOfPayment=" + dateOfPayment +
                ", cnp='" + cnp + '\'' +
                ", amountPayed=" + amountPayed +
                ", idReservation=" + idReservation +
                '}';
    }
}
