function refreshFailedIndicators() {
        fetch('/dashboard/api/failed-indicators-count')
            .then(response => response.json())
            .then(data => {
                document.querySelectorAll('tr[data-target-id]').forEach(row => {
                    const targetId = row.dataset.targetId;
                    if (data[targetId] !== undefined) {
                        row.querySelector('.failed-indicators-count').textContent = data[targetId];
                    }
                });
            })
            .catch(err => console.error('Error fetching failed indicators:', err));
    }

    // Refresh every 3 seconds
    setInterval(refreshFailedIndicators, 3000);

    // Also run once on page load
    refreshFailedIndicators();