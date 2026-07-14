package com.serviteca.orden.enums;

import java.util.Map;
import java.util.Set;

public enum OrdenEstado {

    PENDIENTE,
    EN_DIAGNOSTICO,
    EN_PROCESO,
    ESPERANDO_REPUESTOS,
    LISTO_PARA_ENTREGA,
    ENTREGADO,
    CANCELADO;

    private static final Map<OrdenEstado, Set<OrdenEstado>> TRANSICIONES = Map.of(
        PENDIENTE, Set.of(EN_DIAGNOSTICO, CANCELADO),
        EN_DIAGNOSTICO, Set.of(EN_PROCESO, ESPERANDO_REPUESTOS, CANCELADO),
        EN_PROCESO, Set.of(ESPERANDO_REPUESTOS, LISTO_PARA_ENTREGA, CANCELADO),
        ESPERANDO_REPUESTOS, Set.of(EN_PROCESO, CANCELADO),
        LISTO_PARA_ENTREGA, Set.of(ENTREGADO, CANCELADO),
        ENTREGADO, Set.of(),
        CANCELADO, Set.of()
    );

    public boolean puedeTransicionarA(OrdenEstado destino) {
        return TRANSICIONES.getOrDefault(this, Set.of()).contains(destino);
    }

    public boolean isTerminal() {
        return this == ENTREGADO || this == CANCELADO;
    }
}
