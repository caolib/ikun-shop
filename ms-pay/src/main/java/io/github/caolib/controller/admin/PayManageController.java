package io.github.caolib.controller.admin;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.caolib.domain.R;
import io.github.caolib.domain.vo.PayDetailResVO;
import io.github.caolib.domain.vo.PayStatisticVO;
import io.github.caolib.service.IPayOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 支付管理
 */
@RestController
@RequestMapping("/pays/manage")
@RequiredArgsConstructor
public class PayManageController {
    private final IPayOrderService payOrderService;


    /**
     * 近一个星期的支付统计
     */
    @GetMapping
    public R<List<PayStatisticVO>> weekStatistic(@RequestParam int days) {
        return payOrderService.dayStatistic(days);
    }

    /**
     * 一段时间内的商品销量详情
     */
    @GetMapping("/pay-detail")
    public R<List<PayDetailResVO>> payDetail(String durationStart, String durationEnd) {
        // 将字符串转换为 LocalDateTime
        LocalDate start = LocalDate.parse(durationStart);
        LocalDate end = LocalDate.parse(durationEnd);

        return payOrderService.payDetail(start, end);
    }

    @GetMapping("/history")
    public R<List<PayDetailResVO>> payHistory() {
        try {
            // 从资源文件夹中读取 history.json
            Resource resource = new ClassPathResource("history.json");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            // 解析 JSON 文件
            JsonNode jsonNode = objectMapper.readTree(resource.getInputStream());
            JsonNode dataNode = jsonNode.get("data");

            List<PayDetailResVO> result = new ArrayList<>();
            if (dataNode.isArray()) {
                for (JsonNode node : dataNode) {
                    PayDetailResVO resVO = objectMapper.treeToValue(node, PayDetailResVO.class);
                    result.add(resVO);
                }
            }
            return R.ok(result);
        } catch (Exception e) {
            return R.error("获取历史数据失败");
        }
    }
}
