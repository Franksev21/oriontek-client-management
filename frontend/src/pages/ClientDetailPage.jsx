import { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { clientsApi } from '../services/api'
import { ArrowLeft, Plus, Trash2, Edit, MapPin, Mail, Phone } from 'lucide-react'
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
    <div className="text-center py-12 text-gray-400">Cargando...</div>
  )

  if (!client) return (
    <div className="text-center py-12 text-gray-400">Cliente no encontrado</div>
  )

  const initials = client.fullName.split(' ').map(n => n[0]).join('').slice(0, 2)

  return (
    <div>
      {/* Back button */}
      <button
        onClick={() => navigate('/')}
        className="flex items-center gap-2 text-gray-500 hover:text-gray-800 mb-6 transition-colors"
      >
        <ArrowLeft size={18} />
        <span className="text-sm">Volver</span>
      </button>

      {/* Profile card */}
      <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100 mb-4">
        <div className="flex items-start justify-between">
          <div className="flex items-center gap-4">
            <div className="w-16 h-16 rounded-full bg-indigo-100 flex items-center justify-center">
              <span className="text-indigo-600 font-bold text-xl">{initials}</span>
            </div>
            <div>
              <h1 className="text-xl font-bold text-gray-800">{client.fullName}</h1>
              <div className="flex items-center gap-1 text-gray-500 text-sm mt-1">
                <Mail size={14} />
                <span>{client.email}</span>
              </div>
              {client.phone && (
                <div className="flex items-center gap-1 text-gray-500 text-sm mt-0.5">
                  <Phone size={14} />
                  <span>{client.phone}</span>
                </div>
              )}
            </div>
          </div>
          <button
            onClick={() => setShowEditModal(true)}
            className="flex items-center gap-1.5 text-sm text-indigo-600 border border-indigo-200 px-3 py-1.5 rounded-lg hover:bg-indigo-50 transition-colors"
          >
            <Edit size={14} />
            Editar
          </button>
        </div>
      </div>

      {/* Addresses */}
      <div className="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
        <div className="flex items-center justify-between mb-4">
          <h2 className="font-semibold text-gray-800 flex items-center gap-2">
            <MapPin size={18} className="text-indigo-600" />
            Direcciones ({client.addresses?.length || 0})
          </h2>
          <button
            onClick={() => { setEditingAddress(null); setShowAddressModal(true) }}
            className="flex items-center gap-1.5 text-sm bg-indigo-600 text-white px-3 py-1.5 rounded-lg hover:bg-indigo-700 transition-colors"
          >
            <Plus size={14} />
            Agregar
          </button>
        </div>

        <div className="space-y-3">
          {client.addresses?.map(address => (
            <div
              key={address.id}
              className="flex items-start justify-between p-3 rounded-lg border border-gray-100 hover:border-indigo-100 transition-colors"
            >
              <div className="flex items-start gap-3">
                <MapPin size={16} className="text-indigo-400 mt-0.5 flex-shrink-0" />
                <div>
                  <p className="font-medium text-gray-800 text-sm">{address.street}</p>
                  <p className="text-xs text-gray-500 mt-0.5">
                    {address.city}{address.state ? `, ${address.state}` : ''} — {address.country}
                    {address.zipCode ? ` ${address.zipCode}` : ''}
                  </p>
                  {address.primary && (
                    <span className="text-xs bg-indigo-50 text-indigo-600 px-2 py-0.5 rounded-full mt-1 inline-block">
                      Principal
                    </span>
                  )}
                </div>
              </div>
              <div className="flex items-center gap-2 flex-shrink-0">
                <button
                  onClick={() => { setEditingAddress(address); setShowAddressModal(true) }}
                  className="text-gray-400 hover:text-indigo-500 transition-colors"
                >
                  <Edit size={14} />
                </button>
                <button
                  onClick={() => {
                    if (confirm('¿Eliminar esta dirección?'))
                      deleteAddressMutation.mutate(address.id)
                  }}
                  className="text-gray-400 hover:text-red-500 transition-colors"
                >
                  <Trash2 size={14} />
                </button>
              </div>
            </div>
          ))}

          {client.addresses?.length === 0 && (
            <p className="text-center text-gray-400 text-sm py-6">
              No hay direcciones registradas
            </p>
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
