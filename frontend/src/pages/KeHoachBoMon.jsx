import { useCallback, useEffect, useState } from 'react'
import { api } from '../api.js'
import { useAuth } from '../auth.jsx'
import { usePeriod } from '../period.jsx'

const STATUS = {
  DRAFT: ['Đang lập', 'bg-slate-100 text-slate-600'],
  SUBMITTED: ['Đã nộp', 'bg-amber-100 text-amber-700'],
  APPROVED: ['Đã duyệt', 'bg-emerald-100 text-emerald-700'],
  RETURNED: ['Trả về', 'bg-red-100 text-red-700'],
}

function StatusBadge({ value }) {
  const [label, cls] = STATUS[value] || [value, 'bg-slate-100 text-slate-600']
  return <span className={`rounded-full px-2 py-0.5 text-xs font-semibold ${cls}`}>{label}</span>
}

export default function KeHoachBoMon() {
  const { auth } = useAuth()
  const { namHocId, namHoc } = usePeriod()
  const isTbm = auth?.vaiTro === 'TRUONG_BO_MON'
  const isBcn = auth?.vaiTro === 'BCN_KHOA'
  const [rows, setRows] = useState([])
  const [error, setError] = useState('')
  const [tick, setTick] = useState(0)

  const load = useCallback(() => {
    if (!namHocId) return
    setError('')
    api('/ke-hoach-bo-mon', { params: { namHocId } })
      .then((data) => setRows(data || []))
      .catch((e) => setError(e.message))
  }, [namHocId])

  useEffect(load, [load, tick])

  async function tao() {
    try {
      await api('/ke-hoach-bo-mon', {
        method: 'POST',
        body: { namHocId, boMonId: auth?.boMonId },
      })
      setTick((t) => t + 1)
    } catch (e) {
      alert(e.message)
    }
  }

  async function submit(row) {
    try {
      await api(`/ke-hoach-bo-mon/${row.id}/submit`, { method: 'PATCH' })
      setTick((t) => t + 1)
    } catch (e) {
      alert(e.message)
    }
  }

  async function approve(row) {
    try {
      await api(`/ke-hoach-bo-mon/${row.id}/approve`, { method: 'PATCH' })
      setTick((t) => t + 1)
    } catch (e) {
      alert(e.message)
    }
  }

  async function returnPlan(row) {
    const lyDo = prompt('Lý do trả về:')
    if (!lyDo) return
    try {
      await api(`/ke-hoach-bo-mon/${row.id}/return`, { method: 'PATCH', body: { lyDo } })
      setTick((t) => t + 1)
    } catch (e) {
      alert(e.message)
    }
  }

  async function xemCsr(row) {
    try {
      const csr = await api(`/ke-hoach-bo-mon/${row.id}/csr`)
      alert(`CSR: ${csr.csr}% (${csr.satisfiedChecks}/${csr.totalChecks} kiểm tra đạt, ${csr.hardViolationCount} vi phạm cứng)`)
    } catch (e) {
      alert(e.message)
    }
  }

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap items-end justify-between gap-3">
        <div>
          <h1 className="text-xl font-bold text-slate-900">Kế hoạch phân công bộ môn</h1>
          <p className="text-sm text-slate-500">{namHoc?.ten}</p>
        </div>
        {isTbm && (
          <button className="btn-primary" onClick={tao}>
            Tạo kế hoạch cho bộ môn
          </button>
        )}
      </div>
      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}

      <div className="card p-0">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-slate-200">
            <thead className="bg-slate-50">
              <tr>
                <th className="th">Bộ môn</th>
                <th className="th">Trạng thái</th>
                <th className="th">CSR</th>
                <th className="th">Lý do trả về</th>
                <th className="th"></th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {rows.map((r) => (
                <tr key={r.id} className="hover:bg-slate-50">
                  <td className="td font-medium">{r.boMon?.ten}</td>
                  <td className="td">
                    <StatusBadge value={r.trangThai} />
                  </td>
                  <td className="td font-semibold">{r.csr}%</td>
                  <td className="td max-w-xs truncate text-red-600" title={r.lyDoTraVe}>
                    {r.lyDoTraVe || ''}
                  </td>
                  <td className="td text-right whitespace-nowrap">
                    <button className="mr-3 text-sm font-medium text-slate-600" onClick={() => xemCsr(r)}>
                      Xem CSR
                    </button>
                    {isTbm && (r.trangThai === 'DRAFT' || r.trangThai === 'RETURNED') && (
                      <button className="text-sm font-medium text-brand-600" onClick={() => submit(r)}>
                        Nộp
                      </button>
                    )}
                    {isBcn && r.trangThai === 'SUBMITTED' && (
                      <>
                        <button className="mr-3 text-sm font-medium text-emerald-600" onClick={() => approve(r)}>
                          Duyệt
                        </button>
                        <button className="text-sm font-medium text-red-600" onClick={() => returnPlan(r)}>
                          Trả về
                        </button>
                      </>
                    )}
                  </td>
                </tr>
              ))}
              {rows.length === 0 && (
                <tr>
                  <td className="td text-slate-500" colSpan={5}>
                    Chưa có kế hoạch.
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
