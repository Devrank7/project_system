package com.example.projectsystem.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project")
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String name;
    @Column
    private String company;
    @Column
    private String description;
    @Column
    private String image;
    @Column
    private String creator;
    @ManyToOne
    @JoinColumn
    private FUser user;

    public Project(String name, String company, String description, String image) {
        this.name = name;
        this.company = company;
        this.description = description;
        this.image = image;
    }
}
