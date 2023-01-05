package com.huawei.pwsdemo.service;

import com.google.gson.Gson;
import com.huawei.pwsdemo.config.PWSConfig;
import com.huawei.pwsdemo.entity.response.FabricTokenResponse;
import com.huawei.pwsdemo.utils.OkHttpClientBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.huawei.pwsdemo.utils.OkHttpClientBuilder.JSON;

@Service
public class ApplyFabricTokenService {
    /**
     * apply a fabric token to request other interface
     */
    public String applyFabricToken() {
        Map<String, String> params = new HashMap<>();
        params.put("appSecret", PWSConfig.AppSecret);
        RequestBody body = RequestBody.create(new Gson().toJson(params), JSON);
        Request request = new Request.Builder()
                .url(PWSConfig.BaseUrl + "/payment/v1/token")
                .addHeader("X-APP-Key", PWSConfig.FabricAppId)
                .post(body)
                .build();
        try {
            OkHttpClient client = OkHttpClientBuilder.createClient();
            Response response = client.newCall(request).execute();
            FabricTokenResponse res = new Gson().fromJson(response.body().string(), FabricTokenResponse.class);
            return res.getToken();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }
}
