package com.streaming.settlement.user.service.port;

import com.streaming.settlement.user.domain.User;
import com.streaming.settlement.user.infrastructure.kakao.KakaoUserInfoResponse;

public interface KakaoLogin {

    String getToken(String code);

    KakaoUserInfoResponse getUserInfo(String accessToken);

    User identifyUser(KakaoUserInfoResponse currentUserInfo);
}
