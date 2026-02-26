function refreshTargetStatus() {
        fetch('/dashboard/api/target-status')
            .then(res => res.json())
            .then(data => {
                data.forEach(target => {
                    const row = document.querySelector(`tr[data-target-id='${target.id}']`);
                    if (!row) return;

                    // --- Update Status Badge ---
                    const statusSpan = row.querySelector('td span.badge');
                    if (statusSpan) {
                        statusSpan.textContent = target.status === 'DEFACED'
                            ? 'POSSIBLE DEFACEMENT DETECTED'
                            : target.status;

                        // Reset classes
                        statusSpan.className = 'badge';
                        switch (target.status) {
                            case 'OK':
                                statusSpan.classList.add('bg-success');
                                break;
                            case 'DEFACED':
                                statusSpan.classList.add('bg-danger');
                                break;
                            case 'ERROR':
                                statusSpan.classList.add('bg-warning', 'text-dark');
                                break;
                            default:
                                statusSpan.classList.add('bg-secondary');
                        }
                    }

                    // --- Update Last Scan ---
                    const lastScanTd = row.querySelector('td:nth-child(5)');
                    if (lastScanTd) {
                        lastScanTd.textContent = target.lastScanTime
                            ? new Date(target.lastScanTime).toLocaleString('en-GB', {
                                day: '2-digit', month: '2-digit', year: 'numeric',
                                hour: '2-digit', minute: '2-digit'
                            })
                            : '—';
                    }
                });
            })
            .catch(err => console.error('Error fetching target statuses:', err));
    }

    // Initial load + refresh every 3 seconds
    refreshTargetStatus();
    setInterval(refreshTargetStatus, 3000);