package com.lumina_bank.userservice.domain.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address {
    private String street;
    private String city;
    private String houseNumber;
    private String zipCode;
    private String country;
}
