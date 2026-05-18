package com.example.shopflow.serviceImpl;

import com.example.shopflow.dto.request.AddressRequest;
import com.example.shopflow.dto.request.UpdateProfileRequest;
import com.example.shopflow.dto.response.AddressResponse;
import com.example.shopflow.dto.response.UserProfileResponse;
import com.example.shopflow.entity.Address;
import com.example.shopflow.entity.User;
import com.example.shopflow.exception.BusinessException;
import com.example.shopflow.exception.ResourceNotFoundException;
import com.example.shopflow.mapper.UserMapper;
import com.example.shopflow.repository.AddressRepository;
import com.example.shopflow.repository.UserRepository;
import com.example.shopflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl  implements UserService {

    private final UserRepository    userRepository;
    private final AddressRepository addressRepository;
    private final UserMapper        userMapper;

    // ── Profil ───────────────────────────────────────────────

    @Transactional(readOnly = true)
    @Override
    public UserProfileResponse getMe(String email) {
        return userMapper.toDto(findByEmail(email));
    }

    public UserProfileResponse updateMe(String email, UpdateProfileRequest req) {
        User user = findByEmail(email);

        if (req.getPrenom() != null && !req.getPrenom().isBlank())
            user.setPrenom(req.getPrenom());
        if (req.getNom() != null && !req.getNom().isBlank())
            user.setNom(req.getNom());

        return userMapper.toDto(userRepository.save(user));
    }

    // ── Adresses ─────────────────────────────────────────────

    @Transactional(readOnly = true)
    @Override
    public List<AddressResponse> getAddresses(String email) {
        User user = findByEmail(email);
        return addressRepository.findByUserId(user.getId())
                .stream()
                .map(userMapper::toAddressDto)
                .toList();
    }
    @Override
    public AddressResponse addAddress(String email, AddressRequest req) {
        User user = findByEmail(email);
        List<Address> existing = addressRepository.findByUserId(user.getId());

        if (existing.isEmpty() || req.isPrincipal()) {
            existing.forEach(a -> {
                a.setPrincipal(false);
                addressRepository.save(a);
            });
        }

        Address address = new Address();
        address.setUser(user);
        address.setRue(req.getRue());
        address.setVille(req.getVille());
        address.setCodePostal(req.getCodePostal());
        address.setPays(req.getPays());
        address.setPrincipal(existing.isEmpty() || req.isPrincipal());

        return userMapper.toAddressDto(addressRepository.save(address));
    }
    @Override
    public void deleteAddress(String email, Long addressId) {
        User user = findByEmail(email);
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Adresse non trouvée : " + addressId));

        if (!address.getUser().getId().equals(user.getId()))
            throw new BusinessException("Non autorisé");

        addressRepository.delete(address);
    }

    // ── Admin ─────────────────────────────────────────────────

    @Transactional(readOnly = true)
    @Override
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }
    @Override
    public UserProfileResponse toggleActive(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur non trouvé : " + id));
        user.setActif(!user.isActif());
        return userMapper.toDto(userRepository.save(user));
    }

    // ── Helper ────────────────────────────────────────────────

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur non trouvé"));
    }
}