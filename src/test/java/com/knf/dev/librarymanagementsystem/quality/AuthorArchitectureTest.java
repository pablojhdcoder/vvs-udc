package com.knf.dev.librarymanagementsystem.quality;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.knf.dev.librarymanagementsystem.repository.AuthorRepository;
import com.knf.dev.librarymanagementsystem.service.AuthorService;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
		packages = "com.knf.dev.librarymanagementsystem",
		importOptions = { ImportOption.DoNotIncludeTests.class, ImportOption.DoNotIncludeJars.class })
class AuthorArchitectureTest {

	@ArchTest
	static final ArchRule authorLivesInEntityPackage = classes()
			.that().haveFullyQualifiedName("com.knf.dev.librarymanagementsystem.entity.Author")
			.should().resideInAPackage("..entity..");

	@ArchTest
	static final ArchRule authorServiceImplLivesInImplPackage = classes()
			.that().haveSimpleName("AuthorServiceImpl")
			.should().resideInAPackage("..service.impl..")
			.andShould().implement(AuthorService.class);

	@ArchTest
	static final ArchRule authorServiceImplDependsOnRepository = classes()
			.that().haveSimpleName("AuthorServiceImpl")
			.should().dependOnClassesThat().areAssignableTo(AuthorRepository.class);

	@ArchTest
	static final ArchRule authorEntityDoesNotDependOnUpperLayers = noClasses()
			.that().haveFullyQualifiedName("com.knf.dev.librarymanagementsystem.entity.Author")
			.should().dependOnClassesThat().resideInAnyPackage("..service..", "..controller..", "..repository..");

	@ArchTest
	static final ArchRule authorServiceDoesNotDependOnControllers = noClasses()
			.that().resideInAPackage("..service..")
			.and().haveSimpleNameStartingWith("Author")
			.should().dependOnClassesThat().resideInAPackage("..controller..");
}
