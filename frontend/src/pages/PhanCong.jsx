import { useCallback, useEffect, useMemo, useState } from 'react'
import { api } from '../api.js'
import { useAuth } from '../auth.jsx'
import { usePeriod } from '../period.jsx'

const THU = [2, 3, 4, 5, 6, 7]
const CA = [1, 2, 3, 4]

function CsrBadge({ csr }) {
  if (!csr) return null
  const value = Number(csr.csr)
  const tone = value >= 100 ? 'text-emerald-600' : value >= 80 ? 'text-amber-600' : 'text-red-600'
  return (
    <div className="card flex items-center gap-4">
      <div>
        <div className="text-xs font-semibold uppercase tracking-wide text-slate-500">CSR bộ môn</div>
        <div className={`text-3xl font-bold ${tone}`}>{value}%</div>
        <div className="text-xs text-slate-500">
          {csr.satisfiedChecks}/{csr.totalChecks} kiểm tra đạt · {csr.hardViolationCount} vi phạm cứng
        </div>
      </div>
      <div className="h-3 flex-1 overflow-hidden rounded-full bg-slate-100">
        <div className={`h-full ${value >= 100 ? 'bg-emerald-500' : value >= 80 ? 'bg-amber-500' : 'bg-red-500'}`} style={{ width: `${Math.min(value, 100)}%` }} />
      </div>
    </div>
  )
}

export default function PhanCong() {
  const { auth } = useAuth()
  const { namHocId, hocKyId, namHoc, hocKy } = usePeriod()
  const isTbm = auth?.vaiTro === 'TRUONG_BO_MON'
  const [boMons, setBoMons] = useState([])
  const [boMonId, setBoMonId] = useState(isTbm ? auth?.boMonId || '' : '')
  const [rows, setRows] = useState([])
  const [giangViens, setGiangViens] = useState([])
  const [csr, setCsr] = useState(null)
  const [suggestion, setSuggestion] = useState(null)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!isTbm) {
      api('/bo-mon')
        .then((data) => {
          setBoMons(data || [])
          if (data && data.length > 0) setBoMonId((current) => current || data[0].id)
        })
        .catch(() => {})
    }
  }, [isTbm])

  const load = useCallback(() => {
    if (!hocKyId) return
    setError('')
    Promise.all([
      api('/phan-cong', { params: { hocKyId, boMonId: boMonId || undefined } }),
      api('/giang-vien', { params: { boMonId: boMonId || undefined } }),
    ])
      .then(([nhoms, gvs]) => {
        setRows(nhoms || [])
        setGiangViens(gvs || [])
      })
      .catch((e) => setError(e.message))
  }, [hocKyId, boMonId])

  useEffect(load, [load])

  const loadCsr = useCallback(() => {
    if (!namHocId || !boMonId) {
      setCsr(null)
      return
    }
    api('/phan-cong/csr', { params: { namHocId, boMonId } })
      .then(setCsr)
      .catch(() => setCsr(null))
  }, [namHocId, boMonId])

  useEffect(loadCsr, [loadCsr])

  async function assign(nhom, gvId) {
    if (!gvId) {
      try {
        await api(`/phan-cong/thu-cong/${nhom.id}`, { method: 'DELETE' })
        load()
        loadCsr()
      } catch (e) {
        alert(e.message)
      }
      return
    }
    try {
      await api('/phan-cong/thu-cong', {
        method: 'POST',
        params: { nhomId: nhom.id },
        body: { giangVienId: Number(gvId), isFixed: true },
      })
      load()
      loadCsr()
    } catch (e) {
      alert(e.message)
    }
  }

  async function setSchedule(nhom, thu, ca) {
    try {
      await api(`/phan-cong/${nhom.id}/lich-day`, {
        method: 'PATCH',
        params: { thu: thu || undefined, ca: ca || undefined },
      })
      load()
      loadCsr()
    } catch (e) {
      alert(e.message)
    }
  }

  async function suggest() {
    if (!hocKyId || !boMonId) return
    setBusy(true)
    try {
      const result = await api('/phan-cong/goi-y', {
        method: 'POST',
        params: { hocKyId, boMonId },
      })
      setSuggestion(result)
    } catch (e) {
      alert(e.message)
    } finally {
      setBusy(false)
    }
  }

  async function acceptSuggestion() {
    if (!suggestion?.assignments?.length) return
    setBusy(true)
    try {
      await api('/phan-cong/goi-y/chap-nhan', {
        method: 'POST',
        body: suggestion.assignments,
      })
      setSuggestion(null)
      load()
      loadCsr()
      alert('Đã áp dụng phương án gợi ý')
    } catch (e) {
      alert(e.message)
    } finally {
      setBusy(false)
    }
  }

  const stats = useMemo(() => {
    const assigned = rows.filter((r) => r.giangVien).length
    return { total: rows.length, assigned, unassigned: rows.length - assigned }
  }, [rows])

  const tenBoMon = (id) => boMons.find((b) => b.id === id)?.ten

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap items-end justify-between gap-3">
        <div>
          <h1 className="text-xl font-bold text-slate-900">Phân công giảng dạy</h1>
          <p className="text-sm text-slate-500">
            {namHoc?.ten} {hocKy ? `- Học kỳ ${hocKy.soHk}` : ''}
          </p>
        </div>
        <div className="flex flex-wrap items-end gap-2">
          {!isTbm && (
            <div>
              <label className="label">Bộ môn</label>
              <select className="input w-56" value={boMonId} onChange={(e) => setBoMonId(e.target.value)}>
                <option value="">Tất cả</option>
                {boMons.map((b) => (
                  <option key={b.id} value={b.id}>
                    {b.ten}
                  </option>
                ))}
              </select>
            </div>
          )}
          <button className="btn-secondary" onClick={suggest} disabled={busy || !hocKyId || !boMonId}>
            {busy ? 'Đang giải...' : 'Gợi ý phân công (CP-SAT)'}
          </button>
          <button className="btn-secondary" onClick={load}>
            Tải lại
          </button>
        </div>
      </div>

      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}

      <div className="grid gap-4 sm:grid-cols-3">
        <div className="card">
          <div className="text-xs font-semibold uppercase text-slate-500">Tổng nhóm</div>
          <div className="text-2xl font-bold text-slate-800">{stats.total}</div>
        </div>
        <div className="card">
          <div className="text-xs font-semibold uppercase text-slate-500">Đã phân công</div>
          <div className="text-2xl font-bold text-emerald-600">{stats.assigned}</div>
        </div>
        <div className="card">
          <div className="text-xs font-semibold uppercase text-slate-500">Chưa phân công</div>
          <div className="text-2xl font-bold text-red-600">{stats.unassigned}</div>
        </div>
      </div>

      <CsrBadge csr={csr} />

      {suggestion && (
        <div className="card border-brand-200">
          <div className="mb-2 flex flex-wrap items-center justify-between gap-2">
            <div>
              <h3 className="font-semibold text-slate-800">Kết quả gợi ý</h3>
              <p className="text-xs text-slate-500">
                Trạng thái: {suggestion.status} · Giá trị mục tiêu: {suggestion.objectiveValue} · {suggestion.message}
              </p>
            </div>
            <div className="flex gap-2">
              <button className="btn-primary" onClick={acceptSuggestion} disabled={busy || !suggestion.assignments?.length}>
                Áp dụng
              </button>
              <button className="btn-secondary" onClick={() => setSuggestion(null)}>
                Đóng
              </button>
            </div>
          </div>
          {suggestion.assignments?.length ? (
            <div className="max-h-72 overflow-auto">
              <table className="min-w-full divide-y divide-slate-200">
                <thead className="bg-slate-50">
                  <tr>
                    <th className="th">Môn học</th>
                    <th className="th">Nhóm</th>
                    <th className="th">Loại</th>
                    <th className="th">Giảng viên</th>
                    <th className="th">Tiết</th>
                    <th className="th">Ưu tiên</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {suggestion.assignments.map((a) => (
                    <tr key={`${a.nhomId}-${a.giangVienId}`}>
                      <td className="td">{a.monHoc}</td>
                      <td className="td">{a.soNhom}</td>
                      <td className="td">{a.loai}</td>
                      <td className="td">{a.giangVienTen}</td>
                      <td className="td">{a.soTietThucTe}</td>
                      <td className="td">{a.diemUuTien}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <p className="text-sm text-slate-500">Không có gợi ý (có thể đã phân công đủ).</p>
          )}
        </div>
      )}

      <div className="card p-0">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-slate-200">
            <thead className="bg-slate-50">
              <tr>
                <th className="th">Môn học</th>
                <th className="th">Nhóm</th>
                <th className="th">Loại</th>
                <th className="th">Tiết</th>
                <th className="th">Thứ</th>
                <th className="th">Ca</th>
                <th className="th">Giảng viên</th>
                <th className="th">Nguồn</th>
                <th className="th"></th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {rows.map((n) => (
                <tr key={n.id} className={n.giangVien ? 'hover:bg-slate-50' : 'bg-amber-50/40 hover:bg-amber-50'}>
                  <td className="td">
                    <div className="font-medium">{n.keHoach?.monHoc?.ten}</div>
                    <div className="text-xs text-slate-500">{n.keHoach?.monHoc?.maHp}</div>
                  </td>
                  <td className="td">{n.soNhom}</td>
                  <td className="td">{n.loai}</td>
                  <td className="td">{n.soTietThucTe ?? n.soTietThucHien}</td>
                  <td className="td">
                    <select
                      className="input py-1"
                      value={n.thu ?? ''}
                      onChange={(e) => setSchedule(n, e.target.value ? Number(e.target.value) : null, n.ca)}
                    >
                      <option value="">—</option>
                      {THU.map((t) => (
                        <option key={t} value={t}>
                          {t}
                        </option>
                      ))}
                    </select>
                  </td>
                  <td className="td">
                    <select
                      className="input py-1"
                      value={n.ca ?? ''}
                      onChange={(e) => setSchedule(n, n.thu, e.target.value ? Number(e.target.value) : null)}
                    >
                      <option value="">—</option>
                      {CA.map((c) => (
                        <option key={c} value={c}>
                          {c}
                        </option>
                      ))}
                    </select>
                  </td>
                  <td className="td">
                    <select
                      className="input min-w-[220px] py-1"
                      value={n.giangVien?.id ?? ''}
                      onChange={(e) => assign(n, e.target.value)}
                    >
                      <option value="">— Chưa phân công —</option>
                      {giangViens.map((g) => (
                        <option key={g.id} value={g.id}>
                          {g.maCb} - {g.hoTen}
                        </option>
                      ))}
                    </select>
                  </td>
                  <td className="td">
                    <span className="rounded-full bg-slate-100 px-2 py-0.5 text-xs font-semibold text-slate-600">
                      {n.nguon}
                    </span>
                  </td>
                  <td className="td text-right">
                    {n.giangVien && (
                      <button className="text-sm font-medium text-red-600" onClick={() => assign(n, '')}>
                        Hủy
                      </button>
                    )}
                  </td>
                </tr>
              ))}
              {rows.length === 0 && (
                <tr>
                  <td className="td text-slate-500" colSpan={9}>
                    {boMonId && !isTbm ? `Chưa có nhóm học phần của ${tenBoMon(Number(boMonId)) || 'bộ môn'}.` : 'Chưa có nhóm học phần.'}
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
