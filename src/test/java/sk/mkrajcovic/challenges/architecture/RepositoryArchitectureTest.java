package sk.mkrajcovic.challenges.architecture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static sk.mkrajcovic.challenges.architecture.ArchitectureTestUtil.CLASSES;
import static sk.mkrajcovic.challenges.architecture.ArchitectureTestUtil.createNoFieldInjectionRuleFor;
import static sk.mkrajcovic.challenges.architecture.ArchitectureTestUtil.createRuleForClassesWithNameEnding;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

class RepositoryArchitectureTest {

	private final String repositoryLayer = "Repository";
	private final String repositoryPackages = "..repository.persistence..";
	private final String servicePackages = "..service..";
	private final String controllerPackages = "..controller..";

	@Test
	@DisplayName("Triedy v repository.persistence vrstve by mali mat nazov konciaci na 'Repository'")
	void repositoryNamingConvention() {
		var archRule = createRuleForClassesWithNameEnding(repositoryPackages, repositoryLayer);
		assertDoesNotThrow(() -> archRule.check(CLASSES));
	}

	@Test
	@DisplayName("Repository triedy musia byt umiestnene v repository baliku")
	void repositoriesShouldResideInRepositoryPackage() {
		var archRule = ArchRuleDefinition
				.classes().that().haveNameMatching(".*Repository")
				.should().resideInAPackage(repositoryPackages);

		assertDoesNotThrow(() -> archRule.check(CLASSES));
	}

	@Test
	@DisplayName("Controller vrstva nesmie pouzivat repository priamo")
	void controllersShouldNotAccessRepositories() {
		var archRule = ArchRuleDefinition
				.noClasses().that().resideInAPackage(controllerPackages)
				.should().accessClassesThat().resideInAPackage(repositoryPackages);

		assertDoesNotThrow(() -> archRule.check(CLASSES));
	}

	@Test
	@DisplayName("Repository vrstva by nemala zavisiet od service vrstvy")
	void repositoriesShouldNotDependOnServices() {
		var archRule = ArchRuleDefinition
				.noClasses().that().resideInAPackage(repositoryPackages)
				.should().dependOnClassesThat().resideInAPackage(servicePackages);

		assertDoesNotThrow(() -> archRule.check(CLASSES));
	}

	@Test
	@DisplayName("Repository vrstva by nemala pouzivat controller triedy")
	void repositoriesShouldNotDependOnControllers() {
		var archRule = ArchRuleDefinition
				.noClasses().that().resideInAPackage(repositoryPackages)
				.should().dependOnClassesThat().resideInAPackage(controllerPackages);

		assertDoesNotThrow(() -> archRule.check(CLASSES));
	}

	@Test
	@DisplayName("Repository by nemali pouzivat field injection")
	void repositoriesShouldNotUseFieldInjection() {
		var archRule = createNoFieldInjectionRuleFor(repositoryPackages);
		assertDoesNotThrow(() -> archRule.check(CLASSES));
	}

	@Test
	@DisplayName("Repository triedy by mali byt iba interface (enforcnuty Spring Data styl)")
	void repositoriesShouldBeInterfaces() {
		var archRule = ArchRuleDefinition
			.classes().that().resideInAPackage(repositoryPackages)
			.and().areTopLevelClasses()
			.should().beInterfaces();

		assertDoesNotThrow(() -> archRule.check(CLASSES));
	}

}
