function confirmDelete(username) {
        return confirm(
            "Delete user '" + username + "'?\nThis action cannot be undone."
        );
    }