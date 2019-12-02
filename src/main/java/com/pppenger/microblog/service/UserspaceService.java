package com.pppenger.microblog.service;

import org.springframework.web.multipart.MultipartFile;

public interface UserspaceService {


    /**
     * 保存个人头像
     * @param username
     * @param imagefile
     * @return
     */
    String saveAvatar(String username, MultipartFile imagefile);
}
