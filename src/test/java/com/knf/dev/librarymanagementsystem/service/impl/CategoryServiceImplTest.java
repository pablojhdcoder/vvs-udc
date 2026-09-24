package com.knf.dev.librarymanagementsystem.service.impl;

import com.knf.dev.librarymanagementsystem.entity.Category;
import com.knf.dev.librarymanagementsystem.exception.NotFoundException;
import com.knf.dev.librarymanagementsystem.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CategoryServiceImplTest {
    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryServiceImpl categoryService;

    @Test
    void testFindAllCategories(){
        Category category1 = new Category("Category1");
        Category category2 = new Category("Category2");
        List<Category> categories = List.of(category1, category2);
        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.findAllCategories();
        assertEquals(categories, result);
        verify(categoryRepository).findAll();


        Category category3 = new Category("Category2");
        List<Category> categories2 = List.of(category3);
        when(categoryRepository.findAll()).thenReturn(categories2);

        List<Category> result2 = categoryService.findAllCategories();
        assertEquals(categories2, result2);
        verify(categoryRepository, times(2)).findAll();


        List<Category> emptyCategories = new ArrayList<Category>();
        when(categoryRepository.findAll()).thenReturn(emptyCategories);

        List<Category> emptyResult = categoryService.findAllCategories();
        assertEquals(emptyCategories, emptyResult);
        verify(categoryRepository, times(3)).findAll();
    }


    @Test
    void testFindCategoryById() {
        Category category1 = new Category("Category1");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category1));

        Category result = categoryService.findCategoryById(1L);

        assertEquals(category1, result);
        verify(categoryRepository).findById(1L);


        Category category2 = new Category("Category2");

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(category2));

        Category result2 = categoryService.findCategoryById(2L);

        assertEquals(category2, result2);
        verify(categoryRepository).findById(2L);


        when(categoryRepository.findById(3L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> categoryService.findCategoryById(3L)
        );

        verify(categoryRepository).findById(3L);
    }


    @Test
    void testCreateCategory(){
        Category category = new Category("Category");
        when(categoryRepository.save(category)).thenReturn(category);
        categoryService.createCategory(category);
        verify(categoryRepository).save(category);


        when(categoryRepository.save(null)).thenThrow(IllegalArgumentException.class);
        assertThrows(
                IllegalArgumentException.class,
                () -> categoryService.createCategory(null)
        );
    }


    @Test void testUpdateCategory() {
        Category category1 = new Category("Category1");
        category1.setId(1L);

        categoryService.updateCategory(category1);
        verify(categoryRepository).save(category1);


        Category category2 = new Category("Category2");
        category2.setId(2L);

        categoryService.updateCategory(category2);
        verify(categoryRepository).save(category2);
    }


    @Test
    void testDeleteCategory(){
        Category category = new Category("Category");
        category.setId(1L);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        verify(categoryRepository).findById(1L);
        verify(categoryRepository).deleteById(1L);


        when(categoryRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> categoryService.deleteCategory(2L)
        );

        verify(categoryRepository).findById(2L);
        verify(categoryRepository, never()).deleteById(2L);
    }
}





