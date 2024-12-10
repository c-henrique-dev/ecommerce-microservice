package br.com.loomi.productmicroservice.services;

import br.com.loomi.productmicroservice.exceptions.BadRequestException;
import br.com.loomi.productmicroservice.exceptions.NotFoundException;
import br.com.loomi.productmicroservice.models.dtos.ProductDto;
import br.com.loomi.productmicroservice.models.dtos.UpdateProductDto;
import br.com.loomi.productmicroservice.models.entities.Product;
import br.com.loomi.productmicroservice.repositories.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProductService {

    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product save(ProductDto productDto) {
        Product product = new Product();
        BeanUtils.copyProperties(productDto, product);
        return productRepository.save(product);
    }

    @Transactional
    public void update(UUID id, UpdateProductDto updateProductDto) {
        this.productRepository.findById(id)
                .map(p -> {
                    if (updateProductDto.getName() != null) {
                        p.setName(updateProductDto.getName());
                    }
                    if (updateProductDto.getQtd() != null) {
                        if (updateProductDto.getQtd() != null) {
                            p.setQtd(updateProductDto.getQtd());
                        }
                        if (updateProductDto.getDescription() != null) {
                            p.setDescription(updateProductDto.getDescription());
                        }

                        if (updateProductDto.getPrice() != null) {
                            p.setPrice(updateProductDto.getPrice());
                        }
                    }
                    productRepository.save(p);
                    return p;
                }).orElseThrow(() -> new NotFoundException("Product not found"));
    }

    @Transactional
    public void updateStock(UUID productId, int qtd) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (qtd < 0 && product.getQtd() + qtd < 0) {
            throw new IllegalArgumentException("Insufficient stock for product ID: " + productId);
        }

        product.setQtd(product.getQtd() + qtd);
        productRepository.save(product);
    }


    @Transactional
    public void delete(UUID id) {
        this.productRepository
                .findById(id)
                .map(p -> {
                    productRepository.delete(p);
                    return Void.TYPE;
                }).orElseThrow(() ->
                        new NotFoundException("Product not found"));
    }

    public Product findById(UUID id) {
        return this.productRepository
                .findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Product not found"));
    }

    public Page<Product> find(Product filter, Pageable pageable) throws Exception {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(
                        ExampleMatcher.StringMatcher.CONTAINING);

        Example<Product> example = Example.of(filter, matcher);
        Page<Product> productors = productRepository.findAll(example, pageable);

        return productors;
    }

    public void debitStock(UUID productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (product.getQtd() < quantity) {
            throw new BadRequestException("Insufficient stock.");
        }

        product.setQtd(product.getQtd() - quantity);
        productRepository.save(product);
    }
}
