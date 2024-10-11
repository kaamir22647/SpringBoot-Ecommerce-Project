package com.ecommerce.project.service.impl;

import com.ecommerce.project.config.AppConstant;
import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import com.ecommerce.project.service.CartService;
import com.ecommerce.project.service.FileService;
import com.ecommerce.project.service.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        boolean isProductNotPresent = true;
        List<Product> products = category.getProducts();
        for(Product existingProduct: products){
            if(existingProduct.getProductName().equalsIgnoreCase(productDTO.getProductName()))
            {
                isProductNotPresent=false;
                break;
            }
        }
        if(isProductNotPresent){
            productDTO.setCategory(category);
            productDTO.setImage("default.png");
            double specialPrice = productDTO.getPrice() -
                    (productDTO.getPrice() * (productDTO.getDiscount() * 0.01));
            productDTO.setSpecialPrice(specialPrice);
            Product product = modelMapper.map(productDTO, Product.class);
            Product savedProduct = productRepository.save(product);
            return modelMapper.map(savedProduct, ProductDTO.class);
        }
        else{
            throw new APIException("Product with productName: "+productDTO.getProductName()+" already exists");
        }
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase(AppConstant.ASCENDING) ?
                Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<Product> productsPage = productRepository.findAll(pageable);
        List<Product> products = productsPage.getContent();
        if(products.isEmpty()){
            throw new APIException("No products present");
        }
        List<ProductDTO> productDTOs = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class)).toList();
        return new ProductResponse(productDTOs,productsPage.getNumber(),productsPage.getSize(),productsPage.getTotalElements(),productsPage.getTotalPages(),productsPage.isLast());
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, String sortBy, String sortOrder,Integer pageNumber, Integer pageSize) {

        Sort sort = sortOrder.equalsIgnoreCase(AppConstant.ASCENDING) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Page<Product> productsPage = productRepository.findByCategoryOrderByPriceAsc(category, pageable);
        List<Product> products = productsPage.getContent();
        if (products.isEmpty()) {
            throw new APIException("No products present");
        }
        List<ProductDTO> productDTOs = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class)).toList();
//        return new ProductResponse(productDTOs);
        return new ProductResponse(productDTOs, productsPage.getNumber(), productsPage.getSize(), productsPage.getTotalElements(), productsPage.getTotalPages(), productsPage.isLast());
    }

        @Override
        public ProductResponse searchByKeyword (String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder){
            Sort sort = sortOrder.equalsIgnoreCase(AppConstant.ASCENDING) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

            Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
            Page<Product> productsPage = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%',pageable);
            List<Product> products = productsPage.getContent();
            if (products.isEmpty()) {
                throw new APIException("No products present");
            }
            List<ProductDTO> productDTOs = products.stream()
                    .map(product -> modelMapper.map(product, ProductDTO.class)).toList();
            return new ProductResponse(productDTOs, productsPage.getNumber(), productsPage.getSize(), productsPage.getTotalElements(), productsPage.getTotalPages(), productsPage.isLast());
        }

        @Override
        public ProductDTO updateProduct (ProductDTO productDTO, Long productId){
            Product existingProduct = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

            existingProduct.setProductName(productDTO.getProductName());
            existingProduct.setDescription(productDTO.getDescription());
            existingProduct.setQuantity(productDTO.getQuantity());
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setDiscount(productDTO.getDiscount());

            double specialPrice = existingProduct.getPrice() -
                    (existingProduct.getPrice() * (existingProduct.getDiscount() * 0.01));
            existingProduct.setSpecialPrice(specialPrice);

//        existingProduct.setSpecialPrice(product.getSpecialPrice());
            Product savedProduct = productRepository.save(existingProduct);

            //Updating cart items

            List<Cart> carts = cartRepository.findCartsByProductId(productId);

            List<CartDTO> cartDTOs = carts.stream().map(cart -> {
                CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

                List<ProductDTO> products = cart.getCartItems().stream()
                        .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());

                cartDTO.setProducts(products);

                return cartDTO;

            }).collect(Collectors.toList());

            cartDTOs.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));
            return modelMapper.map(savedProduct, ProductDTO.class);
        }

        @Override
        public ProductDTO deleteProductByProductId (Long productId){

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

            ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
            // DELETE
            List<Cart> carts = cartRepository.findCartsByProductId(productId);
            carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));
            productRepository.deleteById(productId);
            return productDTO;
        }

        @Override
        public ProductDTO updateProductImage (Long productId, MultipartFile image) throws IOException {

            Product existingProduct = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
            //Upload image on server
            //Get the file name of uploaded image
            String newFileName = fileService.uploadFile(path, image);
            // update the new file name to the existing product
            existingProduct.setImage(newFileName);
            Product updatedProduct = productRepository.save(existingProduct);
            //return DTO after mapping
            return modelMapper.map(updatedProduct, ProductDTO.class);
        }


}