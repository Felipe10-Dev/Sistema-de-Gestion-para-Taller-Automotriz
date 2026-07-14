package com.serviteca.configuracion.controller;

import com.serviteca.configuracion.dto.*;
import com.serviteca.configuracion.service.ConfiguracionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/configuracion")
public class ConfiguracionController {

    private final ConfiguracionService service;

    public ConfiguracionController(ConfiguracionService service) {
        this.service = service;
    }

    // --- Empresa ---
    @GetMapping("/empresa")
    public ResponseEntity<EmpresaConfigResponse> obtenerEmpresa() {
        return ResponseEntity.ok(service.obtenerEmpresa());
    }

    @PutMapping("/empresa")
    public ResponseEntity<EmpresaConfigResponse> actualizarEmpresa(@RequestBody EmpresaConfigRequest request) {
        return ResponseEntity.ok(service.actualizarEmpresa(request));
    }

    // --- Parámetros ---
    @GetMapping("/parametros")
    public ResponseEntity<List<ParametroSistemaResponse>> listarParametros() {
        return ResponseEntity.ok(service.listarParametros());
    }

    @GetMapping("/parametros/{codigo}")
    public ResponseEntity<ParametroSistemaResponse> obtenerParametro(@PathVariable String codigo) {
        return ResponseEntity.ok(service.obtenerParametroPorCodigo(codigo));
    }

    @PostMapping("/parametros")
    public ResponseEntity<ParametroSistemaResponse> crearParametro(@RequestBody ParametroSistemaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearParametro(request));
    }

    @PutMapping("/parametros/{id}")
    public ResponseEntity<ParametroSistemaResponse> actualizarParametro(@PathVariable Long id,
                                                                         @RequestBody ParametroSistemaRequest request) {
        return ResponseEntity.ok(service.actualizarParametro(id, request));
    }

    // --- Numeración ---
    @GetMapping("/numeracion")
    public ResponseEntity<List<NumeracionConfigResponse>> listarNumeraciones() {
        return ResponseEntity.ok(service.listarNumeraciones());
    }

    @GetMapping("/numeracion/{modulo}")
    public ResponseEntity<NumeracionConfigResponse> obtenerNumeracion(@PathVariable String modulo) {
        return ResponseEntity.ok(service.obtenerNumeracionPorModulo(modulo));
    }

    @PutMapping("/numeracion/{id}")
    public ResponseEntity<NumeracionConfigResponse> actualizarNumeracion(@PathVariable Long id,
                                                                          @RequestBody NumeracionConfigRequest request) {
        return ResponseEntity.ok(service.actualizarNumeracion(id, request));
    }

    @PostMapping("/numeracion/{modulo}/generar")
    public ResponseEntity<String> generarNumero(@PathVariable String modulo) {
        return ResponseEntity.ok(service.generarSiguienteNumero(modulo));
    }

    // --- Impuestos ---
    @GetMapping("/impuestos")
    public ResponseEntity<List<ImpuestoConfigResponse>> listarImpuestos() {
        return ResponseEntity.ok(service.listarImpuestos());
    }

    @PostMapping("/impuestos")
    public ResponseEntity<ImpuestoConfigResponse> crearImpuesto(@RequestBody ImpuestoConfigRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearImpuesto(request));
    }

    @PutMapping("/impuestos/{id}")
    public ResponseEntity<ImpuestoConfigResponse> actualizarImpuesto(@PathVariable Long id,
                                                                      @RequestBody ImpuestoConfigRequest request) {
        return ResponseEntity.ok(service.actualizarImpuesto(id, request));
    }

    // --- Horarios ---
    @GetMapping("/horarios")
    public ResponseEntity<List<HorarioAtencionResponse>> listarHorarios() {
        return ResponseEntity.ok(service.listarHorarios());
    }

    @PutMapping("/horarios/{id}")
    public ResponseEntity<HorarioAtencionResponse> actualizarHorario(@PathVariable Long id,
                                                                      @RequestBody HorarioAtencionRequest request) {
        return ResponseEntity.ok(service.actualizarHorario(id, request));
    }

    // --- Festivos ---
    @GetMapping("/festivos")
    public ResponseEntity<List<DiaFestivoResponse>> listarFestivos() {
        return ResponseEntity.ok(service.listarFestivos());
    }

    @PostMapping("/festivos")
    public ResponseEntity<DiaFestivoResponse> crearFestivo(@RequestBody DiaFestivoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearFestivo(request));
    }

    @DeleteMapping("/festivos/{id}")
    public ResponseEntity<Void> eliminarFestivo(@PathVariable Long id,
                                                @RequestParam(defaultValue = "Eliminaci\u00f3n manual") String motivo) {
        service.eliminarFestivo(id, motivo);
        return ResponseEntity.noContent().build();
    }

    // --- Backups ---
    @GetMapping("/backups")
    public ResponseEntity<List<BackupRegistroResponse>> listarBackups() {
        return ResponseEntity.ok(service.listarBackups());
    }

    @PostMapping("/backups")
    public ResponseEntity<BackupRegistroResponse> crearBackup(@RequestBody BackupRegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearBackup(request));
    }

    // --- Auditoría ---
    @GetMapping("/auditoria")
    public ResponseEntity<List<AuditoriaResponse>> listarAuditoria(
            @RequestParam(required = false) String modulo) {
        if (modulo != null) {
            return ResponseEntity.ok(service.listarAuditoriaPorModulo(modulo));
        }
        return ResponseEntity.ok(service.listarAuditoria());
    }

    // --- Permisos ---
    @GetMapping("/permisos")
    public ResponseEntity<List<PermisoModuloResponse>> listarPermisos(@RequestParam Long rolId) {
        return ResponseEntity.ok(service.listarPermisosPorRol(rolId));
    }

    @PostMapping("/permisos")
    public ResponseEntity<PermisoModuloResponse> crearPermiso(@RequestBody PermisoModuloRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearPermiso(request));
    }

    @DeleteMapping("/permisos/{id}")
    public ResponseEntity<Void> eliminarPermiso(@PathVariable Long id,
                                                @RequestParam(defaultValue = "Eliminaci\u00f3n manual") String motivo) {
        service.eliminarPermiso(id, motivo);
        return ResponseEntity.noContent().build();
    }
}
