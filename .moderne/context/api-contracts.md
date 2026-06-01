# Api Contracts

## Endpoint contracts, DTO schemas, parameters, exception handlers, and fixture examples

Complete raw material for generating OpenAPI 3.0.3 specs and consumer/provider contract tests deterministically. Five CSVs: endpoint-request-response-schemas.csv (one row per endpoint*status, with request body FQN, response body FQN, collection flag), endpoint-parameters.csv (path/query/header/form params), dto-field-schemas.csv (per-field wire name, type, format, required flag, validation JSON, @Schema example), exception-handlers.csv (@ControllerAdvice + controller-local exception->status->body mappings), and field-example-values.csv (raw jsonPath->value rows mined from src/test/resources fixtures). Join endpoint tables via the endpointId column to service-endpoints.csv, and DTO tables via class FQN to data-assets.csv.

## Data Tables

### Endpoint request/response schemas

**File:** [`endpoint-schemas.csv`](endpoint-schemas.csv)

Per-endpoint request body and response body bindings, one row per (endpoint, status code) pair. Supports OpenAPI 3.0.3 generation by giving the LLM a full mapping from handler to body DTO FQNs.

| Column | Description |
|--------|-------------|
| Endpoint ID | Join key matching the 'Entity ID' column of service-endpoints.csv. |
| Source path | The path to the source file containing the handler. |
| Service class | The fully qualified name of the controller/resource class. |
| Method name | The handler method name. |
| HTTP method | The HTTP method (GET, POST, etc.). |
| Path | The full request path including class-level prefix. |
| Request body FQN | Fully qualified name of the request body DTO class, or null when the handler takes no body. |
| Request body resolution | How the request body type was resolved: 'resolved' (FQN known), 'simple-name' (only the class simple name could be recovered), 'unresolved' (could not be determined), or 'none' (no request body). |
| Request body required | Whether the request body is required (false only when @RequestBody(required=false)). |
| Response status | HTTP status code for this response row (e.g. 200, 201, 400). |
| Response body FQN | Fully qualified name of the response body DTO for this status, after unwrapping ResponseEntity/Mono/Optional/collection wrappers. |
| Response body resolution | How the response body type was resolved: 'resolved' (FQN known), 'simple-name', 'unresolved', or 'none' (void return / no body). |
| Response is collection | True when the response body is a collection type (List/Set/Page/Slice/array). |
| Response source | Where this response row was derived: 'return-type', '@ResponseStatus', or '@ApiResponse'. |
| Framework | The framework hosting the endpoint (Spring, JAX-RS, Micronaut). |

### Endpoint parameters

**File:** [`endpoint-parameters.csv`](endpoint-parameters.csv)

Per-parameter detail for REST endpoint handlers - path, query, header, form. Join to service-endpoints.csv via endpointId.

| Column | Description |
|--------|-------------|
| Endpoint ID | Join key matching the 'Entity ID' column of service-endpoints.csv. |
| Parameter name | The wire-level parameter name (from @PathVariable(name=...), @RequestParam(name=...), etc.). |
| Parameter kind | Where the parameter comes from: 'path', 'query', 'header', or 'form'. |
| Type FQN | Fully qualified name of the parameter's Java type. |
| Type resolution | How the parameter type was resolved: 'resolved' (FQN known), 'simple-name', or 'unresolved'. |
| Format | OpenAPI 3.0.3 format (int32, int64, date-time, uuid, etc.) or null. |
| Required | Whether the parameter is required. |
| Default value | Default value if declared (e.g. @RequestParam(defaultValue = "0")), else null. |
| Java parameter name | The original Java parameter name (may differ from wire name). |

### DTO field schemas

**File:** [`dto-field-schemas.csv`](dto-field-schemas.csv)

Per-field schema detail for request/response DTOs: wire name, type, required flag, OpenAPI format, validation constraints, and any @Schema(example=) example values.

| Column | Description |
|--------|-------------|
| Source path | The path to the source file containing the DTO class. |
| Owner class FQN | Fully qualified name of the DTO class owning this field. Joins to data-assets.csv via 'Class name'. |
| Field name | The Java field name. |
| Serialized name | The JSON name on the wire, after applying @JsonProperty overrides and class-level @JsonNaming. |
| Serialized name source | How the serialized name was derived: 'java-name' (no override), 'json-property' (@JsonProperty value), 'jackson-strategy' (recognized @JsonNaming applied), or 'unknown-strategy' (@JsonNaming present but strategy class isn't a known one - the Java field name is used as a best-effort fallback). |
| Type FQN | Fully qualified name of the field's type (after unwrapping Optional/Collection wrappers). |
| Type resolution | How confidently the type was resolved: 'resolved' (FQN known), 'simple-name' (only the class simple name could be recovered, may not uniquely identify the DTO), or 'unresolved' (the type could not be determined at all). |
| Is collection | True when the declared type is a collection (List/Set/array/etc.) - the type FQN is then the element type. |
| Is map | True when the declared type is a Map - the type FQN is then the value type. |
| Is optional | True when the declared type is java.util.Optional. |
| Format | OpenAPI 3.0.3 format (int32, int64, date-time, uuid, email, binary, ...) or null. |
| Required | Whether the field is required. Set true for @NotNull/@NotBlank/@NotEmpty, primitives, and @JsonProperty(required=true). |
| Validations JSON | JSON map of validation constraint simple name to argument map. Example: {"NotNull":{},"Size":{"min":1,"max":100}}. |
| Example value | Example value from @Schema(example = "...") if declared on the field, else null. |

### Field example values

**File:** [`field-examples.csv`](field-examples.csv)

Raw (fixturePath, jsonPath, value, valueType) rows mined from JSON fixture files. Supply realistic example payloads for contract test generation. LLM correlates jsonPath to DTO fields at spec/contract generation time.

| Column | Description |
|--------|-------------|
| Fixture path | Relative path to the fixture file. |
| JSON path | Dotted path to the leaf inside the fixture (e.g. 'user.address.street'). |
| Value | The literal value at that path, rendered as a string. |
| Value type | JSON type: 'string', 'number', 'boolean', 'null', 'array', or 'object'. |

