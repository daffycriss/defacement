function refreshDefacedCount() {
        fetch('/dashboard/api/defaced-count')
            .then(response => response.json())
            .then(data => {
                document.getElementById('defacedCount').innerText = data.count;
            })
            .catch(error => console.error('Error refreshing dashboard:', error));
    }

    // Refresh every 3 seconds
    setInterval(refreshDefacedCount, 3000);

    // Also refresh immediately on load
    refreshDefacedCount();