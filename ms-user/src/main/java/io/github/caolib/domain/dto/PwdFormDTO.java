package io.github.caolib.domain.dto;

import io.github.caolib.enums.E;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class PwdFormDTO implements Serializable {
    @NotNull(message = E.PWD_IS_NULL)
    private String oldPwd;
    @NotNull(message = E.PWD_IS_NULL)
    private String newPwd;
}
