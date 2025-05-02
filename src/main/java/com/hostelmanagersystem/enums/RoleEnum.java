package com.hostelmanagersystem.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum RoleEnum {

    RENTER("RENTER"),
    OWNER("OWNER"),;

    String roleName;

}
