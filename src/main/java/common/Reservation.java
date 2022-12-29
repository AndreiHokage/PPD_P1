package common;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Reservation implements Serializable {
    private Long idReservation;
    private String name;
    private String cnp;
    private LocalDate dateOfReservation; // the date the reservation was made
    private Long idLocation;
    private Long idTreatment;
    private LocalDate dateTreatment; // treatment appointment date
    private LocalTime timeTreatment; // treatment appointment hour

    public Reservation(){

    }

    public Reservation(Long idReservation, String name, String cnp, Long idLocation, Long idTreatment, LocalDate dateTreatment, LocalTime timeTreatment) {
        this.idReservation = idReservation;
        this.name = name;
        this.cnp = cnp;
        this.dateOfReservation = LocalDate.now();
        this.idLocation = idLocation;
        this.idTreatment = idTreatment;
        this.dateTreatment = dateTreatment;
        this.timeTreatment = timeTreatment;
    }

    public Long getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(Long idReservation) {
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

    public Long getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(Long idLocation) {
        this.idLocation = idLocation;
    }

    public Long getIdTreatment() {
        return idTreatment;
    }

    public void setIdTreatment(Long idTreatment) {
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
