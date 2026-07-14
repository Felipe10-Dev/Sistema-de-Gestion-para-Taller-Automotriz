import { useState, useEffect, FormEvent } from 'react'
import api from '../../services/api'
import { Inventario, Producto } from '../../types'
import DataTable from '../../components/ui/DataTable'
import Modal from '../../components/ui/Modal'
import Pagination from '../../components/ui/Pagination'

export default function InventarioPage() {
  const [inventario, setInventario] = useState<Inventario[]>([])
  const [productos, setProductos] = useState<Producto[]>([])
  const [loading, setLoading] = useState(true)
  const [modalOpen, setModalOpen] = useState(false)
  const [movimOpen, setMovimOpen] = useState(false)
  const [editItem, setEditItem] = useState<Inventario | null>(null)
  const [form, setForm] = useState({ productoId: 0, cantidadMinima: 0, ubicacion: '' })
  const [movForm, setMovForm] = useState({ productoId: 0, tipoMovimiento: 'ENTRADA', cantidad: 1, motivo: '' })

  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)

  const load = (p: number) => Promise.all([
    api.get(`/inventario?page=${p}&size=10`).then(({ data }) => {
      setInventario(data.data.content)
      setPage(data.data.page)
      setTotalPages(data.data.totalPages)
      setTotalElements(data.data.totalElements)
    }),
    api.get('/productos/all').then(({ data }) => setProductos(data.data))
  ]).finally(() => setLoading(false))
  useEffect(() => { load(0) }, [])

  const openCreate = () => { setEditItem(null); setForm({ productoId: 0, cantidadMinima: 0, ubicacion: '' }); setModalOpen(true) }
  const openEdit = (i: Inventario) => { setEditItem(i); setForm({ productoId: i.productoId, cantidadMinima: i.cantidadMinima, ubicacion: i.ubicacion || '' }); setModalOpen(true) }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    try {
      if (editItem) {
        const { data } = await api.put(`/inventario/${editItem.id}`, form)
        setInventario(prev => prev.map(i => i.id === editItem.id ? data.data : i))
      } else {
        const { data } = await api.post('/inventario', form)
        setInventario(prev => [...prev, data.data])
      }
      setModalOpen(false)
    } catch { }
  }

  const handleMovimiento = async (e: FormEvent) => {
    e.preventDefault()
    try {
      await api.post('/inventario/movimiento', movForm)
      setMovimOpen(false)
      load(0)
    } catch { }
  }

  const columns = [
    { header: 'Código', accessor: 'productoCodigo' as keyof Inventario },
    { header: 'Producto', accessor: 'productoNombre' as keyof Inventario },
    { header: 'Stock Actual', accessor: 'cantidadActual' as keyof Inventario },
    { header: 'Stock Mínimo', accessor: 'cantidadMinima' as keyof Inventario },
    { header: 'Estado', accessor: (i: Inventario) => i.bajoStock ? 'Bajo stock' : 'Normal' },
    { header: 'Ubicación', accessor: (i: Inventario) => i.ubicacion || '-' },
  ]

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-xl font-semibold text-gray-800 tracking-tight">Inventario</h1>
        <div className="space-x-2">
          <button onClick={() => { setMovForm({ productoId: 0, tipoMovimiento: 'ENTRADA', cantidad: 1, motivo: '' }); setMovimOpen(true) }} className="p-2 text-green-600 bg-green-50 rounded-lg transition-all" title="Movimiento">
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" /></svg>
          </button>
          <button onClick={openCreate} className="p-2 text-blue-600 bg-blue-50 rounded-lg transition-all" title="Nuevo Registro">
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M12 4v16m8-8H4" /></svg>
          </button>
        </div>
      </div>
      <div className="bg-white rounded-lg shadow">
        <DataTable columns={columns} data={inventario} onEdit={openEdit} loading={loading} />
        <Pagination page={page} totalPages={totalPages} totalElements={totalElements} onPageChange={load} />
      </div>

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title={editItem ? 'Editar Inventario' : 'Nuevo Registro'}>
        <form onSubmit={handleSubmit} className="space-y-3">
          {!editItem && <select value={form.productoId} onChange={e => setForm({ ...form, productoId: Number(e.target.value) })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" required>
            <option value="">Seleccionar producto</option>
            {productos.map(p => <option key={p.id} value={p.id}>{p.codigo} - {p.nombre}</option>)}
          </select>}
          <input placeholder="Cantidad mínima" type="number" value={form.cantidadMinima} onChange={e => setForm({ ...form, cantidadMinima: Number(e.target.value) })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" />
          <input placeholder="Ubicación" value={form.ubicacion} onChange={e => setForm({ ...form, ubicacion: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" />
          <button type="submit" className="w-full bg-blue-600 text-white py-2.5 rounded-lg hover:bg-blue-700 transition-colors font-medium">{editItem ? 'Actualizar' : 'Crear'}</button>
        </form>
      </Modal>

      <Modal open={movimOpen} onClose={() => setMovimOpen(false)} title="Registrar Movimiento">
        <form onSubmit={handleMovimiento} className="space-y-3">
          <select value={movForm.productoId} onChange={e => setMovForm({ ...movForm, productoId: Number(e.target.value) })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" required>
            <option value="">Seleccionar producto</option>
            {productos.map(p => <option key={p.id} value={p.id}>{p.codigo} - {p.nombre}</option>)}
          </select>
          <select value={movForm.tipoMovimiento} onChange={e => setMovForm({ ...movForm, tipoMovimiento: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors">
            <option value="ENTRADA">Entrada</option>
            <option value="SALIDA">Salida</option>
          </select>
          <input placeholder="Cantidad" type="number" value={movForm.cantidad} onChange={e => setMovForm({ ...movForm, cantidad: Number(e.target.value) })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" required />
          <input placeholder="Motivo" value={movForm.motivo} onChange={e => setMovForm({ ...movForm, motivo: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" />
          <button type="submit" className="w-full bg-green-600 text-white py-2.5 rounded-lg hover:bg-green-700 transition-colors font-medium">Registrar</button>
        </form>
      </Modal>
    </div>
  )
}
