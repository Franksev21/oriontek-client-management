import { Link } from 'react-router-dom'
import { Users } from 'lucide-react'

export default function Navbar() {
  return (
    <nav className="bg-indigo-600 text-white shadow-lg">
      <div className="max-w-4xl mx-auto px-4 py-3 flex items-center justify-between">
        <Link to="/" className="flex items-center gap-2 font-bold text-lg">
          <Users size={22} />
          OrionTek
        </Link>
        <span className="text-indigo-200 text-sm">Gestión de Clientes</span>
      </div>
    </nav>
  )
}
