package com.familyspencesapi.controllers.ranking.response;

import java.util.Map;

@SuppressWarnings("java:S6218")
public record SuccessfulResponse( Map<String, Double> ranking) implements Response {

}
