package com.familyspencesapi.service.family;

import com.familyspencesapi.domain.family.FamilyMemberDomain;
import org.springframework.stereotype.Service;
import com.familyspencesapi.repositories.family.FamilyMemberRepository;


import java.util.*;

@Service
public class FamilyMemberService {

    private final FamilyMemberRepository repository;

    public FamilyMemberService(FamilyMemberRepository repository) {
        this.repository = repository;
    }

    // Get all family members
    public List<FamilyMemberDomain> getAll() {
        return repository.findAll();
    }

    // Get one by ID
    public Optional<FamilyMemberDomain> getById(UUID id) {
        return repository.findById(id);
    }

    // Create
    public FamilyMemberDomain create(FamilyMemberDomain member) {
        return repository.save(member);
    }

    // Update
    public FamilyMemberDomain update(UUID id, FamilyMemberDomain member) {
        if (repository.existsById(id)) {
            member.setId(id);
            return repository.save(member);
        }
        return null;
    }

    // Delete
    public boolean delete(UUID id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}