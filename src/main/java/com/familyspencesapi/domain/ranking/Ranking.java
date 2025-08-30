package com.familyspencesapi.domain.ranking;

import java.util.List;
import java.util.UUID;

public class Ranking {
    //TODO Agregar Objeto de gasto(Jose) e ingreso(Simon)
    private UUID idFamily;
    private List<String> nameFamilyMembers;
    //private Expencess expencess
    //private Earning earning

    public Ranking(UUID idFamily, List<String> nameFamilyMembers) {
        this.idFamily = idFamily;
        this.nameFamilyMembers = nameFamilyMembers;
    }

    public UUID getIdFamily() {
        return idFamily;
    }

    public void setIdFamily(final UUID idFamily) {
        this.idFamily = idFamily;
    }

    public List<String> getNameFamilyMembers() {
        return nameFamilyMembers;
    }

    public void setNameFamilyMembers(List<String> nameFamilyMembers) {
        this.nameFamilyMembers = nameFamilyMembers;
    }
}
