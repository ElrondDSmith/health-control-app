package org.snp.blood_pressure.entity;

import lombok.Data;

@Data
public class AppUser {

    public AppUser() {
    }

    public AppUser(String name, String email, String tgId) {
        this.name = name;
        this.email = email;
        this.tgId = tgId;
    }

    private Integer id;
    private String name;
    private String email;
    private String tgId;
}
