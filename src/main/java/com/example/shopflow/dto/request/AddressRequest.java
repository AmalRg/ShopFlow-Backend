package com.example.shopflow.dto.request;

import lombok.Data;

@Data
public class AddressRequest {
    private String  rue;
    private String  ville;
    private String  codePostal;
    private String  pays = "Tunisie";
    private boolean principal;
}