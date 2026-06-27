import { useEffect, useState } from 'react'
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet'
import { MapPin } from 'lucide-react'
import 'leaflet/dist/leaflet.css'
import L from 'leaflet'

delete L.Icon.Default.prototype._getIconUrl
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
})

const NOMINATIM_URL = 'https://nominatim.openstreetmap.org/search'

async function geocodeAddress(address) {
  const query = `${address.street}, ${address.city}, ${address.country}`
  const res = await fetch(
    `${NOMINATIM_URL}?q=${encodeURIComponent(query)}&format=json&limit=1`,
    { headers: { 'Accept-Language': 'es' } }
  )
  const data = await res.json()
  if (data.length > 0) {
    return { lat: parseFloat(data[0].lat), lng: parseFloat(data[0].lon) }
  }
  return null
}

export default function AddressMap({ addresses }) {
  const [locations, setLocations] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!addresses || addresses.length === 0) {
      setLoading(false)
      return
    }

    async function loadLocations() {
      setLoading(true)
      const results = await Promise.all(
        addresses.map(async (addr) => {
          const coords = await geocodeAddress(addr)
          return coords ? { ...addr, ...coords } : null
        })
      )
      setLocations(results.filter(Boolean))
      setLoading(false)
    }

    loadLocations()
  }, [addresses])

  if (loading) return (
    <div className="h-48 rounded-xl bg-gray-100 flex items-center justify-center">
      <div className="flex items-center gap-2 text-gray-400 text-sm">
        <MapPin size={16} className="animate-bounce" />
        Cargando mapa...
      </div>
    </div>
  )

  if (locations.length === 0) return null

  const center = [locations[0].lat, locations[0].lng]

  return (
    <div className="rounded-xl overflow-hidden border border-gray-100 h-56 mt-4">
      <MapContainer
        center={center}
        zoom={13}
        style={{ height: '100%', width: '100%' }}
        scrollWheelZoom={false}
      >
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        {locations.map((loc) => (
          <Marker key={loc.id} position={[loc.lat, loc.lng]}>
            <Popup>
              <div className="text-sm">
                <p className="font-semibold">{loc.street}</p>
                <p className="text-gray-500">{loc.city}, {loc.country}</p>
                {loc.primary && <p className="text-indigo-600 font-medium mt-1">⭐ Principal</p>}
              </div>
            </Popup>
          </Marker>
        ))}
      </MapContainer>
    </div>
  )
}
