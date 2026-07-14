import { useState, useEffect } from 'react'
import api from '../../services/api'
import { DashboardData } from '../../types'
import { formatCurrency } from '../../utils/formatters'

export default function DashboardPage() {
  const [data, setData] = useState<DashboardData | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.get('/dashboard')
      .then(({ data }) => setData(data.data))
      .finally(() => setLoading(false))
  }, [])

  if (loading) {
    return (
      <div className="flex justify-center py-12">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600" />
      </div>
    )
  }

  const cards = [
    { label: 'Clientes', value: data?.totalClientes ?? 0, color: 'bg-blue-500' },
    { label: 'Vehículos', value: data?.totalVehiculos ?? 0, color: 'bg-green-500' },
    { label: 'Ordenes Pendientes', value: data?.ordenesPendientes ?? 0, color: 'bg-yellow-500' },
    { label: 'En Proceso', value: data?.ordenesEnProceso ?? 0, color: 'bg-indigo-500' },
    { label: 'Completadas Hoy', value: data?.ordenesCompletadasHoy ?? 0, color: 'bg-teal-500' },
    { label: 'Stock Bajo', value: data?.productosBajoStock ?? 0, color: 'bg-red-500' },
  ]

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-800 mb-6">Dashboard</h1>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {cards.map((card) => (
          <div key={card.label} className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-500">{card.label}</p>
                <p className="text-3xl font-bold mt-1">{card.value}</p>
              </div>
              <div className={`w-12 h-12 ${card.color} rounded-full opacity-20`} />
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
