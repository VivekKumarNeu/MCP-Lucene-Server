package com.example.mcpluceneserver.controller;

import com.example.mcpluceneserver.model.*;
import com.example.mcpluceneserver.service.LuceneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/mcp/v1")
public class McpController {

    private final LuceneService luceneService;

    public McpController(LuceneService luceneService) {
        this.luceneService = luceneService;
    }

    @GetMapping("/status")
    public ResponseEntity<McpStatusResponse> getStatus() {
        McpStatusResponse status = luceneService.getStatus();
        return ResponseEntity.ok(status);
    }


    @PostMapping("/upsert")
    public ResponseEntity<?> upsert(@RequestBody McpUpsertRequest request) {
        if (request.getDocuments() == null || request.getDocuments().isEmpty()) {
            return ResponseEntity.badRequest().body(new McpErrorResponse("invalid_request", "Missing 'documents' field or empty list."));
        }
        try {
            List<String> upsertedIds = luceneService.upsertDocuments(request.getDocuments());
            return ResponseEntity.ok(new McpUpsertResponse(upsertedIds));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new McpErrorResponse("internal_error", "Failed to process upsert request: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new McpErrorResponse("internal_error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/query")
    public ResponseEntity<?> query(@RequestBody McpQueryRequest request) {
        if (request.getQueries() == null || request.getQueries().isEmpty()) {
            return ResponseEntity.badRequest().body(new McpErrorResponse("invalid_request", "Missing 'queries' field or empty list."));
        }
        try {
            List<McpQueryResult> results = luceneService.queryDocuments(request.getQueries());
            return ResponseEntity.ok(new McpQueryResponse(results));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new McpErrorResponse("internal_error", "Failed to process query request: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new McpErrorResponse("internal_error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody McpDeleteRequest request) {
        // Validate input: Should have 'ids' or potentially a 'filter' in a more advanced version
        if (request.getIds() == null /* && request.getFilter() == null */) {
            return ResponseEntity.badRequest().body(new McpErrorResponse("invalid_request", "Request must contain 'ids' field (list of strings)."));
        }
        // For now, only support deletion by ID
        if (request.getIds() != null) {

            try {
                McpDeleteResponse response = luceneService.deleteDocuments(request.getIds());
                return ResponseEntity.ok(response);
            } catch (IOException e) {

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new McpErrorResponse("internal_error", "Failed to process delete request: " + e.getMessage()));
            } catch (Exception e) {

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new McpErrorResponse("internal_error", "An unexpected error occurred: " + e.getMessage()));
            }
        } else {
            // Handle filter-based deletion if implemented
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(new McpErrorResponse("not_implemented", "Deletion by filter is not yet supported."));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> listDocuments(@RequestParam(defaultValue = "10") int limit,
                                          @RequestParam(defaultValue = "0") int offset) {
        try {
            List<McpDocument> documents = luceneService.listDocuments(limit, offset);
            return ResponseEntity.ok(documents);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new McpErrorResponse("internal_error", "Failed to list documents: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new McpErrorResponse("internal_error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

}