package br.com.loomi.reportmicroservice.models.dtos;

import br.com.loomi.reportmicroservice.models.dtos.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
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
