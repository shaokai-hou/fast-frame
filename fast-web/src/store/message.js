import { defineStore } from "pinia";
import { ref } from "vue";
import { ElMessage } from "element-plus";
import { getUnreadCount } from "@/api/message/message";
import { useUserStore } from "./user";
import router from "@/router";

export const useMessageStore = defineStore("message", () => {
  // 未读消息数量
  const unreadCount = ref(0);

  // WebSocket 连接
  let ws = null;
  let reconnectTimer = null;
  let reconnectCount = 0;
  const MAX_RECONNECT = 3;

  // 获取未读消息数量（初始化时调用）
  async function fetchUnreadCount() {
    try {
      const res = await getUnreadCount();
      unreadCount.value = Number(res.data.total) || 0;
    } catch (e) {
      console.error("获取未读消息数量失败", e);
    }
  }

  // WebSocket 更新未读数量
  function updateUnreadCount(count) {
    unreadCount.value = Number(count) || 0;
  }

  // 连接 WebSocket
  function connectWebSocket() {
    const userStore = useUserStore();
    const token = userStore.token;
    if (!token) return;

    const wsUrl = `${location.protocol === "https:" ? "wss:" : "ws:"}//${location.host}/ws/message?token=${token}`;

    ws = new WebSocket(wsUrl);

    ws.onopen = () => {
      console.log("WebSocket 连接成功");
      reconnectCount = 0;
      if (reconnectTimer) {
        clearInterval(reconnectTimer);
        reconnectTimer = null;
      }
    };

    ws.onmessage = (event) => {
      try {
        const message = JSON.parse(event.data);
        if (message.type === "UNREAD_COUNT_UPDATE") {
          // 后端推送新的未读数量
          updateUnreadCount(message.data.unreadCount);
        } else if (message.type === "NEW_MESSAGE") {
          // 收到新消息，显示通知
          ElMessage.info(`收到新消息: ${message.data.title}`);
        }
      } catch (e) {
        console.error("解析 WebSocket 消息失败", e);
      }
    };

    ws.onerror = (error) => {
      console.error("WebSocket 错误", error);
    };

    ws.onclose = (event) => {
      console.log("WebSocket 连接关闭, code=", event.code);
      ws = null;

      // 1008 表示 policy violation（通常是 token 无效）
      if (event.code === 1008) {
        console.warn("Token 无效，跳转登录页");
        userStore.resetToken();
        router.push("/login");
        return;
      }

      // 断线重连（最多 3 次）
      if (reconnectCount < MAX_RECONNECT && !reconnectTimer) {
        reconnectCount++;
        reconnectTimer = setInterval(() => {
          if (useUserStore().token && reconnectCount < MAX_RECONNECT) {
            connectWebSocket();
          } else {
            clearInterval(reconnectTimer);
            reconnectTimer = null;
          }
        }, 5000);
      }
    };
  }

  // 断开 WebSocket
  function disconnectWebSocket() {
    if (ws) {
      ws.close();
      ws = null;
    }
    if (reconnectTimer) {
      clearInterval(reconnectTimer);
      reconnectTimer = null;
    }
    reconnectCount = 0;
  }

  return {
    unreadCount,
    fetchUnreadCount,
    updateUnreadCount,
    connectWebSocket,
    disconnectWebSocket,
  };
});