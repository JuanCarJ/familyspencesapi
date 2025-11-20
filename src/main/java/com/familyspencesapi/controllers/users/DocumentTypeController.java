package com.familyspencesapi.controllers.users;

import com.familyspencesapi.domain.users.DocumentType;
import com.familyspencesapi.repositories.users.DocumentTypeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/document-types")
public class DocumentTypeController {

    private final DocumentTypeRepository documentTypeRepository;

    public DocumentTypeController(DocumentTypeRepository documentTypeRepository) {
        this.documentTypeRepository = documentTypeRepository;
    }

    @GetMapping
    public List<DocumentType> findAll() {
        return documentTypeRepository.findAll();
    }

}
