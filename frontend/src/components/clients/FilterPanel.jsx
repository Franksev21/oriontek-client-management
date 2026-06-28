import { useState } from 'react'
import { Filter, X, ChevronDown, ChevronUp } from 'lucide-react'

export default function FilterPanel({ onFilter }) {
  const [open, setOpen] = useState(false)
  const [city, setCity] = useState('')
  const [country, setCountry] = useState('')
  const [sortBy, setSortBy] = useState('firstName')
  const [sortDir, setSortDir] = useState('asc')

  const hasFilters = city || country || sortBy !== 'firstName' || sortDir !== 'asc'

  const handleApply = () => {
    onFilter({ city, country, sortBy, sortDir })
    setOpen(false)
  }

  const handleClear = () => {
    setCity('')
    setCountry('')
    setSortBy('firstName')
    setSortDir('asc')
    onFilter({ city: '', country: '', sortBy: 'firstName', sortDir: 'asc' })
  }

  return (
    <div className="mb-4">
      <div className="flex items-center gap-2">
        <button
          onClick={() => setOpen(!open)}
          className={`flex items-center gap-2 px-3 py-2 rounded-xl text-sm font-medium border transition-all ${
            hasFilters
              ? 'bg-indigo-50 dark:bg-indigo-900/30 border-indigo-200 dark:border-indigo-700 text-indigo-600 dark:text-indigo-400'
              : 'bg-white dark:bg-gray-800 border-gray-200 dark:border-gray-700 text-gray-600 dark:text-gray-300'
          }`}
        >
          <Filter size={14} />
          Filtros
          {hasFilters && <span className="w-2 h-2 bg-indigo-500 rounded-full" />}
          {open ? <ChevronUp size={14} /> : <ChevronDown size={14} />}
        </button>
        {hasFilters && (
          <button
            onClick={handleClear}
            className="flex items-center gap-1 px-3 py-2 rounded-xl text-sm text-red-500 hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors"
          >
            <X size={14} />
            Limpiar
          </button>
        )}
      </div>

      {open && (
        <div className="mt-2 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-2xl p-4 shadow-sm">
          <div className="grid grid-cols-2 gap-3 mb-3">
            <div>
              <label className="text-xs font-medium text-gray-500 dark:text-gray-400 block mb-1">Ciudad</label>
              <input
                value={city}
                onChange={e => setCity(e.target.value)}
                placeholder="Santo Domingo..."
                className="w-full border border-gray-200 dark:border-gray-600 rounded-lg px-3 py-2 text-sm bg-white dark:bg-gray-700 dark:text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="text-xs font-medium text-gray-500 dark:text-gray-400 block mb-1">País</label>
              <input
                value={country}
                onChange={e => setCountry(e.target.value)}
                placeholder="República Dominicana..."
                className="w-full border border-gray-200 dark:border-gray-600 rounded-lg px-3 py-2 text-sm bg-white dark:bg-gray-700 dark:text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3 mb-4">
            <div>
              <label className="text-xs font-medium text-gray-500 dark:text-gray-400 block mb-1">Ordenar por</label>
              <select
                value={sortBy}
                onChange={e => setSortBy(e.target.value)}
                className="w-full border border-gray-200 dark:border-gray-600 rounded-lg px-3 py-2 text-sm bg-white dark:bg-gray-700 dark:text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
              >
                <option value="firstName">Nombre</option>
                <option value="lastName">Apellido</option>
                <option value="email">Email</option>
                <option value="createdAt">Fecha de registro</option>
              </select>
            </div>
            <div>
              <label className="text-xs font-medium text-gray-500 dark:text-gray-400 block mb-1">Dirección</label>
              <select
                value={sortDir}
                onChange={e => setSortDir(e.target.value)}
                className="w-full border border-gray-200 dark:border-gray-600 rounded-lg px-3 py-2 text-sm bg-white dark:bg-gray-700 dark:text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
              >
                <option value="asc">A → Z</option>
                <option value="desc">Z → A</option>
              </select>
            </div>
          </div>

          <button
            onClick={handleApply}
            className="w-full bg-gradient-to-r from-indigo-600 to-purple-600 text-white py-2 rounded-xl text-sm font-medium hover:opacity-90 transition-opacity"
          >
            Aplicar filtros
          </button>
        </div>
      )}
    </div>
  )
}
