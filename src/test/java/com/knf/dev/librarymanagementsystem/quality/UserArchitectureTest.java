package com.knf.dev.librarymanagementsystem.quality;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.knf.dev.librarymanagementsystem.repository.UserRepository;
import com.knf.dev.librarymanagementsystem.service.UserService;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
    packages = "com.knf.dev.librarymanagementsystem",
    importOptions = { ImportOption.DoNotIncludeTests.class, ImportOption.DoNotIncludeJars.class })
class UserArchitectureTest {

  @ArchTest
  static final ArchRule userServiceIsAUserDetailsService = classes()
      .that().haveFullyQualifiedName("com.knf.dev.librarymanagementsystem.service.UserService")
      .should().beAssignableTo(UserDetailsService.class);

  @ArchTest
  static final ArchRule userServiceImplLivesInImplPackage = classes()
      .that().haveSimpleName("UserServiceImpl")
      .should().resideInAPackage("..service.impl..")
      .andShould().implement(UserService.class);

  @ArchTest
  static final ArchRule userServiceImplDependsOnRepository = classes()
      .that().haveSimpleName("UserServiceImpl")
      .should().dependOnClassesThat().areAssignableTo(UserRepository.class);

  @ArchTest
  static final ArchRule userServiceIsOnlyUsedBySecurity = classes()
      .that().haveFullyQualifiedName("com.knf.dev.librarymanagementsystem.service.UserService")
      .should().onlyHaveDependentClassesThat().resideInAnyPackage("..securityconfig..", "..service..");

  @ArchTest
  static final ArchRule userServiceDoesNotDependOnControllers = noClasses()
      .that().resideInAPackage("..service..")
      .and().haveSimpleNameStartingWith("User")
      .should().dependOnClassesThat().resideInAPackage("..controller..");
}