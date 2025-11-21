package com.familyspencesapi.controllers.users;

import com.familyspencesapi.domain.users.Relationship;
import com.familyspencesapi.repositories.users.RelationshipRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/relationships")
public class RelationshipController {

    private final RelationshipRepository relationshipRepository;

    public RelationshipController(RelationshipRepository relationshipRepository) {
        this.relationshipRepository = relationshipRepository;
    }

    @GetMapping
    public List<Relationship> findAll() {
        return relationshipRepository.findAll();
    }
}
