import { useState, useEffect, FormEvent } from 'react'
import api from '../../services/api'
import { Vehiculo, Cliente } from '../../types'
import DataTable from '../../components/ui/DataTable'
import Modal from '../../components/ui/Modal'
import Pagination from '../../components/ui/Pagination'

export default function VehiculosPage() {
  const [vehiculos, setVehiculos] = useState<Vehiculo[]>([])
  const [clientes, setClientes] = useState<Cliente[]>([])
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [modalOpen, setModalOpen] = useState(false)
  const [editItem, setEditItem] = useState<Vehiculo | null>(null)
  const defaultForm = { placa: '', marca: '', linea: '', modelo: '', color: '', cilindraje: '', tipoVehiculo: '', clienteId: 0 }
  const [form, setForm] = useState(defaultForm)

  const load = (p: number) => Promise.all([
    api.get(`/vehiculos?page=${p}&size=10`).then(({ data }) => {
      setVehiculos(data.data.content)
      setPage(data.data.page)
      setTotalPages(data.data.totalPages)
      setTotalElements(data.data.totalElements)
    }),
    api.get('/clientes/all').then(({ data }) => setClientes(data.data))
  ]).finally(() => setLoading(false))

  useEffect(() => { load(0) }, [])

  const openCreate = () => { setEditItem(null); setForm(defaultForm); setModalOpen(true) }
  const openEdit = (v: Vehiculo) => { setEditItem(v); setForm({ placa: v.placa, marca: v.marca, linea: v.linea, modelo: v.modelo, color: v.color || '', cilindraje: v.cilindraje || '', tipoVehiculo: v.tipoVehiculo || '', clienteId: v.clienteId }); setModalOpen(true) }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    try {
      if (editItem) {
        const { data } = await api.put(`/vehiculos/${editItem.id}`, form)
        setVehiculos(prev => prev.map(v => v.id === editItem.id ? data.data : v))
      } else {
        const { data } = await api.post('/vehiculos', form)
        setVehiculos(prev => [...prev, data.data])
      }
      setModalOpen(false)
    } catch { }
  }

  const handleDelete = async (v: Vehiculo) => {
    await api.delete(`/vehiculos/${v.id}`); load(0)
  }

  const columns = [
    { header: 'Placa', accessor: 'placa' as keyof Vehiculo },
    { header: 'Marca', accessor: 'marca' as keyof Vehiculo },
    { header: 'Línea', accessor: 'linea' as keyof Vehiculo },
    { header: 'Modelo', accessor: 'modelo' as keyof Vehiculo },
    { header: 'Cliente', accessor: 'clienteNombre' as keyof Vehiculo },
  ]

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-xl font-semibold text-gray-800 tracking-tight">Vehículos</h1>
        <button onClick={openCreate} className="p-2 text-blue-600 bg-blue-50 rounded-lg transition-all" title="Nuevo Vehículo">
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M12 4v16m8-8H4" /></svg>
      </button>
      </div>
      <div className="bg-white rounded-lg shadow">
        <DataTable columns={columns} data={vehiculos} onEdit={openEdit} onDelete={handleDelete} loading={loading} />
        <Pagination page={page} totalPages={totalPages} totalElements={totalElements} onPageChange={load} />
      </div>
      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title={editItem ? 'Editar Vehículo' : 'Nuevo Vehículo'}>
        <form onSubmit={handleSubmit} className="space-y-3">
          <input placeholder="Placa" value={form.placa} onChange={e => setForm({ ...form, placa: e.target.value.toUpperCase() })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" required />
          <input placeholder="Marca" value={form.marca} onChange={e => setForm({ ...form, marca: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" required />
          <input placeholder="Línea" value={form.linea} onChange={e => setForm({ ...form, linea: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" required />
          <input placeholder="Modelo" value={form.modelo} onChange={e => setForm({ ...form, modelo: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" required />
          <input placeholder="Color" value={form.color} onChange={e => setForm({ ...form, color: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" />
          <input placeholder="Cilindraje" value={form.cilindraje} onChange={e => setForm({ ...form, cilindraje: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" />
          <input placeholder="Tipo vehículo" value={form.tipoVehiculo} onChange={e => setForm({ ...form, tipoVehiculo: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" />
          <select value={form.clienteId} onChange={e => setForm({ ...form, clienteId: Number(e.target.value) })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" required>
            <option value="">Seleccionar cliente</option>
            {clientes.map(c => <option key={c.id} value={c.id}>{c.nombre} {c.apellido} - {c.numeroDocumento}</option>)}
          </select>
          <button type="submit" className="w-full bg-blue-600 text-white py-2.5 rounded-lg hover:bg-blue-700 transition-colors font-medium">{editItem ? 'Actualizar' : 'Crear'}</button>
        </form>
      </Modal>
    </div>
  )
}
