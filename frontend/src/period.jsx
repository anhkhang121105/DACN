import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import { api } from './api.js'
import { useAuth } from './auth.jsx'

const PeriodContext = createContext(null)

export function PeriodProvider({ children }) {
  const { auth } = useAuth()
  const [namHocs, setNamHocs] = useState([])
  const [namHocId, setNamHocId] = useState(null)
  const [hocKys, setHocKys] = useState([])
  const [hocKyId, setHocKyId] = useState(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (!auth) {
      setNamHocs([])
      return
    }
    let cancelled = false
    api('/nam-hoc')
      .then((data) => {
        if (cancelled) return
        setNamHocs(data || [])
        if (data && data.length > 0) {
          const stored = Number(localStorage.getItem('pc_nam_hoc_id'))
          const found = data.find((n) => n.id === stored)
          setNamHocId(found ? found.id : data[0].id)
        }
      })
      .catch(() => {})
    return () => {
      cancelled = true
    }
  }, [auth])

  useEffect(() => {
    if (!namHocId) {
      setHocKys([])
      setHocKyId(null)
      return
    }
    localStorage.setItem('pc_nam_hoc_id', String(namHocId))
    setLoading(true)
    let cancelled = false
    api('/hoc-ky', { params: { namHocId } })
      .then((data) => {
        if (cancelled) return
        setHocKys(data || [])
        if (data && data.length > 0) {
          const stored = Number(localStorage.getItem('pc_hoc_ky_id'))
          const found = data.find((h) => h.id === stored)
          const preferred = data.find((h) => h.soHk === 1)
          setHocKyId(found ? found.id : (preferred || data[0]).id)
        } else {
          setHocKyId(null)
        }
      })
      .catch(() => {})
      .finally(() => !cancelled && setLoading(false))
    return () => {
      cancelled = true
    }
  }, [namHocId])

  useEffect(() => {
    if (hocKyId) localStorage.setItem('pc_hoc_ky_id', String(hocKyId))
  }, [hocKyId])

  const value = useMemo(
    () => ({
      namHocs,
      namHocId,
      setNamHocId,
      namHoc: namHocs.find((n) => n.id === namHocId) || null,
      hocKys,
      hocKyId,
      setHocKyId,
      hocKy: hocKys.find((h) => h.id === hocKyId) || null,
      loading,
    }),
    [namHocs, namHocId, hocKys, hocKyId, loading],
  )

  return <PeriodContext.Provider value={value}>{children}</PeriodContext.Provider>
}

export function usePeriod() {
  const ctx = useContext(PeriodContext)
  if (!ctx) throw new Error('usePeriod phải dùng trong PeriodProvider')
  return ctx
}
