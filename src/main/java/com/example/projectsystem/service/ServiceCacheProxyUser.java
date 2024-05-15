package com.example.projectsystem.service;

import com.example.projectsystem.model.FUser;
import com.example.projectsystem.model.Project;
import com.example.projectsystem.repository.Repo_Project;
import com.example.projectsystem.repository.Repo_User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ServiceCacheProxyUser {


    private Repo_User repoUser;

    private ServerCacheUser serverCacheUser;
    private CacheManager cacheManager;

    @Autowired
    public ServiceCacheProxyUser(Repo_User repoUser, ServerCacheUser serverCacheUser, CacheManager cacheManager) {
        this.repoUser = repoUser;
        this.serverCacheUser = serverCacheUser;
        this.cacheManager = cacheManager;
    }

    private final int COUNT = 3;

    private List<String> list = new ArrayList<>();

    public FUser callCache(String s) {
        if (serverCacheUser.isObjectInCache(cacheManager,"usr",s)) {
            log.warn("lv1");
            return serverCacheUser.ofCache(s);
        } else {
            list.add(s);
            log.warn("lv2");
            if (Collections.frequency(list,s) > COUNT) {
                log.warn("lv3");
                return serverCacheUser.ofCache(s);
            }
            return repoUser.findByName(s);
        }
    }
    public FUser putCache(String id,FUser fUser) {
        return serverCacheUser.putCache(id,fUser);
    }
    public void evict(String id) {
        serverCacheUser.cacheEvict(id);
    }

    public boolean isCache(String id) {
        return serverCacheUser.isObjectInCache(cacheManager,"usr",id);
    }

}
