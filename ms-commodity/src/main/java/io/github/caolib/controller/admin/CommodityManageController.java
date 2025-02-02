package io.github.caolib.controller.admin;

import io.github.caolib.domain.dto.CommodityDTO;
import io.github.caolib.service.ICommodityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 商品管理
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/commodity/admin")
@RequiredArgsConstructor
public class CommodityManageController {
    private final ICommodityService commodityService;


    /**
     * 更新商品
     *
     * @param commodityDTO 商品信息
     */
    @PutMapping
    public void updateCommodity(@RequestBody CommodityDTO commodityDTO) {
        log.debug("更新商品信息 {}", commodityDTO);

        commodityService.updateCommodity(commodityDTO);
    }


}
