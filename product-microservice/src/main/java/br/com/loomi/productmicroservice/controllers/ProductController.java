package br.com.loomi.productmicroservice.controllers;

import br.com.loomi.productmicroservice.models.dtos.ProductDto;
import br.com.loomi.productmicroservice.models.dtos.UpdateProductDto;
import br.com.loomi.productmicroservice.models.entities.Product;
import br.com.loomi.productmicroservice.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("product")
@Tag(name = "Product", description = "Endpoints for managing products")
public class ProductController {

    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Create a new product", description = "Allows an admin to create a new product.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Product save(@RequestBody @Valid ProductDto productDto) {
        return this.productService.save(productDto);
    }

    @Operation(summary = "Find products", description = "Retrieves a paginated list of products based on the provided filters.")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public Page<Product> find(
            @RequestParam(name = "price", required = false) @Parameter(description = "Filter by product price") BigDecimal price,
            @RequestParam(name = "qtd", required = false) @Parameter(description = "Filter by product quantity") Integer qtd,
            @RequestParam(name = "name", required = false) @Parameter(description = "Filter by product name") String name,
            @ParameterObject Pageable pageable
    ) throws Exception {
        Product product = new Product(name, price, qtd);
        return this.productService.find(product, pageable);
    }

    @Operation(summary = "Get a product by id", description = "Get a product by id")
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Product findById(@PathVariable @Parameter(description = "ID of the product") UUID id) {
        return this.productService.findById(id);
    }

    @Operation(summary = "Update a product", description = "Allows an admin to update the details of an existing product.")
    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void update(
            @PathVariable @Parameter(description = "ID of the product to update") UUID id,
            @RequestBody @Schema(description = "Updated details of the product") @Valid UpdateProductDto updateProductDto) {
        this.productService.update(id, updateProductDto);
    }

    @Operation(summary = "Update product stock", description = "Allows an admin to update the stock quantity of a product.")
    @PatchMapping("{id}/{qtd}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void updateStock(
            @PathVariable @Parameter(description = "ID of the product") UUID id,
            @PathVariable @Parameter(description = "New stock quantity") Integer qtd) {
        this.productService.updateStock(id, qtd);
    }

    @Operation(summary = "Delete a product", description = "Allows an admin to delete an existing product.")
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void delete(@PathVariable @Parameter(description = "ID of the product to delete") UUID id) {
        this.productService.delete(id);
    }

    @Operation(summary = "Debits the stock of a product")
    @PostMapping("{id}")
    public void debitStock(
            @Parameter(description = "Product ID", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Quantity to debit from stock", required = true)
            @RequestParam Integer qtd) {
        this.productService.debitStock(id, qtd);
    }
}
