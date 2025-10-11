package com.familyspencesapi.domain.home;

import java.time.LocalDate;
import java.util.UUID;

public record MonthlyClosing(UUID familyId,
                            GeneralBalance balance,
                            LocalDate closingDate) {}
