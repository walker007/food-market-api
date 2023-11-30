package com.academiadodesenvolvedor.market.controllers;

import com.academiadodesenvolvedor.market.DTOs.ProductDTO;
import com.academiadodesenvolvedor.market.models.Product;
import com.academiadodesenvolvedor.market.models.Store;
import com.academiadodesenvolvedor.market.requests.CreateProductRequest;
import com.academiadodesenvolvedor.market.services.contracts.ProductServiceContract;
import com.academiadodesenvolvedor.market.services.contracts.StoreServiceContract;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/products")
@RestController
public class ProductController {

    private final ProductServiceContract productService;
    private final StoreServiceContract storeService;
    public ProductController(ProductServiceContract productService,
                             StoreServiceContract storeService){
        this.productService = productService;
        this.storeService = storeService;
    }

    @GetMapping
    private ResponseEntity<Page<ProductDTO>> getProducts(Pageable pageable) {
        Page<Product> productsPage = this.productService.getProducts(pageable);
        Page<ProductDTO> productDTOS = productsPage.map(ProductDTO::new);

        return new ResponseEntity<>(productDTOS, HttpStatus.OK);
    }

    @PostMapping
    private ResponseEntity<ProductDTO> create(@Valid @RequestBody CreateProductRequest request){
        Product product = request.convert();
        Store store = this.storeService.getStoreById(request.getStoreId());
        product.setStore(store);
        Product productSaved = this.productService.createProduct(product);

        return new ResponseEntity<>(new ProductDTO(productSaved), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    private ResponseEntity<ProductDTO> getById(@PathVariable Long id){
            Product product = this.productService.getProductById(id);
            return new ResponseEntity<>(new ProductDTO(product), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    private ResponseEntity<ProductDTO> update(@PathVariable Long id,
                                              @RequestBody CreateProductRequest request){

        Product product = this.productService.getProductById(id);
        Store store = this.storeService.getStoreById(request.getStoreId());
        product.setStore(store);
        product = this.productService.updateProduct(request.update(product));

        return new ResponseEntity<>(new ProductDTO(product), HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    private ResponseEntity delete(@PathVariable Long id){
        Product product = this.productService.getProductById(id);
        this.productService.deleteProduct(product);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
