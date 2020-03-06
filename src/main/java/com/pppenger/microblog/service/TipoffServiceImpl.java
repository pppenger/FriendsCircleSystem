package com.pppenger.microblog.service;

import com.pppenger.microblog.domin.Tipoff;
import com.pppenger.microblog.domin.User;
import com.pppenger.microblog.repository.TipoffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class TipoffServiceImpl implements TipoffService {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private TipoffRepository tipoffRepository;

    @Transactional
    @Override
    public Tipoff createTipoff(Tipoff tipoff) {
        return tipoffRepository.save(tipoff);
    }

    @Override
    public Page<Tipoff> getAllTipoff(int pageIndex, int pageSize) {
        Pageable pageable = new PageRequest(pageIndex, pageSize);
        return tipoffRepository.findAll(pageable);
    }


    @Override
    @Transactional
    public void updateScore(String username, int score) {

        User user = (User)userDetailsService.loadUserByUsername(username);
        if (user.getScore()-score<=0){
            user.setScore(0);
        }else {
            user.setScore(user.getScore()-score);
        }
        userService.saveUser(user);
    }


    @Transactional
    @Override
    public void removeTipoff(Long id) {
        tipoffRepository.delete(id);
    }

}
