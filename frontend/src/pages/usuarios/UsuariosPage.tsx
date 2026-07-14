import { useState, useEffect, FormEvent } from 'react'
import api from '../../services/api'
import { Usuario, Rol } from '../../types'
import DataTable from '../../components/ui/DataTable'
import Modal from '../../components/ui/Modal'
import Pagination from '../../components/ui/Pagination'

export default function UsuariosPage() {
  const [usuarios, setUsuarios] = useState<Usuario[]>([])
  const [roles, setRoles] = useState<Rol[]>([])
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [modalOpen, setModalOpen] = useState(false)
  const [editItem, setEditItem] = useState<Usuario | null>(null)
  const [form, setForm] = useState({ username: '', password: '', nombre: '', apellido: '', email: '', rolId: 0 })

  const load = (p: number) => Promise.all([
    api.get(`/usuarios?page=${p}&size=10`).then(({ data }) => {
      setUsuarios(data.data.content)
      setPage(data.data.page)
      setTotalPages(data.data.totalPages)
      setTotalElements(data.data.totalElements)
    }),
    api.get('/roles')
  ]).then(([_, r]) => setRoles(r.data.data)).finally(() => setLoading(false))
  useEffect(() => { load(0) }, [])

  const openCreate = () => {
    setEditItem(null)
    setForm({ username: '', password: '', nombre: '', apellido: '', email: '', rolId: roles[0]?.id || 0 })
    setModalOpen(true)
  }

  const openEdit = (user: Usuario) => {
    setEditItem(user)
    setForm({ username: user.username, password: '', nombre: user.nombre, apellido: user.apellido, email: user.email, rolId: roles.find(r => r.nombre === user.rol)?.id || 0 })
    setModalOpen(true)
  }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    try {
      if (editItem) {
        const { data } = await api.put(`/usuarios/${editItem.id}`, form)
        setUsuarios(prev => prev.map(u => u.id === editItem.id ? data.data : u))
      } else {
        const { data } = await api.post('/usuarios', form)
        setUsuarios(prev => [...prev, data.data])
      }
      setModalOpen(false)
    } catch { }
  }

  const handleDelete = async (user: Usuario) => {
    await api.delete(`/usuarios/${user.id}`)
    setUsuarios(prev => prev.filter(u => u.id !== user.id))
  }

  const columns = [
    { header: 'Usuario', accessor: 'username' as keyof Usuario },
    { header: 'Nombre', accessor: (u: Usuario) => `${u.nombre} ${u.apellido}` },
    { header: 'Email', accessor: 'email' as keyof Usuario },
    { header: 'Rol', accessor: 'rol' as keyof Usuario },
    { header: 'Estado', accessor: (u: Usuario) => u.activo ? 'Activo' : 'Inactivo' },
  ]

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-xl font-semibold text-gray-800 tracking-tight">Usuarios</h1>
        <button onClick={openCreate} className="p-2 text-blue-600 bg-blue-50 rounded-lg transition-all" title="Nuevo Usuario">
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M12 4v16m8-8H4" /></svg>
        </button>
      </div>
      <div className="bg-white rounded-lg shadow">
        <DataTable columns={columns} data={usuarios} onEdit={openEdit} onDelete={handleDelete} loading={loading} />
        <Pagination page={page} totalPages={totalPages} totalElements={totalElements} onPageChange={load} />
      </div>
      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title={editItem ? 'Editar Usuario' : 'Nuevo Usuario'}>
        <form onSubmit={handleSubmit} className="space-y-3">
          <input placeholder="Username" value={form.username} onChange={e => setForm({ ...form, username: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" required disabled={!!editItem} />
          {!editItem && <input type="password" placeholder="Contraseña" value={form.password} onChange={e => setForm({ ...form, password: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" required />}
          <input placeholder="Nombre" value={form.nombre} onChange={e => setForm({ ...form, nombre: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" required />
          <input placeholder="Apellido" value={form.apellido} onChange={e => setForm({ ...form, apellido: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" required />
          <input placeholder="Email" type="email" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" />
          <select value={form.rolId} onChange={e => setForm({ ...form, rolId: Number(e.target.value) })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors">
            {roles.map(r => <option key={r.id} value={r.id}>{r.nombre}</option>)}
          </select>
          <button type="submit" className="w-full bg-blue-600 text-white py-2.5 rounded-lg hover:bg-blue-700 transition-colors font-medium">
            {editItem ? 'Actualizar' : 'Crear'}
          </button>
        </form>
      </Modal>
    </div>
  )
}
