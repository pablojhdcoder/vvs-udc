package com.knf.dev.librarymanagementsystem.quality;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.knf.dev.librarymanagementsystem.repository.PublisherRepository;
import com.knf.dev.librarymanagementsystem.service.PublisherService;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
    packages = "com.knf.dev.librarymanagementsystem",
    importOptions = { ImportOption.DoNotIncludeTests.class, ImportOption.DoNotIncludeJars.class })
class PublisherArchitectureTest {

  @ArchTest
  static final ArchRule publisherLivesInEntityPackage = classes()
      .that().haveFullyQualifiedName("com.knf.dev.librarymanagementsystem.entity.Publisher")
      .should().resideInAPackage("..entity..");

  @ArchTest
  static final ArchRule publisherServiceImplLivesInImplPackage = classes()
      .that().haveSimpleName("PublisherServiceImpl")
      .should().resideInAPackage("..service.impl..")
      .andShould().implement(PublisherService.class);

  @ArchTest
  static final ArchRule publisherServiceImplDependsOnRepository = classes()
      .that().haveSimpleName("PublisherServiceImpl")
      .should().dependOnClassesThat().areAssignableTo(PublisherRepository.class);

  @ArchTest
  static final ArchRule publisherEntityDoesNotDependOnUpperLayers = noClasses()
      .that().haveFullyQualifiedName("com.knf.dev.librarymanagementsystem.entity.Publisher")
      .should().dependOnClassesThat().resideInAnyPackage("..service..", "..controller..", "..repository..");

  @ArchTest
  static final ArchRule publisherServiceDoesNotDependOnControllers = noClasses()
      .that().resideInAPackage("..service..")
      .and().haveSimpleNameStartingWith("Publisher")
      .should().dependOnClassesThat().resideInAPackage("..controller..");
}