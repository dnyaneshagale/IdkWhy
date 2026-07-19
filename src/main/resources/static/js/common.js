const API_BASE = '/api/rooms';
const ROOM_PASSWORD_PREFIX = 'idkwhy-room-password:';

function normalizeRoomNumber(value) {
    return String(value ?? '').trim().toUpperCase();
}

function roomPasswordKey(roomNumber) {
    return `${ROOM_PASSWORD_PREFIX}${normalizeRoomNumber(roomNumber)}`;
}

function setRoomPassword(roomNumber, password) {
    const key = roomPasswordKey(roomNumber);
    const normalizedPassword = String(password ?? '').trim();
    if (normalizedPassword) {
        sessionStorage.setItem(key, normalizedPassword);
    } else {
        sessionStorage.removeItem(key);
    }
}

function getRoomPassword(roomNumber) {
    return sessionStorage.getItem(roomPasswordKey(roomNumber));
}

function clearRoomPassword(roomNumber) {
    sessionStorage.removeItem(roomPasswordKey(roomNumber));
}

function ensureToastHost() {
    let host = document.querySelector('.toast-host');
    if (!host) {
        host = document.createElement('div');
        host.className = 'toast-host';
        document.body.appendChild(host);
    }
    return host;
}

function showToast(message, type = 'info', title = '') {
    const host = ensureToastHost();
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `${title ? `<span class="toast-title">${title}</span>` : ''}<div>${message}</div>`;
    host.appendChild(toast);
    window.setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(8px) scale(0.98)';
        window.setTimeout(() => toast.remove(), 180);
    }, 2600);
}

async function apiFetch(path, options = {}) {
    const requestHeaders = {
        ...(options.headers ?? {})
    };

    // Preserve JSON payload semantics even when request-specific headers are supplied.
    if (options.body && !requestHeaders['Content-Type'] && !requestHeaders['content-type']) {
        requestHeaders['Content-Type'] = 'application/json';
    }

    const response = await fetch(path, {
        ...options,
        headers: requestHeaders
    });

    if (response.status === 204) {
        return null;
    }

    const contentType = response.headers.get('content-type') ?? '';
    const payload = contentType.includes('application/json') ? await response.json() : await response.text();

    if (!response.ok) {
        const errorMessage = payload?.message || payload?.error || 'Request failed.';
        const error = new Error(errorMessage);
        error.status = response.status;
        error.payload = payload;
        if (response.status === 500) {
            error.serverError = true;
        }
        throw error;
    }

    return payload;
}

function formatTimestamp(value) {
    if (!value) {
        return 'Just now';
    }
    return new Intl.DateTimeFormat(undefined, {
        dateStyle: 'medium',
        timeStyle: 'short'
    }).format(new Date(value));
}

async function copyToClipboard(text) {
    await navigator.clipboard.writeText(text);
}
