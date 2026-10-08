package com.knf.dev.librarymanagementsystem.quality;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.knf.dev.librarymanagementsystem.repository.BookRepository;
import com.knf.dev.librarymanagementsystem.service.BookService;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/** Estructura de Book: paquete, servicio, repositorio y dirección de las dependencias. */
@AnalyzeClasses(
    packages = "com.knf.dev.librarymanagementsystem",
    importOptions = {ImportOption.DoNotIncludeTests.class, ImportOption.DoNotIncludeJars.class})
class BookArchitectureTest {

  @ArchTest
  static final ArchRule bookLivesInEntityPackage =
      classes()
          .that()
          .haveFullyQualifiedName("com.knf.dev.librarymanagementsystem.entity.Book")
          .should()
          .resideInAPackage("..entity..");

  @ArchTest
  static final ArchRule bookServiceImplLivesInImplPackage =
      classes()
          .that()
          .haveSimpleName("BookServiceImpl")
          .should()
          .resideInAPackage("..service.impl..")
          .andShould()
          .implement(BookService.class);

  @ArchTest
  static final ArchRule bookServiceImplDependsOnRepository =
      classes()
          .that()
          .haveSimpleName("BookServiceImpl")
          .should()
          .dependOnClassesThat()
          .areAssignableTo(BookRepository.class);

  @ArchTest
  static final ArchRule bookEntityDoesNotDependOnUpperLayers =
      noClasses()
          .that()
          .haveFullyQualifiedName("com.knf.dev.librarymanagementsystem.entity.Book")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("..service..", "..controller..", "..repository..");

  @ArchTest
  static final ArchRule bookServiceDoesNotDependOnControllers =
      noClasses()
          .that()
          .resideInAPackage("..service..")
          .and()
          .haveSimpleNameStartingWith("Book")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("..controller..");
}
