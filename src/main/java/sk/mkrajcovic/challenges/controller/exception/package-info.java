/**
 * Exception handling for the HTTP/controller layer.
 * <p>
 * The handler hierarchy separates Spring MVC validation handling from
 * application-level exception handling while keeping a single
 * {@code @ControllerAdvice} entry point. This keeps the concerns isolated
 * without introducing multiple competing exception-handler beans.
 * <p>
 * Application exception definitions remain in the parent {@code exception}
 * package.
 */
package sk.mkrajcovic.challenges.controller.exception;
