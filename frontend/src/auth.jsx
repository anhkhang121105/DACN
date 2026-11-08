import { createContext, useContext, useMemo, useState } from 'react'
import { api } from './api.js'

const AuthContext = createContext(null)
const STORAGE_KEY = 'pc_auth'

function readStored() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

export function AuthProvider({ children }) {
  const [auth, setAuth] = useState(readStored)

  async function login(username, password) {
    const data = await api('/auth/login', { method: 'POST', body: { username, password } })
    localStorage.setItem(STORAGE_KEY, JSON.stringify(data))
    setAuth(data)
    return data
  }

  function logout() {
    localStorage.removeItem(STORAGE_KEY)
    setAuth(null)
  }

  const value = useMemo(() => ({ auth, login, logout }), [auth])
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth phải dùng trong AuthProvider')
  return ctx
}

export function hasRole(auth, ...roles) {
  return Boolean(auth && roles.includes(auth.vaiTro))
}
