package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConfig;
import com.ecommerce.project.config.AppConstant;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api")
public class CategoryController {

//    private List<Category> categoryList= new ArrayList<>();

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/public/categories")
//    @GetMapping("/api/public/categories")
//    @RequestMapping(value = "/api/public/categories" ,method = RequestMethod.GET)
    public ResponseEntity<CategoryResponse> getCategory(
            @RequestParam(name="pageNumber" , defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name="pageSize", defaultValue = AppConstant.PAGE_SIZE) Integer pageSize,
            @RequestParam(name="sortBy", defaultValue = AppConstant.SORT_CATEGORIES_BY) String sortBy,
            @RequestParam(name="sortOrder", defaultValue = AppConstant.SORT_ORDER) String sortOrder
    ){
        CategoryResponse categories = categoryService.getCategory(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(categories,HttpStatus.OK);
    }

    @PostMapping("/admin/category")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO){
        CategoryDTO response= categoryService.createCategory(categoryDTO);
        return new ResponseEntity<CategoryDTO>(response,HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId){
//        try {
            return new ResponseEntity<>(categoryService.deleteCategory(categoryId), HttpStatus.OK);
//            types of response entity return
//            return  ResponseEntity.ok(categoryService.deleteCategory(categoryId));
//            return  ResponseEntity.status(HttpStatus.OK).body(categoryService.deleteCategory(categoryId));
//        }
//        catch(ResponseStatusException responseStatusException){
//            return new ResponseEntity<>(responseStatusException.getReason(),responseStatusException.getStatusCode());
//        }
    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO, @PathVariable Long categoryId){
            CategoryDTO updateCategoryDTO = categoryService.updateCategory(categoryDTO,categoryId);
            return new ResponseEntity<>(updateCategoryDTO, HttpStatus.OK);
    }

}
