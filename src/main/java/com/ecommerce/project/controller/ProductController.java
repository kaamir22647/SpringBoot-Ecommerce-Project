package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstant;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO,
                                                 @PathVariable Long categoryId){
//       ProductDTO productDTO =

       return new ResponseEntity<>(productService.addProduct(productDTO,categoryId), HttpStatus.CREATED);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(@RequestParam(name="pageNumber" ,defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
                                                          @RequestParam(name="pageSize" ,defaultValue = AppConstant.PAGE_SIZE) Integer pageSize,
                                                          @RequestParam(name="sortBy" , defaultValue = AppConstant.SORT_PRODUCTS_BY) String sortBy,
                                                          @RequestParam(name="sortOrder",defaultValue = AppConstant.SORT_ORDER) String sortOrder){
        ProductResponse productResponse = productService.getAllProducts(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse,HttpStatus.OK);
        }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getAllProductsCategory(@PathVariable Long categoryId,
                                                                  @RequestParam(name="pageNumber" ,defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
                                                                  @RequestParam(name="pageSize" ,defaultValue = AppConstant.PAGE_SIZE) Integer pageSize,
                                                                  @RequestParam(name="sortBy" , defaultValue = AppConstant.SORT_PRODUCTS_BY) String sortBy,
                                                                  @RequestParam(name="sortOrder",defaultValue = AppConstant.SORT_ORDER) String sortOrder){
        ProductResponse productResponse = productService.searchByCategory(categoryId,sortBy,sortOrder,pageNumber, pageSize);
        return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getAllProductsByKeyword(@PathVariable String keyword,
                                                                   @RequestParam(name="pageNumber" ,defaultValue = AppConstant.PAGE_NUMBER) Integer pageNumber,
                                                                   @RequestParam(name="pageSize" ,defaultValue = AppConstant.PAGE_SIZE) Integer pageSize,
                                                                   @RequestParam(name="sortBy" , defaultValue = AppConstant.SORT_PRODUCTS_BY) String sortBy,
                                                                   @RequestParam(name="sortOrder",defaultValue = AppConstant.SORT_ORDER) String sortOrder){
        ProductResponse productResponse = productService.searchByKeyword(keyword,pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO productDTO,
                                                 @PathVariable Long productId){
//        ProductDTO productDTO = ;
        return new ResponseEntity<>(productService.updateProduct(productDTO,productId), HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProductByProductId(@PathVariable Long productId){
        ProductDTO deletedProduct = productService.deleteProductByProductId(productId);
        return new ResponseEntity<>(deletedProduct, HttpStatus.OK);
    }

    @PutMapping("/product/{productId}/image")
    public ResponseEntity<ProductDTO>  updateProductImage(@PathVariable Long productId,
                                                          @RequestParam("image") MultipartFile image) throws IOException {
        return new ResponseEntity<>(productService.updateProductImage(productId,image),HttpStatus.OK);

    }
}
