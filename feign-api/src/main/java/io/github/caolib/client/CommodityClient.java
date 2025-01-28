package io.github.caolib.client;


import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.CommodityDTO;
import io.github.caolib.domain.dto.OrderDetailDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

@FeignClient(name = "commodity", path = "/commodity")
public interface CommodityClient {
    @GetMapping
    List<CommodityDTO> queryCommodityByIds(@RequestParam("ids") Collection<Long> ids);

    @PutMapping("/stock/deduct")
    R<Void> deductStock(@RequestBody List<OrderDetailDTO> items);


    @PutMapping("/release")
    void releaseStock(@RequestBody List<OrderDetailDTO> dtos);
}
