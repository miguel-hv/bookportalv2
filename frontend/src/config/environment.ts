// Auto-detect API URL based on environment
export const getApiUrl = (): string => {
  // Check if we're in Docker (container networking)
  if (import.meta.env.VITE_API_URL && import.meta.env.VITE_API_URL.includes('backend')) {
    // Docker environment - use container networking
    return import.meta.env.VITE_API_URL;
  }
  
  // Default to localhost for all other cases (local development, browser access)
  return import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
};