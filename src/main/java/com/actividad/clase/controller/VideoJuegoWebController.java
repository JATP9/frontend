package com.actividad.clase.controller;

import com.actividad.clase.models.VideoJuego;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Controller
public class VideoJuegoWebController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String URL_BASE = "http://localhost:8080/api/v1/videojuegos";

    // Listar videojuegos con paginación
    @GetMapping("/videojuegos")
    public String listarVideojuegos(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        String url = URL_BASE + "?page=" + page + "&size=" + size;

        ParameterizedTypeReference<Map<String, Object>> responseType =
                new ParameterizedTypeReference<Map<String, Object>>() {};

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                responseType
        );

        Map<String, Object> result = response.getBody();

        List<LinkedHashMap<String, Object>> contentRaw = (List<LinkedHashMap<String, Object>>) result.get("content");
        List<VideoJuego> videojuegos = new ArrayList<>();
        for (LinkedHashMap<String, Object> item : contentRaw) {
            VideoJuego vj = new VideoJuego();
            vj.setId((String) item.get("id"));
            vj.setTitulo((String) item.get("titulo"));
            vj.setPlataforma((String) item.get("plataforma"));
            vj.setAnioLanzamiento((int) item.get("anioLanzamiento"));
            vj.setDuracionHoras((int) item.get("duracionHoras"));
            videojuegos.add(vj);
        }

        int totalPages = (int) result.get("totalPages");
        model.addAttribute("videojuegos", videojuegos);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "videojuegos";
    }

    // Mostrar formulario para crear nuevo videojuego
    @GetMapping("/videojuegos/nuevo")
    public String nuevoVideojuego(Model model) {
        model.addAttribute("videojuego", new VideoJuego());
        return "formulario_videojuego";
    }

    // Mostrar formulario para editar videojuego existente
    @GetMapping("/videojuegos/editar/{id}")
    public String editarVideojuego(@PathVariable String id, Model model) {
        VideoJuego videojuego = restTemplate.getForObject(URL_BASE + "/" + id, VideoJuego.class);
        model.addAttribute("videojuego", videojuego);
        return "formulario_videojuego";
    }

    // Guardar videojuego (crear o actualizar)
    @PostMapping("/videojuegos/guardar")
    public String guardarVideojuego(@ModelAttribute VideoJuego videojuego) {
        if (videojuego.getId() == null || videojuego.getId().isEmpty()) {
            // Crear nuevo
            restTemplate.postForEntity(URL_BASE, videojuego, VideoJuego.class);
        } else {
            // Actualizar existente
            HttpEntity<VideoJuego> requestUpdate = new HttpEntity<>(videojuego);
            restTemplate.exchange(URL_BASE + "/" + videojuego.getId(), HttpMethod.PUT, requestUpdate, Void.class);
        }
        return "redirect:/videojuegos";
    }

    // Actualizar videojuego (solo para editar)
    @PostMapping("/videojuego/actualizar/{id}")
    public String actualizarVideojuego(@PathVariable String id, @ModelAttribute VideoJuego videojuego) {
        videojuego.setId(id);
        HttpEntity<VideoJuego> requestUpdate = new HttpEntity<>(videojuego);
        restTemplate.exchange(URL_BASE + "/" + id, HttpMethod.PUT, requestUpdate, Void.class);
        return "redirect:/videojuegos";
    }

    // Eliminar videojuego
    @GetMapping("/videojuegos/eliminar/{id}")
    public String eliminarVideojuego(@PathVariable String id) {
        restTemplate.delete(URL_BASE + "/" + id);
        return "redirect:/videojuegos";
    }
}
