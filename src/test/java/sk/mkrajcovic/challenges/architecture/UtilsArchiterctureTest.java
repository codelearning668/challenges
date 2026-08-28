package sk.mkrajcovic.challenges.architecture;

import static sk.mkrajcovic.challenges.architecture.ArchitectureTestUtil.CLASSES;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

class UtilsArchiterctureTest {

	private final String utilsPackages = "..util..";

	@Test
	@DisplayName("Utility triedy by nemali byt Springove Beany")
	// prevents “god utils” that secretly become services
	void utilsShouldNotBeSpringBeans() {
		var archRule = ArchRuleDefinition.classes()
			.that().resideInAPackage(utilsPackages)
			.should().notBeAnnotatedWith("org.springframework.stereotype.Component")
			.andShould().notBeAnnotatedWith("org.springframework.stereotype.Service")
			.andShould().notBeAnnotatedWith("org.springframework.stereotype.Repository");

		archRule.check(CLASSES);
	}

	@Test
	@DisplayName("Utility triedy by nemali zavisiet od controller, service vrstvy - repository je docasne povolena")
	void utilsShouldNotDependOnApplicationLayers() {
	    var archRule = ArchRuleDefinition.noClasses()
	        .that().resideInAPackage(utilsPackages)
	        .should().dependOnClassesThat()
	        .resideInAnyPackage(
	            "..service..",
				/*
				 * In some cases, we intentionally allow passing repository instances
				 * into utility methods to extract repetitive null checks and validations.
				 * This helps keep service methods cleaner and avoids duplicating logic,
				 * even though it violate strict layering rules.
				 *
				 * In case of working with top level interfaces only (like CrudRepository),
				 * such utility is fine, but it should throw what the caller decides,
				 * and your system should translate that to HTTP at the edge of the app,
				 * not inside it.
				 */
//	            "..repository..",
	            "..controller.."
	        );

	    archRule.check(CLASSES);
	}

	@Test
	@DisplayName("Utils balik, by nemal definovat domenove 'Exceptions'")
	void utilsShouldNotDeclareExceptions() {
		var archRule = ArchRuleDefinition
			.noClasses().that().resideInAPackage(utilsPackages)
			.should().haveSimpleNameEndingWith("Exception");

		archRule.check(CLASSES);
	}

	@Nested
	@DisplayName("Testy funkcii na side-effekty")
	class PureFunctionsTest {

		@Test
		@DisplayName("Utility triedy by nemali modifikovat svoj interny stav")
		void utilsShouldNotModifyState() {
			var archRule = ArchRuleDefinition
				.classes().that().resideInAPackage(utilsPackages)
				.and().areTopLevelClasses()
				.should().haveOnlyFinalFields();

			archRule.check(CLASSES);
		}

		@Test
//		@Disabled
		@DisplayName("Utility triedy by nemali logovat -> ma to robit service")
		void utilsShouldNotUseLogging() {
			var archRule = ArchRuleDefinition.noClasses()
				.that().resideInAPackage(utilsPackages)
				.should().dependOnClassesThat()
				.resideInAnyPackage("org.slf4j..", "ch.qos.logback..");
	
			archRule.check(CLASSES);
		}
	}
}
