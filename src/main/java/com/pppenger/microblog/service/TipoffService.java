package com.pppenger.microblog.service;

import com.pppenger.microblog.domin.Tipoff;
import org.springframework.data.domain.Page;

public interface TipoffService {

    Tipoff createTipoff(Tipoff tipoff);

    Page<Tipoff> getAllTipoff(int pageIndex, int pageSize);

    void updateScore(String username, int score);

    void removeTipoff(Long id);

}
