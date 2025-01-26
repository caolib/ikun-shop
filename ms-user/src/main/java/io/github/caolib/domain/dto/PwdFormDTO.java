package io.github.caolib.domain.dto;

import io.github.caolib.enums.E;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PwdFormDTO {
    @NotNull(message = E.PWD_IS_NULL)
    private String oldPwd;
    @NotNull(message = E.PWD_IS_NULL)
    private String newPwd;
}
