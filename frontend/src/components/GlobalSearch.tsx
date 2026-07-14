import { useState, useRef, useEffect, KeyboardEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../services/api'

interface SearchResult {
  type: string
  id: number
  label: string
  subtitle: string
  url: string
}

const typeIcons: Record<string, string> = {
  CLIENTE: 'M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z',
  VEHICULO: 'M13 16V6a1 1 0 00-1-1H4a1 1 0 00-1 1v10m10 0a2 2 0 11-4 0 2 2 0 014 0zM9 10a2 2 0 100 4 2 2 0 000-4z',
  ORDEN: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2',
  PRODUCTO: 'M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4',
  SERVICIO: 'M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.066 2.573c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.573 1.066c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.066-2.573c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z',
}

const typeColors: Record<string, string> = {
  CLIENTE: 'bg-blue-100 text-blue-800',
  VEHICULO: 'bg-green-100 text-green-800',
  ORDEN: 'bg-purple-100 text-purple-800',
  PRODUCTO: 'bg-yellow-100 text-yellow-800',
  SERVICIO: 'bg-indigo-100 text-indigo-800',
}

export default function GlobalSearch() {
  const [query, setQuery] = useState('')
  const [results, setResults] = useState<SearchResult[]>([])
  const [open, setOpen] = useState(false)
  const [loading, setLoading] = useState(false)
  const [selectedIndex, setSelectedIndex] = useState(-1)
  const inputRef = useRef<HTMLInputElement>(null)
  const dropdownRef = useRef<HTMLDivElement>(null)
  const navigate = useNavigate()
  const debounceRef = useRef<number>()

  useEffect(() => {
    if (debounceRef.current) clearTimeout(debounceRef.current)
    if (!query.trim()) { setResults([]); setOpen(false); return }
    setLoading(true)
    debounceRef.current = window.setTimeout(async () => {
      try {
        const { data } = await api.get(`/buscar?q=${encodeURIComponent(query)}&size=8`)
        setResults(data.data.content || [])
        setOpen(true)
        setSelectedIndex(-1)
      } catch { } finally { setLoading(false) }
    }, 300)
    return () => { if (debounceRef.current) clearTimeout(debounceRef.current) }
  }, [query])

  const handleSelect = (r: SearchResult) => {
    setOpen(false)
    setQuery('')
    navigate(r.url)
  }

  const handleKeyDown = (e: KeyboardEvent) => {
    if (!open || results.length === 0) return
    if (e.key === 'ArrowDown') { e.preventDefault(); setSelectedIndex(i => Math.min(i + 1, results.length - 1)) }
    else if (e.key === 'ArrowUp') { e.preventDefault(); setSelectedIndex(i => Math.max(i - 1, 0)) }
    else if (e.key === 'Enter' && selectedIndex >= 0) { e.preventDefault(); handleSelect(results[selectedIndex]) }
    else if (e.key === 'Escape') { setOpen(false); inputRef.current?.blur() }
  }

  useEffect(() => {
    const handleClick = (e: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target as Node) &&
          inputRef.current && !inputRef.current.contains(e.target as Node)) setOpen(false)
    }
    document.addEventListener('mousedown', handleClick)
    return () => document.removeEventListener('mousedown', handleClick)
  }, [])

  return (
    <div className="relative w-full max-w-lg">
      <div className="relative">
        <svg className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
        </svg>
        <input
          ref={inputRef}
          type="text"
          value={query}
          onChange={e => setQuery(e.target.value)}
          onKeyDown={handleKeyDown}
          onFocus={() => { if (results.length > 0) setOpen(true) }}
          placeholder="Buscar clientes, vehículos, órdenes..."
          className="w-full pl-10 pr-4 py-2.5 bg-gray-100 text-gray-700 placeholder-gray-400 rounded-xl border border-gray-200 focus:outline-none focus:ring-1 focus:ring-blue-500/20 focus:border-blue-400 text-sm transition-all"
        />
        {loading && (
          <div className="absolute right-3 top-1/2 -translate-y-1/2">
            <div className="w-4 h-4 border-2 border-blue-500 border-t-transparent rounded-full animate-spin" />
          </div>
        )}
      </div>

      {open && results.length > 0 && (
        <div ref={dropdownRef} className="absolute top-full mt-1 w-full bg-white border border-gray-200 rounded-lg shadow-xl z-50 max-h-96 overflow-y-auto">
          {results.map((r, i) => (
            <button
              key={`${r.type}-${r.id}`}
              onClick={() => handleSelect(r)}
              onMouseEnter={() => setSelectedIndex(i)}
              className={`w-full flex items-start gap-3 px-3 py-2.5 text-left transition-colors ${
                i === selectedIndex ? 'bg-blue-50' : 'hover:bg-gray-50'
              } ${i > 0 ? 'border-t border-gray-100' : ''}`}
            >
              <span className={`flex-shrink-0 p-1.5 rounded-full ${typeColors[r.type] || 'bg-gray-100'}`}>
                <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d={typeIcons[r.type] || ''} />
                </svg>
              </span>
              <div className="min-w-0 flex-1">
                <div className="text-sm font-medium text-gray-900 truncate">{r.label}</div>
                <div className="text-xs text-gray-500 truncate">{r.subtitle}</div>
              </div>
              <span className={`flex-shrink-0 text-[10px] font-medium px-1.5 py-0.5 rounded ${typeColors[r.type] || 'bg-gray-100 text-gray-600'}`}>
                {r.type}
              </span>
            </button>
          ))}
        </div>
      )}

      {open && query.trim() && !loading && results.length === 0 && (
        <div className="absolute top-full mt-1 w-full bg-white border border-gray-200 rounded-lg shadow-xl z-50 p-4 text-center text-sm text-gray-400">
          Sin resultados para "{query}"
        </div>
      )}
    </div>
  )
}
