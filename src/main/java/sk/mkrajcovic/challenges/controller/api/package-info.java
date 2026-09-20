/**
 * Contains HTTP API contract interfaces with OpenAPI (Swagger) documentation.
 * <p>
 * Controllers implement these interfaces. The interfaces centralize OpenAPI
 * annotations and endpoint-level validation constraints, while controllers
 * retain Spring MVC mappings, authorization rules, and request handling.
 * <p>
 * This separation keeps controller implementations focused on delegating HTTP
 * requests to application services while keeping the API contract and its
 * documentation maintainable in one place.
 */
package sk.mkrajcovic.challenges.controller.api;
