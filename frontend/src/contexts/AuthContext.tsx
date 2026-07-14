import { createContext, useContext, useState, useEffect, ReactNode } from 'react'
import { LoginRequest, LoginResponse, Usuario } from '../types'
import { sanitize } from '../utils/security'
import api from '../services/api'

interface AuthContextType {
  user: Usuario | null
  loading: boolean
  login: (data: LoginRequest) => Promise<void>
  logout: () => void
  isAuthenticated: boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<Usuario | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const token = localStorage.getItem('accessToken')
    if (token) {
      api.get('/usuarios/me')
        .then(({ data }) => setUser(data.data))
        .catch(() => localStorage.clear())
        .finally(() => setLoading(false))
    } else {
      setLoading(false)
    }
  }, [])

  const login = async (credentials: LoginRequest) => {
    credentials.username = sanitize(credentials.username)
    const { data } = await api.post('/auth/login', credentials)
    const loginData: LoginResponse = data.data
    localStorage.setItem('accessToken', loginData.accessToken)
    localStorage.setItem('refreshToken', loginData.refreshToken)
    const userData = (await api.get('/usuarios/me')).data.data
    setUser(userData)
  }

  const logout = () => {
    localStorage.clear()
    setUser(null)
    window.location.href = '/login'
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, logout, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) throw new Error('useAuth must be used within AuthProvider')
  return context
}
