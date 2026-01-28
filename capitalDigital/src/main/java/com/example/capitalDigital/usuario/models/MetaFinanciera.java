package com.example.capitalDigital.usuario.models;

public class MetaFinanciera {
    private String nombre;
    private String fechaInicio;
    private String fechaFin;
    private Double montoRequerido;
    private Double montoActual;

    public MetaFinanciera() {}

    public MetaFinanciera(String nombre, String fechaInicio, String fechaFin, Double montoRequerido) {
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.montoRequerido = montoRequerido;
        this.montoActual = 0.0;
    }

    // Getters y setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }

    public Double getMontoRequerido() { return montoRequerido; }
    public void setMontoRequerido(Double montoRequerido) { this.montoRequerido = montoRequerido; }

    public Double getMontoActual() { return montoActual; }
    public void setMontoActual(Double montoActual) { this.montoActual = montoActual; }
}