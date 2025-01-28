package io.github.caolib.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 收货地址实体
 */
@Data
public class AddressDTO implements Serializable {
    /**
     * id
     */
    private Long id;
    /**
     * 省
     */
    @NotNull
    private String province;
    /**
     * 市
     */
    @NotNull
    private String city;
    /**
     * 县/区
     */
    @NotNull
    private String town;
    /**
     * 手机
     */
    @NotNull
    private String mobile;
    /**
     * 详细地址-街道
     */
    @NotNull
    private String street;
    /**
     * 联系人
     */
    @NotNull
    private String contact;
    /**
     * 是否是默认地址 1默认 0否
     */
    @NotNull
    private Integer isDefault;
    /**
     * 地址别名
     */
    private String notes;
}