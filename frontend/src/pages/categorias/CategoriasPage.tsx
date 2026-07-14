import { useState, useEffect, FormEvent } from 'react'
import api from '../../services/api'
import { Categoria } from '../../types'
import DataTable from '../../components/ui/DataTable'
import Modal from '../../components/ui/Modal'
import Pagination from '../../components/ui/Pagination'

export default function CategoriasPage() {
  const [categorias, setCategorias] = useState<Categoria[]>([])
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [modalOpen, setModalOpen] = useState(false)
  const [editItem, setEditItem] = useState<Categoria | null>(null)
  const [form, setForm] = useState({ nombre: '', descripcion: '' })

  const load = (p: number) => api.get(`/categorias?page=${p}&size=10`).then(({ data }) => {
    setCategorias(data.data.content)
    setPage(data.data.page)
    setTotalPages(data.data.totalPages)
    setTotalElements(data.data.totalElements)
  }).finally(() => setLoading(false))
  useEffect(() => { load(0) }, [])

  const openCreate = () => { setEditItem(null); setForm({ nombre: '', descripcion: '' }); setModalOpen(true) }
  const openEdit = (c: Categoria) => { setEditItem(c); setForm({ nombre: c.nombre, descripcion: c.descripcion || '' }); setModalOpen(true) }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    try {
      if (editItem) {
        const { data } = await api.put(`/categorias/${editItem.id}`, form)
        setCategorias(prev => prev.map(c => c.id === editItem.id ? data.data : c))
      } else {
        const { data } = await api.post('/categorias', form)
        setCategorias(prev => [...prev, data.data])
      }
      setModalOpen(false)
    } catch { }
  }

  const handleDelete = async (c: Categoria) => {
    await api.delete(`/categorias/${c.id}`); setCategorias(prev => prev.filter(x => x.id !== c.id))
  }

  const columns = [
    { header: 'Nombre', accessor: 'nombre' as keyof Categoria },
    { header: 'Descripción', accessor: (c: Categoria) => c.descripcion || '-' },
  ]

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-xl font-semibold text-gray-800 tracking-tight">Categorías</h1>
        <button onClick={openCreate} className="p-2 text-blue-600 bg-blue-50 rounded-lg transition-all" title="Nueva Categoría">
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M12 4v16m8-8H4" /></svg>
      </button>
      </div>
      <div className="bg-white rounded-lg shadow">
        <DataTable columns={columns} data={categorias} onEdit={openEdit} onDelete={handleDelete} loading={loading} />
        <Pagination page={page} totalPages={totalPages} totalElements={totalElements} onPageChange={load} />
      </div>
      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title={editItem ? 'Editar Categoría' : 'Nueva Categoría'}>
        <form onSubmit={handleSubmit} className="space-y-3">
          <input placeholder="Nombre" value={form.nombre} onChange={e => setForm({ ...form, nombre: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" required />
          <textarea placeholder="Descripción" value={form.descripcion} onChange={e => setForm({ ...form, descripcion: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" />
          <button type="submit" className="w-full bg-blue-600 text-white py-2.5 rounded-lg hover:bg-blue-700 transition-colors font-medium">{editItem ? 'Actualizar' : 'Crear'}</button>
        </form>
      </Modal>
    </div>
  )
}
