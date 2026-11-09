import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth.jsx'
import { usePeriod } from '../period.jsx'

const ROLE_LABEL = {
  TRO_LY: 'Trợ lý đào tạo',
  TRUONG_BO_MON: 'Trưởng bộ môn',
  BCN_KHOA: 'Ban chủ nhiệm khoa',
  GIANG_VIEN: 'Giảng viên',
}

const NAV = [
  { to: '/dashboard', label: 'Tổng quan', roles: ['TRO_LY', 'TRUONG_BO_MON', 'BCN_KHOA', 'GIANG_VIEN'] },
  { to: '/ke-hoach-mo-lop', label: 'Kế hoạch mở lớp', roles: ['TRO_LY', 'TRUONG_BO_MON'] },
  { to: '/phan-cong', label: 'Phân công giảng dạy', roles: ['TRO_LY', 'TRUONG_BO_MON'] },
  { to: '/nguyen-vong', label: 'Nguyện vọng', roles: ['GIANG_VIEN', 'TRUONG_BO_MON'] },
  { to: '/lich-ranh-ban', label: 'Lịch rảnh bận', roles: ['GIANG_VIEN'] },
  { to: '/ke-hoach-bo-mon', label: 'Kế hoạch bộ môn', roles: ['TRUONG_BO_MON', 'BCN_KHOA'] },
  { to: '/bao-cao', label: 'Báo cáo & thống kê', roles: ['TRO_LY', 'TRUONG_BO_MON', 'BCN_KHOA'] },
  { to: '/admin', label: 'Danh mục', roles: ['TRO_LY'] },
]

export function Layout() {
  const { auth, logout } = useAuth()
  const { namHocs, namHocId, setNamHocId, hocKys, hocKyId, setHocKyId } = usePeriod()
  const navigate = useNavigate()

  const links = NAV.filter((item) => item.roles.includes(auth?.vaiTro))

  function handleLogout() {
    logout()
    navigate('/login')
  }

  return (
    <div className="min-h-screen lg:flex">
      <aside className="w-full shrink-0 bg-slate-900 text-slate-200 lg:w-64">
        <div className="border-b border-slate-800 px-5 py-5">
          <div className="text-lg font-bold text-white">Phân công GV</div>
          <div className="text-xs text-slate-400">Khoa CNTT - Đại học Sài Gòn</div>
        </div>
        <nav className="flex flex-wrap gap-1 p-3 lg:flex-col">
          {links.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                `rounded-lg px-3 py-2 text-sm font-medium transition ${
                  isActive ? 'bg-brand-600 text-white' : 'text-slate-300 hover:bg-slate-800'
                }`
              }
            >
              {item.label}
            </NavLink>
          ))}
        </nav>
      </aside>

      <div className="flex min-w-0 flex-1 flex-col">
        <header className="flex flex-wrap items-center gap-3 border-b border-slate-200 bg-white px-4 py-3">
          <div className="flex items-center gap-2">
            <span className="label mb-0">Năm học</span>
            <select className="input w-44" value={namHocId ?? ''} onChange={(e) => setNamHocId(Number(e.target.value))}>
              {namHocs.map((n) => (
                <option key={n.id} value={n.id}>
                  {n.ten}
                </option>
              ))}
            </select>
          </div>
          <div className="flex items-center gap-2">
            <span className="label mb-0">Học kỳ</span>
            <select className="input w-32" value={hocKyId ?? ''} onChange={(e) => setHocKyId(Number(e.target.value))}>
              {hocKys.map((h) => (
                <option key={h.id} value={h.id}>
                  Học kỳ {h.soHk}
                </option>
              ))}
            </select>
          </div>
          <div className="ml-auto flex items-center gap-3">
            <div className="text-right">
              <div className="text-sm font-semibold text-slate-800">{auth?.hoTen || auth?.username}</div>
              <div className="text-xs text-slate-500">{ROLE_LABEL[auth?.vaiTro] || auth?.vaiTro}</div>
            </div>
            <button className="btn-secondary" onClick={handleLogout}>
              Đăng xuất
            </button>
          </div>
        </header>
        <main className="flex-1 p-4 lg:p-6">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
