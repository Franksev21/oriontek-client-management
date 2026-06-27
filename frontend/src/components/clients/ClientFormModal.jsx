import { useForm } from 'react-hook-form'
import { useMutation } from '@tanstack/react-query'
import { clientsApi } from '../../services/api'
import { X } from 'lucide-react'

export default function ClientFormModal({ client, onClose, onSuccess }) {
  const isEdit = !!client

  const { register, handleSubmit, formState: { errors } } = useForm({
    defaultValues: client || {}
  })

  const mutation = useMutation({
    mutationFn: (data) => isEdit
      ? clientsApi.update(client.id, data)
      : clientsApi.create(data),
    onSuccess,
  })

  return (
    <div className="fixed inset-0 bg-black/50 flex items-end sm:items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl w-full max-w-md">
        <div className="flex items-center justify-between p-5 border-b">
          <h2 className="font-bold text-gray-800">
            {isEdit ? 'Editar Cliente' : 'Nuevo Cliente'}
          </h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit(d => mutation.mutate(d))} className="p-5 space-y-4">
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="text-xs font-medium text-gray-600 block mb-1">Nombre</label>
              <input
                {...register('firstName', { required: 'Requerido' })}
                className="w-full border border-gray-200 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                placeholder="Maria"
              />
              {errors.firstName && <p className="text-red-500 text-xs mt-1">{errors.firstName.message}</p>}
            </div>
            <div>
              <label className="text-xs font-medium text-gray-600 block mb-1">Apellido</label>
              <input
                {...register('lastName', { required: 'Requerido' })}
                className="w-full border border-gray-200 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                placeholder="Rodriguez"
              />
              {errors.lastName && <p className="text-red-500 text-xs mt-1">{errors.lastName.message}</p>}
            </div>
          </div>

          <div>
            <label className="text-xs font-medium text-gray-600 block mb-1">Email</label>
            <input
              {...register('email', { required: 'Requerido' })}
              className="w-full border border-gray-200 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
              placeholder="maria@ejemplo.com"
              type="email"
            />
            {errors.email && <p className="text-red-500 text-xs mt-1">{errors.email.message}</p>}
          </div>

          <div>
            <label className="text-xs font-medium text-gray-600 block mb-1">Telefono</label>
            <input
              {...register('phone')}
              className="w-full border border-gray-200 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
              placeholder="809-555-0100"
            />
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
              {mutation.isPending ? 'Guardando...' : isEdit ? 'Actualizar' : 'Crear Cliente'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
