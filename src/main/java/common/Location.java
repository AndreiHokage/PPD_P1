package common;

import java.io.Serializable;
import java.util.Objects;

public class Location implements Serializable {
    private Long idLocation;
    private String name;

    public Location(){

    }

    public Location(long idLocation, String name) {
        this.idLocation = idLocation;
        this.name = name;
    }

    public Long getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(Long idLocation) {
        this.idLocation = idLocation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(idLocation, location.idLocation) && Objects.equals(name, location.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idLocation, name);
    }

    @Override
    public String toString() {
        return "Location{" +
                "idLocation=" + idLocation +
                ", name='" + name + '\'' +
                '}';
    }
}
