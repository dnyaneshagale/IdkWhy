document.addEventListener('DOMContentLoaded', () => {
    const createForm = document.getElementById('create-form');
    const joinForm = document.getElementById('join-form');
    const createRoomInput = document.getElementById('create-room-number');
    const joinRoomInput = document.getElementById('join-room-number');

    createRoomInput?.focus();

    createForm?.addEventListener('submit', async (event) => {
        event.preventDefault();
        const roomNumber = normalizeRoomNumber(createRoomInput.value);
        const password = document.getElementById('create-password').value;

        try {
            const response = await apiFetch(API_BASE, {
                method: 'POST',
                body: JSON.stringify({ roomNumber, password })
            });
            setRoomPassword(response.roomNumber, password);
            showToast(`Room ${response.roomNumber} created.`, 'success', 'Created');
            window.location.href = `room.html?room=${encodeURIComponent(response.roomNumber)}`;
        } catch (error) {
            const message = error?.status === 500 || error?.serverError
                ? (error?.payload?.message || 'The server failed to complete the request.')
                : error.message;
            showToast(message, 'error', 'Create failed');
        }
    });

    joinForm?.addEventListener('submit', async (event) => {
        event.preventDefault();
        const roomNumber = normalizeRoomNumber(joinRoomInput.value);
        const password = document.getElementById('join-password').value;

        try {
            const response = await apiFetch(`${API_BASE}/join`, {
                method: 'POST',
                body: JSON.stringify({ roomNumber, password })
            });
            setRoomPassword(response.roomNumber, password);
            showToast(`Joined room ${response.roomNumber}.`, 'success', 'Welcome');
            window.location.href = `room.html?room=${encodeURIComponent(response.roomNumber)}`;
        } catch (error) {
            const message = error?.status === 500 || error?.serverError
                ? (error?.payload?.message || 'The server failed to complete the request.')
                : error.message;
            showToast(message, 'error', 'Join failed');
        }
    });

    [createRoomInput, joinRoomInput].forEach((input) => {
        input?.addEventListener('input', () => {
            input.value = input.value.toUpperCase().replace(/[^A-Z0-9]/g, '').slice(0, 12);
        });
    });
});
