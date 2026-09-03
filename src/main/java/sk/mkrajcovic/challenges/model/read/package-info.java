/**
 * Read-only models used to represent data returned by read-oriented use cases.
 * 
 * <p>
 * Types in this package are part of the model layer and intentionally remain
 * independent of both the persistence and controller layers. They represent the
 * data required by the application for reading purposes without carrying entity
 * lifecycle, persistence-context, or HTTP-specific concerns.
 *
 * <p>
 * These models are particularly useful for read operations where loading a full
 * JPA entity would be unnecessary. A persistence adapter may populate these
 * models using a lightweight projection, avoiding unnecessary entity
 * materialization, proxies, dirty tracking, and persistence-context management.
 *
 * <p>
 * Read models are deliberately kept separate from both:
 * <ul>
 * 	<li>domain entities, which represent persistent domain objects and their life-cycle</li>
 * 	<li>controller response DTOs, which represent the external HTTP API contract.</li>
 * </ul>
 *
 * <p>
 * This separation ensures that <b>changes to a persistence query or its projection
 * do not inherently change the HTTP API contract</b>. Likewise, changes to the HTTP
 * representation do not require changes to the persistence model. The
 * controller is responsible for explicitly mapping read models to API response
 * DTOs.
 */
package sk.mkrajcovic.challenges.model.read;
