package com.flightstatus.elasticsearch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightstatus.elasticsearch.document.Flight;
import com.flightstatus.elasticsearch.repository.FlightRepository;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class FlightService {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(IndexService.class);

    private final RestHighLevelClient client;
    private final FlightRepository repository;

    @Autowired
    public FlightService(RestHighLevelClient client, FlightRepository repository) {
        this.client = client;
        this.repository = repository;
    }

    public Boolean index(final Flight flight) {
        LOG.info("Saving the flight details :::: "+flight.toString());
        try {
            final String flightAsString = MAPPER.writeValueAsString(flight);
            IndexRequest indexRequest = new IndexRequest("flight");
            indexRequest.id(flight.getId());
            indexRequest.source(flightAsString, XContentType.JSON);
            IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
            return indexResponse != null && (indexResponse.status().equals(RestStatus.OK));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public Flight findById(final String id) {
        return (Flight) repository.findById(id).orElse(null);
    }

    public Flight getById(final String flightId) {
        try {
            final GetResponse documentFields = client.get(
                    new GetRequest("flight", flightId),
                    RequestOptions.DEFAULT
            );
            if (documentFields == null || documentFields.isSourceEmpty()) {
                return null;
            }
            LOG.info("Return value :::: "+documentFields.getSourceAsString());
            return MAPPER.readValue(documentFields.getSourceAsString(), Flight.class);
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public List<Flight> findFlightStatus(String airlineCode, String flightNumber, Date flightDate) {

        final QueryBuilder airlineCodeQuery = QueryBuilders.matchQuery("airlineCode", airlineCode);
        final QueryBuilder flightNumberQuery = QueryBuilders.matchQuery("flightNumber", flightNumber);
        final QueryBuilder flightDateQuery = QueryBuilders.rangeQuery("departureDate").gte(flightDate);

        BoolQueryBuilder boolQueryBuilder =QueryBuilders.boolQuery()
                .must(airlineCodeQuery)
                .must(flightNumberQuery)
                .must(flightDateQuery);
        SearchSourceBuilder builder = new SearchSourceBuilder()
                .postFilter(boolQueryBuilder);
        final SearchRequest request = new SearchRequest("flight");
        request.source(builder);
        if (request == null) {
            LOG.error("Failed to build search request");
            return Collections.emptyList();
        }

        try {
            final SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            final SearchHit[] searchHits = response.getHits().getHits();
            final List<Flight> flights = new ArrayList<>(searchHits.length);
            for (SearchHit hit : searchHits) {
                flights.add(
                        MAPPER.readValue(hit.getSourceAsString(), Flight.class)
                );
            }

            return flights;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
