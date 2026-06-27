import { Link } from 'react-router-dom'
import { Users, Zap } from 'lucide-react'

export default function Navbar() {
  return (
    <nav className="bg-gradient-to-r from-indigo-600 to-purple-600 text-white shadow-lg">
      <div className="max-w-4xl mx-auto px-4 py-3 flex items-center justify-between">
        <Link to="/" className="flex items-center gap-2 font-bold text-lg tracking-tight">
          <div className="bg-white/20 p-1.5 rounded-lg">
            <Users size={18} />
          </div>
          OrionTek
        </Link>
        <div className="flex items-center gap-2 text-indigo-200 text-sm">
          <Zap size={14} />
          <span>Gestión de Clientes</span>
        </div>
      </div>
    </nav>
  )
}
