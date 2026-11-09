import { useEffect, useState } from 'react'
import { api } from '../api.js'
import { useAuth } from '../auth.jsx'
import { usePeriod } from '../period.jsx'

function Stat({ label, value, tone = 'slate' }) {
  const tones = {
    slate: 'bg-slate-50 text-slate-700',
    blue: 'bg-blue-50 text-blue-700',
    green: 'bg-emerald-50 text-emerald-700',
    red: 'bg-red-50 text-red-700',
    amber: 'bg-amber-50 text-amber-700',
  }
  return (
    <div className="card">
      <div className="text-xs font-semibold uppercase tracking-wide text-slate-500">{label}</div>
      <div className={`mt-2 inline-flex rounded-lg px-3 py-1 text-2xl font-bold ${tones[tone]}`}>{value}</div>
    </div>
  )
}

function StatusBadge({ status }) {
  const map = {
    DAT: ['Đạt', 'bg-emerald-100 text-emerald-700'],
    VUOT_TRAN: ['Vượt trần', 'bg-red-100 text-red-700'],
    DUOI_DINH_MUC: ['Dưới định mức', 'bg-amber-100 text-amber-700'],
  }
  const [label, cls] = map[status] || [status, 'bg-slate-100 text-slate-600']
  return <span className={`rounded-full px-2 py-0.5 text-xs font-semibold ${cls}`}>{label}</span>
}

export default function Dashboard() {
  const { auth } = useAuth()
  const { namHocId, hocKyId, namHoc, hocKy } = usePeriod()
  const [rows, setRows] = useState([])
  const [own, setOwn] = useState(null)
  const [ownClasses, setOwnClasses] = useState([])
  const [csr, setCsr] = useState(null)
  const [error, setError] = useState('')

  const isGV = auth?.vaiTro === 'GIANG_VIEN'
  const boMonId = auth?.vaiTro === 'TRUONG_BO_MON' ? auth?.boMonId : null

  useEffect(() => {
    if (!namHocId) return
    setError('')
    if (isGV) {
      Promise.all([
        api('/giang-vien/me/tai-luong', { params: { namHocId } }),
        hocKyId ? api('/giang-vien/me/phan-cong', { params: { hocKyId } }) : Promise.resolve([]),
      ])
        .then(([luong, classes]) => {
          setOwn(luong)
          setOwnClasses(classes || [])
        })
        .catch((e) => setError(e.message))
      return
    }
    api('/bao-cao/tai-luong', { params: { namHocId, boMonId } })
      .then((data) => setRows(data || []))
      .catch((e) => setError(e.message))
    if (boMonId) {
      api('/phan-cong/csr', { params: { namHocId, boMonId } })
        .then(setCsr)
        .catch(() => {})
    }
  }, [namHocId, hocKyId, isGV, boMonId])

  if (isGV) {
    return (
      <div className="space-y-4">
        <div>
          <h1 className="text-xl font-bold text-slate-900">Tổng quan giảng viên</h1>
          <p className="text-sm text-slate-500">
            {namHoc?.ten} {hocKy ? `- Học kỳ ${hocKy.soHk}` : ''}
          </p>
        </div>
        {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
        {own && (
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
            <Stat label="Tiết giảng dạy" value={own.tietGiangDay} tone="blue" />
            <Stat label="Công tác khác" value={own.tietCongTacKhac} />
            <Stat label="Tổng tải" value={own.tongTiet} tone="green" />
            <Stat
              label="Định mức"
              value={`${own.dinhMucMin} - ${own.dinhMucMax}`}
              tone={own.trangThai === 'VUOT_TRAN' ? 'red' : own.trangThai === 'DUOI_DINH_MUC' ? 'amber' : 'green'}
            />
          </div>
        )}
        <div className="card">
          <h2 className="mb-3 font-semibold text-slate-800">Lớp được phân công</h2>
          {ownClasses.length === 0 ? (
            <p className="text-sm text-slate-500">Chưa có lớp trong học kỳ này.</p>
          ) : (
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-slate-200">
                <thead>
                  <tr>
                    <th className="th">Môn học</th>
                    <th className="th">Nhóm</th>
                    <th className="th">Loại</th>
                    <th className="th">Thứ/Ca</th>
                    <th className="th">Tiết thực tế</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {ownClasses.map((n) => (
                    <tr key={n.id}>
                      <td className="td">{n.keHoach?.monHoc?.ten}</td>
                      <td className="td">{n.soNhom}</td>
                      <td className="td">{n.loai}</td>
                      <td className="td">{n.thu && n.ca ? `Thứ ${n.thu} - Ca ${n.ca}` : '—'}</td>
                      <td className="td">{n.soTietThucTe ?? n.soTietThucHien}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    )
  }

  const total = rows.length
  const vuot = rows.filter((r) => r.trangThai === 'VUOT_TRAN').length
  const duoi = rows.filter((r) => r.trangThai === 'DUOI_DINH_MUC').length
  const tongTiet = rows.reduce((sum, r) => sum + Number(r.tongTiet || 0), 0)

  return (
    <div className="space-y-4">
      <div>
        <h1 className="text-xl font-bold text-slate-900">{auth?.vaiTro === 'BCN_KHOA' ? 'Tổng quan khoa' : 'Tổng quan'}</h1>
        <p className="text-sm text-slate-500">{namHoc?.ten}</p>
      </div>
      {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <Stat label="Giảng viên cơ hữu" value={total} tone="blue" />
        <Stat label="Tổng tiết" value={tongTiet.toFixed(1)} />
        <Stat label="Vượt trần" value={vuot} tone="red" />
        <Stat label="Dưới định mức" value={duoi} tone="amber" />
      </div>
      {csr && (
        <div className="card">
          <div className="flex items-center justify-between">
            <div>
              <div className="text-xs font-semibold uppercase tracking-wide text-slate-500">CSR bộ môn (năm học)</div>
              <div className="mt-1 text-3xl font-bold text-brand-700">{csr.csr}%</div>
              <div className="text-sm text-slate-500">
                {csr.satisfiedChecks}/{csr.totalChecks} kiểm tra đạt - {csr.hardViolationCount} vi phạm cứng
              </div>
            </div>
            <div className="h-3 w-48 overflow-hidden rounded-full bg-slate-100">
              <div className="h-full bg-brand-600" style={{ width: `${Math.min(Number(csr.csr), 100)}%` }} />
            </div>
          </div>
        </div>
      )}
      <div className="card p-0">
        <div className="border-b border-slate-200 px-5 py-3 font-semibold text-slate-800">Tải lượng giảng viên</div>
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-slate-200">
            <thead className="bg-slate-50">
              <tr>
                <th className="th">Mã CB</th>
                <th className="th">Họ tên</th>
                <th className="th">Bộ môn</th>
                <th className="th">Giảng dạy</th>
                <th className="th">Công tác</th>
                <th className="th">Tổng</th>
                <th className="th">Định mức</th>
                <th className="th">Trạng thái</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {rows.map((r) => (
                <tr key={r.giangVienId} className="hover:bg-slate-50">
                  <td className="td font-medium">{r.maCb}</td>
                  <td className="td">{r.hoTen}</td>
                  <td className="td">{r.boMonTen}</td>
                  <td className="td">{r.tietGiangDay}</td>
                  <td className="td">{r.tietCongTacKhac}</td>
                  <td className="td font-semibold">{r.tongTiet}</td>
                  <td className="td">
                    {r.dinhMucMin} - {r.dinhMucMax}
                  </td>
                  <td className="td">
                    <StatusBadge status={r.trangThai} />
                  </td>
                </tr>
              ))}
              {rows.length === 0 && (
                <tr>
                  <td className="td text-slate-500" colSpan={8}>
                    Chưa có dữ liệu tải lượng.
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
