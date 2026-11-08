const API_BASE = '/api'

function getToken() {
  try {
    const raw = localStorage.getItem('pc_auth')
    return raw ? JSON.parse(raw).accessToken : null
  } catch {
    return null
  }
}

export class ApiError extends Error {
  constructor(message, status, details) {
    super(message)
    this.status = status
    this.details = details
  }
}

export async function api(path, { method = 'GET', body, params, raw = false } = {}) {
  let url = `${API_BASE}${path}`
  if (params) {
    const search = new URLSearchParams()
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') search.append(key, value)
    })
    const qs = search.toString()
    if (qs) url += `?${qs}`
  }

  const headers = {}
  const token = getToken()
  if (token) headers.Authorization = `Bearer ${token}`
  if (body !== undefined) headers['Content-Type'] = 'application/json'

  const response = await fetch(url, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  })

  if (response.status === 401) {
    localStorage.removeItem('pc_auth')
    if (!location.pathname.startsWith('/login')) location.href = '/login'
    throw new ApiError('Phiên đăng nhập đã hết hạn', 401)
  }

  if (!response.ok) {
    let message = `Lỗi ${response.status}`
    let details
    try {
      const data = await response.json()
      message = data.message || data.error || message
      details = data
    } catch {
      /* ignore */
    }
    throw new ApiError(message, response.status, details)
  }

  if (raw) return response
  if (response.status === 204) return null
  const text = await response.text()
  return text ? JSON.parse(text) : null
}

export async function download(path, filename, params) {
  const response = await api(path, { raw: true, params })
  const blob = await response.blob()
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  document.body.appendChild(a)
  a.click()
  a.remove()
  URL.revokeObjectURL(url)
}
