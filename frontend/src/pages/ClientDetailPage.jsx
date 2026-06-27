import { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { clientsApi } from '../services/api'
import { ArrowLeft, Plus, Trash2, Edit, MapPin, Mail, Phone, Star } from 'lucide-react'
import ClientFormModal from '../components/clients/ClientFormModal'
import AddressFormModal from '../components/addresses/AddressFormModal'

export default function ClientDetailPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [showEditModal, setShowEditModal] = useState(false)
  const [showAddressModal, setShowAddressModal] = useState(false)
  const [editingAddress, setEditingAddress] = useState(null)

  const { data: client, isLoading } = useQuery({
    queryKey: ['client', id],
    queryFn: () => clientsApi.getById(id).then(r => r.data.data),
  })

  const deleteAddressMutation = useMutation({
    mutationFn: (addressId) => clientsApi.deleteAddress(id, addressId),
    onSuccess: () => queryClient.invalidateQueries(['client', id]),
  })

  if (isLoading) return (
    <div className="space-y-4 animate-pulse">
      <div className="h-8 w-24 bg-gray-200 rounded-lg" />
      <div className="bg-white rounded-2xl p-6 h-40" />
    </div>
  )

  if (!client) return (
    <div className="text-center py-12 text-gray-400">Cliente no encontrado</div>
  )

  const initials = client.fullName.split(' ').map(n => n[0]).join('').slice(0, 2)

  return (
    <div>
      <button
        onClick={() => navigate('/')}
        className="flex items-center gap-2 text-gray-500 hover:text-indigo-600 mb-5 transition-colors group"
      >
        <div className="p-1.5 rounded-lg group-hover:bg-indigo-50 transition-colors">
          <ArrowLeft size={16} />
        </div>
        <span className="text-sm font-medium">Volver</span>
      </button>

      {/* Profile card */}
      <div className="bg-white rounded-2xl overflow-hidden shadow-sm border border-gray-100 mb-4">
        <div className="bg-gradient-to-r from-indigo-500 to-purple-600 px-6 pt-6 pb-6 flex items-center justify-between">
          <div className="flex items-center gap-4">
            <div className="w-14 h-14 rounded-2xl bg-white/20 backdrop-blur flex items-center justify-center border-2 border-white/40 flex-shrink-0">
              <span className="text-white font-bold text-lg">{initials}</span>
            </div>
            <div>
              <h1 className="text-xl font-bold text-white">{client.fullName}</h1>
              <div className="flex items-center gap-1.5 text-indigo-200 text-sm mt-1">
                <Mail size={13} />
                <span>{client.email}</span>
              </div>
              {client.phone && (
                <div className="flex items-center gap-1.5 text-indigo-200 text-sm mt-0.5">
                  <Phone size={13} />
                  <span>{client.phone}</span>
                </div>
              )}
            </div>
          </div>
          <button
            onClick={() => setShowEditModal(true)}
            className="flex items-center gap-1.5 text-sm text-white border border-white/30 bg-white/20 px-3 py-1.5 rounded-xl hover:bg-white/30 transition-colors flex-shrink-0"
          >
            <Edit size={13} />
            Editar
          </button>
        </div>
      </div>

      {/* Addresses */}
      <div className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h2 className="font-bold text-gray-800 flex items-center gap-2">
              <MapPin size={17} className="text-indigo-500" />
              Direcciones
            </h2>
            <p className="text-xs text-gray-400 mt-0.5">{client.addresses?.length || 0} registradas</p>
          </div>
          <button
            onClick={() => { setEditingAddress(null); setShowAddressModal(true) }}
            className="flex items-center gap-1.5 text-sm bg-gradient-to-r from-indigo-600 to-purple-600 text-white px-3 py-1.5 rounded-xl shadow-sm transition-all"
          >
            <Plus size={14} />
            Agregar
          </button>
        </div>

        <div className="space-y-2.5">
          {client.addresses?.map(address => (
            <div
              key={address.id}
              className="flex items-start justify-between p-3.5 rounded-xl border border-gray-100 hover:border-indigo-100 hover:bg-indigo-50/30 transition-all group"
            >
              <div className="flex items-start gap-3">
                <div className="w-8 h-8 rounded-lg bg-indigo-100 flex items-center justify-center flex-shrink-0 mt-0.5">
                  <MapPin size={14} className="text-indigo-500" />
                </div>
                <div>
                  <div className="flex items-center gap-2 flex-wrap">
                    <p className="font-medium text-gray-800 text-sm">{address.street}</p>
                    {address.primary && (
                      <span className="text-xs bg-indigo-100 text-indigo-600 px-2 py-0.5 rounded-full font-medium flex items-center gap-1">
                        <Star size={10} />
                        Principal
                      </span>
                    )}
                  </div>
                  <p className="text-xs text-gray-400 mt-0.5">
                    {address.city}{address.state ? `, ${address.state}` : ''} · {address.country}
                    {address.zipCode ? ` ${address.zipCode}` : ''}
                  </p>
                </div>
              </div>
              <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                <button
                  onClick={() => { setEditingAddress(address); setShowAddressModal(true) }}
                  className="p-1.5 text-gray-400 hover:text-indigo-500 hover:bg-indigo-100 rounded-lg transition-colors"
                >
                  <Edit size={13} />
                </button>
                <button
                  onClick={() => { if(confirm('¿Eliminar esta dirección?')) deleteAddressMutation.mutate(address.id) }}
                  className="p-1.5 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded-lg transition-colors"
                >
                  <Trash2 size={13} />
                </button>
              </div>
            </div>
          ))}

          {client.addresses?.length === 0 && (
            <div className="text-center py-10">
              <div className="w-12 h-12 bg-gray-100 rounded-xl flex items-center justify-center mx-auto mb-3">
                <MapPin size={22} className="text-gray-300" />
              </div>
              <p className="text-gray-400 text-sm font-medium">Sin direcciones registradas</p>
            </div>
          )}
        </div>
      </div>

      {showEditModal && (
        <ClientFormModal
          client={client}
          onClose={() => setShowEditModal(false)}
          onSuccess={() => {
            setShowEditModal(false)
            queryClient.invalidateQueries(['client', id])
          }}
        />
      )}

      {showAddressModal && (
        <AddressFormModal
          clientId={id}
          address={editingAddress}
          onClose={() => setShowAddressModal(false)}
          onSuccess={() => {
            setShowAddressModal(false)
            queryClient.invalidateQueries(['client', id])
          }}
        />
      )}
    </div>
  )
}
