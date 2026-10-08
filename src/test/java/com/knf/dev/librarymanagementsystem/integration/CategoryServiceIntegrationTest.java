package com.knf.dev.librarymanagementsystem.integration;

import java.util.List;
import java.util.Optional;

import com.knf.dev.librarymanagementsystem.repository.BookRepository;
import com.knf.dev.librarymanagementsystem.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.knf.dev.librarymanagementsystem.entity.Category;
import com.knf.dev.librarymanagementsystem.exception.NotFoundException;
import com.knf.dev.librarymanagementsystem.repository.CategoryRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CategoryServiceIntegrationTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void testFindAllCategories() {
        Category category1 = new Category("Category 1");
        Category category2 = new Category("Category 2");

        categoryRepository.save(category1);
        categoryRepository.save(category2);

        List<Category> result = categoryService.findAllCategories();

        assertAll("Check the categories found",
                () -> assertEquals(2, result.size()),
                () -> assertEquals("Category 1", result.get(0).getName()),
                () -> assertEquals("Category 2", result.get(1).getName())
        );
    }

    @Test
    void testFindCategoryById() {
        Category category = new Category("Category 1");
        Category savedCategory = categoryRepository.save(category);

        Category result =
                categoryService.findCategoryById(savedCategory.getId());

        assertAll("Check the category found",
                () -> assertNotNull(result),
                () -> assertEquals(savedCategory.getId(), result.getId()),
                () -> assertEquals("Category 1", result.getName())
            );
    }

    @Test
    void testFindCategoryByIdNotFound() {
        Long id = 999L;

        assertThrows(
                NotFoundException.class,
                () -> categoryService.findCategoryById(id)
        );
    }

    @Test
    void testCreateCategory() {
        Category category = new Category("Category 1");

        categoryService.createCategory(category);

        List<Category> categories = categoryRepository.findAll();


        assertAll("Check that the category is created",
                () -> assertEquals(1, categories.size()),
                () -> assertEquals("Category 1", categories.get(0).getName()),
                () -> assertNotNull(categories.get(0).getId())
            );
    }

    @Test
    void testUpdateCategory() {
        Category category = new Category("Category 1");
        Category savedCategory = categoryRepository.save(category);

        savedCategory.setName("Updated Category");

        categoryService.updateCategory(savedCategory);

        Category result =
                categoryRepository.findById(savedCategory.getId()).orElseThrow();



        assertAll("Check that the category is updated and has the same ID",
                () -> assertEquals(savedCategory.getId(), result.getId()),
                () -> assertEquals("Updated Category", result.getName())
            );
    }

    @Test
    void testDeleteCategory() {
        Category category = new Category("Category 1");
        Category savedCategory = categoryRepository.save(category);

        categoryService.deleteCategory(savedCategory.getId());

        Optional<Category> categoryOptional = categoryRepository.findById(savedCategory.getId());
        assertTrue(categoryOptional.isEmpty());
    }

    @Test
    void testDeleteCategoryAmongSeveral() {
        Category category1 = new Category("Category 1");
        Category category2 = new Category("Category 2");
        Category savedCategory1 = categoryRepository.save(category1);
        Category savedCategory2 = categoryRepository.save(category2);

        categoryService.deleteCategory(category1.getId());

        Optional<Category> categoryOptional1 = categoryRepository.findById(savedCategory1.getId());


        assertAll("Check that category 1 is deleted and that the 2 exists",
                () -> assertTrue(categoryOptional1.isEmpty()),
                () -> assertEquals(categoryRepository.findById(savedCategory2.getId()).get(), savedCategory2)
            );
    }

    @Test
    void testDeleteCategoryNotFound() {
        Long id = 999L;

        assertThrows(
                NotFoundException.class,
                () -> categoryService.deleteCategory(id)
        );
    }
}