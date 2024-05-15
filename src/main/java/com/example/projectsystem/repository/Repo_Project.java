package com.example.projectsystem.repository;

import com.example.projectsystem.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Repo_Project extends JpaRepository<Project,Integer> {
}
