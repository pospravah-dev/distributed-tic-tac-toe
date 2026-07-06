import { useState, useCallback, useRef, useEffect } from 'react';

interface SSEState {
  isConnected: boolean;
  error: Error | null;
  lastEventId: string | null;
}

export function useSSE(
  url: string,
  onEvent: (event: any) => void
): SSEState {
  const [state, setState] = useState<SSEState>({
    isConnected: false,
    error: null,
    lastEventId: null,
  });

  const eventSourceRef = useRef<EventSource | null>(null);

  useEffect(() => {
    const eventSource = new EventSource(url);
    eventSourceRef.current = eventSource;

    eventSource.onopen = () => {
      setState((prev) => ({ ...prev, isConnected: true, error: null }));
    };

    eventSource.onmessage = (event) => {
      const parsedData = JSON.parse(event.data);
      onEvent(parsedData);
    };

    eventSource.onerror = (error) => {
      setState((prev) => ({
        ...prev,
        error: error instanceof Error ? error : new Error('SSE connection error'),
        isConnected: false,
      }));
      eventSource.close();
    };

    eventSource.onclose = () => {
      setState((prev) => ({ ...prev, isConnected: false }));
    };

    return () => {
      if (eventSourceRef.current) {
        eventSourceRef.current.close();
      }
    };
  }, [url, onEvent]);

  return state;
}
