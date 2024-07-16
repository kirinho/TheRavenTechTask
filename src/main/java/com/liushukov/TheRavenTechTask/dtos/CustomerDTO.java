package com.liushukov.TheRavenTechTask.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerDTO(
        @NotBlank(message = "Full name is mandatory")
        @Size(min = 2, max = 50, message = "Full name should be from 2 to 50 chars")
        String fullName,
        @NotBlank(message = "Email is mandatory")
        @Email(message = "Email should be valid")
        @Size(min = 2, max = 100, message = "Email should be from 2 to 100 chars")
        String email,
        @NotBlank(message = "Phone is mandatory")
        @Pattern(regexp = "^\\+\\d{5,13}$", message = "Phone number must start with + and contain between 6 and 14 digits")
        String phone
) {
}
