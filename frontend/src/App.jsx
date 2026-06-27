import { Routes, Route } from 'react-router-dom'
import ClientsPage from './pages/ClientsPage'
import ClientDetailPage from './pages/ClientDetailPage'
import Navbar from './components/shared/Navbar'

export default function App() {
  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <main className="max-w-4xl mx-auto px-4 py-6">
        <Routes>
          <Route path="/" element={<ClientsPage />} />
          <Route path="/clients/:id" element={<ClientDetailPage />} />
        </Routes>
      </main>
    </div>
  )
}
