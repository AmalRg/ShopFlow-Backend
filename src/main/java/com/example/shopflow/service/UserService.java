package com.example.shopflow.service;

import com.example.shopflow.dto.request.AddressRequest;
import com.example.shopflow.dto.request.UpdateProfileRequest;
import com.example.shopflow.dto.response.AddressResponse;
import com.example.shopflow.dto.response.UserProfileResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional

public interface UserService {



    UserProfileResponse getMe(String email) ;

    UserProfileResponse updateMe(String email, UpdateProfileRequest req) ;


    List<AddressResponse> getAddresses(String email) ;

    AddressResponse addAddress(String email, AddressRequest req) ;


    void deleteAddress(String email, Long addressId) ;


    List<UserProfileResponse> getAllUsers() ;

    UserProfileResponse toggleActive(Long id) ;

}