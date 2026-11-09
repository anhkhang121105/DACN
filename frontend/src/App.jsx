import { Navigate, Route, Routes, useLocation } from 'react-router-dom'
import { useAuth } from './auth.jsx'
import { Layout } from './components/Layout.jsx'
import Login from './pages/Login.jsx'
import Dashboard from './pages/Dashboard.jsx'
import KeHoachMoLop from './pages/KeHoachMoLop.jsx'
import NguyenVong from './pages/NguyenVong.jsx'
import LichRanhBan from './pages/LichRanhBan.jsx'
import PhanCong from './pages/PhanCong.jsx'
import KeHoachBoMon from './pages/KeHoachBoMon.jsx'
import BaoCao from './pages/BaoCao.jsx'
import Admin from './pages/Admin.jsx'

function RequireAuth({ children }) {
  const { auth } = useAuth()
  const location = useLocation()
  if (!auth) return <Navigate to="/login" replace state={{ from: location }} />
  return children
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route
        path="/"
        element={
          <RequireAuth>
            <Layout />
          </RequireAuth>
        }
      >
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="ke-hoach-mo-lop" element={<KeHoachMoLop />} />
        <Route path="nguyen-vong" element={<NguyenVong />} />
        <Route path="lich-ranh-ban" element={<LichRanhBan />} />
        <Route path="phan-cong" element={<PhanCong />} />
        <Route path="ke-hoach-bo-mon" element={<KeHoachBoMon />} />
        <Route path="bao-cao" element={<BaoCao />} />
        <Route path="admin/*" element={<Admin />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
