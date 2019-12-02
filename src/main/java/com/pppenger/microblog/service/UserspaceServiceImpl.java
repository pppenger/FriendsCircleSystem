package com.pppenger.microblog.service;

import com.pppenger.microblog.domin.User;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 个人空间
 *
 * author: luqipeng
 */
@Service
public class UserspaceServiceImpl implements UserspaceService{

    @Autowired
    UserService userService;

    @Autowired
    UserDetailsService userDetailsService;


    @Override
    public String saveAvatar(String username, MultipartFile imagefile){
        StringBuilder sb=new StringBuilder();
        if (!imagefile.isEmpty()){
            String filename=imagefile.getOriginalFilename();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            String newFileName=uuid+".png";

            String path="D:/uploadImages/";
            // 生成保存路径名
            String realPath = path + "/" + newFileName;
            File dest = new File(realPath);
            //判断文件父目录是否存在
            if(!dest.getParentFile().exists()){
                dest.getParentFile().mkdirs();
            }
            try {
                //保存文件
                imagefile.transferTo(dest);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thumbnails.of(realPath)
                        .size(50, 50)
                        .toFile(path + "/compress/" + newFileName);
            }catch (IOException e) {
                e.printStackTrace();
            }
            //读取相对路径
            String getpath="/" + newFileName;
            String getcompresspath="/compress/" + newFileName;
            System.out.println(getpath);
            System.out.println(getcompresspath);
            sb.append(getpath+","+getcompresspath+";");

            User user = (User) userDetailsService.loadUserByUsername(username);
            user.setAvatar("/"+newFileName);
            userService.updateUser(user);


        }

        String msg=sb.toString();
        return msg;
    }
}
