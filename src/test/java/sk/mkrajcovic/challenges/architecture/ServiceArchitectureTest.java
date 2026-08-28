package sk.mkrajcovic.challenges.architecture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static sk.mkrajcovic.challenges.architecture.ArchitectureTestUtil.CLASSES;
import static sk.mkrajcovic.challenges.architecture.ArchitectureTestUtil.createNoFieldInjectionRuleFor;
import static sk.mkrajcovic.challenges.architecture.ArchitectureTestUtil.createRuleForClassesWithNameEnding;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

class ServiceArchitectureTest {

	private final String serviceLayer = "Service";
	private final String servicePackages = "..service..";
	private final String repositoryPackages = "..repository..";

	@Test
	@DisplayName("Triedy v service vrstve by mali mať názov končiaci na 'Service'")
	void serviceNamingConvention() {
		var archRule = createRuleForClassesWithNameEnding(servicePackages, serviceLayer);
	    assertDoesNotThrow(() -> archRule.check(CLASSES));
	}

	@Test
	@DisplayName("Service vrstva nesmie závisieť od controller vrstvy")
	void servicesShouldNotDependOnControllers() {
	    var archRrule = ArchRuleDefinition.noClasses()
	            .that().resideInAPackage(servicePackages)
	            .should().dependOnClassesThat().resideInAPackage("..controller..");

	    assertDoesNotThrow(() -> archRrule.check(CLASSES));
	}

	@Test
	@DisplayName("Service vrstva by nemala byť používaná repozitármi ani infraštruktúrou")
	void servicesShouldNotBeAccessedByRepositories() {
	    var archRule = ArchRuleDefinition.noClasses()
	            .that().resideInAPackage(repositoryPackages)
	            .should().accessClassesThat().resideInAPackage(servicePackages);

	    assertDoesNotThrow(() -> archRule.check(CLASSES));
	}

	@Test
	@DisplayName("Service vrstva by nemala používať field injection")
	void servicesShouldNotUseFieldInjection() {
		var archRule = createNoFieldInjectionRuleFor(servicePackages);
	    assertDoesNotThrow(() -> archRule.check(CLASSES));
	}

	@Test
	@DisplayName("Service triedy by nemali obsahovať stav okrem závislostí")
	void servicesShouldBeStateless() {

		// TODO: extract this
		ArchCondition<JavaField> beFinalOrAutowiredSetterInjected = new ArchCondition<>("be final or injected by @Autowired setter") {

			@Override
			public void check(JavaField field, ConditionEvents events) {
				// skip compiler-generated synthetic fields (e.g. $SWITCH_TABLE$... from enum switches)
	            if (field.getName().startsWith("$")) {
	                return;
	            }

				boolean isFinal = field.getModifiers().contains(JavaModifier.FINAL);

				boolean injectedByAutowiredSetter = field.getOwner().getMethods().stream()
						.filter(method -> method.isAnnotatedWith(Autowired.class))
						.filter(method -> method.getName().startsWith("set")).anyMatch(method -> method
								.getRawParameterTypes().stream().anyMatch(type -> type.equals(field.getRawType())));

				boolean satisfied = isFinal || injectedByAutowiredSetter;

				String message = String.format("Field %s in %s should be final or injected via @Autowired setter",
						field.getName(), field.getOwner().getName());

				events.add(new SimpleConditionEvent(field, satisfied, message));
			}
		};

		var archRule = ArchRuleDefinition
			.fields().that().areDeclaredInClassesThat().areTopLevelClasses()
			.and().areDeclaredInClassesThat().resideInAPackage(servicePackages)
			.should(beFinalOrAutowiredSetterInjected);

		assertDoesNotThrow(() -> archRule.check(CLASSES));
	}
}
