function refreshDefacedTargets() {
        fetch('/dashboard/api/defaced-count') // use correct endpoint
            .then(res => res.json())
            .then(data => {
                const elem = document.getElementById('realtime-defaced-count');
                if (elem && data.count !== undefined) { // use "count" instead of "realtimeDefacedCount"
                    elem.textContent = data.count;
                }
            })
            .catch(err => console.error('Error fetching defaced targets count:', err));
    }

    // Run immediately and every 3 seconds
    refreshDefacedTargets();
    setInterval(refreshDefacedTargets, 3000);