import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '../api'
import { useRouter } from 'vue-router'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const user = ref<any>(null)
  const router = useRouter()

  const isAuthenticated = computed(() => !!token.value)

  const login = async (credentials: any) => {
    try {
      const response = await api.post('/auth/login', credentials)
      token.value = response.data.token
      localStorage.setItem('token', token.value!)
      // In a real app, you might want to fetch user details here
      user.value = { username: credentials.username } 
      return true
    } catch (error) {
      console.error('Login failed', error)
      return false
    }
  }

  const register = async (userData: any) => {
    try {
      await api.post('/auth/register', userData)
      return true
    } catch (error) {
      console.error('Registration failed', error)
      return false
    }
  }

  const logout = () => {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
    router.push('/login')
  }

  return {
    token,
    user,
    isAuthenticated,
    login,
    register,
    logout
  }
})
