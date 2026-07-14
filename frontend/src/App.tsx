import { Routes, Route } from 'react-router-dom'
import MainLayout from './layouts/MainLayout'
import ProtectedRoute from './routes/ProtectedRoute'
import LoginPage from './pages/auth/LoginPage'
import DashboardPage from './pages/dashboard/DashboardPage'
import OrdenesPage from './pages/ordenes/OrdenesPage'
import ClientesPage from './pages/clientes/ClientesPage'
import VehiculosPage from './pages/vehiculos/VehiculosPage'
import ServiciosPage from './pages/servicios/ServiciosPage'
import ProductosPage from './pages/productos/ProductosPage'
import InventarioPage from './pages/inventario/InventarioPage'
import ProveedoresPage from './pages/proveedores/ProveedoresPage'
import CategoriasPage from './pages/categorias/CategoriasPage'
import UsuariosPage from './pages/usuarios/UsuariosPage'

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        element={
          <ProtectedRoute>
            <MainLayout />
          </ProtectedRoute>
        }
      >
        <Route path="/" element={<DashboardPage />} />
        <Route path="/ordenes" element={<OrdenesPage />} />
        <Route path="/clientes" element={<ClientesPage />} />
        <Route path="/vehiculos" element={<VehiculosPage />} />
        <Route path="/servicios" element={<ServiciosPage />} />
        <Route path="/productos" element={<ProductosPage />} />
        <Route path="/inventario" element={<InventarioPage />} />
        <Route path="/proveedores" element={<ProveedoresPage />} />
        <Route path="/categorias" element={<CategoriasPage />} />
        <Route path="/usuarios" element={<UsuariosPage />} />
      </Route>
    </Routes>
  )
}
