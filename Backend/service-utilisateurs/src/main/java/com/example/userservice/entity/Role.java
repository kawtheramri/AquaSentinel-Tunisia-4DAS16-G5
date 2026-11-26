package com.example.userservice.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="roles")
public class Role {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Enumerated(EnumType.STRING) @Column(nullable=false, unique=true, length=30) private RoleName name;
 @Column(length=255) private String description;
 @OneToMany(mappedBy="role") private List<User> users=new ArrayList<>();
 protected Role() {}
 public Role(RoleName name,String description){this.name=name;this.description=description;}
 public Long getId(){return id;} public RoleName getName(){return name;} public String getDescription(){return description;}
 public void setName(RoleName name){this.name=name;} public void setDescription(String description){this.description=description;}
}
