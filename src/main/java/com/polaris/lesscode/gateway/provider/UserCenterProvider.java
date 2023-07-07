package com.polaris.lesscode.gateway.provider;

import org.springframework.cloud.openfeign.FeignClient;

import com.polaris.lesscode.consts.ApplicationConsts;
import com.polaris.lesscode.uc.internal.api.UserCenterApi;

@FeignClient(value = ApplicationConsts.APPLICATION_USERCENTER)
public interface UserCenterProvider extends UserCenterApi{
}
