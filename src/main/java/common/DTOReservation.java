package common;

import java.io.Serializable;

public class DTOReservation implements Serializable {
    private Boolean wasAccepted;
    private Reservation reservation;

    public DTOReservation(Boolean wasAccepted, Reservation reservation) {
        this.wasAccepted = wasAccepted;
        this.reservation = reservation;
    }

    public Boolean getWasAccepted() {
        return wasAccepted;
    }

    public void setWasAccepted(Boolean wasAccepted) {
        this.wasAccepted = wasAccepted;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    @Override
    public String toString() {
        return "DTOReservation{" +
                "wasAccepted=" + wasAccepted +
                ", reservation=" + reservation +
                '}';
    }
}
