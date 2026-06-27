import { useForm } from 'react-hook-form'
import { useMutation } from '@tanstack/react-query'
import { clientsApi } from '../../services/api'
import { X } from 'lucide-react'

export default function AddressFormModal({ clientId, address, onClose, onSuccess }) {
  const isEdit = !!address

  const { register, handleSubmit, formState: { errors } } = useForm({
    defaultValues: address || { primary: false }
  })

  const mutation = useMutation({
    mutationFn: (data) => isEdit
      ? clientsApi.updateAddress(clientId, address.id, data)
      : clientsApi.addAddress(clientId, data),
    onSuccess,
  })

  return (
    <div className="fixed inset-0 bg-black/50 flex items-end sm:items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl w-full max-w-md">
        <div className="flex items-center justify-between p-5 border-b">
          <h2 className="font-bold text-gray-800">
            {isEdit ? 'Editar Direccion' : 'Nueva Direccion'}
          </h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit(d => mutation.mutate(d))} className="p-5 space-y-4">
          <div>
            <label className="text-xs font-medium text-gray-600 block mb-1">Calle</label>
            <input
              {...register('street', { required: 'Requerido' })}
              className="w-full border border-gray-200 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
              placeholder="Calle Duarte 45"
            />
            {errors.street && <p className="text-red-500 text-xs mt-1">{errors.street.message}</p>}
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="text-xs font-medium text-gray-600 block mb-1">Ciudad</label>
              <input
                {...register('city', { required: 'Requerido' })}
                className="w-full border border-gray-200 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                placeholder="Santo Domingo"
              />
              {errors.city && <p className="text-red-500 text-xs mt-1">{errors.city.message}</p>}
            </div>
            <div>
              <label className="text-xs font-medium text-gray-600 block mb-1">Provincia</label>
              <input
                {...register('state')}
                className="w-full border border-gray-200 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                placeholder="Distrito Nacional"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="text-xs font-medium text-gray-600 block mb-1">Pais</label>
              <input
                {...register('country', { required: 'Requerido' })}
                className="w-full border border-gray-200 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                placeholder="Republica Dominicana"
              />
              {errors.country && <p className="text-red-500 text-xs mt-1">{errors.country.message}</p>}
            </div>
            <div>
              <label className="text-xs font-medium text-gray-600 block mb-1">Codigo Postal</label>
              <input
                {...register('zipCode')}
                className="w-full border border-gray-200 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                placeholder="10101"
              />
            </div>
          </div>

          <div className="flex items-center gap-2">
            <input
              type="checkbox"
              id="primary"
              {...register('primary')}
              className="w-4 h-4 accent-indigo-600"
            />
            <label htmlFor="primary" className="text-sm text-gray-600">
              Marcar como direccion principal
            </label>
          </div>

          {mutation.isError && (
            <p className="text-red-500 text-sm bg-red-50 p-3 rounded-lg">
              {mutation.error?.response?.data?.message || 'Error al guardar'}
            </p>
          )}

          <div className="flex gap-3 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 border border-gray-200 text-gray-600 py-2.5 rounded-lg text-sm font-medium hover:bg-gray-50"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={mutation.isPending}
              className="flex-1 bg-indigo-600 text-white py-2.5 rounded-lg text-sm font-medium hover:bg-indigo-700 disabled:opacity-50"
            >
              {mutation.isPending ? 'Guardando...' : isEdit ? 'Actualizar' : 'Agregar'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
