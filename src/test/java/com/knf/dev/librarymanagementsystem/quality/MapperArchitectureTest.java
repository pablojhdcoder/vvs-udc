package com.knf.dev.librarymanagementsystem.quality;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.knf.dev.librarymanagementsystem.util.Mapper;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
    packages = "com.knf.dev.librarymanagementsystem",
    importOptions = { ImportOption.DoNotIncludeTests.class, ImportOption.DoNotIncludeJars.class })
class MapperArchitectureTest {

  @ArchTest
  static final ArchRule mapperLivesInUtilPackage = classes()
      .that().haveFullyQualifiedName("com.knf.dev.librarymanagementsystem.util.Mapper")
      .should().resideInAPackage("..util..");

  @ArchTest
  static final ArchRule mapperOnlyUsesEntitiesAndRecords = classes()
      .that().haveFullyQualifiedName("com.knf.dev.librarymanagementsystem.util.Mapper")
      .should().onlyDependOnClassesThat().resideInAnyPackage("..util..", "..entity..", "..vo..", "java..");

  @ArchTest
  static final ArchRule mapperDoesNotDependOnUpperLayers = noClasses()
      .that().haveFullyQualifiedName("com.knf.dev.librarymanagementsystem.util.Mapper")
      .should().dependOnClassesThat().resideInAnyPackage("..service..", "..controller..", "..repository..");

  @ArchTest
  static final ArchRule mapperMethodsAreStatic = methods()
      .that().areDeclaredIn(Mapper.class)
      .should().beStatic();
}