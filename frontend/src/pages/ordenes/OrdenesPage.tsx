import { useState, useEffect, FormEvent } from 'react'
import api from '../../services/api'
import { OrdenTrabajo, Cliente, Vehiculo, Servicio, Producto, Usuario } from '../../types'
import DataTable from '../../components/ui/DataTable'
import Modal from '../../components/ui/Modal'
import Pagination from '../../components/ui/Pagination'
import { formatCurrency, formatDate, getEstadoColor } from '../../utils/formatters'

export default function OrdenesPage() {
  const [ordenes, setOrdenes] = useState<OrdenTrabajo[]>([])
  const [clientes, setClientes] = useState<Cliente[]>([])
  const [vehiculos, setVehiculos] = useState<Vehiculo[]>([])
  const [servicios, setServicios] = useState<Servicio[]>([])
  const [productos, setProductos] = useState<Producto[]>([])
  const [usuarios, setUsuarios] = useState<Usuario[]>([])
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [modalOpen, setModalOpen] = useState(false)
  const [detailOpen, setDetailOpen] = useState(false)
  const [detailItem, setDetailItem] = useState<OrdenTrabajo | null>(null)
  const [editItem, setEditItem] = useState<OrdenTrabajo | null>(null)
  const [form, setForm] = useState({ clienteId: 0, vehiculoId: 0, kilometraje: 0, tecnicoId: 0, observaciones: '', servicios: [] as { servicioId: number; cantidad: number; observaciones: string }[], productos: [] as { productoId: number; cantidad: number }[] })

  const load = (p: number) => Promise.all([
    api.get(`/ordenes?page=${p}&size=10`).then(({ data }) => {
      setOrdenes(data.data.content)
      setPage(data.data.page)
      setTotalPages(data.data.totalPages)
      setTotalElements(data.data.totalElements)
    }),
    api.get('/clientes/all').then(({ data }) => setClientes(data.data)),
    api.get('/vehiculos/all').then(({ data }) => setVehiculos(data.data)),
    api.get('/servicios/all').then(({ data }) => setServicios(data.data)),
    api.get('/productos/all').then(({ data }) => setProductos(data.data)),
    api.get('/usuarios/all').then(({ data }) => setUsuarios(data.data))
  ]).finally(() => setLoading(false))
  useEffect(() => { load(0) }, [])

  const openCreate = () => {
    setEditItem(null)
    setForm({ clienteId: 0, vehiculoId: 0, kilometraje: 0, tecnicoId: 0, observaciones: '', servicios: [], productos: [] })
    setModalOpen(true)
  }

  const openDetail = (o: OrdenTrabajo) => {
    setDetailItem(o)
    setDetailOpen(true)
  }

  const addServicio = () => {
    setForm(prev => ({ ...prev, servicios: [...prev.servicios, { servicioId: 0, cantidad: 1, observaciones: '' }] }))
  }

  const updateServicio = (idx: number, field: string, value: any) => {
    setForm(prev => {
      const s = [...prev.servicios]
      s[idx] = { ...s[idx], [field]: value }
      return { ...prev, servicios: s }
    })
  }

  const removeServicio = (idx: number) => {
    setForm(prev => ({ ...prev, servicios: prev.servicios.filter((_, i) => i !== idx) }))
  }

  const addProducto = () => {
    setForm(prev => ({ ...prev, productos: [...prev.productos, { productoId: 0, cantidad: 1 }] }))
  }

  const updateProducto = (idx: number, field: string, value: any) => {
    setForm(prev => {
      const p = [...prev.productos]
      p[idx] = { ...p[idx], [field]: value }
      return { ...prev, productos: p }
    })
  }

  const removeProducto = (idx: number) => {
    setForm(prev => ({ ...prev, productos: prev.productos.filter((_, i) => i !== idx) }))
  }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    try {
      const payload = { ...form, tecnicoId: form.tecnicoId || null }
      const { data } = await api.post('/ordenes', payload)
      setOrdenes(prev => [data.data, ...prev])
      setModalOpen(false)
    } catch { }
  }

  const cambiarEstado = async (id: number, estado: string) => {
    try {
      const { data } = await api.patch(`/ordenes/${id}/estado`, { estado })
      setOrdenes(prev => prev.map(o => o.id === id ? data.data : o))
    } catch { }
  }

  const estados = ['PENDIENTE', 'EN_PROCESO', 'COMPLETADA', 'CANCELADA']

  const columns = [
    { header: 'Número', accessor: 'numeroOrden' as keyof OrdenTrabajo },
    { header: 'Cliente', accessor: 'clienteNombre' as keyof OrdenTrabajo },
    { header: 'Placa', accessor: 'vehiculoPlaca' as keyof OrdenTrabajo },
    { header: 'Estado', accessor: (o: OrdenTrabajo) => <span className={`px-2 py-1 rounded-full text-xs font-medium ${getEstadoColor(o.estado)}`}>{o.estado}</span> },
    { header: 'Total', accessor: (o: OrdenTrabajo) => formatCurrency(o.totalGeneral) },
    { header: 'Ingreso', accessor: (o: OrdenTrabajo) => formatDate(o.fechaIngreso) },
  ]

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-xl font-semibold text-gray-800 tracking-tight">Ordenes de Trabajo</h1>
        <button onClick={openCreate} className="p-2 text-blue-600 bg-blue-50 rounded-lg transition-all" title="Nueva Orden">
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M12 4v16m8-8H4" /></svg>
      </button>
      </div>
      <div className="bg-white rounded-lg shadow">
        <DataTable columns={columns} data={ordenes} loading={loading} />
        <Pagination page={page} totalPages={totalPages} totalElements={totalElements} onPageChange={load} />
      </div>

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title="Nueva Orden de Trabajo">
        <form onSubmit={handleSubmit} className="space-y-3">
          <select value={form.clienteId} onChange={e => setForm({ ...form, clienteId: Number(e.target.value) })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" required>
            <option value="">Seleccionar cliente</option>
            {clientes.map(c => <option key={c.id} value={c.id}>{c.nombre} {c.apellido} - {c.numeroDocumento}</option>)}
          </select>
          <select value={form.vehiculoId} onChange={e => setForm({ ...form, vehiculoId: Number(e.target.value) })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" required>
            <option value="">Seleccionar vehículo</option>
            {vehiculos.filter(v => !form.clienteId || v.clienteId === form.clienteId).map(v => <option key={v.id} value={v.id}>{v.placa} - {v.marca} {v.linea}</option>)}
          </select>
          <input placeholder="Kilometraje" type="number" value={form.kilometraje} onChange={e => setForm({ ...form, kilometraje: Number(e.target.value) })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" />
          <select value={form.tecnicoId} onChange={e => setForm({ ...form, tecnicoId: Number(e.target.value) })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors">
            <option value={0}>Sin técnico</option>
            {usuarios.filter(u => u.rol === 'TECNICO').map(u => <option key={u.id} value={u.id}>{u.nombre} {u.apellido}</option>)}
          </select>
          <textarea placeholder="Observaciones" value={form.observaciones} onChange={e => setForm({ ...form, observaciones: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" />

          <div className="border-t pt-3">
            <div className="flex justify-between items-center mb-2">
              <h4 className="font-medium">Servicios</h4>
              <button type="button" onClick={addServicio} className="text-blue-600 text-sm">+ Agregar</button>
            </div>
            {form.servicios.map((s, i) => (
              <div key={i} className="flex gap-2 mb-2">
                <select value={s.servicioId} onChange={e => updateServicio(i, 'servicioId', Number(e.target.value))} className="flex-1 px-3 py-2 border rounded text-sm" required>
                  <option value="">Seleccionar</option>
                  {servicios.map(sv => <option key={sv.id} value={sv.id}>{sv.nombre} - {formatCurrency(sv.precio)}</option>)}
                </select>
                <input type="number" value={s.cantidad} onChange={e => updateServicio(i, 'cantidad', Number(e.target.value))} className="w-16 px-2 py-2 border rounded text-sm" min={1} />
                <button type="button" onClick={() => removeServicio(i)} className="text-red-500 text-sm">X</button>
              </div>
            ))}
          </div>

          <div className="border-t pt-3">
            <div className="flex justify-between items-center mb-2">
              <h4 className="font-medium">Productos</h4>
              <button type="button" onClick={addProducto} className="text-blue-600 text-sm">+ Agregar</button>
            </div>
            {form.productos.map((p, i) => (
              <div key={i} className="flex gap-2 mb-2">
                <select value={p.productoId} onChange={e => updateProducto(i, 'productoId', Number(e.target.value))} className="flex-1 px-3 py-2 border rounded text-sm" required>
                  <option value="">Seleccionar</option>
                  {productos.map(pr => <option key={pr.id} value={pr.id}>{pr.nombre} - {formatCurrency(pr.precioVenta)}</option>)}
                </select>
                <input type="number" value={p.cantidad} onChange={e => updateProducto(i, 'cantidad', Number(e.target.value))} className="w-16 px-2 py-2 border rounded text-sm" min={1} />
                <button type="button" onClick={() => removeProducto(i)} className="text-red-500 text-sm">X</button>
              </div>
            ))}
          </div>

          <button type="submit" className="w-full bg-blue-600 text-white py-2.5 rounded-lg hover:bg-blue-700 transition-colors font-medium">Crear Orden</button>
        </form>
      </Modal>
    </div>
  )
}
