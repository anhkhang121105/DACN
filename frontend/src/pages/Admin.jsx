import { useEffect, useState } from 'react'
import { api } from '../api.js'

const TABS = [
  { key: 'bo-mon', label: 'Bộ môn' },
  { key: 'mon-hoc', label: 'Môn học' },
  { key: 'giang-vien', label: 'Giảng viên' },
  { key: 'chuyen-mon', label: 'Chuyên môn' },
]

function EmptyRow({ cols, text = 'Chưa có dữ liệu.' }) {
  return (
    <tr>
      <td className="td text-slate-500" colSpan={cols}>
        {text}
      </td>
    </tr>
  )
}

function useData(loader, deps) {
  const [data, setData] = useState([])
  const [error, setError] = useState('')
  const [tick, setTick] = useState(0)

  useEffect(() => {
    let cancelled = false
    setError('')
    loader()
      .then((d) => !cancelled && setData(d || []))
      .catch((e) => !cancelled && setError(e.message))
    return () => {
      cancelled = true
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [...deps, tick])

  return { data, error, reload: () => setTick((t) => t + 1) }
}

function BoMonTab() {
  const [form, setForm] = useState({ ma: '', ten: '' })
  const [editing, setEditing] = useState(null)
  const { data, error, reload } = useData(() => api('/bo-mon'), [])

  async function save(e) {
    e.preventDefault()
    try {
      if (editing) await api(`/bo-mon/${editing}`, { method: 'PUT', body: form })
      else await api('/bo-mon', { method: 'POST', body: form })
      setForm({ ma: '', ten: '' })
      setEditing(null)
      reload()
    } catch (err) {
      alert(err.message)
    }
  }

  async function remove(id) {
    if (!confirm('Xóa bộ môn này?')) return
    try {
      await api(`/bo-mon/${id}`, { method: 'DELETE' })
      reload()
    } catch (err) {
      alert(err.message)
    }
  }

  return (
    <div className="grid gap-4 lg:grid-cols-3">
      <div className="card lg:col-span-2">
        <h3 className="mb-3 font-semibold">Danh sách bộ môn</h3>
        {error && <div className="mb-2 text-sm text-red-600">{error}</div>}
        <table className="min-w-full divide-y divide-slate-200">
          <thead>
            <tr>
              <th className="th">Mã</th>
              <th className="th">Tên</th>
              <th className="th"></th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {data.map((b) => (
              <tr key={b.id}>
                <td className="td font-medium">{b.ma}</td>
                <td className="td">{b.ten}</td>
                <td className="td text-right">
                  <button
                    className="mr-2 text-sm font-medium text-brand-600"
                    onClick={() => {
                      setEditing(b.id)
                      setForm({ ma: b.ma, ten: b.ten })
                    }}
                  >
                    Sửa
                  </button>
                  <button className="text-sm font-medium text-red-600" onClick={() => remove(b.id)}>
                    Xóa
                  </button>
                </td>
              </tr>
            ))}
            {data.length === 0 && <EmptyRow cols={3} />}
          </tbody>
        </table>
      </div>
      <form className="card space-y-3" onSubmit={save}>
        <h3 className="font-semibold">{editing ? 'Sửa bộ môn' : 'Thêm bộ môn'}</h3>
        <div>
          <label className="label">Mã</label>
          <input className="input" value={form.ma} onChange={(e) => setForm({ ...form, ma: e.target.value })} required />
        </div>
        <div>
          <label className="label">Tên</label>
          <input className="input" value={form.ten} onChange={(e) => setForm({ ...form, ten: e.target.value })} required />
        </div>
        <div className="flex gap-2">
          <button className="btn-primary" type="submit">
            Lưu
          </button>
          {editing && (
            <button
              type="button"
              className="btn-secondary"
              onClick={() => {
                setEditing(null)
                setForm({ ma: '', ten: '' })
              }}
            >
              Hủy
            </button>
          )}
        </div>
      </form>
    </div>
  )
}

const EMPTY_MON = { maHp: '', ten: '', soTc: 3, tietLt: 30, tietBt: 0, tietTh: 0, heSoHp: 1, boMonId: '' }

function MonHocTab() {
  const [boMonId, setBoMonId] = useState('')
  const [form, setForm] = useState(EMPTY_MON)
  const [editing, setEditing] = useState(null)
  const { data: boMons } = useData(() => api('/bo-mon'), [])
  const { data, error, reload } = useData(
    () => api('/mon-hoc', { params: { boMonId: boMonId || undefined } }),
    [boMonId],
  )

  useEffect(() => {
    if (!form.boMonId && boMons.length > 0) setForm((f) => ({ ...f, boMonId: boMons[0].id }))
  }, [boMons, form.boMonId])

  async function save(e) {
    e.preventDefault()
    const body = {
      maHp: form.maHp,
      ten: form.ten,
      soTc: Number(form.soTc),
      tietLt: Number(form.tietLt),
      tietBt: Number(form.tietBt),
      tietTh: Number(form.tietTh),
      heSoHp: Number(form.heSoHp),
      boMon: { id: Number(form.boMonId) },
    }
    try {
      if (editing) await api(`/mon-hoc/${editing}`, { method: 'PUT', body })
      else await api('/mon-hoc', { method: 'POST', body })
      setForm({ ...EMPTY_MON, boMonId: form.boMonId })
      setEditing(null)
      reload()
    } catch (err) {
      alert(err.message)
    }
  }

  async function remove(id) {
    if (!confirm('Xóa môn học này?')) return
    try {
      await api(`/mon-hoc/${id}`, { method: 'DELETE' })
      reload()
    } catch (err) {
      alert(err.message)
    }
  }

  return (
    <div className="grid gap-4 lg:grid-cols-3">
      <div className="card lg:col-span-2">
        <div className="mb-3 flex items-center justify-between">
          <h3 className="font-semibold">Danh sách môn học</h3>
          <select className="input w-56" value={boMonId} onChange={(e) => setBoMonId(e.target.value)}>
            <option value="">Tất cả bộ môn</option>
            {boMons.map((b) => (
              <option key={b.id} value={b.id}>
                {b.ten}
              </option>
            ))}
          </select>
        </div>
        {error && <div className="mb-2 text-sm text-red-600">{error}</div>}
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-slate-200">
            <thead>
              <tr>
                <th className="th">Mã HP</th>
                <th className="th">Tên</th>
                <th className="th">TC</th>
                <th className="th">LT/BT/TH</th>
                <th className="th">Hệ số</th>
                <th className="th"></th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {data.map((m) => (
                <tr key={m.id}>
                  <td className="td font-medium">{m.maHp}</td>
                  <td className="td">{m.ten}</td>
                  <td className="td">{m.soTc}</td>
                  <td className="td">
                    {m.tietLt}/{m.tietBt}/{m.tietTh}
                  </td>
                  <td className="td">{m.heSoHp}</td>
                  <td className="td text-right whitespace-nowrap">
                    <button
                      className="mr-2 text-sm font-medium text-brand-600"
                      onClick={() => {
                        setEditing(m.id)
                        setForm({
                          maHp: m.maHp,
                          ten: m.ten,
                          soTc: m.soTc,
                          tietLt: m.tietLt,
                          tietBt: m.tietBt,
                          tietTh: m.tietTh,
                          heSoHp: m.heSoHp,
                          boMonId: m.boMon?.id || '',
                        })
                      }}
                    >
                      Sửa
                    </button>
                    <button className="text-sm font-medium text-red-600" onClick={() => remove(m.id)}>
                      Xóa
                    </button>
                  </td>
                </tr>
              ))}
              {data.length === 0 && <EmptyRow cols={6} />}
            </tbody>
          </table>
        </div>
      </div>
      <form className="card space-y-3" onSubmit={save}>
        <h3 className="font-semibold">{editing ? 'Sửa môn học' : 'Thêm môn học'}</h3>
        <div>
          <label className="label">Mã học phần</label>
          <input className="input" value={form.maHp} onChange={(e) => setForm({ ...form, maHp: e.target.value })} required />
        </div>
        <div>
          <label className="label">Tên môn</label>
          <input className="input" value={form.ten} onChange={(e) => setForm({ ...form, ten: e.target.value })} required />
        </div>
        <div>
          <label className="label">Bộ môn</label>
          <select className="input" value={form.boMonId} onChange={(e) => setForm({ ...form, boMonId: e.target.value })}>
            {boMons.map((b) => (
              <option key={b.id} value={b.id}>
                {b.ten}
              </option>
            ))}
          </select>
        </div>
        <div className="grid grid-cols-2 gap-2">
          {[
            ['soTc', 'Số TC'],
            ['heSoHp', 'Hệ số HP'],
            ['tietLt', 'Tiết LT'],
            ['tietBt', 'Tiết BT'],
            ['tietTh', 'Tiết TH'],
          ].map(([key, label]) => (
            <div key={key}>
              <label className="label">{label}</label>
              <input
                className="input"
                type="number"
                step="0.01"
                value={form[key]}
                onChange={(e) => setForm({ ...form, [key]: e.target.value })}
              />
            </div>
          ))}
        </div>
        <div className="flex gap-2">
          <button className="btn-primary" type="submit">
            Lưu
          </button>
          {editing && (
            <button
              type="button"
              className="btn-secondary"
              onClick={() => {
                setEditing(null)
                setForm({ ...EMPTY_MON, boMonId: form.boMonId })
              }}
            >
              Hủy
            </button>
          )}
        </div>
      </form>
    </div>
  )
}

const EMPTY_GV = {
  maCb: '',
  hoTen: '',
  hocVi: 'ThS',
  loai: 'CO_HUU',
  boMonId: '',
  dinhMucMin: 200,
  dinhMucMax: 350,
  donViCongTac: '',
  chuyenNganh: '',
  laTapSu: false,
}

function GiangVienTab() {
  const [boMonId, setBoMonId] = useState('')
  const [form, setForm] = useState(EMPTY_GV)
  const [editing, setEditing] = useState(null)
  const { data: boMons } = useData(() => api('/bo-mon'), [])
  const { data, error, reload } = useData(
    () => api('/giang-vien', { params: { boMonId: boMonId || undefined } }),
    [boMonId],
  )

  useEffect(() => {
    if (!form.boMonId && boMons.length > 0) setForm((f) => ({ ...f, boMonId: boMons[0].id }))
  }, [boMons, form.boMonId])

  async function save(e) {
    e.preventDefault()
    const body = {
      maCb: form.maCb,
      hoTen: form.hoTen,
      hocVi: form.hocVi,
      loai: form.loai,
      boMon: form.boMonId ? { id: Number(form.boMonId) } : null,
      dinhMucMin: Number(form.dinhMucMin),
      dinhMucMax: Number(form.dinhMucMax),
      donViCongTac: form.donViCongTac,
      chuyenNganh: form.chuyenNganh,
      laTapSu: Boolean(form.laTapSu),
    }
    try {
      if (editing) await api(`/giang-vien/${editing}`, { method: 'PUT', body })
      else await api('/giang-vien', { method: 'POST', body })
      setForm({ ...EMPTY_GV, boMonId: form.boMonId })
      setEditing(null)
      reload()
    } catch (err) {
      alert(err.message)
    }
  }

  async function remove(id) {
    if (!confirm('Xóa giảng viên này?')) return
    try {
      await api(`/giang-vien/${id}`, { method: 'DELETE' })
      reload()
    } catch (err) {
      alert(err.message)
    }
  }

  return (
    <div className="grid gap-4 lg:grid-cols-3">
      <div className="card lg:col-span-2">
        <div className="mb-3 flex items-center justify-between">
          <h3 className="font-semibold">Danh sách giảng viên</h3>
          <select className="input w-56" value={boMonId} onChange={(e) => setBoMonId(e.target.value)}>
            <option value="">Tất cả bộ môn</option>
            {boMons.map((b) => (
              <option key={b.id} value={b.id}>
                {b.ten}
              </option>
            ))}
          </select>
        </div>
        {error && <div className="mb-2 text-sm text-red-600">{error}</div>}
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-slate-200">
            <thead>
              <tr>
                <th className="th">Mã CB</th>
                <th className="th">Họ tên</th>
                <th className="th">Học vị</th>
                <th className="th">Loại</th>
                <th className="th">Bộ môn</th>
                <th className="th">Định mức</th>
                <th className="th"></th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {data.map((g) => (
                <tr key={g.id}>
                  <td className="td font-medium">{g.maCb}</td>
                  <td className="td">{g.hoTen}</td>
                  <td className="td">{g.hocVi}</td>
                  <td className="td">{g.loai}</td>
                  <td className="td">{g.boMon?.ten}</td>
                  <td className="td">
                    {g.dinhMucMin} - {g.dinhMucMax}
                  </td>
                  <td className="td text-right whitespace-nowrap">
                    <button
                      className="mr-2 text-sm font-medium text-brand-600"
                      onClick={() => {
                        setEditing(g.id)
                        setForm({
                          maCb: g.maCb,
                          hoTen: g.hoTen,
                          hocVi: g.hocVi || '',
                          loai: g.loai,
                          boMonId: g.boMon?.id || '',
                          dinhMucMin: g.dinhMucMin,
                          dinhMucMax: g.dinhMucMax,
                          donViCongTac: g.donViCongTac || '',
                          chuyenNganh: g.chuyenNganh || '',
                          laTapSu: Boolean(g.laTapSu),
                        })
                      }}
                    >
                      Sửa
                    </button>
                    <button className="text-sm font-medium text-red-600" onClick={() => remove(g.id)}>
                      Xóa
                    </button>
                  </td>
                </tr>
              ))}
              {data.length === 0 && <EmptyRow cols={7} />}
            </tbody>
          </table>
        </div>
      </div>
      <form className="card space-y-3" onSubmit={save}>
        <h3 className="font-semibold">{editing ? 'Sửa giảng viên' : 'Thêm giảng viên'}</h3>
        <div className="grid grid-cols-2 gap-2">
          <div>
            <label className="label">Mã CB</label>
            <input className="input" value={form.maCb} onChange={(e) => setForm({ ...form, maCb: e.target.value })} required />
          </div>
          <div>
            <label className="label">Học vị</label>
            <input className="input" value={form.hocVi} onChange={(e) => setForm({ ...form, hocVi: e.target.value })} />
          </div>
        </div>
        <div>
          <label className="label">Họ tên</label>
          <input className="input" value={form.hoTen} onChange={(e) => setForm({ ...form, hoTen: e.target.value })} required />
        </div>
        <div className="grid grid-cols-2 gap-2">
          <div>
            <label className="label">Loại</label>
            <select className="input" value={form.loai} onChange={(e) => setForm({ ...form, loai: e.target.value })}>
              <option value="CO_HUU">Cơ hữu</option>
              <option value="MOI_GIANG">Mời giảng</option>
              <option value="THINH_GIANG">Thỉnh giảng</option>
            </select>
          </div>
          <div>
            <label className="label">Bộ môn</label>
            <select className="input" value={form.boMonId} onChange={(e) => setForm({ ...form, boMonId: e.target.value })}>
              {boMons.map((b) => (
                <option key={b.id} value={b.id}>
                  {b.ten}
                </option>
              ))}
            </select>
          </div>
        </div>
        <div className="grid grid-cols-2 gap-2">
          <div>
            <label className="label">Định mức min</label>
            <input
              className="input"
              type="number"
              value={form.dinhMucMin}
              onChange={(e) => setForm({ ...form, dinhMucMin: e.target.value })}
            />
          </div>
          <div>
            <label className="label">Định mức max</label>
            <input
              className="input"
              type="number"
              value={form.dinhMucMax}
              onChange={(e) => setForm({ ...form, dinhMucMax: e.target.value })}
            />
          </div>
        </div>
        <div>
          <label className="label">Đơn vị công tác</label>
          <input
            className="input"
            value={form.donViCongTac}
            onChange={(e) => setForm({ ...form, donViCongTac: e.target.value })}
          />
        </div>
        <div>
          <label className="label">Chuyên ngành</label>
          <input
            className="input"
            value={form.chuyenNganh}
            onChange={(e) => setForm({ ...form, chuyenNganh: e.target.value })}
          />
        </div>
        <label className="flex items-center gap-2 text-sm text-slate-700">
          <input
            type="checkbox"
            checked={form.laTapSu}
            onChange={(e) => setForm({ ...form, laTapSu: e.target.checked })}
          />
          Là tập sự
        </label>
        <div className="flex gap-2">
          <button className="btn-primary" type="submit">
            Lưu
          </button>
          {editing && (
            <button
              type="button"
              className="btn-secondary"
              onClick={() => {
                setEditing(null)
                setForm({ ...EMPTY_GV, boMonId: form.boMonId })
              }}
            >
              Hủy
            </button>
          )}
        </div>
      </form>
    </div>
  )
}

function ChuyenMonTab() {
  const { data: giangViens } = useData(() => api('/giang-vien'), [])
  const { data: monHocs } = useData(() => api('/mon-hoc'), [])
  const [giangVienId, setGiangVienId] = useState('')
  const [monHocId, setMonHocId] = useState('')
  const [lt, setLt] = useState(true)
  const [th, setTh] = useState(true)
  const { data, error, reload } = useData(
    () => (giangVienId ? api('/giang-vien-mon-hoc', { params: { giangVienId } }) : Promise.resolve([])),
    [giangVienId],
  )

  async function add(e) {
    e.preventDefault()
    if (!giangVienId || !monHocId) return
    try {
      await api('/giang-vien-mon-hoc', {
        method: 'POST',
        body: {
          giangVienId: Number(giangVienId),
          monHocId: Number(monHocId),
          coTheDayLt: lt,
          coTheDayTh: th,
        },
      })
      reload()
    } catch (err) {
      alert(err.message)
    }
  }

  async function remove(mid) {
    try {
      await api(`/giang-vien-mon-hoc/${giangVienId}/${mid}`, { method: 'DELETE' })
      reload()
    } catch (err) {
      alert(err.message)
    }
  }

  const tenMon = (id) => monHocs.find((m) => m.id === id)?.ten || `#${id}`

  return (
    <div className="grid gap-4 lg:grid-cols-3">
      <div className="card lg:col-span-2">
        <div className="mb-3 flex items-center justify-between">
          <h3 className="font-semibold">Chuyên môn giảng viên</h3>
          <select className="input w-64" value={giangVienId} onChange={(e) => setGiangVienId(e.target.value)}>
            <option value="">Chọn giảng viên</option>
            {giangViens.map((g) => (
              <option key={g.id} value={g.id}>
                {g.maCb} - {g.hoTen}
              </option>
            ))}
          </select>
        </div>
        {error && <div className="mb-2 text-sm text-red-600">{error}</div>}
        <table className="min-w-full divide-y divide-slate-200">
          <thead>
            <tr>
              <th className="th">Môn học</th>
              <th className="th">Dạy LT</th>
              <th className="th">Dạy TH</th>
              <th className="th"></th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {data.map((r) => (
              <tr key={`${r.giangVienId}-${r.monHocId}`}>
                <td className="td">{tenMon(r.monHocId)}</td>
                <td className="td">{r.coTheDayLt ? 'Có' : 'Không'}</td>
                <td className="td">{r.coTheDayTh ? 'Có' : 'Không'}</td>
                <td className="td text-right">
                  <button className="text-sm font-medium text-red-600" onClick={() => remove(r.monHocId)}>
                    Xóa
                  </button>
                </td>
              </tr>
            ))}
            {data.length === 0 && <EmptyRow cols={4} text="Chọn giảng viên để xem chuyên môn." />}
          </tbody>
        </table>
      </div>
      <form className="card space-y-3" onSubmit={add}>
        <h3 className="font-semibold">Thêm chuyên môn</h3>
        <div>
          <label className="label">Giảng viên</label>
          <select className="input" value={giangVienId} onChange={(e) => setGiangVienId(e.target.value)} required>
            <option value="">Chọn giảng viên</option>
            {giangViens.map((g) => (
              <option key={g.id} value={g.id}>
                {g.maCb} - {g.hoTen}
              </option>
            ))}
          </select>
        </div>
        <div>
          <label className="label">Môn học</label>
          <select className="input" value={monHocId} onChange={(e) => setMonHocId(e.target.value)} required>
            <option value="">Chọn môn học</option>
            {monHocs.map((m) => (
              <option key={m.id} value={m.id}>
                {m.maHp} - {m.ten}
              </option>
            ))}
          </select>
        </div>
        <div className="flex gap-4 text-sm">
          <label className="flex items-center gap-2">
            <input type="checkbox" checked={lt} onChange={(e) => setLt(e.target.checked)} /> Dạy LT
          </label>
          <label className="flex items-center gap-2">
            <input type="checkbox" checked={th} onChange={(e) => setTh(e.target.checked)} /> Dạy TH
          </label>
        </div>
        <button className="btn-primary" type="submit">
          Thêm
        </button>
      </form>
    </div>
  )
}

export default function Admin() {
  const [tab, setTab] = useState('bo-mon')
  return (
    <div className="space-y-4">
      <h1 className="text-xl font-bold text-slate-900">Quản lý danh mục</h1>
      <div className="flex flex-wrap gap-2">
        {TABS.map((t) => (
          <button
            key={t.key}
            className={tab === t.key ? 'btn-primary' : 'btn-secondary'}
            onClick={() => setTab(t.key)}
          >
            {t.label}
          </button>
        ))}
      </div>
      {tab === 'bo-mon' && <BoMonTab />}
      {tab === 'mon-hoc' && <MonHocTab />}
      {tab === 'giang-vien' && <GiangVienTab />}
      {tab === 'chuyen-mon' && <ChuyenMonTab />}
    </div>
  )
}
