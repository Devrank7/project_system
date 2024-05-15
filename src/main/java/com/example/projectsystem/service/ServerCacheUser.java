package com.example.projectsystem.service;

import com.example.projectsystem.model.FUser;
import com.example.projectsystem.model.Project;
import com.example.projectsystem.repository.Repo_Project;
import com.example.projectsystem.repository.Repo_User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ServerCacheUser {

    private Repo_User repoUser;


    public boolean isObjectInCache(CacheManager cacheManager, String cacheName, Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            return cache.get(key) != null;
        }

        return false;
    }

    @Autowired
    public ServerCacheUser(Repo_User repoUser) {
        this.repoUser = repoUser;
    }

    @Cacheable(value = "usr", key = "#id")
    public FUser ofCache(String id) {
        return repoUser.findByName(id);
    }
    @CachePut(cacheNames = "usr",key = "#id")
    public FUser putCache(String id,FUser project) {
        return project;
    }
    @CacheEvict(cacheNames = "usr",key = "#id")
    public void cacheEvict(String id) {}
}
