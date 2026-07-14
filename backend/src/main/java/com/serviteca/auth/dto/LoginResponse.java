package com.serviteca.auth.dto;

public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private String username;
    private String rol;
    private String nombre;
    private Long empresaId;
    private Long sedeId;

    public LoginResponse() {}

    public LoginResponse(String accessToken, String refreshToken, String username, String rol, String nombre,
                         Long empresaId, Long sedeId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.username = username;
        this.rol = rol;
        this.nombre = nombre;
        this.empresaId = empresaId;
        this.sedeId = sedeId;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }
    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }
}
