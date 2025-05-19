package com.actividad.clase.models;

import lombok.Data;
import java.util.UUID;
/*
@Data
public class VideoJuego {
    private UUID id;
    private String titulo;
    private String anioLanzamiento;
    private String plataforma;
    private Integer duracionHoras;
}*/
import lombok.Data;

@Data
public class VideoJuego {
    private String id;
    private String titulo;
    private int anioLanzamiento;
    private String plataforma;
    private int duracionHoras;
}
