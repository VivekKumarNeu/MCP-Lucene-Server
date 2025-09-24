package com.example.mcpluceneserver.service;

import com.example.mcpluceneserver.config.LuceneProperties;
import com.example.mcpluceneserver.model.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LuceneService {

    private static final Logger log = LoggerFactory.getLogger(LuceneService.class);

    // --- Constants for Lucene Field Names ---
    private static final String FIELD_ID = "mcp_id";
    private static final String FIELD_CONTENT = "content";
    private static final String FIELD_METADATA_PREFIX = "meta_";
    // ----------------------------------------

    private final LuceneProperties luceneProperties;
    private Directory indexDirectory;
    private StandardAnalyzer analyzer;
    private IndexWriter indexWriter;
    private SearcherManager searcherManager;


    public LuceneService(LuceneProperties luceneProperties) {
        this.luceneProperties = luceneProperties;
    }

    @PostConstruct
    public void initialize() throws IOException {
        try {
            analyzer = new StandardAnalyzer();
            indexDirectory = FSDirectory.open(Paths.get(luceneProperties.getPath()));

            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND); // Create if not exists, append if exists

            indexWriter = new IndexWriter(indexDirectory, config);
            indexWriter.commit();

            searcherManager = new SearcherManager(indexWriter, true, true, null);

            logCurrentDocCount();

        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Lucene", e);
        }
    }

    private void logCurrentDocCount() {
        IndexSearcher searcher = null;
        try {
            searcherManager.maybeRefresh(); // Ensure we have the latest reader
            searcher = searcherManager.acquire();
        } catch (IOException e) {
            log.error("Error getting document count: {}", e.getMessage());
        } finally {
            if (searcher != null) {
                try {
                    searcherManager.release(searcher);
                } catch (IOException e) {
                    log.error("Error releasing searcher: {}", e.getMessage());
                }
            }
        }
    }


    @PreDestroy
    public void close() {
        System.out.println("Closing Lucene resources...");
        try {
            if (searcherManager != null) {
                searcherManager.close();
                System.out.println("SearcherManager closed.");
            }
            if (indexWriter != null && indexWriter.isOpen()) {
                indexWriter.commit(); // Commit final changes
                indexWriter.close();
                System.out.println("IndexWriter closed.");
            }
            if (analyzer != null) {
                analyzer.close();
                System.out.println("Analyzer closed.");
            }
            if (indexDirectory != null) {
                indexDirectory.close();
                System.out.println("Index directory closed.");
            }
        } catch (IOException e) {
            System.out.println("Error closing Lucene resources:" + e.getMessage());
        }
    }

    // --- MCP Operations ---

    public List<String> upsertDocuments(List<McpDocument> mcpDocuments) throws IOException {
        List<String> upsertedIds = new ArrayList<>();
        for (McpDocument mcpDoc : mcpDocuments) {
            if (mcpDoc.getId() == null || mcpDoc.getId().isBlank()) {
                continue;
            }
            if (mcpDoc.getText() == null) {
                continue;
            }

            Document luceneDoc = convertToLuceneDoc(mcpDoc);
            Term idTerm = new Term(FIELD_ID, mcpDoc.getId());

            indexWriter.updateDocument(idTerm, luceneDoc);
            upsertedIds.add(mcpDoc.getId());
        }

        // Commit changes to make them visible for searching
        // Consider batching commits for high throughput scenarios
        indexWriter.commit();
        searcherManager.maybeRefresh(); // Make changes visible to searchers

        System.out.println("Committed upsert operation for documents." + upsertedIds.size());
        return upsertedIds;
    }

    public List<McpQueryResult> queryDocuments(List<McpQuery> mcpQueries) throws IOException {
        List<McpQueryResult> resultsList = new ArrayList<>();
        IndexSearcher searcher = null;
        try {
            searcherManager.maybeRefresh(); // Ensure searcher sees latest changes
            searcher = searcherManager.acquire();
            QueryParser defaultParser = new QueryParser(FIELD_CONTENT, analyzer); // Default search field

            for (McpQuery mcpQuery : mcpQueries) {
                BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
                Query mainQuery;

                try {
                    mainQuery = defaultParser.parse(mcpQuery.getQuery());
                    booleanQueryBuilder.add(mainQuery, BooleanClause.Occur.MUST); // Main query is mandatory
                } catch (ParseException e) {
                    resultsList.add(new McpQueryResult(new ArrayList<>()));
                    continue; // Skip to the next query
                }

                // Handle filtering
                if (mcpQuery.getFilter() != null && !mcpQuery.getFilter().isEmpty()) {
                    for (Map.Entry<String, String> entry : mcpQuery.getFilter().entrySet()) {
                        String fieldName = FIELD_METADATA_PREFIX + entry.getKey();
                        String value = entry.getValue();
                        // Assuming exact match for filter for now.
                        // Consider using TermRangeQuery or other query types for more complex filters.
                        Query filterQuery = new TermQuery(new Term(fieldName, value));
                        booleanQueryBuilder.add(filterQuery, BooleanClause.Occur.FILTER); // Use FILTER for non-scoring constraints
                    }
                }

                Query finalQuery = booleanQueryBuilder.build();
                TopDocs topDocs = searcher.search(finalQuery, mcpQuery.getTop_k());

                List<McpQueryResult.Result> queryResults = new ArrayList<>();
                for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                    Document hitDoc = searcher.doc(scoreDoc.doc);
                    McpQueryResult.Result result = convertToMcpResult(hitDoc, scoreDoc.score);
                    queryResults.add(result);
                }
                resultsList.add(new McpQueryResult(queryResults));

            }
        } finally {
            if (searcher != null) {
                searcherManager.release(searcher);
            }
        }
        return resultsList;
    }

    public McpDeleteResponse deleteDocuments(List<String> ids) throws IOException {
        if (ids == null || ids.isEmpty()) {
            return new McpDeleteResponse(true, 0); // Nothing to delete
        }

        Term[] idTerms = ids.stream()
                .filter(id -> id != null && !id.isBlank())
                .map(id -> new Term(FIELD_ID, id))
                .toArray(Term[]::new);

        if (idTerms.length == 0) {
            return new McpDeleteResponse(true, 0);
        }

        indexWriter.deleteDocuments(idTerms);
        indexWriter.commit();
        searcherManager.maybeRefresh();

        System.out.println("Committed delete operation for IDs." + idTerms.length);
        return new McpDeleteResponse(true, idTerms.length);
    }

    public List<McpDocument> listDocuments(int limit, int offset) throws IOException {
        List<McpDocument> documents = new ArrayList<>();
        IndexSearcher searcher = null;
        
        try {
            searcherManager.maybeRefresh();
            searcher = searcherManager.acquire();
            
            // Create a match all query to get all documents
            Query matchAllQuery = new MatchAllDocsQuery();
            TopDocs topDocs = searcher.search(matchAllQuery, offset + limit);
            
            // Apply offset and limit
            int startIndex = Math.min(offset, topDocs.scoreDocs.length);
            int endIndex = Math.min(offset + limit, topDocs.scoreDocs.length);
            
            for (int i = startIndex; i < endIndex; i++) {
                ScoreDoc scoreDoc = topDocs.scoreDocs[i];
                Document hitDoc = searcher.doc(scoreDoc.doc);
                McpDocument mcpDoc = convertToMcpDocument(hitDoc);
                documents.add(mcpDoc);
            }
            
        } finally {
            if (searcher != null) {
                searcherManager.release(searcher);
            }
        }
        
        return documents;
    }

    public McpStatusResponse getStatus() {
        IndexSearcher searcher = null;
        long docCount = -1;
        try {
            searcherManager.maybeRefresh();
            searcher = searcherManager.acquire();
            docCount = searcher.getIndexReader().numDocs();
        } catch (IOException e) {
            System.out.println("Error" + e.getMessage());
        } finally {
            if (searcher != null) {
                try {
                    searcherManager.release(searcher);
                } catch (IOException e) {
                    System.out.println("Error" + e.getMessage());
                }
            }
        }

        Map<String, Object> details = new HashMap<>();
        details.put("lucene_version", org.apache.lucene.util.Version.LATEST.toString());
        details.put("analyzer", analyzer.getClass().getName());

        return new McpStatusResponse(
                "OK",
                luceneProperties.getPath(),
                docCount,
                details
        );
    }



    // --- Conversion Helpers ---

    private Document convertToLuceneDoc(McpDocument mcpDoc) {
        Document doc = new Document();

        doc.add(new StringField(FIELD_ID, mcpDoc.getId(), Field.Store.YES));

        doc.add(new TextField(FIELD_CONTENT, mcpDoc.getText(), Field.Store.YES));

        // Metadata Fields: Store and index metadata values.
        // Choose Field type based on needs:
        // - StringField: For exact matches, filtering, sorting (not tokenized)
        // - TextField: For full-text search within metadata values (tokenized)
        // - StoredField: Store only, not indexed/searchable.
        if (mcpDoc.getMetadata() != null) {
            for (Map.Entry<String, String> entry : mcpDoc.getMetadata().entrySet()) {
                String fieldName = FIELD_METADATA_PREFIX + entry.getKey();
                String value = entry.getValue();
                if (value != null) {
                    doc.add(new StringField(fieldName, value, Field.Store.YES));
                }
            }
        }
        return doc;
    }

    private McpQueryResult.Result convertToMcpResult(Document luceneDoc, float score) {
        McpQueryResult.Result result = new McpQueryResult.Result();
        result.setId(luceneDoc.get(FIELD_ID));
        result.setText(luceneDoc.get(FIELD_CONTENT)); // Assumes content is stored
        result.setScore(score);

        Map<String, String> metadata = new HashMap<>();
        for (IndexableField field : luceneDoc.getFields()) {
            if (field.name().startsWith(FIELD_METADATA_PREFIX)) {
                String metaKey = field.name().substring(FIELD_METADATA_PREFIX.length());
                metadata.put(metaKey, field.stringValue());
            }
        }
        result.setMetadata(metadata);

        return result;
    }

    private McpDocument convertToMcpDocument(Document luceneDoc) {
        McpDocument mcpDoc = new McpDocument();
        mcpDoc.setId(luceneDoc.get(FIELD_ID));
        mcpDoc.setText(luceneDoc.get(FIELD_CONTENT));

        Map<String, String> metadata = new HashMap<>();
        for (IndexableField field : luceneDoc.getFields()) {
            if (field.name().startsWith(FIELD_METADATA_PREFIX)) {
                String metaKey = field.name().substring(FIELD_METADATA_PREFIX.length());
                metadata.put(metaKey, field.stringValue());
            }
        }
        mcpDoc.setMetadata(metadata);

        return mcpDoc;
    }
}