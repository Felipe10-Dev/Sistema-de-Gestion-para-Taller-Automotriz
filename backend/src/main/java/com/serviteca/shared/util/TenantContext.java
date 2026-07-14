package com.serviteca.shared.util;

public class TenantContext {
    private static final ThreadLocal<Long> currentEmpresaId = new ThreadLocal<>();
    private static final ThreadLocal<Long> currentSedeId = new ThreadLocal<>();

    public static void setEmpresaId(Long empresaId) { currentEmpresaId.set(empresaId); }
    public static Long getEmpresaId() { return currentEmpresaId.get(); }
    public static void setSedeId(Long sedeId) { currentSedeId.set(sedeId); }
    public static Long getSedeId() { return currentSedeId.get(); }
    public static void clear() {
        currentEmpresaId.remove();
        currentSedeId.remove();
    }
}
