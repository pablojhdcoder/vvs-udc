package com.knf.dev.librarymanagementsystem.quality;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.knf.dev.librarymanagementsystem.repository.CategoryRepository;
import com.knf.dev.librarymanagementsystem.service.CategoryService;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/** Estructura de Category: paquete, servicio, repositorio y dirección de las dependencias. */
@AnalyzeClasses(
    packages = "com.knf.dev.librarymanagementsystem",
    importOptions = {ImportOption.DoNotIncludeTests.class, ImportOption.DoNotIncludeJars.class})
class CategoryArchitectureTest {

  @ArchTest
  static final ArchRule categoryLivesInEntityPackage =
      classes()
          .that()
          .haveFullyQualifiedName("com.knf.dev.librarymanagementsystem.entity.Category")
          .should()
          .resideInAPackage("..entity..");

  @ArchTest
  static final ArchRule categoryServiceImplLivesInImplPackage =
      classes()
          .that()
          .haveSimpleName("CategoryServiceImpl")
          .should()
          .resideInAPackage("..service.impl..")
          .andShould()
          .implement(CategoryService.class);

  @ArchTest
  static final ArchRule categoryServiceImplDependsOnRepository =
      classes()
          .that()
          .haveSimpleName("CategoryServiceImpl")
          .should()
          .dependOnClassesThat()
          .areAssignableTo(CategoryRepository.class);

  @ArchTest
  static final ArchRule categoryEntityDoesNotDependOnUpperLayers =
      noClasses()
          .that()
          .haveFullyQualifiedName("com.knf.dev.librarymanagementsystem.entity.Category")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("..service..", "..controller..", "..repository..");

  @ArchTest
  static final ArchRule categoryServiceDoesNotDependOnControllers =
      noClasses()
          .that()
          .resideInAPackage("..service..")
          .and()
          .haveSimpleNameStartingWith("Category")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("..controller..");
}
