import { useState, useEffect, FormEvent } from 'react'
import api from '../../services/api'
import { Cliente } from '../../types'
import DataTable from '../../components/ui/DataTable'
import Modal from '../../components/ui/Modal'
import Pagination from '../../components/ui/Pagination'

export default function ClientesPage() {
  const [clientes, setClientes] = useState<Cliente[]>([])
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [modalOpen, setModalOpen] = useState(false)
  const [editItem, setEditItem] = useState<Cliente | null>(null)
  const defaultForm = { tipoDocumento: 'CC', numeroDocumento: '', nombre: '', apellido: '', telefono: '', email: '', direccion: '' }
  const [form, setForm] = useState(defaultForm)

  const load = (p: number) => api.get(`/clientes?page=${p}&size=10`).then(({ data }) => {
    setClientes(data.data.content)
    setPage(data.data.page)
    setTotalPages(data.data.totalPages)
    setTotalElements(data.data.totalElements)
  }).finally(() => setLoading(false))
  useEffect(() => { load(0) }, [])

  const openCreate = () => { setEditItem(null); setForm(defaultForm); setModalOpen(true) }
  const openEdit = (c: Cliente) => { setEditItem(c); setForm(c); setModalOpen(true) }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    try {
      if (editItem) {
        const { data } = await api.put(`/clientes/${editItem.id}`, form)
        setClientes(prev => prev.map(c => c.id === editItem.id ? data.data : c))
      } else {
        const { data } = await api.post('/clientes', form)
        setClientes(prev => [...prev, data.data])
      }
      setModalOpen(false)
    } catch { }
  }

  const handleDelete = async (c: Cliente) => {
    await api.delete(`/clientes/${c.id}`); load(0)
  }

  const columns = [
    { header: 'Documento', accessor: (c: Cliente) => `${c.tipoDocumento} ${c.numeroDocumento}` },
    { header: 'Nombre', accessor: (c: Cliente) => `${c.nombre} ${c.apellido}` },
    { header: 'Teléfono', accessor: 'telefono' as keyof Cliente },
    { header: 'Email', accessor: 'email' as keyof Cliente },
  ]

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-xl font-semibold text-gray-800 tracking-tight">Clientes</h1>
        <button onClick={openCreate} className="p-2 text-blue-600 bg-blue-50 rounded-lg transition-all" title="Nuevo Cliente">
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M12 4v16m8-8H4" /></svg>
      </button>
      </div>
      <div className="bg-white rounded-lg shadow">
        <DataTable columns={columns} data={clientes} onEdit={openEdit} onDelete={handleDelete} loading={loading} />
        <Pagination page={page} totalPages={totalPages} totalElements={totalElements} onPageChange={load} />
      </div>
      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title={editItem ? 'Editar Cliente' : 'Nuevo Cliente'}>
        <form onSubmit={handleSubmit} className="space-y-3">
          <select value={form.tipoDocumento} onChange={e => setForm({ ...form, tipoDocumento: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors">
            <option value="CC">C.C.</option>
            <option value="NIT">NIT</option>
            <option value="CE">C.E.</option>
          </select>
          <input placeholder="Número de documento" value={form.numeroDocumento} onChange={e => setForm({ ...form, numeroDocumento: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" required />
          <input placeholder="Nombre" value={form.nombre} onChange={e => setForm({ ...form, nombre: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" required />
          <input placeholder="Apellido" value={form.apellido} onChange={e => setForm({ ...form, apellido: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" required />
          <input placeholder="Teléfono" value={form.telefono} onChange={e => setForm({ ...form, telefono: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" />
          <input placeholder="Email" type="email" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" />
          <input placeholder="Dirección" value={form.direccion} onChange={e => setForm({ ...form, direccion: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" />
          <button type="submit" className="w-full bg-blue-600 text-white py-2.5 rounded-lg hover:bg-blue-700 transition-colors font-medium">{editItem ? 'Actualizar' : 'Crear'}</button>
        </form>
      </Modal>
    </div>
  )
}
