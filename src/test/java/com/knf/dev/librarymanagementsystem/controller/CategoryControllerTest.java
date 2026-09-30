package com.knf.dev.librarymanagementsystem.controller;

import com.knf.dev.librarymanagementsystem.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CategoryControllerTest {
    @Mock
    CategoryService categoryService;

    @InjectMocks
    CategoryController categoryController;

    @Test
    void testFindAllCategories() {
        String findAllCategoriesResult = categoryController.findAllCategories(null);
        assertEquals(findAllCategoriesResult, "list-category");
    }

}
