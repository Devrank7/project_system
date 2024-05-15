package com.example.projectsystem.repository;

import com.example.projectsystem.model.FUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

@Repository
public interface Repo_User extends JpaRepository<FUser,Integer> {
    @Query(value = "select * from usr where name = :name",nativeQuery = true)
    FUser findByName(@Param("name") String name);
}
