package com.codewithiyke.dreamshops.model;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Role {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @ManyToMany(mappedBy = "roles")
  private Collection<User> users = new HashSet<>();

  public Role(String name) {
    this.name = name;
  }
}
