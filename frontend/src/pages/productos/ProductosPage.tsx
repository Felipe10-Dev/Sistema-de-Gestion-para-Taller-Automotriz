import { useState, useEffect, FormEvent } from 'react'
import api from '../../services/api'
import { Producto, Categoria, Proveedor } from '../../types'
import DataTable from '../../components/ui/DataTable'
import Modal from '../../components/ui/Modal'
import Pagination from '../../components/ui/Pagination'
import { formatCurrency } from '../../utils/formatters'

export default function ProductosPage() {
  const [productos, setProductos] = useState<Producto[]>([])
  const [categorias, setCategorias] = useState<Categoria[]>([])
  const [proveedores, setProveedores] = useState<Proveedor[]>([])
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [modalOpen, setModalOpen] = useState(false)
  const [editItem, setEditItem] = useState<Producto | null>(null)
  const defaultForm = { codigo: '', nombre: '', descripcion: '', precioCompra: 0, precioVenta: 0, stockMinimo: 0, categoriaId: 0, proveedorId: 0 }
  const [form, setForm] = useState(defaultForm)

  const load = (p: number) => Promise.all([
    api.get(`/productos?page=${p}&size=10`).then(({ data }) => {
      setProductos(data.data.content)
      setPage(data.data.page)
      setTotalPages(data.data.totalPages)
      setTotalElements(data.data.totalElements)
    }),
    api.get('/categorias/all').then(({ data }) => setCategorias(data.data)),
    api.get('/proveedores/all').then(({ data }) => setProveedores(data.data))
  ]).finally(() => setLoading(false))
  useEffect(() => { load(0) }, [])

  const openCreate = () => { setEditItem(null); setForm(defaultForm); setModalOpen(true) }
  const openEdit = (p: Producto) => { setEditItem(p); setForm({ codigo: p.codigo, nombre: p.nombre, descripcion: p.descripcion || '', precioCompra: p.precioCompra || 0, precioVenta: p.precioVenta, stockMinimo: p.stockMinimo || 0, categoriaId: p.categoriaId || 0, proveedorId: p.proveedorId || 0 }); setModalOpen(true) }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    try {
      const payload = { ...form, categoriaId: form.categoriaId || null, proveedorId: form.proveedorId || null }
      if (editItem) {
        const { data } = await api.put(`/productos/${editItem.id}`, payload)
        setProductos(prev => prev.map(p => p.id === editItem.id ? data.data : p))
      } else {
        const { data } = await api.post('/productos', payload)
        setProductos(prev => [...prev, data.data])
      }
      setModalOpen(false)
    } catch { }
  }

  const handleDelete = async (p: Producto) => {
    await api.delete(`/productos/${p.id}`); load(0)
  }

  const columns = [
    { header: 'Código', accessor: 'codigo' as keyof Producto },
    { header: 'Nombre', accessor: 'nombre' as keyof Producto },
    { header: 'Precio Venta', accessor: (p: Producto) => formatCurrency(p.precioVenta) },
    { header: 'Categoría', accessor: (p: Producto) => p.categoriaNombre || '-' },
    { header: 'Proveedor', accessor: (p: Producto) => p.proveedorNombre || '-' },
  ]

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-xl font-semibold text-gray-800 tracking-tight">Productos</h1>
        <button onClick={openCreate} className="p-2 text-blue-600 bg-blue-50 rounded-lg transition-all" title="Nuevo Producto">
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M12 4v16m8-8H4" /></svg>
      </button>
      </div>
      <div className="bg-white rounded-lg shadow">
        <DataTable columns={columns} data={productos} onEdit={openEdit} onDelete={handleDelete} loading={loading} />
        <Pagination page={page} totalPages={totalPages} totalElements={totalElements} onPageChange={load} />
      </div>
      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title={editItem ? 'Editar Producto' : 'Nuevo Producto'}>
        <form onSubmit={handleSubmit} className="space-y-3">
          <input placeholder="Código" value={form.codigo} onChange={e => setForm({ ...form, codigo: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" required />
          <input placeholder="Nombre" value={form.nombre} onChange={e => setForm({ ...form, nombre: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" required />
          <textarea placeholder="Descripción" value={form.descripcion} onChange={e => setForm({ ...form, descripcion: e.target.value })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" />
          <div className="grid grid-cols-2 gap-2">
            <input placeholder="Precio compra" type="number" step="0.01" value={form.precioCompra} onChange={e => setForm({ ...form, precioCompra: Number(e.target.value) })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" />
            <input placeholder="Precio venta" type="number" step="0.01" value={form.precioVenta} onChange={e => setForm({ ...form, precioVenta: Number(e.target.value) })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" required />
          </div>
          <input placeholder="Stock mínimo" type="number" value={form.stockMinimo} onChange={e => setForm({ ...form, stockMinimo: Number(e.target.value) })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors" />
          <select value={form.categoriaId} onChange={e => setForm({ ...form, categoriaId: Number(e.target.value) })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors">
            <option value={0}>Sin categoría</option>
            {categorias.map(c => <option key={c.id} value={c.id}>{c.nombre}</option>)}
          </select>
          <select value={form.proveedorId} onChange={e => setForm({ ...form, proveedorId: Number(e.target.value) })} className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-1 focus:ring-blue-500 focus:border-blue-500 text-sm transition-colors">
            <option value={0}>Sin proveedor</option>
            {proveedores.map(p => <option key={p.id} value={p.id}>{p.nombre}</option>)}
          </select>
          <button type="submit" className="w-full bg-blue-600 text-white py-2.5 rounded-lg hover:bg-blue-700 transition-colors font-medium">{editItem ? 'Actualizar' : 'Crear'}</button>
        </form>
      </Modal>
    </div>
  )
}
