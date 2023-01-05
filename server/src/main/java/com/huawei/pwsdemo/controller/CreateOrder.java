package com.huawei.pwsdemo.controller;

import com.google.gson.Gson;
import com.huawei.pwsdemo.config.PWSConfig;
import com.huawei.pwsdemo.entity.request.CreateOrderRequest;
import com.huawei.pwsdemo.entity.response.CreateOrderResponse;
import com.huawei.pwsdemo.service.ApplyFabricTokenService;
import com.huawei.pwsdemo.utils.OkHttpClientBuilder;
import com.huawei.pwsdemo.utils.ToolUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.huawei.pwsdemo.utils.OkHttpClientBuilder.JSON;

@CrossOrigin
@Controller
public class CreateOrder {
    @Autowired
    ApplyFabricTokenService applyFabricTokenService;

    /**
     * create a PWS order
     */
    @ResponseBody
    @RequestMapping("/create/order")
    public String applyH5Token(@org.springframework.web.bind.annotation.RequestBody CreateOrderRequest input) {
        String fabricToken = applyFabricTokenService.applyFabricToken();
        Map<String, Object> params = createRequestObject(input);
        RequestBody body = RequestBody.create(new Gson().toJson(params), JSON);
        Request request = new Request.Builder()
                .url(PWSConfig.BaseUrl + "/payment/v1/merchant/preOrder")
                .addHeader("X-APP-Key", PWSConfig.FabricAppId)
                .addHeader("Authorization", fabricToken)
                .post(body)
                .build();
        try {
            OkHttpClient client = OkHttpClientBuilder.createClient();
            Response response = client.newCall(request).execute();
            CreateOrderResponse createOrderResponse = new Gson().fromJson(response.body().string(), CreateOrderResponse.class);
            return createRawRequest(createOrderResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private Map<String, Object> createRequestObject(CreateOrderRequest input) {
        Map<String, Object> req = new HashMap<>();
        req.put("timestamp", ToolUtils.createTimeStamp());
        req.put("nonce_str", ToolUtils.createNonceStr());
        req.put("method", "payment.preorder");
        req.put("version", "1.0");
        Map<String, Object> biz = new HashMap<>();
        req.put("biz_content", biz);

        // fill biz object
        biz.put("notify_url", "https://www.baidu.com");
        biz.put("trade_type", "InApp");
        biz.put("appid", PWSConfig.MerchantAppId);
        biz.put("merch_code", PWSConfig.MerchantCode);
        biz.put("merch_order_id", createMerchantOrderId());
        biz.put("title", input.getTitle());
        biz.put("total_amount", input.getAmount());
        biz.put("trans_currency", "ETB");
        biz.put("timeout_express", "120m");

        // sign type and sign string
        req.put("sign_type", "SHA256WithRSA");
        req.put("sign", ToolUtils.signRequestBody(req));
        return req;
    }

    private String createMerchantOrderId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date now = new Date();
        return sdf.format(now) + now.getTime();
    }

    private String createRawRequest(CreateOrderResponse response) {
        Map<String, Object> map = new HashMap<>();
        map.put("appid", PWSConfig.MerchantAppId);
        map.put("merch_code", PWSConfig.MerchantCode);
        map.put("nonce_str", ToolUtils.createNonceStr());
        map.put("prepay_id", response.getBiz_content().getPrepay_id());
        map.put("timestamp", ToolUtils.createTimeStamp());
        String sign = ToolUtils.signRequestBody(map);
        String rawRequest = "";
        for (String key : map.keySet()) {
            rawRequest += key + "=" + map.get(key) + "&";
        }
        rawRequest += "sign=" + sign + "&sign_type=SHA256WithRSA";
        return rawRequest;
    }
}
