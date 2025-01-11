package io.github.caolib.domain.dto;

import lombok.Data;

/**
 * 收货地址实体
 */
@Data
public class AddressDTO {
    /**
     * id
     */
    private Long id;
    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 县/区
     */
    private String town;
    /**
     * 手机
     */
    private String mobile;
    /**
     * 详细地址
     */
    private String street;
    /**
     * 联系人
     */
    private String contact;
    /**
     * 是否是默认 1默认 0否
     */
    private Integer isDefault;
    /**
     * 备注
     */
    private String notes;
}