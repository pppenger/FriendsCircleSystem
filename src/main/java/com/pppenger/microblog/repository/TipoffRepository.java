package com.pppenger.microblog.repository;

import com.pppenger.microblog.domin.Tipoff;
import com.pppenger.microblog.domin.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

public interface TipoffRepository extends JpaRepository<Tipoff, Long> {


}
