import { Server } from "@modelcontextprotocol/sdk/server/index.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import { ListToolsRequestSchema, CallToolRequestSchema } from "@modelcontextprotocol/sdk/types.js";
import axios from "axios";

const LUCENE_BASE_URL = process.env.LUCENE_BASE_URL || "http://localhost:8080/mcp/v1";
const FORCE_TEXT = process.env.MCP_FORCE_TEXT === "1" || process.env.MCP_FORCE_TEXT === "true";

function toMcpContent(res) {
  const contentType = res.headers?.["content-type"] || "";
  const isJson = contentType.includes("application/json");
  const body = res.data;
  // Debug to stderr for troubleshooting
  console.error("[shim] content-type:", contentType, "type:", typeof body, "forceText:", FORCE_TEXT);
  const pretty = typeof body === "string" ? body : JSON.stringify(body, null, 2);
  if (FORCE_TEXT) {
    return { content: [{ type: "text", text: pretty }] };
  }
  if (isJson || typeof body === "object") {
    return {
      content: [
        { type: "json", json: body },
        { type: "text", text: pretty }
      ]
    };
  }
  return { content: [{ type: "text", text: pretty }] };
}

const tools = [
  {
    name: "lucene_status",
    description: "Get status of the Lucene MCP server (index path, doc count).",
    inputSchema: { type: "object", properties: {} }
  },
  {
    name: "lucene_upsert",
    description: "Upsert documents into Lucene index.",
    inputSchema: {
      type: "object",
      required: ["documents"],
      properties: {
        documents: {
          type: "array",
          items: {
            type: "object",
            required: ["id", "text"],
            properties: {
              id: { type: "string" },
              text: { type: "string" },
              metadata: { type: "object", additionalProperties: { type: "string" } }
            }
          }
        }
      }
    }
  },
  {
    name: "lucene_query",
    description: "Query Lucene index with optional metadata filter and top_k.",
    inputSchema: {
      type: "object",
      required: ["queries"],
      properties: {
        queries: {
          type: "array",
          items: {
            type: "object",
            required: ["query"],
            properties: {
              query: { type: "string" },
              top_k: { type: "number" },
              filter: { type: "object", additionalProperties: { type: "string" } }
            }
          }
        }
      }
    }
  },
  {
    name: "lucene_delete",
    description: "Delete documents by ids.",
    inputSchema: {
      type: "object",
      required: ["ids"],
      properties: {
        ids: { type: "array", items: { type: "string" } }
      }
    }
  },
  {
    name: "lucene_list",
    description: "List documents with pagination (limit, offset).",
    inputSchema: {
      type: "object",
      properties: {
        limit: { type: "number", default: 10 },
        offset: { type: "number", default: 0 }
      }
    }
  }
];

const server = new Server(
  { name: "mcp-lucene-shim", version: "0.1.0" },
  { capabilities: { tools: {} } }
);

server.setRequestHandler(ListToolsRequestSchema, async () => ({
  tools: tools.map(t => ({ name: t.name, description: t.description, inputSchema: t.inputSchema }))
}));

server.setRequestHandler(CallToolRequestSchema, async (req) => {
  const { name, arguments: args } = req.params;
  try {
    switch (name) {
      case "lucene_status": {
        const res = await axios.get(`${LUCENE_BASE_URL}/status`);
        return toMcpContent(res);
      }
      case "lucene_upsert": {
        const res = await axios.post(`${LUCENE_BASE_URL}/upsert`, args);
        return toMcpContent(res);
      }
      case "lucene_query": {
        const res = await axios.post(`${LUCENE_BASE_URL}/query`, args);
        return toMcpContent(res);
      }
      case "lucene_delete": {
        const res = await axios.post(`${LUCENE_BASE_URL}/delete`, args);
        return toMcpContent(res);
      }
      case "lucene_list": {
        const limit = args?.limit ?? 10;
        const offset = args?.offset ?? 0;
        const res = await axios.get(`${LUCENE_BASE_URL}/list`, { params: { limit, offset } });
        return toMcpContent(res);
      }
      default:
        throw new Error(`Unknown tool: ${name}`);
    }
  } catch (err) {
    const status = err?.response?.status;
    const data = err?.response?.data;
    console.error("[shim] error status:", status, "payload type:", typeof data);
    if (FORCE_TEXT) {
      const text = typeof data === "string" ? data : JSON.stringify(data ?? { error: err?.message || "Unknown error" }, null, 2);
      return { content: [{ type: "text", text }] };
    }
    if (typeof data === "object") {
      return { content: [{ type: "json", json: data }] };
    }
    const text = typeof data === "string" ? data : (err?.message || "Unknown error");
    return { content: [{ type: "text", text }] };
  }
});

const transport = new StdioServerTransport();
await server.connect(transport);
console.error("mcp-lucene-shim running (STDIO), forwarding to:", LUCENE_BASE_URL, "forceText:", FORCE_TEXT);
