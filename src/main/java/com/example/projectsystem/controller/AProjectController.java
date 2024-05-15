package com.example.projectsystem.controller;

import com.example.projectsystem.jwt.CookieUtils;
import com.example.projectsystem.jwt.JwtUtils;
import com.example.projectsystem.model.FUser;
import com.example.projectsystem.model.Project;
import com.example.projectsystem.repository.Repo_Project;
import com.example.projectsystem.repository.Repo_User;
import com.example.projectsystem.service.ServerCacheProject;
import com.example.projectsystem.service.ServiceCacheProxy;
import com.example.projectsystem.service.ServiceCacheProxyUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/project")
@Slf4j
public class AProjectController {

    private Repo_Project repoProject;
    private Repo_User repoUser;
    private JwtUtils jwtUtils;

    private ServiceCacheProxy serverCacheProject;

    private ServiceCacheProxyUser serviceCacheProxyUser;


    @Value("${upload.path}")
    private String img;


    @Autowired
    public AProjectController(Repo_Project repoProject, Repo_User repoUser, JwtUtils jwtUtils, ServiceCacheProxy serverCacheProject, ServiceCacheProxyUser serviceCacheProxyUser) {
        this.repoProject = repoProject;
        this.repoUser = repoUser;
        this.jwtUtils = jwtUtils;
        this.serverCacheProject = serverCacheProject;
        this.serviceCacheProxyUser = serviceCacheProxyUser;
    }

    @GetMapping("/all")
    public String findAll(Model model) {
        model.addAttribute("pro", repoProject.findAll());
        return "pros";
    }

    @GetMapping("/{id}")
    public String get_pro(@PathVariable("id") int id, Model model) {
        model.addAttribute("pro", serverCacheProject.callCache(id));
        return "pro";
    }

    @GetMapping("/add")
    public String add_pro(Model model) {
        model.addAttribute("pro", new Project());
        return "add";
    }

    @PostMapping("/add")
    public String add_post(@ModelAttribute("pro") Project project, @RequestParam("file") MultipartFile multipartFile, HttpServletRequest request) throws IOException {
        if (!multipartFile.isEmpty()) {
            File file1 = new File(img);
            if (!file1.exists()) {
                file1.mkdir();
            }
            String random = UUID.randomUUID().toString();
            String photo = random + "." + multipartFile.getOriginalFilename();
            multipartFile.transferTo(new File(img + "/" + photo));
            project.setImage(photo);
        }
        FUser fUser1 = serviceCacheProxyUser.callCache(jwtUtils.getUsername(CookieUtils.getCookie(request)));
        project.setCreator(fUser1.getName());
        repoProject.save(project);
        return "redirect:/project/all";
    }

    @GetMapping("/update/{id}")
    public String updare(@PathVariable("id") int id, Model model) {
        model.addAttribute("pro", serverCacheProject.callCache(id));
        return "up";
    }

    @PostMapping("/update/{id}")
    public String updare(@ModelAttribute("pro") Project project, @RequestParam("file") MultipartFile multipartFile, @PathVariable("id") int id
    ) throws IOException {
        if (!multipartFile.isEmpty()) {
            File file1 = new File(img);
            if (!file1.exists()) {
                file1.mkdir();
            }
            String random = UUID.randomUUID().toString();
            String photo = random + "." + multipartFile.getOriginalFilename();
            multipartFile.transferTo(new File(img + "/" + photo));
            project.setImage(photo);
        }
        Project project1 = repoProject.findById(id).orElseThrow();
        project.setCreator(project1.getCreator());
        project.setId(project1.getId());
        if (serverCacheProject.isCache(project1.getId())) {
            serverCacheProject.putCache(id,project);
        }
        repoProject.save(project);
        return "redirect:/project/all";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        if (serverCacheProject.isCache(id)) {
            serverCacheProject.evict(id);
        }
        repoProject.deleteById(id);
        return "redirect:/project/all";
    }

    @GetMapping("/buy/{id}")
    public String toReciev(@PathVariable("id") int id) {
        return "buy";
    }

    @PostMapping("/buy/{id}")
    public String toBuy(@PathVariable("id") int id, @RequestParam(value = "check", defaultValue = "false") boolean check, HttpServletRequest request) {
        if (!check) {
            log.warn("It does not want the project");
            return "redirect:/project/all";
        }
        FUser fUser1 = serviceCacheProxyUser.callCache(jwtUtils.getUsername(CookieUtils.getCookie(request)));
        Project project = serverCacheProject.callCache(id);
        project.setUser(fUser1);
        if (serverCacheProject.isCache(id)) {
            serverCacheProject.putCache(id,project);
        }
        repoProject.save(project);
        return "redirect:/project/all";

    }

    @GetMapping("/detach/{id}")
    public String detachProject(@PathVariable("id") int id) {
        Project project = serverCacheProject.callCache(id);
        project.setUser(null);
        if (serverCacheProject.isCache(project.getId())) {
            serverCacheProject.putCache(id,project);
        }
        repoProject.save(project);
        log.info("the project was succeeded to delete");
        return "redirect:/project/all";
    }

}
