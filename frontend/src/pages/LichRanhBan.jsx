import { useEffect, useState } from 'react'
import { api } from '../api.js'
import { useAuth } from '../auth.jsx'
import { usePeriod } from '../period.jsx'

const THU = [2, 3, 4, 5, 6, 7]
const CA = [1, 2, 3, 4]

function key(thu, ca) {
  return `${thu}-${ca}`
}

export default function LichRanhBan() {
  const { auth } = useAuth()
  const { hocKyId, namHoc, hocKy } = usePeriod()
  const [busySlots, setBusySlots] = useState(new Set())
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)
  const [message, setMessage] = useState('')

  useEffect(() => {
    if (!hocKyId || !auth?.giangVienId) return
    setError('')
    setMessage('')
    api('/lich-ranh-ban/cua-toi', { params: { hocKyId } })
      .then((data) => {
        const set = new Set()
        ;(data || []).forEach((x) => {
          if (!x.ranh) set.add(key(x.thu, x.ca))
        })
        setBusySlots(set)
      })
      .catch((e) => setError(e.message))
  }, [hocKyId, auth?.giangVienId])

  function toggle(thu, ca) {
    const k = key(thu, ca)
    const next = new Set(busySlots)
    if (next.has(k)) next.delete(k)
    else next.add(k)
    setBusySlots(next)
  }

  async function save() {
    if (!hocKyId) return
    setSaving(true)
    setMessage('')
    try {
      const items = []
      THU.forEach((thu) => {
        CA.forEach((ca) => {
          items.push({ thu, ca, ranh: !busySlots.has(key(thu, ca)) })
        })
      })
      await api('/lich-ranh-ban/cua-toi', { method: 'PUT', body: { hocKyId, items } })
      setMessage('Đã lưu lịch rảnh bận')
    } catch (e) {
      setError(e.message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="space-y-4">
      <div>
        <h1 className="text-xl font-bold text-slate-900">Lịch rảnh bận</h1>
        <p className="text-sm text-slate-500">
          {namHoc?.ten} {hocKy ? `- Học kỳ ${hocKy.soHk}` : ''} · Nhấn vào ô để chuyển Rảnh/Bận
        </p>
      </div>
      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      {message && <div className="rounded-lg bg-emerald-50 px-3 py-2 text-sm text-emerald-700">{message}</div>}

      <div className="card overflow-x-auto">
        <table className="min-w-full border-separate border-spacing-1">
          <thead>
            <tr>
              <th className="th">Ca \ Thứ</th>
              {THU.map((t) => (
                <th key={t} className="th text-center">
                  Thứ {t}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {CA.map((ca) => (
              <tr key={ca}>
                <td className="td font-semibold">Ca {ca}</td>
                {THU.map((thu) => {
                  const busy = busySlots.has(key(thu, ca))
                  return (
                    <td key={thu} className="p-0.5">
                      <button
                        type="button"
                        onClick={() => toggle(thu, ca)}
                        className={`w-full rounded-lg px-3 py-2 text-sm font-medium transition ${
                          busy ? 'bg-red-100 text-red-700 hover:bg-red-200' : 'bg-emerald-100 text-emerald-700 hover:bg-emerald-200'
                        }`}
                      >
                        {busy ? 'Bận' : 'Rảnh'}
                      </button>
                    </td>
                  )
                })}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="flex items-center gap-3">
        <button className="btn-primary" onClick={save} disabled={saving || !hocKyId}>
          {saving ? 'Đang lưu...' : 'Lưu lịch'}
        </button>
        <span className="text-sm text-slate-500">Mặc định tất cả các ca là rảnh.</span>
      </div>
    </div>
  )
}
