import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../auth.jsx'

const DEMO = [
  { username: 'trolydt', label: 'Trợ lý đào tạo' },
  { username: 'bcnkhoa', label: 'Ban chủ nhiệm khoa' },
  { username: 'tbm_ktpm', label: 'Trưởng bộ môn KTPM' },
  { username: 'gv10002', label: 'Giảng viên' },
]

export default function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [username, setUsername] = useState('trolydt')
  const [password, setPassword] = useState('123456')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function submit(event) {
    event.preventDefault()
    setError('')
    setLoading(true)
    try {
      await login(username.trim(), password)
      navigate('/dashboard')
    } catch (e) {
      setError(e.message || 'Đăng nhập thất bại')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-slate-900 via-slate-800 to-brand-700 p-4">
      <div className="w-full max-w-md rounded-2xl bg-white p-8 shadow-2xl">
        <div className="mb-6 text-center">
          <h1 className="text-2xl font-bold text-slate-900">Hệ thống phân công giảng viên</h1>
          <p className="mt-1 text-sm text-slate-500">Khoa Công nghệ thông tin - Đại học Sài Gòn</p>
        </div>
        <form onSubmit={submit} className="space-y-4">
          <div>
            <label className="label">Tên đăng nhập</label>
            <input className="input" value={username} onChange={(e) => setUsername(e.target.value)} autoFocus />
          </div>
          <div>
            <label className="label">Mật khẩu</label>
            <input
              className="input"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>
          {error && <div className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div>}
          <button className="btn-primary w-full" type="submit" disabled={loading}>
            {loading ? 'Đang đăng nhập...' : 'Đăng nhập'}
          </button>
        </form>
        <div className="mt-6 rounded-lg bg-slate-50 p-3">
          <div className="mb-2 text-xs font-semibold uppercase text-slate-500">Tài khoản demo (mật khẩu 123456)</div>
          <div className="grid grid-cols-2 gap-2">
            {DEMO.map((d) => (
              <button
                key={d.username}
                type="button"
                className="rounded border border-slate-200 bg-white px-2 py-1 text-left text-xs hover:border-brand-500"
                onClick={() => {
                  setUsername(d.username)
                  setPassword('123456')
                }}
              >
                <span className="font-semibold text-slate-700">{d.username}</span>
                <span className="block text-slate-500">{d.label}</span>
              </button>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
