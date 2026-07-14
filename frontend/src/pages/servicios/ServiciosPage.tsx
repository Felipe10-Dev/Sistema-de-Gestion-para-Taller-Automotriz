import { useState, useEffect, FormEvent } from 'react'
import api from '../../services/api'
import { Servicio, Categoria } from '../../types'
import DataTable from '../../components/ui/DataTable'
import Modal from '../../components/ui/Modal'
import Pagination from '../../components/ui/Pagination'
import { formatCurrency } from '../../utils/formatters'

export default function ServiciosPage() {
  const [servicios, setServicios] = useState<Servicio[]>([])
  const [categorias, setCategorias] = useState<Categoria[]>([])
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [modalOpen, setModalOpen] = useState(false)
  const [editItem, setEditItem] = useState<Servicio | null>(null)
  const defaultForm = { nombre: '', descripcion: '', precio: 0, duracionEstimadaMinutos: 60, categoriaId: 0 }
  const [form, setForm] = useState(defaultForm)

  const load = (p: number) => Promise.all([
    api.get(`/servicios?page=${p}&size=10`).then(({ data }) => {
      setServicios(data.data.content)
      setPage(data.data.page)
      setTotalPages(data.data.totalPages)
      setTotalElements(data.data.totalElements)
    }),
    api.get('/categorias/all').then(({ data }) => setCategorias(data.data))
  ]).finally(() => setLoading(false))
  useEffect(() => { load(0) }, [])

  const openCreate = () => { setEditItem(null); setForm(defaultForm); setModalOpen(true) }
  const openEdit = (s: Servicio) => { setEditItem(s); setForm({ nombre: s.nombre, descripcion: s.descripcion || '', precio: s.precio, duracionEstimadaMinutos: s.duracionEstimadaMinutos || 60, categoriaId: s.categoriaId || 0 }); setModalOpen(true) }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    try {
      const payload = { ...form, categoriaId: form.categoriaId || null }
      if (editItem) {
        const { data } = await api.put(`/servicios/${editItem.id}`, payload)
        setServicios(prev => prev.map(s => s.id === editItem.id ? data.data : s))
      } else {
        const { data } = await api.post('/servicios', payload)
        setServicios(prev => [...prev, data.data])
      }
      setModalOpen(false)
    } catch { }
  }

  const handleDelete = async (s: Servicio) => {
    await api.delete(`/servicios/${s.id}`); load(0)
  }

  const columns = [
    { header: 'Nombre', accessor: 'nombre' as keyof Servicio },
    { header: 'Precio', accessor: (s: Servicio) => formatCurrency(s.precio) },
    { header: 'Duración', accessor: (s: Servicio) => s.duracionEstimadaMinutos ? `${s.duracionEstimadaMinutos} min` : '-' },
    { header: 'Categoría', accessor: (s: Servicio) => s.categoriaNombre || '-' },
  ]

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-xl font-semibold text-gray-800 tracking-tight">Servicios</h1>
        <button onClick={openCreate} className="p-2 text-blue-600 bg-blue-50 rounded-lg transition-all" title="Nuevo Servicio">
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M12 4v16m8-8H4" /></svg>
      </button>
      </div>
      <div className="bg-white rounded-lg shadow">
        <DataTable columns={columns} data={servicios} onEdit={openEdit} onDelete={handleDelete} loading={loading} />
        <Pagination page={page} totalPages={totalPages} totalElements={totalElements} onPageChange={load} />
      </div>
      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title={editItem ? 'Editar Servicio' : 'Nuevo Servicio'}>
        <form onSubmit={handleSubmit} className="space-y-3">
          <input placeholder="Nombre" value={form.nombre} onChange={e => setForm({ ...form, nombre: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" required />
          <textarea placeholder="Descripción" value={form.descripcion} onChange={e => setForm({ ...form, descripcion: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" rows={3} />
          <input placeholder="Precio" type="number" step="0.01" value={form.precio} onChange={e => setForm({ ...form, precio: Number(e.target.value) })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" required />
          <input placeholder="Duración (minutos)" type="number" value={form.duracionEstimadaMinutos} onChange={e => setForm({ ...form, duracionEstimadaMinutos: Number(e.target.value) })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" />
          <select value={form.categoriaId} onChange={e => setForm({ ...form, categoriaId: Number(e.target.value) })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors">
            <option value={0}>Sin categoría</option>
            {categorias.map(c => <option key={c.id} value={c.id}>{c.nombre}</option>)}
          </select>
          <button type="submit" className="w-full bg-blue-600 text-white py-2.5 rounded-lg hover:bg-blue-700 transition-colors font-medium">{editItem ? 'Actualizar' : 'Crear'}</button>
        </form>
      </Modal>
    </div>
  )
}
