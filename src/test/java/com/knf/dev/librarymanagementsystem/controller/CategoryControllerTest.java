package com.knf.dev.librarymanagementsystem.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.knf.dev.librarymanagementsystem.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

  @Mock private CategoryService categoryService;
  @Mock private Model model;
  @InjectMocks private CategoryController categoryController;

  @Test
  void testFindAllCategories() {
    assertEquals("list-categories", categoryController.findAllCategories(model));
  }
}
