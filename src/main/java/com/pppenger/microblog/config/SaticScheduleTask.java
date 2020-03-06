package com.pppenger.microblog.config;

import com.pppenger.microblog.domin.Top;
import com.pppenger.microblog.domin.User;
import com.pppenger.microblog.service.TopService;
import com.pppenger.microblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class SaticScheduleTask {

    @Autowired
    private UserService userService;
    @Autowired
    private TopService topService;


    //3.添加定时任务
    //@Scheduled(cron = "0/5 * * * * ?")
    @Scheduled(cron = "0 59 23 ? * SAT")
    //或直接指定时间间隔，例如：5秒
    //@Scheduled(fixedRate=5000)
    private void configureTasks() {
        System.err.println("执行静态定时任务时间: " + LocalDateTime.now());
        List<User> users = userService.loadUserByScore(100);
        if (users.size()>0){
            List<User> saveUsers = users.stream().map(user -> {
                if ((user.getScore() + 10) > 100) {
                    user.setScore(100);
                } else {
                    user.setScore(user.getScore() + 10);
                }
                return user;
            }).collect(Collectors.toList());
            userService.saveUserList(saveUsers);
        }
    }

    //3.添加定时任务,每天11点，归元头条版的抢夺资格
    @Scheduled(cron = "0 0 23 * * ?")
    //或直接指定时间间隔，例如：5秒
    //@Scheduled(fixedRate=5000)
    private void clearTOP1() {
        System.err.println("执行静态定时任务时间: " + LocalDateTime.now());

        Top top1=topService.findOne((long) 1);
        top1.setTopUsername(null);
        top1.setHaveSend(0);
        topService.save(top1);
    }
}
