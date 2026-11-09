import { useCallback, useEffect, useMemo, useState } from 'react'
import { api } from '../api.js'
import { useAuth } from '../auth.jsx'
import { usePeriod } from '../period.jsx'

const STATUS = {
  CHO_DUYET: ['Chờ duyệt', 'bg-amber-100 text-amber-700'],
  DA_DUYET: ['Đã duyệt', 'bg-emerald-100 text-emerald-700'],
  TU_CHOI: ['Từ chối', 'bg-red-100 text-red-700'],
}

function StatusBadge({ value }) {
  const [label, cls] = STATUS[value] || [value, 'bg-slate-100 text-slate-600']
  return <span className={`rounded-full px-2 py-0.5 text-xs font-semibold ${cls}`}>{label}</span>
}

export default function NguyenVong() {
  const { auth } = useAuth()
  const { hocKyId, namHoc, hocKy } = usePeriod()
  const isGV = auth?.vaiTro === 'GIANG_VIEN'
  const [rows, setRows] = useState([])
  const [monHocs, setMonHocs] = useState([])
  const [allowed, setAllowed] = useState([])
  const [form, setForm] = useState({ monHocId: '', mucUuTien: 3, caUuTien: '' })
  const [error, setError] = useState('')
  const [tick, setTick] = useState(0)

  useEffect(() => {
    api('/mon-hoc')
      .then((data) => setMonHocs(data || []))
      .catch(() => {})
    if (isGV && auth?.giangVienId) {
      api('/giang-vien-mon-hoc', { params: { giangVienId: auth.giangVienId } })
        .then((data) => setAllowed((data || []).map((x) => x.monHocId)))
        .catch(() => {})
    }
  }, [isGV, auth?.giangVienId])

  const load = useCallback(() => {
    if (!hocKyId) return
    setError('')
    const request = isGV
      ? api('/nguyen-vong/cua-toi', { params: { hocKyId } })
      : api('/nguyen-vong', { params: { hocKyId, boMonId: auth?.boMonId || undefined } })
    request.then((data) => setRows(data || [])).catch((e) => setError(e.message))
  }, [hocKyId, isGV, auth?.boMonId])

  useEffect(load, [load, tick])

  const monCuaToi = useMemo(() => monHocs.filter((m) => allowed.includes(m.id)), [monHocs, allowed])
  const availableMon = monCuaToi.filter((m) => !rows.some((r) => r.monHoc?.id === m.id))

  async function add(e) {
    e.preventDefault()
    if (!hocKyId || !form.monHocId) return
    try {
      await api('/nguyen-vong', {
        method: 'POST',
        body: {
          hocKy: { id: hocKyId },
          monHoc: { id: Number(form.monHocId) },
          mucUuTien: Number(form.mucUuTien),
          caUuTien: form.caUuTien,
        },
      })
      setForm({ monHocId: '', mucUuTien: 3, caUuTien: '' })
      setTick((t) => t + 1)
    } catch (err) {
      alert(err.message)
    }
  }

  async function remove(id) {
    if (!confirm('Xóa nguyện vọng này?')) return
    try {
      await api(`/nguyen-vong/${id}`, { method: 'DELETE' })
      setTick((t) => t + 1)
    } catch (err) {
      alert(err.message)
    }
  }

  async function review(id, trangThai) {
    try {
      await api(`/nguyen-vong/${id}/duyet`, { method: 'PATCH', params: { trangThai } })
      setTick((t) => t + 1)
    } catch (err) {
      alert(err.message)
    }
  }

  return (
    <div className="space-y-4">
      <div>
        <h1 className="text-xl font-bold text-slate-900">Nguyện vọng giảng dạy</h1>
        <p className="text-sm text-slate-500">
          {namHoc?.ten} {hocKy ? `- Học kỳ ${hocKy.soHk}` : ''}
          {hocKy && !hocKy.moDangKy && isGV ? ' · Học kỳ chưa mở đăng ký' : ''}
        </p>
      </div>
      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}

      {isGV && (
        <form className="card grid gap-3 md:grid-cols-4" onSubmit={add}>
          <div className="md:col-span-2">
            <label className="label">Môn học (đúng chuyên môn)</label>
            <select
              className="input"
              value={form.monHocId}
              onChange={(e) => setForm({ ...form, monHocId: e.target.value })}
              required
            >
              <option value="">Chọn môn</option>
              {availableMon.map((m) => (
                <option key={m.id} value={m.id}>
                  {m.maHp} - {m.ten}
                </option>
              ))}
            </select>
            {availableMon.length === 0 && (
              <p className="mt-1 text-xs text-amber-600">Không còn môn nào để đăng ký (hoặc chưa có chuyên môn).</p>
            )}
          </div>
          <div>
            <label className="label">Mức ưu tiên (1-5)</label>
            <input
              className="input"
              type="number"
              min="1"
              max="5"
              value={form.mucUuTien}
              onChange={(e) => setForm({ ...form, mucUuTien: e.target.value })}
            />
          </div>
          <div>
            <label className="label">Ca ưu tiên</label>
            <input
              className="input"
              placeholder="VD: Thứ 3 - Ca 2"
              value={form.caUuTien}
              onChange={(e) => setForm({ ...form, caUuTien: e.target.value })}
            />
          </div>
          <div className="md:col-span-4">
            <button className="btn-primary" type="submit" disabled={!hocKyId || !form.monHocId}>
              Đăng ký nguyện vọng
            </button>
          </div>
        </form>
      )}

      <div className="card p-0">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-slate-200">
            <thead className="bg-slate-50">
              <tr>
                {!isGV && <th className="th">Giảng viên</th>}
                <th className="th">Môn học</th>
                <th className="th">Ưu tiên</th>
                <th className="th">Ca ưu tiên</th>
                <th className="th">Trạng thái</th>
                <th className="th"></th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {rows.map((r) => (
                <tr key={r.id} className="hover:bg-slate-50">
                  {!isGV && (
                    <td className="td">
                      {r.giangVien?.maCb} - {r.giangVien?.hoTen}
                    </td>
                  )}
                  <td className="td font-medium">{r.monHoc?.ten}</td>
                  <td className="td">{r.mucUuTien}</td>
                  <td className="td">{r.caUuTien || '—'}</td>
                  <td className="td">
                    <StatusBadge value={r.trangThai} />
                  </td>
                  <td className="td text-right whitespace-nowrap">
                    {isGV && r.trangThai === 'CHO_DUYET' && (
                      <button className="text-sm font-medium text-red-600" onClick={() => remove(r.id)}>
                        Xóa
                      </button>
                    )}
                    {!isGV && r.trangThai === 'CHO_DUYET' && (
                      <>
                        <button
                          className="mr-3 text-sm font-medium text-emerald-600"
                          onClick={() => review(r.id, 'DA_DUYET')}
                        >
                          Duyệt
                        </button>
                        <button className="text-sm font-medium text-red-600" onClick={() => review(r.id, 'TU_CHOI')}>
                          Từ chối
                        </button>
                      </>
                    )}
                  </td>
                </tr>
              ))}
              {rows.length === 0 && (
                <tr>
                  <td className="td text-slate-500" colSpan={isGV ? 5 : 6}>
                    Chưa có nguyện vọng.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
