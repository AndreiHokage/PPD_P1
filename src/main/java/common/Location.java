package common;

import java.io.Serializable;

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
    public String toString() {
        return "Location{" +
                "idLocation=" + idLocation +
                ", name='" + name + '\'' +
                '}';
    }
}
