import axios, { AxiosError, AxiosInstance, InternalAxiosRequestConfig } from 'axios';
import type { ApiError } from '@/types';

// Retrieve base URL strictly from environment variables
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

/**
 * Single configured Axios client for all backend REST communication.
 * Note: ZERO mock fallbacks are used. Any communication failure is surfaced directly.
 */
export const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
  timeout: 15000,
});

// Request Interceptor: Attach authentication token if available
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('auth_token');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

// Response Interceptor: Format errors and unwrap ApiResponse envelopes consistently
apiClient.interceptors.response.use(
  (response) => {
    // Unwrap Spring Boot ApiResponse<T> wrapper ({ success: true, message: "...", data: T })
    if (
      response.data &&
      typeof response.data === 'object' &&
      'data' in response.data &&
      ('success' in response.data || 'message' in response.data)
    ) {
      return { ...response, data: response.data.data };
    }
    return response;
  },
  (error: AxiosError<{ message?: string; error?: string; code?: string; details?: Record<string, string[]> }>) => {
    const formattedError: ApiError = {
      message:
        error.response?.data?.message ||
        error.response?.data?.error ||
        error.message ||
        'An unexpected network error occurred. Please check your connection to the server.',
      statusCode: error.response?.status,
      code: error.response?.data?.code || error.code,
      details: error.response?.data?.details,
      timestamp: new Date().toISOString(),
    };

    // Handle 401 Unauthorized globally if token expired
    if (error.response?.status === 401) {
      localStorage.removeItem('auth_token');
    }

    return Promise.reject(formattedError);
  }
);

export default apiClient;
