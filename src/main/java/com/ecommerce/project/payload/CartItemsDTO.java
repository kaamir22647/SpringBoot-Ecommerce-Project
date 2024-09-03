package com.ecommerce.project.payload;

import com.ecommerce.project.model.Cart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemsDTO {

    private Long cartItemId;
    private CartDTO cartDTO;
    private ProductDTO productDTO;
    private Double discount;
    private Double productPrice;
}
