import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { clientsApi } from '../services/api'
import { Search, Plus, Trash2, ChevronRight, Users, MapPin, TrendingUp } from 'lucide-react'
import ClientFormModal from '../components/clients/ClientFormModal'
import toast from 'react-hot-toast'

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
    onSuccess: () => {
      queryClient.invalidateQueries(['clients', 'stats'])
      toast.success('Cliente eliminado')
    },
    onError: () => toast.error('No se pudo eliminar el cliente'),
  })

  const handleDelete = (e, id, name) => {
    e.stopPropagation()
    toast((t) => (
      <div style={{display:'flex',flexDirection:'column',gap:8}}>
        <p style={{margin:0,fontSize:13,fontWeight:500}}>¿Eliminar a {name}?</p>
        <div style={{display:'flex',gap:8}}>
          <button
            onClick={() => { toast.dismiss(t.id); deleteMutation.mutate(id) }}
            style={{flex:1,background:'#ef4444',color:'white',border:'none',borderRadius:8,padding:'6px 12px',fontSize:12,cursor:'pointer',fontWeight:500}}
          >Eliminar</button>
          <button
            onClick={() => toast.dismiss(t.id)}
            style={{flex:1,background:'#f3f4f6',color:'#374151',border:'none',borderRadius:8,padding:'6px 12px',fontSize:12,cursor:'pointer',fontWeight:500}}
          >Cancelar</button>
        </div>
      </div>
    ), { duration: 5000 })
  }

  const colors = [
    'from-violet-500 to-purple-600',
    'from-blue-500 to-indigo-600',
    'from-emerald-500 to-teal-600',
    'from-orange-500 to-red-500',
    'from-pink-500 to-rose-600',
  ]

  return (
    <div>
      {stats && (
        <div className="grid grid-cols-3 gap-3 mb-6">
          <div className="bg-gradient-to-br from-indigo-500 to-purple-600 rounded-2xl p-4 text-white shadow-lg shadow-indigo-200">
            <Users size={20} className="mb-2 opacity-80" />
            <p className="text-2xl font-bold">{stats.totalClients}</p>
            <p className="text-xs text-indigo-200 mt-0.5">Clientes</p>
          </div>
          <div className="bg-gradient-to-br from-emerald-500 to-teal-600 rounded-2xl p-4 text-white shadow-lg shadow-emerald-200">
            <MapPin size={20} className="mb-2 opacity-80" />
            <p className="text-2xl font-bold">{stats.totalAddresses}</p>
            <p className="text-xs text-emerald-200 mt-0.5">Direcciones</p>
          </div>
          <div className="bg-gradient-to-br from-orange-500 to-pink-600 rounded-2xl p-4 text-white shadow-lg shadow-orange-200">
            <TrendingUp size={20} className="mb-2 opacity-80" />
            <p className="text-2xl font-bold">{stats.newThisMonth}</p>
            <p className="text-xs text-orange-200 mt-0.5">Este mes</p>
          </div>
        </div>
      )}

      <div className="flex items-center justify-between mb-4">
        <div>
          <h1 className="text-xl font-bold text-gray-800 dark:text-white">Clientes</h1>
          <p className="text-xs text-gray-500 dark:text-gray-400 mt-0.5">{data?.totalElements || 0} registros</p>
        </div>
        <button
          onClick={() => setShowModal(true)}
          className="flex items-center gap-2 bg-gradient-to-r from-indigo-600 to-purple-600 text-white px-4 py-2.5 rounded-xl text-sm font-medium shadow-lg shadow-indigo-200 hover:shadow-indigo-300 transition-all hover:-translate-y-0.5"
        >
          <Plus size={16} />
          Nuevo
        </button>
      </div>

      <div className="relative mb-4">
        <Search size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400" />
        <input
          type="text"
          placeholder="Buscar por nombre o email..."
          value={search}
          onChange={e => { setSearch(e.target.value); setPage(0) }}
          className="w-full pl-10 pr-4 py-3 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 shadow-sm dark:text-white dark:placeholder-gray-500"
        />
      </div>

      {isLoading ? (
        <div className="space-y-3">
          {[1,2,3].map(i => (
            <div key={i} className="bg-white dark:bg-gray-800 rounded-2xl p-4 animate-pulse">
              <div className="flex items-center gap-3">
                <div className="w-11 h-11 rounded-xl bg-gray-200 dark:bg-gray-700" />
                <div className="flex-1">
                  <div className="h-3.5 bg-gray-200 dark:bg-gray-700 rounded w-32 mb-2" />
                  <div className="h-3 bg-gray-100 dark:bg-gray-700 rounded w-48" />
                </div>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="space-y-3">
          {data?.content?.map((client, i) => (
            <div
              key={client.id}
              onClick={() => navigate(`/clients/${client.id}`)}
              className="bg-white dark:bg-gray-800 rounded-2xl p-4 shadow-sm border border-gray-100 dark:border-gray-700 flex items-center gap-3 cursor-pointer hover:shadow-md hover:border-indigo-100 dark:hover:border-indigo-800 transition-all hover:-translate-y-0.5"
            >
              <div className={`w-11 h-11 rounded-xl bg-gradient-to-br ${colors[i % colors.length]} flex items-center justify-center flex-shrink-0 shadow-sm`}>
                <span className="text-white font-bold text-sm">
                  {client.fullName.split(' ').map(n => n[0]).join('').slice(0,2)}
                </span>
              </div>
              <div className="flex-1 min-w-0">
                <p className="font-semibold text-gray-800 dark:text-white truncate">{client.fullName}</p>
                <p className="text-xs text-gray-400 truncate mt-0.5">{client.email}</p>
              </div>
              <div className="flex items-center gap-2 flex-shrink-0">
                <span className="text-xs bg-indigo-50 dark:bg-indigo-900/50 text-indigo-600 dark:text-indigo-400 font-medium px-2.5 py-1 rounded-full">
                  {client.addressCount} dir.
                </span>
                <button
                  onClick={(e) => handleDelete(e, client.id, client.fullName)}
                  className="p-1.5 text-gray-300 hover:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/30 rounded-lg transition-colors"
                >
                  <Trash2 size={14} />
                </button>
                <ChevronRight size={16} className="text-gray-300 dark:text-gray-600" />
              </div>
            </div>
          ))}

          {data?.content?.length === 0 && (
            <div className="text-center py-16">
              <div className="w-16 h-16 bg-gray-100 dark:bg-gray-800 rounded-2xl flex items-center justify-center mx-auto mb-4">
                <Users size={28} className="text-gray-300" />
              </div>
              <p className="text-gray-400 font-medium">No se encontraron clientes</p>
            </div>
          )}
        </div>
      )}

      {data && data.totalPages > 1 && (
        <div className="flex justify-center gap-2 mt-6">
          <button onClick={() => setPage(p => p - 1)} disabled={data.first}
            className="px-4 py-2 rounded-xl border dark:border-gray-700 text-sm font-medium disabled:opacity-40 hover:bg-gray-50 dark:hover:bg-gray-800 dark:text-gray-300">
            Anterior
          </button>
          <span className="px-4 py-2 text-sm text-gray-500 dark:text-gray-400 font-medium">{page + 1} / {data.totalPages}</span>
          <button onClick={() => setPage(p => p + 1)} disabled={data.last}
            className="px-4 py-2 rounded-xl border dark:border-gray-700 text-sm font-medium disabled:opacity-40 hover:bg-gray-50 dark:hover:bg-gray-800 dark:text-gray-300">
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
            toast.success('Cliente creado')
          }}
        />
      )}
    </div>
  )
}
