import { useCallback, useEffect, useState } from 'react'
import { api, download } from '../api.js'
import { useAuth } from '../auth.jsx'
import { usePeriod } from '../period.jsx'

const REPORTS = [
  { key: 'mau-1', label: 'Mẫu 1 - Phân công giảng viên theo học kỳ', scope: 'hocKy' },
  { key: 'mau-2', label: 'Mẫu 2 - Tổng hợp tải lượng giảng viên cơ hữu', scope: 'namHoc', roles: ['TRO_LY', 'BCN_KHOA'] },
  { key: 'mau-3', label: 'Mẫu 3 - Giảng viên mời giảng nội bộ', scope: 'namHoc', roles: ['TRO_LY', 'BCN_KHOA'] },
  { key: 'mau-4', label: 'Mẫu 4 - Giảng viên thỉnh giảng', scope: 'namHoc', roles: ['TRO_LY', 'BCN_KHOA'] },
  { key: 'mau-5', label: 'Mẫu 5 - Hoạt động ngoài trường', scope: 'namHoc', roles: ['TRO_LY', 'BCN_KHOA'] },
]

const EMPTY_HD = {
  trinhDo: 'DH',
  khoa: '',
  noiDung: '',
  diaDiem: '',
  hocKy: 1,
  soNgay: 1,
  tuNgay: '',
  denNgay: '',
  soLuongSv: 0,
}

export default function BaoCao() {
  const { auth } = useAuth()
  const { namHocId, hocKyId, namHoc, hocKy } = usePeriod()
  const isTbm = auth?.vaiTro === 'TRUONG_BO_MON'
  const canManageHd = auth?.vaiTro === 'TRO_LY'
  const [hoatDongs, setHoatDongs] = useState([])
  const [form, setForm] = useState(EMPTY_HD)
  const [error, setError] = useState('')
  const [tick, setTick] = useState(0)

  const loadHd = useCallback(() => {
    if (!namHocId) return
    api('/hoat-dong-ngoai-truong', { params: { namHocId } })
      .then((data) => setHoatDongs(data || []))
      .catch(() => {})
  }, [namHocId])

  useEffect(loadHd, [loadHd, tick])

  async function exportReport(report) {
    setError('')
    try {
      if (report.scope === 'hocKy') {
        const params = { hocKyId, boMonId: isTbm ? auth?.boMonId : undefined }
        await download(`/bao-cao/${report.key}`, `${report.key}_HK${hocKy?.soHk || ''}.xlsx`, params)
      } else {
        await download(`/bao-cao/${report.key}`, `${report.key}_${namHoc?.ten || ''}.xlsx`, { namHocId })
      }
    } catch (e) {
      setError(e.message)
    }
  }

  async function addHoatDong(e) {
    e.preventDefault()
    try {
      await api('/hoat-dong-ngoai-truong', {
        method: 'POST',
        body: {
          namHoc: { id: namHocId },
          trinhDo: form.trinhDo,
          khoa: form.khoa,
          noiDung: form.noiDung,
          diaDiem: form.diaDiem,
          hocKy: Number(form.hocKy),
          soNgay: Number(form.soNgay),
          tuNgay: form.tuNgay || null,
          denNgay: form.denNgay || null,
          soLuongSv: Number(form.soLuongSv),
        },
      })
      setForm(EMPTY_HD)
      setTick((t) => t + 1)
    } catch (err) {
      alert(err.message)
    }
  }

  async function removeHoatDong(id) {
    if (!confirm('Xóa hoạt động này?')) return
    try {
      await api(`/hoat-dong-ngoai-truong/${id}`, { method: 'DELETE' })
      setTick((t) => t + 1)
    } catch (err) {
      alert(err.message)
    }
  }

  const visible = REPORTS.filter((r) => !r.roles || r.roles.includes(auth?.vaiTro))

  return (
    <div className="space-y-4">
      <div>
        <h1 className="text-xl font-bold text-slate-900">Báo cáo & xuất Excel</h1>
        <p className="text-sm text-slate-500">
          {namHoc?.ten} {hocKy ? `- Học kỳ ${hocKy.soHk}` : ''}
        </p>
      </div>
      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}

      <div className="grid gap-3 md:grid-cols-2">
        {visible.map((r) => (
          <div key={r.key} className="card flex items-center justify-between gap-3">
            <div className="text-sm font-medium text-slate-700">{r.label}</div>
            <button className="btn-secondary whitespace-nowrap" onClick={() => exportReport(r)}>
              Tải Excel
            </button>
          </div>
        ))}
      </div>

      <div className="card">
        <h2 className="mb-3 font-semibold text-slate-800">Hoạt động tập trung ngoài trường</h2>
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-slate-200">
            <thead>
              <tr>
                <th className="th">Trình độ</th>
                <th className="th">Khoa</th>
                <th className="th">Nội dung</th>
                <th className="th">Địa điểm</th>
                <th className="th">HK</th>
                <th className="th">Số ngày</th>
                <th className="th">Thời gian</th>
                <th className="th">SV</th>
                <th className="th"></th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {hoatDongs.map((h) => (
                <tr key={h.id}>
                  <td className="td">{h.trinhDo}</td>
                  <td className="td">{h.khoa}</td>
                  <td className="td">{h.noiDung}</td>
                  <td className="td">{h.diaDiem}</td>
                  <td className="td">{h.hocKy}</td>
                  <td className="td">{h.soNgay}</td>
                  <td className="td">
                    {h.tuNgay || '—'} → {h.denNgay || '—'}
                  </td>
                  <td className="td">{h.soLuongSv}</td>
                  <td className="td text-right">
                    {canManageHd && (
                      <button className="text-sm font-medium text-red-600" onClick={() => removeHoatDong(h.id)}>
                        Xóa
                      </button>
                    )}
                  </td>
                </tr>
              ))}
              {hoatDongs.length === 0 && (
                <tr>
                  <td className="td text-slate-500" colSpan={9}>
                    Chưa có hoạt động.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {canManageHd && (
        <form className="card grid gap-3 md:grid-cols-4" onSubmit={addHoatDong}>
          <h2 className="font-semibold text-slate-800 md:col-span-4">Thêm hoạt động</h2>
          <div>
            <label className="label">Trình độ</label>
            <select className="input" value={form.trinhDo} onChange={(e) => setForm({ ...form, trinhDo: e.target.value })}>
              <option value="DH">Đại học</option>
              <option value="CĐ">Cao đẳng</option>
              <option value="SDH">Sau đại học</option>
            </select>
          </div>
          <div>
            <label className="label">Khoa</label>
            <input className="input" value={form.khoa} onChange={(e) => setForm({ ...form, khoa: e.target.value })} required />
          </div>
          <div className="md:col-span-2">
            <label className="label">Nội dung</label>
            <input
              className="input"
              value={form.noiDung}
              onChange={(e) => setForm({ ...form, noiDung: e.target.value })}
              required
            />
          </div>
          <div className="md:col-span-2">
            <label className="label">Địa điểm</label>
            <input className="input" value={form.diaDiem} onChange={(e) => setForm({ ...form, diaDiem: e.target.value })} />
          </div>
          <div>
            <label className="label">Học kỳ</label>
            <input
              className="input"
              type="number"
              min="1"
              max="3"
              value={form.hocKy}
              onChange={(e) => setForm({ ...form, hocKy: e.target.value })}
            />
          </div>
          <div>
            <label className="label">Số ngày</label>
            <input
              className="input"
              type="number"
              value={form.soNgay}
              onChange={(e) => setForm({ ...form, soNgay: e.target.value })}
            />
          </div>
          <div>
            <label className="label">Từ ngày</label>
            <input className="input" type="date" value={form.tuNgay} onChange={(e) => setForm({ ...form, tuNgay: e.target.value })} />
          </div>
          <div>
            <label className="label">Đến ngày</label>
            <input className="input" type="date" value={form.denNgay} onChange={(e) => setForm({ ...form, denNgay: e.target.value })} />
          </div>
          <div>
            <label className="label">Số lượng SV</label>
            <input
              className="input"
              type="number"
              value={form.soLuongSv}
              onChange={(e) => setForm({ ...form, soLuongSv: e.target.value })}
            />
          </div>
          <div className="flex items-end">
            <button className="btn-primary w-full" type="submit" disabled={!namHocId}>
              Thêm
            </button>
          </div>
        </form>
      )}
    </div>
  )
}
