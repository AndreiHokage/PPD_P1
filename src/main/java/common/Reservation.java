package common;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Reservation implements Serializable {
    private long idReservation;
    private String name;
    private String cnp;
    private final LocalDate dateOfReservation; // the date the reservation was made
    private long idLocation;
    private long idTreatment;
    private LocalDate dateTreatment; // treatment appointment date
    private LocalTime timeTreatment; // treatment appointment hour

    public Reservation(long idReservation, String name, String cnp, long idLocation, long idTreatment, LocalDate dateTreatment, LocalTime timeTreatment) {
        this.idReservation = idReservation;
        this.name = name;
        this.cnp = cnp;
        this.dateOfReservation = LocalDate.now();
        this.idLocation = idLocation;
        this.idTreatment = idTreatment;
        this.dateTreatment = dateTreatment;
        this.timeTreatment = timeTreatment;
    }

    public long getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(long idReservation) {
        this.idReservation = idReservation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCnp() {
        return cnp;
    }

    public void setCnp(String cnp) {
        this.cnp = cnp;
    }

    public LocalDate getDateOfReservation() {
        return dateOfReservation;
    }

    public long getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(long idLocation) {
        this.idLocation = idLocation;
    }

    public long getIdTreatment() {
        return idTreatment;
    }

    public void setIdTreatment(long idTreatment) {
        this.idTreatment = idTreatment;
    }

    public LocalDate getDateTreatment() {
        return dateTreatment;
    }

    public void setDateTreatment(LocalDate dateTreatment) {
        this.dateTreatment = dateTreatment;
    }

    public LocalTime getTimeTreatment() {
        return timeTreatment;
    }

    public void setTimeTreatment(LocalTime timeTreatment) {
        this.timeTreatment = timeTreatment;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "idReservation=" + idReservation +
                ", name='" + name + '\'' +
                ", cnp='" + cnp + '\'' +
                ", dateOfReservation=" + dateOfReservation +
                ", idLocation=" + idLocation +
                ", idTreatment=" + idTreatment +
                ", dateTreatment=" + dateTreatment +
                ", timeTreatment=" + timeTreatment +
                '}';
    }
}
