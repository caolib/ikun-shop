package io.github.caolib.domain.dto;

import io.github.caolib.enums.Valid;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Validated
public class RegisterFormDTO {
    @Size(min = 6, message = Valid.VALIDATION_USERNAME)
    private String username;

    @Size(min = 6, message = Valid.VALIDATION_PASSWORD)
    private String password;

    @Pattern(regexp = "^\\d{11}$", message = Valid.VALIDATION_PHONE)
    private String phone;
}