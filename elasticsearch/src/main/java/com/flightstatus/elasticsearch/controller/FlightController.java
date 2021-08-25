package com.flightstatus.elasticsearch.controller;

import com.flightstatus.elasticsearch.document.Flight;
import com.flightstatus.elasticsearch.service.FlightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/flight")
public class FlightController {

    private final static Logger LOGGER = LoggerFactory.getLogger(FlightController.class);
    private final FlightService flightService;

    @Autowired
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PostMapping
    public void index(@RequestBody final Flight flight) {
        flightService.index(flight);
    }

    @GetMapping("/{id}")
    public Flight findById(@PathVariable final String id) {
        LOGGER.info("********* FlightController calling getById :: "+id);
        return flightService.getById(id);
    }

    @GetMapping("/flightstatus/{airlineCode}/{flightNumber}/{flightDate}")
    public List<Flight> findFlightStatus(
            @PathVariable String airlineCode,
            @PathVariable String flightNumber,
            @PathVariable String flightDate) {
        LOGGER.info("airlineCode ::: "+airlineCode+ " flightNumber :::"+flightNumber+ "flightDate :::"+flightDate);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date departure = null;
        try {
            departure = df.parse(flightDate);
            System.out.println("depDate 1:::: "+departure.getTime());
        }catch (ParseException pe) {
        }
        return flightService.findFlightStatus(airlineCode, flightNumber, departure);
    }

}
