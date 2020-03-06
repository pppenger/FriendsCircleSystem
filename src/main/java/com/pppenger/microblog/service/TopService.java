package com.pppenger.microblog.service;

import com.pppenger.microblog.domin.Top;

public interface TopService {

    Top save(Top top);
    Top findOne(Long id);


    int pickTop(String username);
}
