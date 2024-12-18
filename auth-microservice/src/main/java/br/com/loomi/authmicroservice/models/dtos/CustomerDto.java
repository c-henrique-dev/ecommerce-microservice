package br.com.loomi.authmicroservice.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CustomerDto {
    private UUID id;
    private UserDto user;
    private String fullName;
    private String contact;
    private String address;
    private Boolean status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
