package com.familyspencesapi.controllers.expense;

public class ApiResponse {
    private String mensaje;
    private String idExpense;
    private long timestamp;

    public ApiResponse(String mensaje) {
        this.mensaje = mensaje;
        this.timestamp = System.currentTimeMillis();
    }

    public ApiResponse(String mensaje, String idExpense) {
        this.mensaje = mensaje;
        this.idExpense = idExpense;
        this.timestamp = System.currentTimeMillis();
    }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getIdExpense() { return idExpense; }
    public void setIdExpense(String idExpense) { this.idExpense = idExpense; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

}
