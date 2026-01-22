<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import { useRouter } from 'vue-router'
import api from '../api'
import AdComponent from '../components/AdComponent.vue'

const authStore = useAuthStore()
const router = useRouter()

const conversations = ref<any[]>([])
const currentConversationId = ref<number | null>(null)
const messages = ref<any[]>([])
const newMessage = ref('')
const isLoading = ref(false)

const handleLogout = () => {
  authStore.logout()
}

const fetchConversations = async () => {
  try {
    const response = await api.get('/chat/history')
    conversations.value = response.data
  } catch (error) {
    console.error('Failed to fetch conversations', error)
  }
}

const selectConversation = async (id: number) => {
  currentConversationId.value = id
  try {
    const response = await api.get(`/chat/${id}/messages`)
    messages.value = response.data
  } catch (error) {
    console.error('Failed to fetch messages', error)
  }
}

const sendMessage = async () => {
  if (!newMessage.value.trim()) return
  
  const content = newMessage.value
  newMessage.value = ''
  isLoading.value = true

  // Optimistic update
  messages.value.push({ role: 'user', content })

  try {
    const payload: any = { message: content }
    if (currentConversationId.value) {
      payload.conversationId = currentConversationId.value.toString()
    }

    const response = await api.post('/chat/send', payload)
    
    // Update conversation ID if new
    if (!currentConversationId.value) {
      currentConversationId.value = response.data.conversationId
      await fetchConversations() // Refresh list
    }

    // Add assistant response
    messages.value.push({
      role: 'assistant',
      content: response.data.response,
      citation: response.data.citation
    })
  } catch (error) {
    console.error('Failed to send message', error)
    messages.value.push({ role: 'error', content: 'Failed to send message' })
  } finally {
    isLoading.value = false
  }
}

const newChat = () => {
  currentConversationId.value = null
  messages.value = []
}

onMounted(() => {
  if (authStore.isAuthenticated) {
    fetchConversations()
  }
})
</script>

<template>
  <div class="flex h-screen bg-gray-100">
    <!-- Sidebar -->
    <aside class="w-64 bg-white border-r border-gray-200 flex flex-col">
      <div class="p-4 border-b border-gray-200 flex items-center justify-between">
        <h1 class="text-xl font-bold text-blue-600">Solvex AI</h1>
      </div>
      
      <div class="flex-1 overflow-y-auto p-4">
        <button @click="newChat" class="w-full mb-4 bg-blue-600 text-white py-2 rounded hover:bg-blue-700 transition">
            + New Chat
        </button>
        <div class="text-sm text-gray-500 mb-2">History</div>
        <div class="space-y-2">
            <div 
                v-for="conv in conversations" 
                :key="conv.id"
                @click="selectConversation(conv.id)"
                class="p-2 rounded cursor-pointer hover:bg-gray-100 truncate"
                :class="{'bg-blue-50 text-blue-700': currentConversationId === conv.id}"
            >
                {{ conv.title || 'Untitled Chat' }}
            </div>
        </div>
        
        <div class="mt-auto pt-4">
            <AdComponent />
        </div>
      </div>

      <div class="p-4 border-t border-gray-200">
        <div v-if="authStore.isAuthenticated" class="flex items-center justify-between">
            <span class="text-sm font-medium text-gray-700 truncate">{{ authStore.user?.username || 'User' }}</span>
            <button @click="handleLogout" class="text-xs text-red-500 hover:text-red-700">Logout</button>
        </div>
        <div v-else>
            <router-link to="/login" class="block w-full text-center bg-blue-500 text-white py-2 rounded hover:bg-blue-600">Login</router-link>
        </div>
      </div>
    </aside>

    <!-- Main Chat Area -->
    <main class="flex-1 flex flex-col">
      <!-- Chat Header -->
      <header class="bg-white border-b border-gray-200 p-4 shadow-sm flex justify-between items-center">
        <h2 class="text-lg font-semibold text-gray-800">Assistant</h2>
        <!-- Ads Placeholder -->
        <div class="text-xs text-gray-400 border border-gray-200 px-2 py-1 rounded">
            Ad Space
        </div>
      </header>

      <!-- Messages Area -->
      <div class="flex-1 overflow-y-auto p-4 space-y-4">
        <div v-if="messages.length === 0" class="flex justify-center items-center h-full text-gray-400">
            Start a new conversation...
        </div>
        <div 
            v-for="(msg, index) in messages" 
            :key="index" 
            class="flex"
            :class="msg.role === 'user' ? 'justify-end' : 'justify-start'"
        >
            <div 
                class="rounded-lg p-3 max-w-3xl shadow-sm relative group"
                :class="msg.role === 'user' ? 'bg-blue-600 text-white' : 'bg-white border border-gray-200 text-gray-800'"
            >
                <p>{{ msg.content }}</p>
                
                <!-- Citation -->
                <div v-if="msg.citation" class="mt-2 pt-2 border-t border-gray-100 text-xs">
                    <span class="font-semibold text-blue-500 cursor-pointer hover:underline" title="Click to view source">
                        [Citation Source]
                    </span>
                    <div class="hidden group-hover:block absolute bottom-full left-0 bg-black text-white p-2 rounded text-xs w-64 z-10 mb-2">
                        {{ msg.citation }}
                    </div>
                </div>
            </div>
        </div>
        <div v-if="isLoading" class="flex justify-start">
             <div class="bg-white border border-gray-200 rounded-lg p-3 shadow-sm animate-pulse">
                Thinking...
            </div>
        </div>
      </div>

      <!-- Input Area -->
      <div class="p-4 bg-white border-t border-gray-200">
        <div class="flex space-x-4">
            <input 
                v-model="newMessage"
                @keyup.enter="sendMessage"
                type="text" 
                placeholder="Type your message..." 
                class="flex-1 border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                :disabled="isLoading"
            />
            <button 
                @click="sendMessage"
                :disabled="isLoading"
                class="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50"
            >
                Send
            </button>
        </div>
      </div>
    </main>
  </div>
</template>
