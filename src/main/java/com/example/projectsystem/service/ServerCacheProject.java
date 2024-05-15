package com.example.projectsystem.service;

import com.example.projectsystem.model.Project;
import com.example.projectsystem.repository.Repo_Project;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ServerCacheProject {

    private Repo_Project repoProject;

    public boolean isObjectInCache(CacheManager cacheManager,String cacheName, Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            return cache.get(key) != null;
        }

        return false;
    }

    @Autowired
    public ServerCacheProject(Repo_Project repoProject) {
        this.repoProject = repoProject;
    }

    @Cacheable(cacheNames = "proj", key = "#id")
    public Project ofCache(int id) {
        return repoProject.findById(id).orElseThrow();
    }
    @CachePut(cacheNames = "proj",key = "#id")
    public Project putCache(int id,Project project) {
        return project;
    }
    @CacheEvict(cacheNames = "proj",key = "#id")
    public void cacheEvict(int id) {}
}
