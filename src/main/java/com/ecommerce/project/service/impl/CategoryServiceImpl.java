package com.ecommerce.project.service.impl;

import com.ecommerce.project.config.AppConstant;
import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

//    List<Category> categories = new ArrayList<>();

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

//    private Long nextId=1L;


    @Override
    public CategoryResponse getCategory(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sort =sortOrder.equalsIgnoreCase(AppConstant.ASCENDING) ?
                Sort.by(sortBy).ascending():Sort.by(sortBy).descending();

        CategoryResponse response = new CategoryResponse();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sort);
        Page<Category> categoriesPage= categoryRepository.findAll(pageDetails);
        List<Category> categories = categoriesPage.getContent();
        if(categories.isEmpty()){
            throw new APIException("No category created till now");
        }
//        ModelMapper modelMapper = new ModelMapper();
        List<CategoryDTO> content = categories.stream().
                map(category -> modelMapper.map(category,CategoryDTO.class)).toList();

        response.setContent(content);
        response.setPageNumber(categoriesPage.getNumber());
        response.setPageSize(categoriesPage.getSize());
        response.setTotalElements(categoriesPage.getTotalElements());
        response.setTotalPages(categoriesPage.getTotalPages());
        response.setLastPage(categoriesPage.isLast());
        return response;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {


        Category category = modelMapper.map(categoryDTO,Category.class);
        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if(savedCategory!=null){
                throw new APIException("Category name "+categoryDTO.getCategoryName()+" already exists");
        }
//        category.setCategoryId(nextId++);

        categoryRepository.save(category);
        categoryDTO.setCategoryId(category.getCategoryId());
        return categoryDTO;
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {

        Category category = categoryRepository.findById(categoryId).
                orElseThrow(()-> new ResourceNotFoundException("Category","categoryId",categoryId));
        categoryRepository.delete(category);

        return modelMapper.map(category,CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Category savedCategory = categoryRepository.findById(categoryId).
//                orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource not found"));
                    orElseThrow(()-> new ResourceNotFoundException("Category","categoryId",categoryId));
            savedCategory.setCategoryName(categoryDTO.getCategoryName());
            categoryRepository.save(savedCategory);
            return modelMapper.map(savedCategory,CategoryDTO.class);
    }
}
