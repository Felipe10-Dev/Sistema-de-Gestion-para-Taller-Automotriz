package com.serviteca.configuracion.service;

import com.serviteca.configuracion.dto.*;
import com.serviteca.configuracion.entity.*;
import com.serviteca.configuracion.repository.*;
import com.serviteca.rol.repository.RolRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConfiguracionService {

    private final EmpresaConfigRepository empresaConfigRepository;
    private final ParametroSistemaRepository parametroRepository;
    private final NumeracionConfigRepository numeracionRepository;
    private final ImpuestoConfigRepository impuestoRepository;
    private final HorarioAtencionRepository horarioRepository;
    private final DiaFestivoRepository festivoRepository;
    private final BackupRegistroRepository backupRepository;
    private final AuditoriaGlobalRepository auditoriaRepository;
    private final PermisoModuloRepository permisoRepository;
    private final RolRepository rolRepository;

    public ConfiguracionService(EmpresaConfigRepository empresaConfigRepository,
                                ParametroSistemaRepository parametroRepository,
                                NumeracionConfigRepository numeracionRepository,
                                ImpuestoConfigRepository impuestoRepository,
                                HorarioAtencionRepository horarioRepository,
                                DiaFestivoRepository festivoRepository,
                                BackupRegistroRepository backupRepository,
                                AuditoriaGlobalRepository auditoriaRepository,
                                PermisoModuloRepository permisoRepository,
                                RolRepository rolRepository) {
        this.empresaConfigRepository = empresaConfigRepository;
        this.parametroRepository = parametroRepository;
        this.numeracionRepository = numeracionRepository;
        this.impuestoRepository = impuestoRepository;
        this.horarioRepository = horarioRepository;
        this.festivoRepository = festivoRepository;
        this.backupRepository = backupRepository;
        this.auditoriaRepository = auditoriaRepository;
        this.permisoRepository = permisoRepository;
        this.rolRepository = rolRepository;
    }

    // --- Empresa ---

    public EmpresaConfigResponse obtenerEmpresa() {
        EmpresaConfig empresa = empresaConfigRepository.findByActivoTrue()
                .orElseThrow(() -> new EntityNotFoundException("No hay configuración de empresa activa"));
        return toEmpresaResponse(empresa);
    }

    @Transactional
    public EmpresaConfigResponse actualizarEmpresa(EmpresaConfigRequest request) {
        EmpresaConfig empresa = empresaConfigRepository.findByActivoTrue()
                .orElseThrow(() -> new EntityNotFoundException("No hay configuración de empresa activa"));
        if (request.getNombre() != null) empresa.setNombre(request.getNombre());
        if (request.getRazonSocial() != null) empresa.setRazonSocial(request.getRazonSocial());
        if (request.getNit() != null) empresa.setNit(request.getNit());
        if (request.getDireccion() != null) empresa.setDireccion(request.getDireccion());
        if (request.getCiudad() != null) empresa.setCiudad(request.getCiudad());
        if (request.getTelefono() != null) empresa.setTelefono(request.getTelefono());
        if (request.getEmail() != null) empresa.setEmail(request.getEmail());
        if (request.getSitioWeb() != null) empresa.setSitioWeb(request.getSitioWeb());
        if (request.getLogo() != null) empresa.setLogo(request.getLogo());
        if (request.getMoneda() != null) empresa.setMoneda(request.getMoneda());
        if (request.getZonaHoraria() != null) empresa.setZonaHoraria(request.getZonaHoraria());
        return toEmpresaResponse(empresaConfigRepository.save(empresa));
    }

    // --- Parámetros ---

    public List<ParametroSistemaResponse> listarParametros() {
        return parametroRepository.findAll().stream()
                .map(this::toParametroResponse)
                .collect(Collectors.toList());
    }

    public ParametroSistemaResponse obtenerParametroPorCodigo(String codigo) {
        ParametroSistema p = parametroRepository.findByCodigo(codigo)
                .orElseThrow(() -> new EntityNotFoundException("Parámetro no encontrado: " + codigo));
        return toParametroResponse(p);
    }

    @Transactional
    public ParametroSistemaResponse crearParametro(ParametroSistemaRequest request) {
        ParametroSistema p = new ParametroSistema();
        p.setCodigo(request.getCodigo());
        p.setNombre(request.getNombre());
        p.setValor(request.getValor());
        p.setDescripcion(request.getDescripcion());
        p.setTipo(request.getTipo() != null ? request.getTipo() : "STRING");
        if (request.getActivo() != null) p.setActivo(request.getActivo());
        return toParametroResponse(parametroRepository.save(p));
    }

    @Transactional
    public ParametroSistemaResponse actualizarParametro(Long id, ParametroSistemaRequest request) {
        ParametroSistema p = parametroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parámetro no encontrado"));
        if (request.getCodigo() != null) p.setCodigo(request.getCodigo());
        if (request.getNombre() != null) p.setNombre(request.getNombre());
        if (request.getValor() != null) p.setValor(request.getValor());
        if (request.getDescripcion() != null) p.setDescripcion(request.getDescripcion());
        if (request.getTipo() != null) p.setTipo(request.getTipo());
        if (request.getActivo() != null) p.setActivo(request.getActivo());
        return toParametroResponse(parametroRepository.save(p));
    }

    // --- Numeración ---

    public List<NumeracionConfigResponse> listarNumeraciones() {
        return numeracionRepository.findAll().stream()
                .map(this::toNumeracionResponse)
                .collect(Collectors.toList());
    }

    public NumeracionConfigResponse obtenerNumeracionPorModulo(String modulo) {
        NumeracionConfig n = numeracionRepository.findByModulo(modulo)
                .orElseThrow(() -> new EntityNotFoundException("Numeración no encontrada: " + modulo));
        return toNumeracionResponse(n);
    }

    @Transactional
    public NumeracionConfigResponse actualizarNumeracion(Long id, NumeracionConfigRequest request) {
        NumeracionConfig n = numeracionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Numeración no encontrada"));
        if (request.getPrefijo() != null) n.setPrefijo(request.getPrefijo());
        if (request.getSufijo() != null) n.setSufijo(request.getSufijo());
        if (request.getLongitud() > 0) n.setLongitud(request.getLongitud());
        if (request.getNumeroInicial() > 0) {
            n.setNumeroInicial(request.getNumeroInicial());
            n.setNumeroActual(request.getNumeroInicial() - 1);
        }
        n.setReinicioAnual(request.isReinicioAnual());
        if (request.getActivo() != null) n.setActivo(request.getActivo());
        return toNumeracionResponse(numeracionRepository.save(n));
    }

    @Transactional
    public String generarSiguienteNumero(String modulo) {
        NumeracionConfig n = numeracionRepository.findByModulo(modulo)
                .orElseThrow(() -> new EntityNotFoundException("Numeración no encontrada: " + modulo));
        n.setNumeroActual(n.getNumeroActual() + 1);
        numeracionRepository.save(n);
        String numStr = String.format("%0" + n.getLongitud() + "d", n.getNumeroActual());
        return n.getPrefijo() + numStr + n.getSufijo();
    }

    // --- Impuestos ---

    public List<ImpuestoConfigResponse> listarImpuestos() {
        return impuestoRepository.findAll().stream()
                .map(this::toImpuestoResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ImpuestoConfigResponse crearImpuesto(ImpuestoConfigRequest request) {
        ImpuestoConfig imp = new ImpuestoConfig();
        imp.setNombre(request.getNombre());
        imp.setPorcentaje(request.getPorcentaje());
        if (request.getActivo() != null) imp.setActivo(request.getActivo());
        imp.setAplicacionPorDefecto(request.isAplicacionPorDefecto());
        return toImpuestoResponse(impuestoRepository.save(imp));
    }

    @Transactional
    public ImpuestoConfigResponse actualizarImpuesto(Long id, ImpuestoConfigRequest request) {
        ImpuestoConfig imp = impuestoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Impuesto no encontrado"));
        if (request.getNombre() != null) imp.setNombre(request.getNombre());
        if (request.getPorcentaje() != null) imp.setPorcentaje(request.getPorcentaje());
        if (request.getActivo() != null) imp.setActivo(request.getActivo());
        imp.setAplicacionPorDefecto(request.isAplicacionPorDefecto());
        return toImpuestoResponse(impuestoRepository.save(imp));
    }

    // --- Horarios ---

    public List<HorarioAtencionResponse> listarHorarios() {
        return horarioRepository.findAll().stream()
                .map(this::toHorarioResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public HorarioAtencionResponse actualizarHorario(Long id, HorarioAtencionRequest request) {
        HorarioAtencion h = horarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Horario no encontrado"));
        if (request.getDiaSemana() != null) h.setDiaSemana(request.getDiaSemana());
        if (request.getHoraApertura() != null) h.setHoraApertura(request.getHoraApertura());
        if (request.getHoraCierre() != null) h.setHoraCierre(request.getHoraCierre());
        if (request.getActivo() != null) h.setActivo(request.getActivo());
        return toHorarioResponse(horarioRepository.save(h));
    }

    // --- Festivos ---

    public List<DiaFestivoResponse> listarFestivos() {
        return festivoRepository.findAll().stream()
                .map(this::toFestivoResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DiaFestivoResponse crearFestivo(DiaFestivoRequest request) {
        if (festivoRepository.findByFecha(request.getFecha()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un festivo en la fecha " + request.getFecha());
        }
        DiaFestivo f = new DiaFestivo();
        f.setFecha(request.getFecha());
        f.setDescripcion(request.getDescripcion());
        if (request.getActivo() != null) f.setActivo(request.getActivo());
        return toFestivoResponse(festivoRepository.save(f));
    }

    @Transactional
    public void eliminarFestivo(Long id, String motivo) {
        DiaFestivo f = festivoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Festivo no encontrado"));
        f.setActivo(false);
        f.setFechaEliminacion(java.time.LocalDateTime.now());
        f.setEliminadoPor(obtenerUsuarioActual());
        f.setMotivoEliminacion(motivo);
        festivoRepository.save(f);
    }

    // --- Backups ---

    public List<BackupRegistroResponse> listarBackups() {
        return backupRepository.findAllByOrderByFechaDesc().stream()
                .map(this::toBackupResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BackupRegistroResponse crearBackup(BackupRegistroRequest request) {
        BackupRegistro b = new BackupRegistro();
        b.setUsuario(request.getUsuario());
        b.setTamanio(request.getTamanio());
        b.setEstado(request.getEstado() != null ? request.getEstado() : "EXITOSO");
        b.setObservaciones(request.getObservaciones());
        return toBackupResponse(backupRepository.save(b));
    }

    // --- Auditoría ---

    public List<AuditoriaResponse> listarAuditoria() {
        return auditoriaRepository.findAllByOrderByFechaDesc().stream()
                .map(this::toAuditoriaResponse)
                .collect(Collectors.toList());
    }

    public List<AuditoriaResponse> listarAuditoriaPorModulo(String modulo) {
        return auditoriaRepository.findByModuloOrderByFechaDesc(modulo).stream()
                .map(this::toAuditoriaResponse)
                .collect(Collectors.toList());
    }

    // --- Permisos ---

    public List<PermisoModuloResponse> listarPermisosPorRol(Long rolId) {
        return permisoRepository.findByRolId(rolId).stream()
                .map(this::toPermisoResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PermisoModuloResponse crearPermiso(PermisoModuloRequest request) {
        PermisoModulo p = new PermisoModulo();
        p.setRol(rolRepository.findById(request.getRolId())
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado")));
        p.setModulo(request.getModulo());
        p.setPermiso(request.getPermiso());
        if (request.getActivo() != null) p.setActivo(request.getActivo());
        return toPermisoResponse(permisoRepository.save(p));
    }

    @Transactional
    public void eliminarPermiso(Long id, String motivo) {
        PermisoModulo p = permisoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Permiso no encontrado"));
        p.setActivo(false);
        p.setFechaEliminacion(java.time.LocalDateTime.now());
        p.setEliminadoPor(obtenerUsuarioActual());
        p.setMotivoEliminacion(motivo);
        permisoRepository.save(p);
    }

    private String obtenerUsuarioActual() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SISTEMA";
    }

    // --- Mappers ---

    private EmpresaConfigResponse toEmpresaResponse(EmpresaConfig e) {
        EmpresaConfigResponse r = new EmpresaConfigResponse();
        r.setId(e.getId());
        r.setNombre(e.getNombre());
        r.setRazonSocial(e.getRazonSocial());
        r.setNit(e.getNit());
        r.setDireccion(e.getDireccion());
        r.setCiudad(e.getCiudad());
        r.setTelefono(e.getTelefono());
        r.setEmail(e.getEmail());
        r.setSitioWeb(e.getSitioWeb());
        r.setLogo(e.getLogo());
        r.setMoneda(e.getMoneda());
        r.setZonaHoraria(e.getZonaHoraria());
        r.setFechaCreacion(e.getFechaCreacion());
        r.setFechaModificacion(e.getFechaModificacion());
        r.setActivo(e.isActivo());
        return r;
    }

    private ParametroSistemaResponse toParametroResponse(ParametroSistema p) {
        ParametroSistemaResponse r = new ParametroSistemaResponse();
        r.setId(p.getId());
        r.setCodigo(p.getCodigo());
        r.setNombre(p.getNombre());
        r.setValor(p.getValor());
        r.setDescripcion(p.getDescripcion());
        r.setTipo(p.getTipo());
        r.setActivo(p.isActivo());
        return r;
    }

    private NumeracionConfigResponse toNumeracionResponse(NumeracionConfig n) {
        NumeracionConfigResponse r = new NumeracionConfigResponse();
        r.setId(n.getId());
        r.setModulo(n.getModulo());
        r.setPrefijo(n.getPrefijo());
        r.setSufijo(n.getSufijo());
        r.setLongitud(n.getLongitud());
        r.setNumeroActual(n.getNumeroActual());
        r.setNumeroInicial(n.getNumeroInicial());
        r.setReinicioAnual(n.isReinicioAnual());
        r.setActivo(n.isActivo());
        return r;
    }

    private ImpuestoConfigResponse toImpuestoResponse(ImpuestoConfig i) {
        ImpuestoConfigResponse r = new ImpuestoConfigResponse();
        r.setId(i.getId());
        r.setNombre(i.getNombre());
        r.setPorcentaje(i.getPorcentaje());
        r.setActivo(i.isActivo());
        r.setAplicacionPorDefecto(i.isAplicacionPorDefecto());
        return r;
    }

    private HorarioAtencionResponse toHorarioResponse(HorarioAtencion h) {
        HorarioAtencionResponse r = new HorarioAtencionResponse();
        r.setId(h.getId());
        r.setDiaSemana(h.getDiaSemana());
        r.setHoraApertura(h.getHoraApertura());
        r.setHoraCierre(h.getHoraCierre());
        r.setActivo(h.isActivo());
        return r;
    }

    private DiaFestivoResponse toFestivoResponse(DiaFestivo f) {
        DiaFestivoResponse r = new DiaFestivoResponse();
        r.setId(f.getId());
        r.setFecha(f.getFecha());
        r.setDescripcion(f.getDescripcion());
        r.setActivo(f.isActivo());
        return r;
    }

    private BackupRegistroResponse toBackupResponse(BackupRegistro b) {
        BackupRegistroResponse r = new BackupRegistroResponse();
        r.setId(b.getId());
        r.setFecha(b.getFecha());
        r.setUsuario(b.getUsuario());
        r.setTamanio(b.getTamanio());
        r.setEstado(b.getEstado());
        r.setObservaciones(b.getObservaciones());
        return r;
    }

    private AuditoriaResponse toAuditoriaResponse(AuditoriaGlobal a) {
        AuditoriaResponse r = new AuditoriaResponse();
        r.setId(a.getId());
        r.setUsuario(a.getUsuario());
        r.setFecha(a.getFecha());
        r.setAccion(a.getAccion());
        r.setModulo(a.getModulo());
        r.setEntidad(a.getEntidad());
        r.setEntidadId(a.getEntidadId());
        r.setValorAnterior(a.getValorAnterior());
        r.setValorNuevo(a.getValorNuevo());
        return r;
    }

    private PermisoModuloResponse toPermisoResponse(PermisoModulo p) {
        PermisoModuloResponse r = new PermisoModuloResponse();
        r.setId(p.getId());
        r.setRolId(p.getRol().getId());
        r.setRolNombre(p.getRol().getNombre());
        r.setModulo(p.getModulo());
        r.setPermiso(p.getPermiso());
        r.setActivo(p.isActivo());
        return r;
    }
}
