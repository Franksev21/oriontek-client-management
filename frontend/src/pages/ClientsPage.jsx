import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { clientsApi } from '../services/api'
import { Search, Plus, Trash2, Eye, Users, MapPin } from 'lucide-react'
import ClientFormModal from '../components/clients/ClientFormModal'

export default function ClientsPage() {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [search, setSearch] = useState('')
  const [page, setPage] = useState(0)
  const [showModal, setShowModal] = useState(false)

  const { data, isLoading } = useQuery({
    queryKey: ['clients', search, page],
    queryFn: () => clientsApi.getAll({ search, page, size: 10 }).then(r => r.data.data),
  })

  const { data: stats } = useQuery({
    queryKey: ['stats'],
    queryFn: () => clientsApi.getStats().then(r => r.data.data),
  })

  const deleteMutation = useMutation({
    mutationFn: (id) => clientsApi.delete(id),
    onSuccess: () => queryClient.invalidateQueries(['clients', 'stats']),
  })

  const handleDelete = (e, id) => {
    e.stopPropagation()
    if (confirm('¿Eliminar este cliente?')) deleteMutation.mutate(id)
  }

  return (
    <div>
      {/* Stats */}
      {stats && (
        <div className="grid grid-cols-2 gap-3 mb-6">
          <div className="bg-white rounded-xl p-4 shadow-sm border border-gray-100">
            <div className="flex items-center gap-2 text-indigo-600 mb-1">
              <Users size={18} />
              <span className="text-sm font-medium">Total Clientes</span>
            </div>
            <p className="text-2xl font-bold text-gray-800">{stats.totalClients}</p>
          </div>
          <div className="bg-white rounded-xl p-4 shadow-sm border border-gray-100">
            <div className="flex items-center gap-2 text-indigo-600 mb-1">
              <MapPin size={18} />
              <span className="text-sm font-medium">Direcciones</span>
            </div>
            <p className="text-2xl font-bold text-gray-800">{stats.totalAddresses}</p>
          </div>
        </div>
      )}

      {/* Header */}
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-xl font-bold text-gray-800">Clientes</h1>
        <button
          onClick={() => setShowModal(true)}
          className="flex items-center gap-2 bg-indigo-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-indigo-700 transition-colors"
        >
          <Plus size={16} />
          Nuevo
        </button>
      </div>

      {/* Search */}
      <div className="relative mb-4">
        <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
        <input
          type="text"
          placeholder="Buscar por nombre o email..."
          value={search}
          onChange={e => { setSearch(e.target.value); setPage(0) }}
          className="w-full pl-9 pr-4 py-2.5 bg-white border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
      </div>

      {/* List */}
      {isLoading ? (
        <div className="text-center py-12 text-gray-400">Cargando...</div>
      ) : (
        <div className="space-y-3">
          {data?.content?.map(client => (
            <div
              key={client.id}
              onClick={() => navigate(`/clients/${client.id}`)}
              className="bg-white rounded-xl p-4 shadow-sm border border-gray-100 flex items-center gap-3 cursor-pointer hover:border-indigo-200 hover:shadow-md transition-all"
            >
              <div className="w-10 h-10 rounded-full bg-indigo-100 flex items-center justify-center flex-shrink-0">
                <span className="text-indigo-600 font-semibold text-sm">
                  {client.fullName.split(' ').map(n => n[0]).join('').slice(0,2)}
                </span>
              </div>
              <div className="flex-1 min-w-0">
                <p className="font-semibold text-gray-800 truncate">{client.fullName}</p>
                <p className="text-sm text-gray-500 truncate">{client.email}</p>
              </div>
              <div className="flex items-center gap-3 flex-shrink-0">
                <span className="text-xs bg-indigo-50 text-indigo-600 px-2 py-1 rounded-full">
                  {client.addressCount} dir.
                </span>
                <button
                  onClick={(e) => handleDelete(e, client.id)}
                  className="text-gray-400 hover:text-red-500 transition-colors"
                >
                  <Trash2 size={16} />
                </button>
                <Eye size={16} className="text-gray-400" />
              </div>
            </div>
          ))}

          {data?.content?.length === 0 && (
            <div className="text-center py-12 text-gray-400">
              No se encontraron clientes
            </div>
          )}
        </div>
      )}

      {/* Pagination */}
      {data && data.totalPages > 1 && (
        <div className="flex justify-center gap-2 mt-6">
          <button
            onClick={() => setPage(p => p - 1)}
            disabled={data.first}
            className="px-4 py-2 rounded-lg border text-sm disabled:opacity-40 hover:bg-gray-50"
          >
            Anterior
          </button>
          <span className="px-4 py-2 text-sm text-gray-600">
            {page + 1} / {data.totalPages}
          </span>
          <button
            onClick={() => setPage(p => p + 1)}
            disabled={data.last}
            className="px-4 py-2 rounded-lg border text-sm disabled:opacity-40 hover:bg-gray-50"
          >
            Siguiente
          </button>
        </div>
      )}

      {showModal && (
        <ClientFormModal
          onClose={() => setShowModal(false)}
          onSuccess={() => {
            setShowModal(false)
            queryClient.invalidateQueries(['clients', 'stats'])
          }}
        />
      )}
    </div>
  )
}
