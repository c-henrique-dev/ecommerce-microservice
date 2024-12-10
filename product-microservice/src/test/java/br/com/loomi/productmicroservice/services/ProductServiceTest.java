package br.com.loomi.productmicroservice.services;

import br.com.loomi.productmicroservice.exceptions.NotFoundException;
import br.com.loomi.productmicroservice.models.dtos.ProductDto;
import br.com.loomi.productmicroservice.models.dtos.UpdateProductDto;
import br.com.loomi.productmicroservice.models.entities.Product;
import br.com.loomi.productmicroservice.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Test
    public void createProduct_WithValidData_ReturnsProduct() {
        Product product = Product.builder()
                .name("Playstation 5")
                .description("description test")
                .price(BigDecimal.valueOf(5200))
                .qtd(5)
                .build();

        ProductDto productDto = ProductDto.builder()
                .name(product.getName())
                .qtd(product.getQtd())
                .price(product.getPrice())
                .description(product.getDescription())
                .build();

        when(productRepository.save(product)).thenReturn(product);

        Product sut = productService.save(productDto);

        assertThat(sut).isEqualTo(product);
        assertThat(sut).isNotNull();
        assertThat(sut.getName()).isEqualTo(product.getName());
    }

   @Test
    public void createProduct_WithInvalidData_ThrowsException() {
       Product product = Product.builder()
               .name("Playstation 5")
               .description("description test")
               .price(BigDecimal.valueOf(5200))
               .qtd(5)
               .build();

       ProductDto productDto = ProductDto.builder()
               .name(product.getName())
               .qtd(product.getQtd())
               .price(product.getPrice())
               .description(product.getDescription())
               .build();

        when(productRepository.save(product)).thenThrow(RuntimeException.class);

        assertThatThrownBy(() -> productService.save(productDto)).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void removeCustomer_WithExistingId_doesNotThrowAnyException() {
        Product product = Product.builder()
                .name("Playstation 5")
                .description("description test")
                .price(BigDecimal.valueOf(5200))
                .qtd(5)
                .build();

        ProductDto productDto = ProductDto.builder()
                .name(product.getName())
                .qtd(product.getQtd())
                .price(product.getPrice())
                .description(product.getDescription())
                .build();

        when(productRepository.findById(product.getId())).thenReturn(Optional.ofNullable(product));

        assertThatCode(() -> productService.delete(product.getId())).doesNotThrowAnyException();
    }

    @Test
    public void removeProduct_WithUnexistingId_ThrowsException() {
        UUID invalidId = UUID.randomUUID();

        when(productRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.delete(invalidId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void listProducts_ReturnsAllProducts() throws Exception {
        Product product1 = Product.builder()
                .name("Playstation 5")
                .description("description test")
                .price(BigDecimal.valueOf(5200))
                .qtd(5)
                .build();

        Product product2 = Product.builder()
                .name("TV")
                .description("description test")
                .price(BigDecimal.valueOf(3200))
                .qtd(3)
                .build();

        List<Product> productList = List.of(product1, product2);
        Pageable pageable = Pageable.ofSize(10);

        when(productRepository.findAll(any(), eq(pageable)))
                .thenReturn(new PageImpl<>(productList, pageable, productList.size()));

        Page<Product> result = productService.find(
                Product.builder().name("Playstation5").build(),
                pageable
        );

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactlyInAnyOrder(product1, product2);

        verify(productRepository).findAll(any(), eq(pageable));
    }

    @Test
    public void find_WhenRepositoryReturnsEmptyPage_ShouldReturnEmptyPage() throws Exception {
        Pageable pageable = Pageable.ofSize(10);

        when(productRepository.findAll(any(), eq(pageable)))
                .thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0));

        Page<Product> result = productService.find(
                Product.builder().name("Playstation5").build(),
                pageable
        );

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);

        verify(productRepository).findAll(any(), eq(pageable));
    }

    @Test
    public void update_WhenProductsExists_ShouldUpdateProductsSuccessfully() {
        UUID id = UUID.randomUUID();

        UpdateProductDto updateProductDto = UpdateProductDto.builder()
                .name("Playstation")
                .build();

        Product existingProduct = Product.builder()
                .id(id)
                .name(updateProductDto.getName())
                .build();

        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));

        productService.update(id, updateProductDto);

        verify(productRepository).findById(id);
        verify(productRepository).save(existingProduct);

        assertThat(existingProduct.getName()).isEqualTo("Playstation");
    }

    @Test
    public void update_WhenProductsDoesNotExist_ShouldThrowNotFoundException() {
        UUID id = UUID.randomUUID();

        UpdateProductDto updateProductDto = UpdateProductDto.builder()
                .name("Playstation")
                .build();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(id, updateProductDto))
                .isInstanceOf(NotFoundException.class);

        verify(productRepository).findById(id);
        verify(productRepository, never()).save(any());
    }
}
