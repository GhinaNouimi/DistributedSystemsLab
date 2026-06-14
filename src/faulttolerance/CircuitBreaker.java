package faulttolerance;

public class CircuitBreaker {

    private final int failureThreshold;
    private final long openDurationMillis;

    private int failureCount;
    private long openedAt;
    private CircuitBreakerState state;

    public CircuitBreaker(
            int failureThreshold,
            long openDurationMillis
    ) {
        this.failureThreshold = failureThreshold;
        this.openDurationMillis = openDurationMillis;
        this.failureCount = 0;
        this.openedAt = 0;
        this.state = CircuitBreakerState.CLOSED;
    }

    public synchronized boolean allowRequest() {

        if (state == CircuitBreakerState.CLOSED) {
            return true;
        }

        if (state == CircuitBreakerState.OPEN) {
            long currentTime =
                    System.currentTimeMillis();

            if (currentTime - openedAt >= openDurationMillis) {
                state = CircuitBreakerState.HALF_OPEN;
                return true;
            }

            return false;
        }

        return state == CircuitBreakerState.HALF_OPEN;
    }

    public synchronized void recordSuccess() {
        failureCount = 0;
        state = CircuitBreakerState.CLOSED;
    }

    public synchronized void recordFailure() {

        failureCount++;

        if (state == CircuitBreakerState.HALF_OPEN ||
                failureCount >= failureThreshold) {

            state = CircuitBreakerState.OPEN;
            openedAt = System.currentTimeMillis();
        }
    }

    public synchronized CircuitBreakerState getState() {
        return state;
    }

    public synchronized int getFailureCount() {
        return failureCount;
    }
}