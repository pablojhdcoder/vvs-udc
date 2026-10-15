package com.knf.dev.librarymanagementsystem.service.impl;

import com.knf.dev.librarymanagementsystem.entity.Category;
import com.knf.dev.librarymanagementsystem.exception.NotFoundException;
import com.knf.dev.librarymanagementsystem.repository.CategoryRepository;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.LongRange;
import net.jqwik.api.constraints.StringLength;
import net.jqwik.spring.JqwikSpringSupport;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
@JqwikSpringSupport
public class CategoryServiceImplTest {
    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryServiceImpl categoryService;

    @Test
    void testFindAllCategoriesWithTwoCategories() {
        Category category1 = new Category("Category1");
        Category category2 = new Category("Category2");
        List<Category> categories = List.of(category1, category2);

        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.findAllCategories();

        assertAll(
                "Comprobar que se obtienen correctamente dos categorías",
                () -> assertEquals(categories, result)
        );

        verify(categoryRepository).findAll();
    }

    @Test
    void testFindAllCategoriesWithOneCategory() {
        Category category3 = new Category("Category2");
        List<Category> categories2 = List.of(category3);

        when(categoryRepository.findAll()).thenReturn(categories2);

        List<Category> result2 = categoryService.findAllCategories();

        assertAll(
                "Comprobar que se obtiene correctamente una categoría",
                () -> assertEquals(categories2, result2)
        );

        verify(categoryRepository).findAll();
    }

    @Test
    void testFindAllCategoriesWithNoCategories() {
        List<Category> emptyCategories = new ArrayList<Category>();

        when(categoryRepository.findAll()).thenReturn(emptyCategories);

        List<Category> emptyResult = categoryService.findAllCategories();

        assertAll(
                "Comprobar que se obtiene una lista vacía cuando no existen categorías",
                () -> assertEquals(emptyCategories, emptyResult)
        );

        verify(categoryRepository).findAll();
    }


    @Test
    void testFindCategoryById() {
        Category category1 = new Category("Category1");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category1));

        Category result = categoryService.findCategoryById(1L);

        assertAll(
                "Comprobar que se encuentra correctamente la categoría con ID 1",
                () -> assertEquals(category1, result)
        );

        verify(categoryRepository).findById(1L);
    }

    @Test
    void testFindCategoryByIdWhenCategoryDoesNotExist() {
        when(categoryRepository.findById(3L))
                .thenReturn(Optional.empty());

        assertAll(
                "Comprobar que se lanza NotFoundException cuando no existe la categoría con ID 3",
                () -> assertThrows(
                        NotFoundException.class,
                        () -> categoryService.findCategoryById(3L)
                )
        );

        verify(categoryRepository).findById(3L);
    }


    @Test
    void testCreateCategory() {
        Category category = new Category("Category");

        when(categoryRepository.save(category)).thenReturn(category);

        categoryService.createCategory(category);

        assertAll(
                "Comprobar que se guarda correctamente una categoría",
                () -> verify(categoryRepository).save(category)
        );
    }

    @Test
    void testCreateCategoryWithNull() {
        when(categoryRepository.save(null))
                .thenThrow(IllegalArgumentException.class);

        assertAll(
                "Comprobar que se lanza IllegalArgumentException al intentar crear una categoría nula",
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> categoryService.createCategory(null)
                )
        );
    }


    @Property(tries = 20)
    void testUpdateCategory(
            @ForAll @LongRange(min = 1, max = 10_000) long id,
            @ForAll @StringLength(min = 1, max = 50) String name) {
        CategoryRepository repository = mock(CategoryRepository.class);
        CategoryServiceImpl service = new CategoryServiceImpl(repository);
        Category category = new Category(name);
        category.setId(id);

        service.updateCategory(category);

        verify(repository).save(category);
    }


    @Test
    void testDeleteCategory() {
        Category category = new Category("Category");
        category.setId(1L);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        assertAll(
                "Comprobar que se busca y elimina correctamente una categoría existente",
                () -> verify(categoryRepository).findById(1L),
                () -> verify(categoryRepository).deleteById(1L)
        );
    }

    @Test
    void testDeleteCategoryWhenCategoryDoesNotExist() {
        when(categoryRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertAll(
                "Comprobar que se lanza NotFoundException al eliminar una categoría inexistente",
                () -> assertThrows(
                        NotFoundException.class,
                        () -> categoryService.deleteCategory(2L)
                )
        );

        verify(categoryRepository).findById(2L);
        verify(categoryRepository, never()).deleteById(2L);
    }
}



