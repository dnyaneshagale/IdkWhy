document.addEventListener('DOMContentLoaded', () => {
    const params = new URLSearchParams(window.location.search);
    const roomNumber = normalizeRoomNumber(params.get('room'));
    const roomTitle = document.getElementById('room-number');
    const roomNumberCopy = document.getElementById('room-number-copy');
    const passwordBadge = document.getElementById('room-password-badge');
    const roomStatus = document.getElementById('room-status');
    const sidebarRoom = document.getElementById('sidebar-room');
    const sidebarCount = document.getElementById('sidebar-count');
    const sidebarActivity = document.getElementById('sidebar-activity');
    const messageList = document.getElementById('message-list');
    const emptyState = document.getElementById('empty-state');
    const composeForm = document.getElementById('compose-form');
    const messageInput = document.getElementById('message-text');
    const counter = document.getElementById('char-counter');
    const copyRoomButton = document.getElementById('copy-room-number');
    const clearMessagesButton = document.getElementById('clear-messages');
    const deleteRoomButton = document.getElementById('delete-room');
    const leaveRoomButton = document.getElementById('leave-room');
    const modal = document.getElementById('password-modal');
    const modalForm = document.getElementById('password-form');
    const modalInput = document.getElementById('room-password-input');
    const modalError = document.getElementById('password-error');
    const refreshHint = document.getElementById('refresh-hint');

    let roomState = null;
    let refreshTimer = null;
    let loading = false;
    let accessPassword = roomNumber ? getRoomPassword(roomNumber) : null;

    if (!roomNumber) {
        showToast('Room number is missing from the URL.', 'error', 'Invalid link');
        leaveRoom();
        return;
    }

    roomTitle.textContent = roomNumber;
    refreshHint.textContent = 'Refreshing every 2 seconds';
    messageInput.maxLength = 10000;

    function updateCounter() {
        counter.textContent = `${messageInput.value.length}/10000`;
    }

    function openModal(message = '') {
        modal.classList.add('open');
        modalError.textContent = message;
        modalInput.value = accessPassword ?? '';
        window.setTimeout(() => modalInput.focus(), 0);
    }

    function closeModal() {
        modal.classList.remove('open');
        modalError.textContent = '';
    }

    function leaveRoom() {
        if (refreshTimer) {
            window.clearInterval(refreshTimer);
        }
        window.location.href = '/';
    }

    function renderRoom(room) {
        roomState = room;
        roomNumberCopy.textContent = room.roomNumber;
        sidebarRoom.textContent = room.roomNumber;
        sidebarCount.textContent = String(room.messageCount ?? room.messages.length);
        sidebarActivity.textContent = formatTimestamp(room.lastActivity);
        roomStatus.textContent = room.passwordProtected ? 'Protected room loaded' : 'Public room loaded';
        passwordBadge.textContent = room.passwordProtected ? 'Password protected: Yes' : 'Password protected: No';
        passwordBadge.className = room.passwordProtected ? 'tag tag-warn' : 'tag tag-success';

        const messages = room.messages ?? [];
        messageList.innerHTML = '';

        if (!messages.length) {
            emptyState.classList.remove('hidden');
        } else {
            emptyState.classList.add('hidden');
            messages.forEach((message) => {
                const card = document.createElement('article');
                card.className = 'message-card fade-in';

                const text = document.createElement('p');
                text.className = 'message-text';
                text.textContent = message.text;

                const footer = document.createElement('div');
                footer.className = 'message-footer';

                const time = document.createElement('div');
                time.className = 'message-time';
                time.textContent = `Copied ${formatTimestamp(message.createdAt)}`;

                const actions = document.createElement('div');
                actions.className = 'message-actions';

                const copyButton = document.createElement('button');
                copyButton.type = 'button';
                copyButton.className = 'btn btn-secondary';
                copyButton.textContent = 'Copy';
                copyButton.addEventListener('click', async () => {
                    try {
                        await copyToClipboard(message.text);
                        showToast('Copied!', 'success', 'Clipboard');
                    } catch {
                        showToast('Clipboard access was denied.', 'error', 'Copy failed');
                    }
                });

                const deleteButton = document.createElement('button');
                deleteButton.type = 'button';
                deleteButton.className = 'btn btn-danger';
                deleteButton.textContent = 'Delete';
                deleteButton.addEventListener('click', async () => {
                    if (!window.confirm('Delete this message?')) {
                        return;
                    }
                    try {
                        await apiFetch(`${API_BASE}/${encodeURIComponent(roomNumber)}/messages/${encodeURIComponent(message.id)}`, {
                            method: 'DELETE',
                            headers: accessPassword ? { 'X-Room-Password': accessPassword } : {}
                        });
                        showToast('Message deleted.', 'success', 'Removed');
                        await loadRoom();
                    } catch (error) {
                        handleApiError(error, 'Delete failed');
                    }
                });

                actions.append(copyButton, deleteButton);
                footer.append(time, actions);
                card.append(text, footer);
                messageList.appendChild(card);
            });
        }

        roomTitle.textContent = room.roomNumber;
        copyRoomButton.disabled = false;
        clearMessagesButton.disabled = false;
        deleteRoomButton.disabled = false;
    }

    function handleApiError(error, title = 'Error') {
        if (error?.status === 403) {
            accessPassword = null;
            setRoomPassword(roomNumber, '');
            openModal('Password required or incorrect.');
            showToast('Password required or incorrect.', 'error', 'Access denied');
            return;
        }
        if (error?.status === 404) {
            showToast('Room not found or expired.', 'error', title);
            leaveRoom();
            return;
        }
        if (error?.status === 500 || error?.serverError) {
            const serverMessage = error?.payload?.message || 'The server failed to complete the request.';
            showToast(serverMessage, 'error', 'Server error');
            return;
        }
        showToast(error?.message ?? 'Unexpected error.', 'error', title);
    }

    async function loadRoom() {
        if (loading) {
            return;
        }
        loading = true;
        try {
            const headers = accessPassword ? { 'X-Room-Password': accessPassword } : {};
            const room = await apiFetch(`${API_BASE}/${encodeURIComponent(roomNumber)}`, { headers });
            renderRoom(room);
            closeModal();
        } catch (error) {
            roomStatus.textContent = 'Waiting for access';
            handleApiError(error, 'Load failed');
        } finally {
            loading = false;
        }
    }

    composeForm?.addEventListener('submit', async (event) => {
        event.preventDefault();
        const text = messageInput.value;

        if (!text.trim()) {
            showToast('Message cannot be blank.', 'error', 'Validation');
            messageInput.focus();
            return;
        }

        try {
            const room = await apiFetch(`${API_BASE}/${encodeURIComponent(roomNumber)}/messages`, {
                method: 'POST',
                headers: accessPassword ? { 'X-Room-Password': accessPassword } : {},
                body: JSON.stringify({ text })
            });
            messageInput.value = '';
            updateCounter();
            renderRoom(room);
            showToast('Message sent.', 'success', 'Sent');
            messageInput.focus();
        } catch (error) {
            handleApiError(error, 'Send failed');
        }
    });

    messageInput?.addEventListener('input', updateCounter);
    messageInput?.addEventListener('keydown', (event) => {
        if ((event.ctrlKey || event.metaKey) && event.key === 'Enter') {
            event.preventDefault();
            composeForm.requestSubmit();
        }
    });

    if (window.matchMedia('(max-width: 720px)').matches) {
        copyRoomButton.classList.remove('btn-secondary');
        copyRoomButton.classList.add('btn-primary');
    }

    copyRoomButton?.addEventListener('click', async () => {
        try {
            await copyToClipboard(roomNumber);
            showToast('Room number copied.', 'success', 'Copied');
        } catch {
            showToast('Clipboard access was denied.', 'error', 'Copy failed');
        }
    });

    clearMessagesButton?.addEventListener('click', async () => {
        if (!window.confirm('Clear all messages in this room?')) {
            return;
        }
        try {
            const room = await apiFetch(`${API_BASE}/${encodeURIComponent(roomNumber)}/messages`, {
                method: 'DELETE',
                headers: accessPassword ? { 'X-Room-Password': accessPassword } : {}
            });
            renderRoom(room);
            showToast('All messages cleared.', 'success', 'Done');
        } catch (error) {
            handleApiError(error, 'Clear failed');
        }
    });

    deleteRoomButton?.addEventListener('click', async () => {
        if (!window.confirm('Delete this room permanently?')) {
            return;
        }
        try {
            await apiFetch(`${API_BASE}/${encodeURIComponent(roomNumber)}`, {
                method: 'DELETE',
                headers: accessPassword ? { 'X-Room-Password': accessPassword } : {}
            });
            clearRoomPassword(roomNumber);
            showToast('Room deleted.', 'success', 'Deleted');
            leaveRoom();
        } catch (error) {
            handleApiError(error, 'Delete room failed');
        }
    });

    leaveRoomButton?.addEventListener('click', leaveRoom);

    modalForm?.addEventListener('submit', async (event) => {
        event.preventDefault();
        accessPassword = modalInput.value.trim();
        setRoomPassword(roomNumber, accessPassword);
        await loadRoom();
    });

    modalInput?.addEventListener('keydown', (event) => {
        if (event.key === 'Escape') {
            modal.classList.remove('open');
        }
    });

    updateCounter();
    loadRoom();
    refreshTimer = window.setInterval(loadRoom, 2000);
    messageInput.focus();
});
