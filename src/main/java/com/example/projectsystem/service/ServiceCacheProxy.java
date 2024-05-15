package com.example.projectsystem.service;

import com.example.projectsystem.model.FUser;
import com.example.projectsystem.model.Project;
import com.example.projectsystem.repository.Repo_Project;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ServiceCacheProxy {

    private Repo_Project repoProject;
    
    private ServerCacheProject serverCacheproject;
    private CacheManager cacheManager;

    @Autowired
    public ServiceCacheProxy(Repo_Project repoProject, ServerCacheProject serverCacheproject, CacheManager cacheManager) {
        this.repoProject = repoProject;
        this.serverCacheproject = serverCacheproject;
        this.cacheManager = cacheManager;
    }

    private final int COUNT = 3;

    private List<Integer> list = new ArrayList<>();

    public Project callCache(int id) {
        if (serverCacheproject.isObjectInCache(cacheManager, "proj", id)) {
            log.warn("lv1");
            return serverCacheproject.ofCache(id);
        } else {
            list.add(id);
            log.warn("lv2");
            if (Collections.frequency(list, id) > COUNT) {
                log.warn("lv3");
                return serverCacheproject.ofCache(id);
            }
            return repoProject.findById(id).orElseThrow();
        }
    }

    public Project putCache(int id, Project fUser) {
        return serverCacheproject.putCache(id, fUser);
    }

    public void evict(int id) {
        serverCacheproject.cacheEvict(id);
    }
    public boolean isCache(int id) {
        return serverCacheproject.isObjectInCache(cacheManager,"proj",id);
    }

}
