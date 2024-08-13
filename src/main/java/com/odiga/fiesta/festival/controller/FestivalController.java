package com.odiga.fiesta.festival.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Festival", description = "페스티벌 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/festivals")
public class FestivalController {
}
