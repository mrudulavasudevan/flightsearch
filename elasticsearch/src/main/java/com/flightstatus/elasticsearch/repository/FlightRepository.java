package com.flightstatus.elasticsearch.repository;

import com.flightstatus.elasticsearch.document.Flight;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface FlightRepository extends ElasticsearchRepository<Flight, String> {
}
