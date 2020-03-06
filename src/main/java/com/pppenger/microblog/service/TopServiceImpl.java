package com.pppenger.microblog.service;

import com.pppenger.microblog.domin.Top;
import com.pppenger.microblog.repository.TopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopServiceImpl   implements TopService {

    @Autowired
    private TopRepository topRepository;


    @Override
    public Top save(Top top) {
        return topRepository.save(top);
    }

    @Override
    public Top findOne(Long id) {
        return topRepository.findOne(id);
    }


    @Override
    public int pickTop(String username){
        return topRepository.pickTop(username);
    };
}
