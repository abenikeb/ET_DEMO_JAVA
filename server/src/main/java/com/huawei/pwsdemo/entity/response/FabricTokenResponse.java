package com.huawei.pwsdemo.entity.response;

import lombok.Data;

/**
 * apply fabric token response
 */
@Data
public class FabricTokenResponse {
    private String token;
    private String effectiveDate;
    private String expirationDate;
}
