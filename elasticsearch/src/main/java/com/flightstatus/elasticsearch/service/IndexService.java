package com.flightstatus.elasticsearch.service;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Service
public class IndexService {
    public static final String FLIGHT_INDEX = "flight";

    private static final Logger LOG = LoggerFactory.getLogger(IndexService.class);
    private static final List<String> INDICES;

    static {
        INDICES = new ArrayList<String>();
        INDICES.add(FLIGHT_INDEX);
    }

    private final RestHighLevelClient client;

    @Autowired
    public IndexService(RestHighLevelClient client) {
        this.client = client;
    }

    @PostConstruct
    public void tryToCreateIndices() {
        recreateIndices(false);
    }

    public void recreateIndices(final boolean deleteExisting) {

        String settings = null;
        try {
            //final File resource = new ClassPathResource("static/settings.json").getFile();
            //settings = new String(Files.readAllBytes(resource.toPath()));
            ClassPathResource cpr = new ClassPathResource("static/settings.json");
            byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
            settings = new String(bdata, StandardCharsets.UTF_8);
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
        }
        if (settings == null) {
            LOG.error("Failed to load index settings");
            return;
        }

        for (final String indexName : INDICES) {
            try {
                final boolean indexExists = client
                        .indices()
                        .exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT);
                if (indexExists) {
                    if (!deleteExisting) {
                        continue;
                    }
                    client.indices().delete(
                            new DeleteIndexRequest(indexName),
                            RequestOptions.DEFAULT
                    );
                }

                final CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
                createIndexRequest.settings(settings, XContentType.JSON);

                final String mappings = loadMappings(indexName);
                if (mappings != null) {
                    createIndexRequest.mapping(mappings, XContentType.JSON);
                }

                client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            } catch (final Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    private String loadMappings(String indexName) {
        String mappings = null;
        try {
            //final File resource = new ClassPathResource("../../../../resources/static/mappings/" + indexName + ".json", IndexService.class).getFile();
            //mappings = new String(Files.readAllBytes(resource.toPath()));
            ClassPathResource cpr = new ClassPathResource("static/mappings/" +indexName+".json");
            byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
            mappings = new String(bdata, StandardCharsets.UTF_8);
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
        }
        if (mappings == null) {
            LOG.error("Failed to load mappings for index with name '{}'", indexName);
            return null;
        }

        return mappings;
    }
}
