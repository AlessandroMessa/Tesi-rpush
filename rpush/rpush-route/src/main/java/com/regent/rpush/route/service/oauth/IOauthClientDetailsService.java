package com.regent.rpush.route.service.oauth;

import com.regent.rpush.route.model.OauthClientDetails;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 钟宝林
 * @since 2021-05-16
 */
public interface IOauthClientDetailsService extends IService<OauthClientDetails> {

    void initSuperAdmin(String superAdminUsername, String superAdminPass);
}
