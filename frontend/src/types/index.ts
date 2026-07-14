export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
  timestamp: string
}

export interface PagedResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  last: boolean
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  nombre: string
  apellido: string
  email?: string
}

export interface LoginResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  username: string
  rol: string
  nombre: string
}

export interface Usuario {
  id: number
  username: string
  nombre: string
  apellido: string
  email: string
  rol: string
  activo: boolean
}

export interface Rol {
  id: number
  nombre: string
  descripcion: string
}

export interface Cliente {
  id: number
  tipoDocumento: string
  numeroDocumento: string
  nombre: string
  apellido: string
  telefono: string
  email: string
  direccion: string
  activo: boolean
}

export interface Vehiculo {
  id: number
  placa: string
  marca: string
  linea: string
  modelo: string
  color: string
  cilindraje: string
  tipoVehiculo: string
  clienteId: number
  clienteNombre: string
  activo: boolean
}

export interface Servicio {
  id: number
  nombre: string
  descripcion: string
  precio: number
  duracionEstimadaMinutos: number
  categoriaId: number
  categoriaNombre: string
  activo: boolean
}

export interface Categoria {
  id: number
  nombre: string
  descripcion: string
  activo: boolean
}

export interface Producto {
  id: number
  codigo: string
  nombre: string
  descripcion: string
  precioCompra: number
  precioVenta: number
  stockMinimo: number
  categoriaId: number
  categoriaNombre: string
  proveedorId: number
  proveedorNombre: string
  activo: boolean
}

export interface Proveedor {
  id: number
  nit: string
  nombre: string
  contacto: string
  telefono: string
  email: string
  direccion: string
  activo: boolean
}

export interface Inventario {
  id: number
  productoId: number
  productoCodigo: string
  productoNombre: string
  cantidadActual: number
  cantidadMinima: number
  bajoStock: boolean
  ubicacion: string
}

export interface OrdenTrabajo {
  id: number
  numeroOrden: string
  clienteId: number
  clienteNombre: string
  clienteDocumento: string
  vehiculoId: number
  vehiculoPlaca: string
  vehiculoMarca: string
  vehiculoLinea: string
  kilometraje: number
  estado: string
  fechaIngreso: string
  fechaSalida: string
  tecnicoId: number
  tecnicoNombre: string
  observaciones: string
  totalServicios: number
  totalProductos: number
  totalGeneral: number
  servicios: OrdenServicio[]
  productos: OrdenProducto[]
}

export interface OrdenServicio {
  id: number
  servicioId: number
  servicioNombre: string
  cantidad: number
  precioUnitario: number
  subtotal: number
  observaciones: string
}

export interface OrdenProducto {
  id: number
  productoId: number
  productoNombre: string
  productoCodigo: string
  cantidad: number
  precioUnitario: number
  subtotal: number
}

export interface DashboardData {
  totalClientes: number
  totalVehiculos: number
  ordenesPendientes: number
  ordenesEnProceso: number
  ordenesCompletadasHoy: number
  productosBajoStock: number
  totalUsuarios: number
  ingresosHoy: number
}
