import { useEffect, useState } from 'react'
import { api } from '../api.js'
import { useAuth } from '../auth.jsx'
import { usePeriod } from '../period.jsx'

const EMPTY = { monHocId: '', khoaHienThi: 'CNTT', tongSv: 80, maxSvNhom: 50, ghiChu: '', sinhNhom: true }

export default function KeHoachMoLop() {
  const { auth } = useAuth()
  const { hocKyId, namHoc, hocKy } = usePeriod()
  const [rows, setRows] = useState([])
  const [monHocs, setMonHocs] = useState([])
  const [form, setForm] = useState(EMPTY)
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)
  const [tick, setTick] = useState(0)

  const canEdit = auth?.vaiTro === 'TRO_LY'

  useEffect(() => {
    api('/mon-hoc')
      .then((data) => {
        setMonHocs(data || [])
        if (data && data.length > 0) setForm((f) => ({ ...f, monHocId: f.monHocId || data[0].id }))
      })
      .catch(() => {})
  }, [])

  useEffect(() => {
    if (!hocKyId) return
    setError('')
    api('/ke-hoach-mo-lop', { params: { hocKyId } })
      .then((data) => setRows(data || []))
      .catch((e) => setError(e.message))
  }, [hocKyId, tick])

  async function create(e) {
    e.preventDefault()
    if (!hocKyId) return
    setBusy(true)
    try {
      await api('/ke-hoach-mo-lop', {
        method: 'POST',
        params: { sinhNhom: form.sinhNhom },
        body: {
          hocKy: { id: hocKyId },
          monHoc: { id: Number(form.monHocId) },
          khoaHienThi: form.khoaHienThi,
          tongSv: Number(form.tongSv),
          maxSvNhom: Number(form.maxSvNhom),
          ghiChu: form.ghiChu,
        },
      })
      setForm({ ...EMPTY, monHocId: form.monHocId })
      setTick((t) => t + 1)
    } catch (err) {
      alert(err.message)
    } finally {
      setBusy(false)
    }
  }

  async function sinhNhom(id) {
    if (!confirm('Sinh lại nhóm học phần? Chỉ thực hiện khi chưa phân công.')) return
    try {
      await api(`/ke-hoach-mo-lop/${id}/sinh-nhom`, { method: 'POST', params: { force: true } })
      setTick((t) => t + 1)
      alert('Đã sinh nhóm học phần')
    } catch (err) {
      alert(err.message)
    }
  }

  async function remove(id) {
    if (!confirm('Xóa kế hoạch mở lớp này?')) return
    try {
      await api(`/ke-hoach-mo-lop/${id}`, { method: 'DELETE' })
      setTick((t) => t + 1)
    } catch (err) {
      alert(err.message)
    }
  }

  const soNhom = (r) => r.soNhom ?? Math.ceil((r.tongSv || 0) / (r.maxSvNhom || 1))

  return (
    <div className="space-y-4">
      <div>
        <h1 className="text-xl font-bold text-slate-900">Kế hoạch mở lớp</h1>
        <p className="text-sm text-slate-500">
          {namHoc?.ten} {hocKy ? `- Học kỳ ${hocKy.soHk}` : ''}
        </p>
      </div>
      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}

      {canEdit && (
        <form className="card grid gap-3 md:grid-cols-6" onSubmit={create}>
          <div className="md:col-span-2">
            <label className="label">Môn học</label>
            <select
              className="input"
              value={form.monHocId}
              onChange={(e) => setForm({ ...form, monHocId: e.target.value })}
              required
            >
              <option value="">Chọn môn</option>
              {monHocs.map((m) => (
                <option key={m.id} value={m.id}>
                  {m.maHp} - {m.ten}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="label">Khoa hiển thị</label>
            <input
              className="input"
              value={form.khoaHienThi}
              onChange={(e) => setForm({ ...form, khoaHienThi: e.target.value })}
            />
          </div>
          <div>
            <label className="label">Tổng SV</label>
            <input
              className="input"
              type="number"
              value={form.tongSv}
              onChange={(e) => setForm({ ...form, tongSv: e.target.value })}
            />
          </div>
          <div>
            <label className="label">SV tối đa/nhóm</label>
            <input
              className="input"
              type="number"
              value={form.maxSvNhom}
              onChange={(e) => setForm({ ...form, maxSvNhom: e.target.value })}
            />
          </div>
          <div className="flex items-end">
            <label className="flex items-center gap-2 text-sm">
              <input
                type="checkbox"
                checked={form.sinhNhom}
                onChange={(e) => setForm({ ...form, sinhNhom: e.target.checked })}
              />
              Tự sinh nhóm
            </label>
          </div>
          <div className="md:col-span-5">
            <label className="label">Ghi chú</label>
            <input className="input" value={form.ghiChu} onChange={(e) => setForm({ ...form, ghiChu: e.target.value })} />
          </div>
          <div className="flex items-end">
            <button className="btn-primary w-full" type="submit" disabled={busy || !hocKyId}>
              {busy ? 'Đang lưu...' : 'Thêm kế hoạch'}
            </button>
          </div>
        </form>
      )}

      <div className="card p-0">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-slate-200">
            <thead className="bg-slate-50">
              <tr>
                <th className="th">Môn học</th>
                <th className="th">Mã HP</th>
                <th className="th">Khoa</th>
                <th className="th">Tổng SV</th>
                <th className="th">SV/nhóm</th>
                <th className="th">Số nhóm</th>
                <th className="th">Ghi chú</th>
                <th className="th"></th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {rows.map((r) => (
                <tr key={r.id} className="hover:bg-slate-50">
                  <td className="td font-medium">{r.monHoc?.ten}</td>
                  <td className="td">{r.monHoc?.maHp}</td>
                  <td className="td">{r.khoaHienThi}</td>
                  <td className="td">{r.tongSv}</td>
                  <td className="td">{r.maxSvNhom}</td>
                  <td className="td font-semibold">{soNhom(r)}</td>
                  <td className="td max-w-xs truncate" title={r.ghiChu}>
                    {r.ghiChu}
                  </td>
                  <td className="td text-right whitespace-nowrap">
                    {canEdit && (
                      <>
                        <button className="mr-3 text-sm font-medium text-brand-600" onClick={() => sinhNhom(r.id)}>
                          Sinh nhóm
                        </button>
                        <button className="text-sm font-medium text-red-600" onClick={() => remove(r.id)}>
                          Xóa
                        </button>
                      </>
                    )}
                  </td>
                </tr>
              ))}
              {rows.length === 0 && (
                <tr>
                  <td className="td text-slate-500" colSpan={8}>
                    Chưa có kế hoạch mở lớp trong học kỳ này.
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
