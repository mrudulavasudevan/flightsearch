package com.flightstatus.elasticsearch.document;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Flight {

    private String id;
    private String airlineCode;
    private String flightNumber;
    private String departureApt;
    private String destinationApt;
    private String flightStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String departureDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String arrivalDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAirlineCode() {
        return airlineCode;
    }

    public void setAirlineCode(String airlineCode) {
        this.airlineCode = airlineCode;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getDepartureApt() {
        return departureApt;
    }

    public void setDepartureApt(String departureApt) {
        this.departureApt = departureApt;
    }

    public String getDestinationApt() {
        return destinationApt;
    }

    public void setDestinationApt(String destinationApt) {
        this.destinationApt = destinationApt;
    }

    public String getFlightStatus() {
        return flightStatus;
    }

    public void setFlightStatus(String flightStatus) {
        this.flightStatus = flightStatus;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(String arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "id='" + id + '\'' +
                ", airlineCode='" + airlineCode + '\'' +
                ", flightNumber='" + flightNumber + '\'' +
                ", departureApt='" + departureApt + '\'' +
                ", destinationApt='" + destinationApt + '\'' +
                ", flightStatus='" + flightStatus + '\'' +
                ", departureDate='" + departureDate + '\'' +
                ", arrivalDate='" + arrivalDate + '\'' +
                '}';
    }
}
